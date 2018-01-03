package com.simonvn.tdtu.student.service.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.simonvn.tdtu.student.models.User;
import com.simonvn.tdtu.student.models.email.EmailPageSave;
import com.simonvn.tdtu.student.models.thongbao.DonviItem;
import com.simonvn.tdtu.student.service.CheckEmailService;
import com.simonvn.tdtu.student.service.CheckNewsService;
import com.simonvn.tdtu.student.service.ServiceUtils;

import io.realm.Realm;

/**
 * Created by bichan on 9/8/17.
 */

public class RestartAlarmsReceiver extends BroadcastReceiver {
    private Realm realm;
    private User user;

    @Override
    public void onReceive(Context context, Intent intent) {
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            if(user != null){

                // check config
                if(user.getEmailServiceConfig().isOpen()
                        && realm.where(EmailPageSave.class).findFirst() != null){
                    ServiceUtils.startService(context
                            , CheckEmailService.class
                            , user.getEmailServiceConfig().getTimeReplay());
                }

                if(user.getTbServiceConfig().isOpen()
                        && realm.where(DonviItem.class).count() > 0){
                    ServiceUtils.startService(context
                            , CheckNewsService.class
                            , user.getTbServiceConfig().getTimeReplay());
                }

            }
        }

        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null == activeNetwork) {
                return;
            }

            Log.d("ahihi", "network connected");

            if(user != null){
                // disable check when network change again
                realm.beginTransaction();
                user.setCheckNetworkState(false);
                realm.copyToRealmOrUpdate(user);
                realm.commitTransaction();

                if(user.getEmailServiceConfig().isOpen()
                        && realm.where(EmailPageSave.class).findFirst() != null){
                    ServiceUtils.startService(context
                            , CheckEmailService.class);
                }

                if(user.getTbServiceConfig().isOpen()
                        && realm.where(DonviItem.class).count() > 0){
                    ServiceUtils.startService(context
                            , CheckNewsService.class);
                }
            }
        }

        realm.close();
    }
}
