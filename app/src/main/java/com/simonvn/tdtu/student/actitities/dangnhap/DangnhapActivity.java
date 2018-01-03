package com.simonvn.tdtu.student.actitities.dangnhap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.simonvn.tdtu.student.BuildConfig;
import com.simonvn.tdtu.student.R;
import com.simonvn.tdtu.student.Token;
import com.simonvn.tdtu.student.actitities.Drawer;
import com.simonvn.tdtu.student.api.Api;
import com.simonvn.tdtu.student.models.ServiceConfig;
import com.simonvn.tdtu.student.models.User;
import com.simonvn.tdtu.student.service.CheckEmailService;
import com.simonvn.tdtu.student.service.CheckNewsService;
import com.simonvn.tdtu.student.service.ServiceUtils;
import com.simonvn.tdtu.student.views.widget.MaterialSquareLoading;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class DangnhapActivity extends Activity implements View.OnClickListener{
    private Button btnDangNhap;
    private EditText tvUsername;
    private EditText tvPassword;
    private TextView tvVersion;
    private TextInputLayout inputLayoutName, inputLayoutPassword;
    private MaterialSquareLoading waitProgress;
    private ImageButton ibtnHideShowPass;

    private String textUserGet, textPasswordGet;
    private boolean isShowPass = true;

    private Realm realm;
    private RealmConfiguration realmConfig;

    private User user;
    private void khoiTao(){
        realm = Realm.getDefaultInstance();
    }
    private void anhXa(){
        khoiTao();
        btnDangNhap = (Button)findViewById(R.id.button_dangnhap);
        btnDangNhap.setOnClickListener(this);
        tvUsername = (EditText) findViewById(R.id.tvUsername);
        tvPassword = (EditText) findViewById(R.id.tvPassword);

        tvVersion = (TextView) findViewById(R.id.tvVersion);
        tvVersion.setText(BuildConfig.VERSION_NAME);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);

        tvPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    danhNhap();
                }
                return false;
            }
        });
        waitProgress = (MaterialSquareLoading) findViewById(R.id.progressBar);
        waitProgress.hide();
        ibtnHideShowPass = (ImageButton) findViewById(R.id.hide_show_pass);
        ibtnHideShowPass.setOnClickListener(this);
        hideShowPass();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dangnhap);
        anhXa();
        ServiceUtils.stopService(this, CheckEmailService.class);
        ServiceUtils.stopService(this, CheckNewsService.class);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_dangnhap:
                danhNhap();
                break;
            case R.id.hide_show_pass:
                hideShowPass();
                break;
        }
    }

    private void hideShowPass(){
        int start;
        int end;
        if(isShowPass){
            ibtnHideShowPass.setImageResource(R.drawable.ic_eye);
            isShowPass = false;
            start = tvPassword.getSelectionStart();
            end = tvPassword.getSelectionEnd();
            tvPassword.setTransformationMethod(new PasswordTransformationMethod());;
            tvPassword.setSelection(start,end);
        }else {
            ibtnHideShowPass.setImageResource(R.drawable.ic_eye_off);
            isShowPass = true;
            start = tvPassword.getSelectionStart();
            end = tvPassword.getSelectionEnd();
            tvPassword.setTransformationMethod(null);
            tvPassword.setSelection(start,end);
        }
    }

    private void danhNhap(){
        textUserGet = tvUsername.getText().toString().trim();
        textPasswordGet = tvPassword.getText().toString().trim();
        if(textUserGet.equals("")){
            inputLayoutName.setError(getResources().getString(R.string.mess_error_user));
            requestFocus(tvUsername);
            return;
        }
        if(textPasswordGet.equals("")){
            inputLayoutPassword.setError(getResources().getString(R.string.mess_error_password));
            requestFocus(tvPassword);
            return;
        }
        inputLayoutName.setErrorEnabled(false);
        inputLayoutPassword.setErrorEnabled(false);
        checkLogin();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void checkLogin(){
        waitProgress.show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new readThongTinDanhNhap().execute("");
            }
        });
    }

    public class readThongTinDanhNhap extends AsyncTask<String, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", textUserGet)
                        .data("token", Token.getToken(textUserGet, textPasswordGet))
                        .data("act", "avatar")
                        .timeout(30000)
                        .get();
                JSONObject root = new JSONObject(doc.text());
                if(root.has("status")){
                    if(root.getBoolean("status")){
                        JSONObject data = root.getJSONObject("data");
                        user = new User();
                        user.setUserName(textUserGet);
                        user.setPassWord(textPasswordGet);
                        user.setName(data.getString("name"));
                        user.setLinkAvatar(data.getString("src"));
                        user.setId(1);
                        return true;
                    }
                }
                return false;
            } catch (IOException e) {
                return false;
            } catch (JSONException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if(s){

                // set data config for user
                ServiceConfig tbServiceConfig = new ServiceConfig();
                tbServiceConfig.setId(1);
                tbServiceConfig.setOpen(false);
                tbServiceConfig.setSound(false);
                tbServiceConfig.setVibrate(false);
                tbServiceConfig.setTimeReplay(0);
                user.setTbServiceConfig(tbServiceConfig);

                ServiceConfig emailServiceConfig = new ServiceConfig();
                emailServiceConfig.setId(2);
                emailServiceConfig.setOpen(false);
                emailServiceConfig.setSound(false);
                emailServiceConfig.setVibrate(false);
                emailServiceConfig.setTimeReplay(0);
                user.setEmailServiceConfig(emailServiceConfig);
                user.setShowAvatar(false);

                user.setCheckNetworkState(false);

                realm.beginTransaction();
                realm.copyToRealmOrUpdate(user);
                realm.commitTransaction();

                Intent trangChu = new Intent(DangnhapActivity.this, Drawer.class);
                startActivity(trangChu);
                finish();

            }else {
                inputLayoutPassword.setError(getResources().getString(R.string.mess_error_login));
            }
            waitProgress.hide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
