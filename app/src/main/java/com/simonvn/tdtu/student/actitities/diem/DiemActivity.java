package com.simonvn.tdtu.student.actitities.diem;

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
import com.simonvn.tdtu.student.fragments.diem.DiemFragment;
import com.simonvn.tdtu.student.fragments.diem.DiemTonghopFragment;
import com.simonvn.tdtu.student.models.User;
import com.simonvn.tdtu.student.models.diem.Diem;
import com.simonvn.tdtu.student.models.diem.DiemHockyItem;
import com.simonvn.tdtu.student.models.diem.DiemItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.simonvn.tdtu.student.utils.Tag.idHocKy;

public class DiemActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private PagerSlidingTabStrip tabs;
    private FragmentAdapter fragmentAdapter;
    private ArrayList<Fragment> fragmentArrayList;
    private ArrayList<DiemHockyItem> diemHockyItems;

    private Realm realm;
    private User user;
    private String userText, passText;

    private MultiStateView mMultiStateView;
    AppCompatImageButton btnBack;
    AppCompatImageButton btnReload;

    private void khoiTao(){
        realm = Realm.getDefaultInstance();

        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();

        fragmentArrayList = new ArrayList<Fragment>();
        fragmentAdapter = new FragmentAdapter(getApplicationContext(), getSupportFragmentManager(), fragmentArrayList);

        diemHockyItems = new ArrayList<DiemHockyItem>();
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
        setContentView(R.layout.activity_diem);
        anhXa();
        checkOffline();
    }

    private void checkOffline(){
        if(realm.where(DiemHockyItem.class).count() > 0){
            RealmResults<DiemHockyItem> realmResults = realm.where(DiemHockyItem.class).findAll();
            diemHockyItems.addAll(realmResults);
            addPaper();
        }else {
            getHocKy();
        }
    }


    private void addPaper(){
        DiemTonghopFragment diemTonghopFragment = new DiemTonghopFragment();
        fragmentArrayList.add(diemTonghopFragment);
        fragmentAdapter.addTitle("Điểm tổng hợp");


        DiemFragment diemFragment = null;
        Bundle bundle = null;
        for(int i = 0 ; i < diemHockyItems.size(); i++){
            diemFragment = new DiemFragment();
            bundle = new Bundle();
            bundle.putString(idHocKy, diemHockyItems.get(i).getNameTable());
            diemFragment.setArguments(bundle);
            fragmentArrayList.add(diemFragment);
            fragmentAdapter.addTitle(diemHockyItems.get(i).getTenHocKy());
        }
        fragmentAdapter.notifyDataSetChanged();
    }

    private void getHocKy(){
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
                        .data("act", "kqht")
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
            if (s != null) {
                try {
                    JSONObject root = new JSONObject(s);
                    if(root.has("status")) {
                        if (root.getBoolean("status")) {
                            realm.beginTransaction();
                            JSONArray data = root.getJSONArray("data");
                            JSONObject rootObject = null;
                            DiemHockyItem diemHockyItem = null;
                            if (data.length() > 0) {
                                for (int i = 0; i < data.length(); i++) {
                                    rootObject = data.getJSONObject(i);
                                    diemHockyItem = new DiemHockyItem();
                                    diemHockyItem.setId(rootObject.getString("id"));
                                    diemHockyItem.setNameTable(rootObject.getString("NameTable"));
                                    diemHockyItem.setTenHocKy(rootObject.getString("TenHocKy"));
                                    diemHockyItems.add(diemHockyItem);
                                    realm.copyToRealmOrUpdate(diemHockyItem);
                                }
                            }
                            realm.commitTransaction();
                        }else{
                            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (java.lang.IllegalStateException e) {
                    realm.close();
                }
                addPaper();
            }
        }
    }

    private void reload(){
        diemHockyItems.clear();
        fragmentArrayList.clear();
        fragmentAdapter.clearTitle();
        fragmentAdapter.notifyDataSetChanged();

        // remove data offline
        realm.beginTransaction();
        realm.delete(DiemHockyItem.class);
        realm.delete(DiemItem.class);
        realm.delete(Diem.class);
        realm.commitTransaction();

        getHocKy();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
