package com.simonvn.tdtu.student.actitities.lichthi;

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
import com.simonvn.tdtu.student.fragments.lichthi.LichThiFragment;
import com.simonvn.tdtu.student.models.Config;
import com.simonvn.tdtu.student.models.User;
import com.simonvn.tdtu.student.models.lichthi.LichThiHocKyItem;
import com.simonvn.tdtu.student.models.lichthi.LichThiItem;
import com.simonvn.tdtu.student.models.lichthi.LichThiLichItem;
import com.simonvn.tdtu.student.utils.Tag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.realm.Realm;
import io.realm.RealmResults;

public class LichThiActivity extends AppCompatActivity implements OnChildSwipeRefreshListener {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private PagerSlidingTabStrip tabs;
    private FragmentAdapter fragmentAdapter;
    private ArrayList<Fragment> fragmentArrayList;

    private Realm realm;
    private User user;
    private String userText, passText;

    private String idHocKy = null;
    private ArrayList<LichThiHocKyItem> lichThiHocKyItems;
    private LichThiItem lichThiItem;

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

        lichThiHocKyItems = new ArrayList<LichThiHocKyItem>();
    }
    private void anhXa(){
        khoiTao();
        viewPager = (ViewPager) findViewById(R.id.viewpaper);
        viewPager.setAdapter(fragmentAdapter);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);
        tabs.setVisibility(View.GONE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLichThi();
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
            public void onClick(View v) {
                reload();
            }
        });
        btnDefault = (AppCompatImageButton) findViewById(R.id.btnDefault);
        btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIdHocKyMacDinh();
            }
        });

        btnChonHocKy = (MaterialRippleLayout) findViewById(R.id.btnChonHocKy);
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

        tvTiteHocKy = (TextView)findViewById(R.id.tvHocKy);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lich_thi);
        anhXa();
        checkOffline();
    }

    private void reload(){
        fragmentArrayList.clear();
        fragmentAdapter.clearTitle();
        fragmentAdapter.notifyDataSetChanged();
        lichThiHocKyItems.clear();

        realm.beginTransaction();
        realm.delete(LichThiHocKyItem.class);
        realm.delete(LichThiItem.class);
        realm.delete(LichThiLichItem.class);
        realm.commitTransaction();
        getHocky();
    }

    private void setIdHocKyMacDinh(){
        realm.beginTransaction();
        if(user.getConfig() == null){
            Config config = realm.createObject(Config.class);
            user.setConfig(config);
        }
        user.getConfig().setLichThiIdHocKyMacDinh(idHocKy);
        idHocKyMacDinh = idHocKy;
        realm.commitTransaction();
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
        if(realm.where(LichThiHocKyItem.class).count() > 0){
            RealmResults<LichThiHocKyItem> realmResults = realm.where(LichThiHocKyItem.class).findAll();
            lichThiHocKyItems.addAll(realmResults);
            showHocky();
        }else {
            getHocky();
        }
    }

    private void checkLichThiOffline(){
        lichThiItem = realm.where(LichThiItem.class)
                .equalTo("idHocKy", idHocKy)
                .findFirst();
        if(lichThiItem == null){
            getLichThi();
        }else {
            showLichThi();
        }
    }

    private void getLichThi(){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new GetLichThi().execute("");
            }
        });
    }

    @Override
    public void onChildSwipeRefreshListener() {
        getLichThi();
    }

    public class GetLichThi extends AsyncTask<String , Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "lt")
                        .data("id", idHocKy)
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
                            lichThiItem = new LichThiItem();
                            JSONObject data = root.getJSONObject("data");
                            lichThiItem.setIdHocKy(idHocKy);

                            JSONArray lichThiGiaKyArray = data.getJSONArray("giuaky");
                            LichThiLichItem lichThiLichItem = null;

                            JSONObject licThiObject = null;
                            for(int i = 0 ; i < lichThiGiaKyArray.length(); i++){
                                licThiObject = lichThiGiaKyArray.getJSONObject(i);
                                lichThiLichItem = new LichThiLichItem();
                                lichThiLichItem.setTenMH(licThiObject.getString("TenMH"));
                                lichThiLichItem.setGioThi(licThiObject.getString("GioThi"));
                                lichThiLichItem.setMaMH(licThiObject.getString("MaMH"));
                                lichThiLichItem.setNgayThi(licThiObject.getString("NgayThi"));
                                lichThiLichItem.setPhong(licThiObject.getString("Phong"));
                                lichThiLichItem.setThoiLuong(licThiObject.getString("ThoiLuong"));
                                lichThiLichItem.setTo(licThiObject.getString("To"));
                                lichThiLichItem.setNhom(licThiObject.getString("Nhom"));
                                lichThiItem.getGiuaKy().add(lichThiLichItem);
                            }

                            Collections.sort(lichThiItem.getGiuaKy(), new Comparator<LichThiLichItem>() {
                                @Override
                                public int compare(LichThiLichItem o1, LichThiLichItem o2) {
                                    return o1.getNgayThi().compareTo(o2.getNgayThi());
                                }
                            });


                            JSONArray lichThiCuoiKyArray = data.getJSONArray("cuoiky");
                            lichThiLichItem = null;

                            licThiObject = null;
                            for(int i = 0 ; i < lichThiCuoiKyArray.length(); i++){
                                licThiObject = lichThiCuoiKyArray.getJSONObject(i);
                                lichThiLichItem = new LichThiLichItem();
                                lichThiLichItem.setTenMH(licThiObject.getString("TenMH"));
                                lichThiLichItem.setGioThi(licThiObject.getString("GioThi"));
                                lichThiLichItem.setMaMH(licThiObject.getString("MaMH"));
                                lichThiLichItem.setNgayThi(licThiObject.getString("NgayThi"));
                                lichThiLichItem.setPhong(licThiObject.getString("Phong"));
                                lichThiLichItem.setThoiLuong(licThiObject.getString("ThoiLuong"));
                                lichThiLichItem.setTo(licThiObject.getString("To"));
                                lichThiLichItem.setNhom(licThiObject.getString("Nhom"));
                                lichThiItem.getCuoiKy().add(lichThiLichItem);
                            }

                            Collections.sort(lichThiItem.getCuoiKy(), new Comparator<LichThiLichItem>() {
                                @Override
                                public int compare(LichThiLichItem o1, LichThiLichItem o2) {
                                    return o1.getNgayThi().compareTo(o2.getNgayThi());
                                }
                            });

                            realm.copyToRealmOrUpdate(lichThiItem);
                            realm.commitTransaction();
                        }else{
                            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                        }
                    }else{
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    realm.commitTransaction();
                } catch (java.lang.IllegalStateException e) {
                    realm.commitTransaction();
                }
            }
            showLichThi();
        }
    }

    private void showLichThi(){
        addPaper();
    }

    private void addPaper(){
        tabs.setVisibility(View.VISIBLE);

        Bundle bundle = null;


        viewPager.removeAllViews();
        fragmentAdapter.clearTitle();
        fragmentArrayList.clear();
        fragmentAdapter.notifyDataSetChanged();

        bundle = new Bundle();
        bundle.putString(Tag.idHocKy, idHocKy);
        bundle.putInt(LichThiFragment.ARG_TYPE, 1);
        LichThiFragment lichThiFragment = new LichThiFragment();
        lichThiFragment.setArguments(bundle);
        fragmentArrayList.add(lichThiFragment);
        fragmentAdapter.addTitle("Giữa kỳ");

        bundle = new Bundle();
        bundle.putString(Tag.idHocKy, idHocKy);
        bundle.putInt(LichThiFragment.ARG_TYPE, 2);
        lichThiFragment = new LichThiFragment();
        lichThiFragment.setArguments(bundle);
        fragmentArrayList.add(lichThiFragment);
        fragmentAdapter.addTitle("Cuối kỳ");

        fragmentAdapter.notifyDataSetChanged();
    }

    private void getHocky(){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new GetHocky().execute("");
            }
        });
    }

    public class GetHocky extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "lt")
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
                    if(root.has("status")){
                        if(root.getBoolean("status")){
                            JSONArray data = root.getJSONArray("data");
                            JSONObject get = null;
                            LichThiHocKyItem lichThiHocKyItem = null;
                            realm.beginTransaction();
                            for(int i = 0 ; i < data.length(); i++){
                                get = data.getJSONObject(i);
                                lichThiHocKyItem = new LichThiHocKyItem();
                                lichThiHocKyItem.setId(get.getString("id"));
                                lichThiHocKyItem.setTenHocKy(get.getString("TenHocKy"));
                                lichThiHocKyItems.add(lichThiHocKyItem);
                                realm.copyToRealmOrUpdate(lichThiHocKyItem);
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

    private void showHocky(){
        int pos = -1;
        //Chuyển đến thời khóa biểu mặc định
        if(user.getConfig() != null){
            if(user.getConfig().getLichThiIdHocKyMacDinh() != null) {
                idHocKyMacDinh = user.getConfig().getLichThiIdHocKyMacDinh();
                for (int i = 0; i < lichThiHocKyItems.size(); i++) {
                    if (lichThiHocKyItems.get(i).getId().equals(idHocKyMacDinh)) {
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
            if(lichThiHocKyItems.size() > 0){
                chonHocKy(0);
            }
        }

        initDialogHocKy();

    }

    private void initDialogHocKy(){
        ArrayAdapter<String> tenHocKys = new ArrayAdapter<String>(this
                , R.layout.my_select_dialog_item);

        for(LichThiHocKyItem e: lichThiHocKyItems){
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
        tvTiteHocKy.setText(lichThiHocKyItems.get(postition).getTenHocKy());
        idHocKy = lichThiHocKyItems.get(postition).getId();
        setIconDefault();
        checkLichThiOffline();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
