package com.simonvn.tdtu.student.actitities.trangchu;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import com.simonvn.tdtu.student.BuildConfig;
import com.simonvn.tdtu.student.MainActivity;
import com.simonvn.tdtu.student.R;
import com.simonvn.tdtu.student.fragments.trangchu.TrangchuMenuFragment;
import com.simonvn.tdtu.student.models.User;
import com.simonvn.tdtu.student.models.firebase.News;
import com.simonvn.tdtu.student.models.firebase.UpdateApp;
import com.simonvn.tdtu.student.models.firebase.UserOnline;
import com.simonvn.tdtu.student.utils.StringUtil;
import com.simonvn.tdtu.student.views.widget.SnowingView;
import io.realm.Realm;
import ru.alexbykov.nopermission.PermissionHelper;

public class TrangchuActivity extends AppCompatActivity{
    private Realm realm;
    private User user;

    private String userText, passText, avatarText, nameText;
    private TextView massv, name;

    private DatabaseReference mDatabase;
    private DatabaseReference updateReference;
    private DatabaseReference newsReference;
    private DatabaseReference userReference;

    private static final String KEY_EVENT_UPDATE = "KEY_EVENT_UPDATE";
    private static final String KEY_EVENT_NEWS = "KEY_EVENT_NEWS";
    /* test simon
    private static final String KEY_EVENT_USER = "KEY_EVENT_USER";
    */
    private HashMap<String, ValueEventListener> eventListenerHashMap;
    private HashMap<String, DatabaseReference> referenceHashMap;

    private LinearLayout layoutUpdate;
    private TextView tvTileUpdate;
    private WebView logUpdate;
    private Button btnUpdate;

    private LinearLayout layoutNews;
    private TextView tvTileNews;
    private WebView logNews;

    private Button btnLogout;

    private UpdateApp updateApp;
    private News news;

    private SnowingView snowingView;


    private void khoiTao(){
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();
        nameText = user.getName();
        avatarText = user.getLinkAvatar();

        layoutUpdate = (LinearLayout) findViewById(R.id.layoutUpdate);
        tvTileUpdate = (TextView) findViewById(R.id.tvTitleUpdate);
        logUpdate = (WebView) findViewById(R.id.logUpdate);
        btnUpdate = (Button) findViewById(R.id.btnDownloadUpdate);

        layoutNews = (LinearLayout) findViewById(R.id.layoutNews);
        tvTileNews = (TextView) findViewById(R.id.tvTitleNews);
        logNews = (WebView) findViewById(R.id.logNews);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        eventListenerHashMap = new HashMap<>();
        referenceHashMap = new HashMap<>();
    }
    private void anhXa(){
        khoiTao();


        name = (TextView) findViewById(R.id.name_text);
        massv = (TextView) findViewById(R.id.mssv_text);


        name.setText(nameText);
        massv.setText(userText);


        snowingView = (SnowingView) findViewById(R.id.snowing_view);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trangchu);
        anhXa();
        addPaper();

        PermissionHelper permissionHelper = new PermissionHelper(this); //getActivity in fragments

