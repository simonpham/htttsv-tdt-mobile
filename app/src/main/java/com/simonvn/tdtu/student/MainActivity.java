package com.simonvn.tdtu.student;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.simonvn.tdtu.student.actitities.Drawer;
import com.simonvn.tdtu.student.actitities.dangnhap.DangnhapActivity;
import com.simonvn.tdtu.student.models.User;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {
    private Realm realm;
    private RealmConfiguration realmConfig;
    private User user;
    private void khoiTao(){
        realm = Realm.getDefaultInstance();
    }
    private void anhXa(){
        khoiTao();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        khoiTao();
        checkAccount();
    }


    private void checkAccount(){
        user = realm.where(User.class).findFirst();

        if(user != null){
            Intent trangChu = new Intent(MainActivity.this, Drawer.class);
            startActivity(trangChu);
            finish();
        }else {
            Intent dangNhap = new Intent(MainActivity.this, DangnhapActivity.class);
            startActivity(dangNhap);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
