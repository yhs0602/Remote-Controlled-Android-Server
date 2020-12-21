package com.kyunggi.worker2;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.service.notification.*;
import android.util.*;
import android.widget.*;
import java.io.*;

/**
 * Created by mukesh on 19/5/15.
 */
public class NotificationService extends NotificationListenerService {

    Context context;

	private String TAG="BTOPP NOTISERVICE";

    @Override

    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();
		startForeground(1, new Notification());
		Toast.makeText(this,"NotiService starting",1).show();
    }
    @Override

    public void onNotificationPosted(StatusBarNotification sbn) {
		Log.v(TAG,"NOTI POSTED");
        String pack = sbn.getPackageName();
        String ticker ="";
        if(sbn.getNotification().tickerText !=null) {
            ticker = sbn.getNotification().tickerText.toString();
        }
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        String text = extras.getCharSequence("android.text").toString();
        int id1 = extras.getInt(Notification.EXTRA_SMALL_ICON);
        Bitmap id = sbn.getNotification().largeIcon;


        Log.i(TAG,"package"+pack);
        Log.i(TAG,"ticker"+ticker);
        Log.i(TAG,"Title"+title);
        Log.i(TAG,"Text"+text);

        Intent msgrcv = new Intent("com.kyunggi.worker2.noti");
        msgrcv.putExtra("package", pack);
        msgrcv.putExtra("ticker", ticker);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);
        if(id != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            id.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            msgrcv.putExtra("icon",byteArray);
        }
		sendBroadcast(msgrcv);
      //  LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);


    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"Notification Removed");
		String pack = sbn.getPackageName();
        String ticker ="";
        if(sbn.getNotification().tickerText !=null) {
            ticker = sbn.getNotification().tickerText.toString();
        }
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        String text = extras.getCharSequence("android.text").toString();
      //  int id1 = extras.getInt(Notification.EXTRA_SMALL_ICON);
     //   Bitmap id = sbn.getNotification().largeIcon;


        Log.i(TAG,"package"+pack);
        Log.i(TAG,"ticker"+ticker);
        Log.i(TAG,"Title"+title);
        Log.i(TAG,"Text"+text);
		Intent msgrcv=new Intent("com.kyunggi.worker2.notidel");
		msgrcv.putExtra("package", pack);
        msgrcv.putExtra("ticker", ticker);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);
		
    }
}