        permissionHelper.check(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onSuccess(new Runnable() {
                    @Override
                    public void run() {

                    }
                })
                .onFailure(new Runnable() {
                    @Override
                    public void run() {

                    }
                })
                .onNeverAskAgain(new Runnable() {
                    @Override
                    public void run() {

                    }
                })
                .run();

        userReference = mDatabase.child("UserOnline");
/* test simon
        userReference.keepSynced(false);
        referenceHashMap.put(KEY_EVENT_USER, userReference);
        eventListenerHashMap.put(KEY_EVENT_USER, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int onlineNum = 0;
                UserOnline userOnline = null;
                long timeNow = System.currentTimeMillis();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    userOnline = postSnapshot.getValue(UserOnline.class);
                    if((timeNow - userOnline.time) <= 2 * 60 * 1000)
                        onlineNum++;

                }
                //tvOnlineNum.setText(""+onlineNum);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        userReference.addValueEventListener(eventListenerHashMap.get(KEY_EVENT_USER));
*/

        updateReference = mDatabase.child("Update");
        referenceHashMap.put(KEY_EVENT_UPDATE, updateReference);
        eventListenerHashMap.put(KEY_EVENT_UPDATE, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateApp = dataSnapshot.getValue(UpdateApp.class);
                checkUpdate();
                changeSnowView(updateApp.calendar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        updateReference.addValueEventListener(eventListenerHashMap.get(KEY_EVENT_UPDATE));

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateApp.downloadUrl));
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(TrangchuActivity.this, "No application can handle this request."
                            + " Please install a web browser",  Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        newsReference = mDatabase.child("News");
        referenceHashMap.put(KEY_EVENT_NEWS, newsReference);
        eventListenerHashMap.put(KEY_EVENT_NEWS, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                news = dataSnapshot.getValue(News.class);
                news();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        newsReference.addValueEventListener(eventListenerHashMap.get(KEY_EVENT_NEWS));

    }


    private void changeSnowView(int calendar) {
        switch (calendar){
            case 1:
                snowingView.setIcon(R.drawable.snowflake);
                break;
            case 2:
                snowingView.setIcon(R.drawable.ic_moon_festival);
                break;
            case 3:
                snowingView.setIcon(R.drawable.ic_halloween);
                break;
            case 4:
                snowingView.setIcon(R.drawable.ic_hoamai);
                break;
            case 5:
                snowingView.setIcon(R.drawable.ic_heart_icon);
                break;
            default:
                snowingView.setVisibility(View.GONE);
                snowingView.stopFall();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserOnline userOnline = new UserOnline();
        userOnline.mssv = userText;
        userOnline.time = System.currentTimeMillis();
        userReference.child(userOnline.mssv).setValue(userOnline);
    }

    private void news(){
        if(news == null)
            return;

        if(news.show){
            //String title = news.title + " - " + StringUtil.getDate(news.time, "dd/MM/yyyy");
            String title = " " + news.title + " - " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date (news.time*1000));
            tvTileNews.setText(title);
            logNews.getSettings().setJavaScriptEnabled(true);
            logNews.setBackgroundColor(Color.TRANSPARENT);
            logNews.loadDataWithBaseURL("", news.body, "text/html", "UTF-8", "");
            layoutNews.setVisibility(View.VISIBLE);
        }

        if(!news.show){
            layoutNews.setVisibility(View.GONE);
        }
    }

    private void checkUpdate(){
        if(updateApp == null)
            return;
        if(BuildConfig.VERSION_NAME.equals(updateApp.ver)){
            layoutUpdate.setVisibility(View.GONE);
            return;
        }
        String title = getString(R.string.new_version) + updateApp.ver + " - " + StringUtil.getDate(updateApp.time, "dd/MM/yyyy");
        tvTileUpdate.setText(title);
        logUpdate.getSettings().setJavaScriptEnabled(true);
        logUpdate.setBackgroundColor(Color.TRANSPARENT);
        logUpdate.loadDataWithBaseURL("", updateApp.changelog, "text/html", "UTF-8", "");
        layoutUpdate.setVisibility(View.VISIBLE);
    }

    private void addPaper(){
        TrangchuMenuFragment trangchuMenuFragment = new TrangchuMenuFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, trangchuMenuFragment);
        ft.commit();
    }

    private void logOut(){
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.mess_logout_tile))
                .setMessage(getResources().getString(R.string.mess_logout_info))
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realm.beginTransaction();
                        realm.deleteAll();
                        realm.commitTransaction();
                        Intent mainActicity = new Intent(TrangchuActivity.this, MainActivity.class);
                        startActivity(mainActicity);
                        finish();
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        for(Map.Entry<String, ValueEventListener> entry : eventListenerHashMap.entrySet()){
            referenceHashMap.get(entry.getKey()).removeEventListener(entry.getValue());
        }
        referenceHashMap.clear();
        eventListenerHashMap.clear();
    }
}
