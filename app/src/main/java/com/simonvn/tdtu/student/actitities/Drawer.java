package com.simonvn.tdtu.student.actitities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
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
import com.simonvn.tdtu.student.BuildConfig;
import com.simonvn.tdtu.student.MainActivity;
import com.simonvn.tdtu.student.R;
import com.simonvn.tdtu.student.actitities.cnsv.CnsvActivity;
import com.simonvn.tdtu.student.actitities.diem.DiemActivity;
import com.simonvn.tdtu.student.actitities.email.EmailActivity;
import com.simonvn.tdtu.student.actitities.email.EmailNewActivity;
import com.simonvn.tdtu.student.actitities.hdpt.HdptActivity;
import com.simonvn.tdtu.student.actitities.hocphi.HocphiActivity;
import com.simonvn.tdtu.student.actitities.lichthi.LichThiActivity;
import com.simonvn.tdtu.student.actitities.ndtt.NdttActivity;
import com.simonvn.tdtu.student.actitities.sakai.SakaiActivity;
import com.simonvn.tdtu.student.actitities.thongbao.ThongbaoActivity;
import com.simonvn.tdtu.student.actitities.tkb.TkbActivity;
import com.simonvn.tdtu.student.fragments.trangchu.TrangchuMenuFragment;
import com.simonvn.tdtu.student.models.User;
import com.simonvn.tdtu.student.models.firebase.News;
import com.simonvn.tdtu.student.models.firebase.UpdateApp;
import com.simonvn.tdtu.student.models.firebase.UserOnline;
import com.simonvn.tdtu.student.views.widget.SnowingView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import ru.alexbykov.nopermission.PermissionHelper;

public class Drawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

    public void setLocale(String lang) {
        SharedPreferences prefs = this.getSharedPreferences("com.simonvn.tdt.student", Context.MODE_PRIVATE);
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, Drawer.class);

        prefs.edit().putString("com.simonvn.tdtu.student.locale", lang).apply();

        startActivity(refresh);
        finish();
    }
    public String getLocale() {
        Locale current = getResources().getConfiguration().locale;
        return current.getLanguage();
    }

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
        setContentView(R.layout.activity_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefs = this.getSharedPreferences("com.simonvn.tdt.student", Context.MODE_PRIVATE);


        String locale = prefs.getString("com.simonvn.tdtu.student.locale", "en");
        if (!(locale.equals(getLocale()))) {
            setLocale(locale);
        }

        anhXa();
        addPaper();

        getSupportActionBar().setTitle(getString(R.string.hello) + name.getText().toString());

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
                    Toast.makeText(Drawer.this, "No application can handle this request."
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

        // select dashboard on startup
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        navigationView.setItemIconTintList(null);


    }   // end onCreate

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
        try {
            if(updateApp == null)
                return;
            if(BuildConfig.VERSION_NAME.equals(updateApp.ver)){
                layoutUpdate.setVisibility(View.GONE);
                return;
            }
            String title = getString(R.string.new_version) + updateApp.ver + " - " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date (updateApp.time*1000));
            tvTileUpdate.setText(title);
            logUpdate.getSettings().setJavaScriptEnabled(true);
            logUpdate.setBackgroundColor(Color.TRANSPARENT);
            logUpdate.loadDataWithBaseURL("", updateApp.changelog, "text/html", "UTF-8", "");
            layoutUpdate.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            // do nothing
        }
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
                        Intent mainActicity = new Intent(Drawer.this, MainActivity.class);
                        startActivity(mainActicity);
                        finish();
                    }
                })
                .show();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (getLocale().equals("en")) {
                setLocale("vi");
            } else {
                setLocale("en");
            }
            return true;
        } else if (id == R.id.action_logout) {
            logOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Handle the camera action
        } else if (id == R.id.nav_news) {
            Intent thongBao = new Intent(this, ThongbaoActivity.class);
            startActivity(thongBao);
        } else if (id == R.id.nav_email) {
            Intent email = new Intent(this, EmailActivity.class);
            startActivity(email);
        } else if (id == R.id.nav_schedule) {
            Intent tkb = new Intent(this, TkbActivity.class);
            startActivity(tkb);
        } else if (id == R.id.nav_exam) {
            Intent lichthi = new Intent(this, LichThiActivity.class);
            startActivity(lichthi);
        } else if (id == R.id.nav_grades) {
            Intent diem = new Intent(this, DiemActivity.class);
            startActivity(diem);
        } else if (id == R.id.nav_extrac) {
            Intent hdpt = new Intent(this, HdptActivity.class);
            startActivity(hdpt);
        } else if (id == R.id.nav_fee) {
            Intent hocphi = new Intent(this, HocphiActivity.class);
            startActivity(hocphi);
        } else if (id == R.id.nav_sakai) {
            Intent sakai = new Intent(this, SakaiActivity.class);
            startActivity(sakai);
        } else if (id == R.id.nav_verify) {
            Intent cnsv = new Intent(this, CnsvActivity.class);
            startActivity(cnsv);
        } else if (id == R.id.nav_onlineapp) {
            Intent ndtt = new Intent(this, NdttActivity.class);
            startActivity(ndtt);
        } else if (id == R.id.nav_switchlang) {
            if (getLocale().equals("en")) {
                setLocale("vi");
            } else {
                setLocale("en");
            }
        } else if (id == R.id.nav_bugs) {
            Intent emailBug = new Intent(this, EmailNewActivity.class);
            emailBug.putExtra(EmailNewActivity.EXTRA_BUG, true);
            emailBug.putExtra(EmailNewActivity.EXTRA_TO, "51702071@student.tdt.edu.vn");
            emailBug.putExtra(EmailNewActivity.EXTRA_SUBJECT, "Báo lỗi");
            startActivity(emailBug);
        } else if (id == R.id.nav_about) {
            Intent about = new Intent(this, AboutActivity.class);
            startActivity(about);
        } else if (id == R.id.nav_logout) {
            logOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
