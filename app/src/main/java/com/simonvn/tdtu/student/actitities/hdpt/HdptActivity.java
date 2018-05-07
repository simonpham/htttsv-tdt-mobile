package com.simonvn.tdtu.student.actitities.hdpt;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.balysv.materialripple.MaterialRippleLayout;
import com.kennyc.view.MultiStateView;
import com.simonvn.tdtu.student.R;
import com.simonvn.tdtu.student.Token;
import com.simonvn.tdtu.student.actitities.OnChildSwipeRefreshListener;
import com.simonvn.tdtu.student.adapters.thongbao.FragmentAdapter;
import com.simonvn.tdtu.student.api.Api;
import com.simonvn.tdtu.student.fragments.hdpt.HdptDanhgiaFragment;
import com.simonvn.tdtu.student.fragments.hdpt.HdptHoatdongFragment;
import com.simonvn.tdtu.student.models.Config;
import com.simonvn.tdtu.student.models.User;
import com.simonvn.tdtu.student.models.hdpt.HdptDanhgiaItem;
import com.simonvn.tdtu.student.models.hdpt.HdptHoatdongItem;
import com.simonvn.tdtu.student.models.hdpt.HdptHockyItem;
import com.simonvn.tdtu.student.models.hdpt.HdptItem;
import com.simonvn.tdtu.student.models.hdpt.HdptTagDanhgiaItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class HdptActivity extends AppCompatActivity implements OnChildSwipeRefreshListener {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private PagerSlidingTabStrip tabs;
    private FragmentAdapter fragmentAdapter;
    private ArrayList<Fragment> fragmentArrayList;

    private Realm realm;
    private User user;
    private String userText, passText;

    private String idHocKy = null;
    private ArrayList<HdptHockyItem> hdptHockyItems;
    private HdptItem hdptItem;


    private MultiStateView mMultiStateView;

    private AppCompatImageButton btnBack;
    private AppCompatImageButton btnReload;
    private AppCompatImageButton btnDefault;

    private MaterialRippleLayout btnChonHocKy;

    AlertDialog.Builder dialogHocKy;

    private TextView tvTiteHocKy;

    private String idHocKyMacDinh = "";

    private void khoiTao(){
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();


        fragmentArrayList = new ArrayList<Fragment>();
        fragmentAdapter = new FragmentAdapter(getApplicationContext(), getSupportFragmentManager(), fragmentArrayList);

        hdptHockyItems = new ArrayList<HdptHockyItem>();

    }
    private void anhXa(){
        khoiTao();
        viewPager = findViewById(R.id.viewpaper);
        viewPager.setAdapter(fragmentAdapter);

        tabs = findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);
        tabs.setVisibility(View.GONE);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mMultiStateView = findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHdpt();
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
            public void onClick(View v) {
                reload();
            }
        });
        btnDefault = findViewById(R.id.btnDefault);
        btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIdHocKyMacDinh();
            }
        });

        btnChonHocKy = findViewById(R.id.btnChonHocKy);
        btnChonHocKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogHocKy != null) {
                    try {
                        dialogHocKy.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        tvTiteHocKy = findViewById(R.id.tvHocKy);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hdpt);
        anhXa();
        checkOffline();
    }
    private void reload(){
        fragmentArrayList.clear();
        fragmentAdapter.clearTitle();
        fragmentAdapter.notifyDataSetChanged();
        hdptHockyItems.clear();

        realm.beginTransaction();
        realm.delete(HdptHockyItem.class);
        realm.delete(HdptItem.class);
        realm.commitTransaction();
        getHdptHocky();
    }

    private void setIdHocKyMacDinh(){
        realm.beginTransaction();
        if(user.getConfig() == null){
            Config config = realm.createObject(Config.class);
            user.setConfig(config);
        }
        user.getConfig().setHdptIdHocKyMacDinh(idHocKy);
        realm.commitTransaction();
        idHocKyMacDinh = idHocKy;
        setIconDefault();
    }

    private void setIconDefault(){
        if(idHocKyMacDinh != null){
            if(idHocKyMacDinh.equals(idHocKy)){
                btnDefault.setImageResource(R.drawable.ic_favorite_black_24dp);
            }else{
                btnDefault.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }
        }
    }

    private void checkOffline(){
        if(realm.where(HdptHockyItem.class).count() > 0){
            RealmResults<HdptHockyItem> realmResults = realm.where(HdptHockyItem.class).findAll();
            hdptHockyItems.addAll(realmResults);
            showHdptHocky();
        }else {
            getHdptHocky();
        }
    }

    
    private void checkHdptOffline(){
        hdptItem = realm.where(HdptItem.class)
                .equalTo("idHocKy", idHocKy)
                .findFirst();
        if(hdptItem == null){
            getHdpt();
        }else {
            showHdpt();
        }
    }

    private void getHdpt(){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new getHdpt().execute();
            }
        });
    }

    @Override
    public void onChildSwipeRefreshListener() {
        getHdpt();
    }

    public class getHdpt extends AsyncTask<Void , Integer, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("id", idHocKy)
                        .data("act", "hdpt")
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
            if(s != null) {
                try {
                    JSONObject root = new JSONObject(s);
                    if(root.has("status")) {
                        if (root.getBoolean("status")) {
                            realm.beginTransaction();
                            hdptItem = new HdptItem();
                            hdptItem.setIdHocKy(idHocKy);
                            JSONObject dataRootObject = root.getJSONObject("data");
                            JSONArray hdptArray = dataRootObject.getJSONArray("hdpt");
                            JSONObject hdptObject = null;
                            HdptHoatdongItem hdptHoatdongItem = null;
                            for(int i = 0 ; i < hdptArray.length(); i++){
                                hdptObject = hdptArray.getJSONObject(i);
                                hdptHoatdongItem = new HdptHoatdongItem();
                                hdptHoatdongItem.setsTT(hdptObject.getString("STT"));
                                hdptHoatdongItem.setTenSuKien(hdptObject.getString("TenSuKien").trim());
                                hdptHoatdongItem.setThoiGian(hdptObject.getString("ThoiGian").replace("\n", ""));
                                hdptHoatdongItem.setDiemRL(hdptObject.getString("DiemRL"));
                                hdptItem.getHdptHoatdongItems().add(hdptHoatdongItem);
                            }

                            JSONArray drlArray = dataRootObject.getJSONArray("drl");
                            JSONObject drlObject = null;
                            HdptTagDanhgiaItem hdptTagDanhgiaItem = null;
                            HdptDanhgiaItem hdptDanhgiaItem = null;
                            for(int i = 0 ; i < drlArray.length(); i++){
                                drlObject = drlArray.getJSONObject(i);
                                hdptTagDanhgiaItem = new HdptTagDanhgiaItem();
                                hdptTagDanhgiaItem.setTitle(drlObject.getString("title"));
                                if(drlObject.has("data")) {
                                    JSONArray dataArray = drlObject.getJSONArray("data");
                                    JSONObject dataObject = null;
                                    for (int j = 0; j < dataArray.length(); j++) {
                                        dataObject = dataArray.getJSONObject(j);
                                        hdptDanhgiaItem = new HdptDanhgiaItem();
                                        hdptDanhgiaItem.setsTT(dataObject.getString("STT"));
                                        hdptDanhgiaItem.setNoiDung(dataObject.getString("NoiDung").trim());
                                        hdptDanhgiaItem.setKetQua(dataObject.getString("KetQua"));
                                        hdptDanhgiaItem.setDiem(dataObject.getString("Diem"));
                                        hdptTagDanhgiaItem.getHdptDanhgiaItems().add(hdptDanhgiaItem);
                                    }
                                }
                                hdptItem.getHdptTagDanhgiaItems().add(hdptTagDanhgiaItem);
                            }
                            hdptItem.setDiem(dataRootObject.getString("diem"));
                            realm.copyToRealmOrUpdate(hdptItem);
                            realm.commitTransaction();

                        }else{
                            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                        }
                    }else{
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (java.lang.IllegalStateException e) {

                }
            }
            showHdpt();
        }
    }


    private void showHdpt(){
        addPaper();
    }

    private void addPaper(){
        tabs.setVisibility(View.VISIBLE);

        Bundle bundle = null;

        viewPager.removeAllViews();
        fragmentAdapter.clearTitle();
        fragmentArrayList.clear();
        fragmentAdapter.notifyDataSetChanged();

        HdptHoatdongFragment hdptHoatdongFragment = new HdptHoatdongFragment();
        bundle = new Bundle();
        bundle.putString(HdptHoatdongFragment.EXTRA_IDHOCKY, idHocKy);
        hdptHoatdongFragment.setArguments(bundle);
        fragmentArrayList.add(hdptHoatdongFragment);
        fragmentAdapter.addTitle("Hoạt động");

        HdptDanhgiaFragment hdptDanhgiaFragment = new HdptDanhgiaFragment();
        bundle = new Bundle();
        bundle.putString(HdptDanhgiaFragment.EXTRA_IDHOCKY, idHocKy);
        hdptDanhgiaFragment.setArguments(bundle);
        fragmentArrayList.add(hdptDanhgiaFragment);
        fragmentAdapter.addTitle("Đánh giá");

        fragmentAdapter.notifyDataSetChanged();

    }

    private void getHdptHocky(){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new getHdptHocky().execute();
            }
        });
    }

    public class getHdptHocky extends AsyncTask<Void, Integer, String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "hdpt")
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
            if(s != null) {
                try {

                    JSONObject root = new JSONObject(s);
                    if(root.has("status")) {
                        if (root.getBoolean("status")) {
                            realm.beginTransaction();
                            JSONArray data = root.getJSONArray("data");
                            JSONObject get = null;
                            HdptHockyItem hdptHockyItem = null;
                            for(int i = 0 ; i < data.length(); i++){
                                get = data.getJSONObject(i);
                                hdptHockyItem = new HdptHockyItem();
                                hdptHockyItem.setId(get.getString("id"));
                                hdptHockyItem.setTenHocKy(get.getString("TenHocKy"));
                                hdptHockyItems.add(hdptHockyItem);
                                realm.copyToRealmOrUpdate(hdptHockyItem);
                            }
                            realm.commitTransaction();
                        }else{
                            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                        }
                    }else{
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (java.lang.IllegalStateException e) {

                }
            }
            showDialogHocKy();
        }
    }

    private void showDialogHocKy(){
        initDialogHocKy();
        if (dialogHocKy != null) {
            try {
                dialogHocKy.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showHdptHocky(){
        int pos = -1;
        //Chuyển đến thời khóa biểu mặc định
        if(user.getConfig() != null){
            if(user.getConfig().getHdptIdHocKyMacDinh() != null) {
                idHocKyMacDinh = user.getConfig().getHdptIdHocKyMacDinh();
                for (int i = 0; i < hdptHockyItems.size(); i++) {
                    if (hdptHockyItems.get(i).getId().equals(idHocKyMacDinh)) {
                        pos = i;
                        break;
                    }
                }
                if (pos != -1) {
                    chonHocKy(pos);
                }
            }
        }

        if(pos == -1){
            chonHocKy(hdptHockyItems.size() - 1);
        }
        initDialogHocKy();
    }

    private void initDialogHocKy(){
        ArrayAdapter<String> tenHocKys = new ArrayAdapter<String>(this
                , R.layout.my_select_dialog_item);

        for(HdptHockyItem e: hdptHockyItems){
            tenHocKys.add(e.getTenHocKy());
        }

        dialogHocKy = new AlertDialog.Builder(this)
                .setTitle(R.string.bottom_sheet_title_hocky)
                .setAdapter(tenHocKys, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        chonHocKy(i);
                    }
                });

        dialogHocKy.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private void chonHocKy(int postition){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        tvTiteHocKy.setText(hdptHockyItems.get(postition).getTenHocKy());
        idHocKy = hdptHockyItems.get(postition).getId();
        setIconDefault();
        checkHdptOffline();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
