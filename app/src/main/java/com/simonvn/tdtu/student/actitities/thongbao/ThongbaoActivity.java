package com.simonvn.tdtu.student.actitities.thongbao;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.kennyc.view.MultiStateView;
import com.simonvn.tdtu.student.R;
import com.simonvn.tdtu.student.Token;
import com.simonvn.tdtu.student.adapters.thongbao.FragmentAdapter;
import com.simonvn.tdtu.student.api.Api;
import com.simonvn.tdtu.student.fragments.dialog.EditServiceDialogFragment;
import com.simonvn.tdtu.student.fragments.thongbao.ThongbaoFragment;
import com.simonvn.tdtu.student.models.User;
import com.simonvn.tdtu.student.models.thongbao.DonviItem;
import com.simonvn.tdtu.student.models.thongbao.ThongbaoCache;
import com.simonvn.tdtu.student.models.thongbao.ThongbaoItem;
import com.simonvn.tdtu.student.service.CheckNewsService;
import com.simonvn.tdtu.student.service.ServiceUtils;
import com.simonvn.tdtu.student.utils.Tag;
import com.simonvn.tdtu.student.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class ThongbaoActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private PagerSlidingTabStrip tabs;
    private FragmentAdapter fragmentAdapter;
    private ArrayList<Fragment> fragmentArrayList;
    private ArrayList<DonviItem> donViArrayList;

    private Realm realm;
    private User user;
    private String userText, passText;

    private MultiStateView mMultiStateView;
    AppCompatImageButton btnBack;
    AppCompatImageButton btnReload;
    AppCompatImageButton btnNoti;

    private boolean enableNoti = false;

    private void khoiTao(){
        realm = Realm.getDefaultInstance();

        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();

        fragmentArrayList = new ArrayList<Fragment>();
        fragmentAdapter = new FragmentAdapter(getApplicationContext(), getSupportFragmentManager(), fragmentArrayList);

        donViArrayList = new ArrayList<DonviItem>();
    }
    private void anhXa(){
        khoiTao();
        viewPager = findViewById(R.id.viewpaper);
        viewPager.setAdapter(fragmentAdapter);

        tabs = findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mMultiStateView = findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();
            }
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnReload = findViewById(R.id.btnReload);
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reload();
            }
        });
        btnNoti = findViewById(R.id.btnNoti);

        btnNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(enableNoti){
                    if(user.getTbServiceConfig().isOpen()){
                        openOrCloseService();
                    }else{
                        openDialogConfigService();
                    }
                }
            }
        });

        btnNoti.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(enableNoti){
                    openDialogConfigService();
                }
                return true;
            }
        });
    }

    private void openDialogConfigService(){
        FragmentManager fm = getSupportFragmentManager();
        EditServiceDialogFragment alertDialog = EditServiceDialogFragment
                .newInstance(EditServiceDialogFragment.TYPE_TB);
        alertDialog.show(fm, "fragment_alert");

        alertDialog.setOnDismissEvent(new EditServiceDialogFragment.OnDismissEvent() {
            @Override
            public void onDismiss() {
                setIconNoti();
            }
        });
    }

    private void openOrCloseService(){
        realm.beginTransaction();
        if(user.getTbServiceConfig().isOpen()){
            user.getTbServiceConfig().setOpen(false);
            ServiceUtils.stopService(this
                    , CheckNewsService.class);
        }else{
            user.getTbServiceConfig().setOpen(true);
            ServiceUtils.startService(this
                    , CheckNewsService.class
                    , ServiceUtils.TIME_REPLAY[(int)user.getTbServiceConfig().getTimeReplay()]);
        }
        realm.commitTransaction();
        setIconNoti();
    }

    private void setIconNoti(){

        if(user.getTbServiceConfig().isOpen()){
            ServiceUtils.startService(this, CheckNewsService.class, user.getTbServiceConfig().getTimeReplay());
        }

        btnNoti.setImageResource(user.getTbServiceConfig().isOpen()?
                R.drawable.ic_notifications_active_black_24dp:R.drawable.ic_notifications_off_black_24dp);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thongbao);
        anhXa();
        checkHaveOffline();
    }


    private void checkHaveOffline(){
        if(realm.where(DonviItem.class).count() > 0){
            enableNoti = true;
            setIconNoti();
            RealmResults<DonviItem> realmResults = realm.where(DonviItem.class).findAll();
            donViArrayList.addAll(realmResults);
            addDonVi();
        }else {
            if(Util.isNetworkAvailable(this)){
                getDonVi();
            }else {
                Toast.makeText(this, R.string.offline_msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void getDonVi(){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new GetDonViAsync().execute("");
            }
        });
    }

    public class GetDonViAsync extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                DonviItem donViNew = null;
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "tb")
                        .timeout(30000)
                        .get();
                JSONObject root = new JSONObject(doc.text());

                if(root.has("status")){
                    if(root.getBoolean("status")){
                        JSONObject data = root.getJSONObject("data");
                        JSONArray thongBaoArray = data.getJSONArray("donvi");
                        for(int i = 0; i < thongBaoArray.length(); i++){
                            JSONObject thongBaoItem = thongBaoArray.getJSONObject(i);
                            donViNew = new DonviItem();
                            donViNew.setTitle(thongBaoItem.getString("title"));
                            donViNew.setId(thongBaoItem.getString("id"));
                            donViArrayList.add(donViNew);
                        }
                    }
                    return "success";
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            if(s != null){
                addDonVi();
                updateDonVi();
                enableNoti = true;
            }else{
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
            }
        }
    }

    private void updateDonVi(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DonviItem donviItem = null;
                for(DonviItem e: donViArrayList){
                    realm.copyToRealmOrUpdate(e);
                }
            }
        });
    }

    private void reload(){
        donViArrayList.clear();
        fragmentArrayList.clear();
        fragmentAdapter.clearTitle();
        fragmentAdapter.notifyDataSetChanged();

        // remove data offline
        realm.beginTransaction();
        realm.delete(ThongbaoCache.class);
        realm.delete(ThongbaoItem.class);
        realm.delete(DonviItem.class);
        realm.commitTransaction();
        getDonVi();
    }


    private void addDonVi(){
        ThongbaoFragment thongbaoFragment = null;
        Bundle bundle = null;

        thongbaoFragment = new ThongbaoFragment();
        bundle = new Bundle();
        bundle.putString(Tag.idDonVi, "");
        thongbaoFragment.setArguments(bundle);
        fragmentArrayList.add(thongbaoFragment);
        fragmentAdapter.addTitle("Tổng hợp");

        for(DonviItem e: donViArrayList){
            thongbaoFragment = new ThongbaoFragment();
            bundle = new Bundle();
            bundle.putString(Tag.idDonVi, e.getId());
            thongbaoFragment.setArguments(bundle);
            fragmentArrayList.add(thongbaoFragment);
            fragmentAdapter.addTitle(e.getTitle());
        }

        fragmentAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
