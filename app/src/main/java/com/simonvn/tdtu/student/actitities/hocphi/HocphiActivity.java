package com.simonvn.tdtu.student.actitities.hocphi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.kennyc.view.MultiStateView;
import com.simonvn.tdtu.student.R;
import com.simonvn.tdtu.student.Token;
import com.simonvn.tdtu.student.adapters.thongbao.FragmentAdapter;
import com.simonvn.tdtu.student.api.Api;
import com.simonvn.tdtu.student.fragments.hocphi.HocphiFragment;
import com.simonvn.tdtu.student.models.User;
import com.simonvn.tdtu.student.models.hocphi.HockyItem;
import com.simonvn.tdtu.student.models.hocphi.HocphiChitiet;
import com.simonvn.tdtu.student.models.hocphi.HocphiItem;
import com.simonvn.tdtu.student.utils.Tag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class HocphiActivity extends AppCompatActivity{
    private Toolbar toolbar;
    private ViewPager viewPager;
    private PagerSlidingTabStrip tabs;
    private FragmentAdapter fragmentAdapter;
    private ArrayList<Fragment> fragmentArrayList;
    private RealmList<HockyItem> hocKyArrayList;

    private MultiStateView mMultiStateView;
    AppCompatImageButton btnBack;
    AppCompatImageButton btnReload;

    private Realm realm;
    private User user;
    private String userText, passText;


    private String idHockyNow;
    private void khoiTao(){
        realm = Realm.getDefaultInstance();

        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();

        fragmentArrayList = new ArrayList<Fragment>();
        fragmentAdapter = new FragmentAdapter(getApplicationContext(), getSupportFragmentManager(), fragmentArrayList);

        hocKyArrayList = new RealmList<HockyItem>();
    }
    private void anhXa(){
        khoiTao();
        viewPager = (ViewPager) findViewById(R.id.viewpaper);
        viewPager.setAdapter(fragmentAdapter);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();
            }
        });

        btnBack = (AppCompatImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnReload = (AppCompatImageButton) findViewById(R.id.btnReload);
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reload();
            }
        });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hocphi);
        anhXa();
        checkHaveOffline();
    }


    private void checkHaveOffline(){
        if(realm.where(HockyItem.class).count() > 0){
            RealmResults<HockyItem> realmResults = realm.where(HockyItem.class).findAll();
            hocKyArrayList.addAll(realmResults);
            addDonVi();
        }else {
            getHocKy();
        }
    }

    public void getHocKy(){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new getHocKy().execute("");
            }
        });
    }


    public class getHocKy extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "hp")
                        .data("option", "lhk")
                        .timeout(30000)
                        .get();
                return doc.text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            if(s != null){
                try {
                    JSONObject root = new JSONObject(s);
                    if(root.has("status")) {
                        if (root.getBoolean("status")) {
                            JSONArray data = root.getJSONArray("data");
                            realm.beginTransaction();
                            HockyItem hockyItemNew = null;
                            hocKyArrayList.clear();
                            for(int i = 0; i < data.length(); i++){
                                JSONObject hockyItem = data.getJSONObject(i);
                                hockyItemNew = new HockyItem();
                                hockyItemNew.setId(hockyItem.getString("ID"));
                                hockyItemNew.setName(hockyItem.getString("DisplayName"));
                                realm.copyToRealmOrUpdate(hockyItemNew);
                                hocKyArrayList.add(hockyItemNew);
                            }
                            realm.commitTransaction();
                        }
                    }else{
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            addDonVi();
        }
    }


    private void reload(){
        hocKyArrayList.clear();
        fragmentArrayList.clear();
        fragmentAdapter.clearTitle();
        fragmentAdapter.notifyDataSetChanged();

        // remove data offline
        realm.beginTransaction();
        realm.delete(HockyItem.class);
        realm.delete(HocphiChitiet.class);
        realm.delete(HocphiItem.class);
        realm.commitTransaction();

        getHocKy();
    }


    private void addDonVi(){
        HocphiFragment hocphiFragment = null;
        Bundle bundle = null;
        for(HockyItem e: hocKyArrayList){
            hocphiFragment = new HocphiFragment();
            bundle = new Bundle();
            bundle.putString(Tag.idHocKy, e.getId());
            hocphiFragment.setArguments(bundle);
            fragmentArrayList.add(hocphiFragment);
            fragmentAdapter.addTitle(e.getName());
        }

        fragmentAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}