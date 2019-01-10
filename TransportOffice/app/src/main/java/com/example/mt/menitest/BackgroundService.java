package com.example.mt.menitest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by mladen.todorovic on 1/4/2018.
 */

public class BackgroundService extends Service implements LoadJSONTask.Listener {

    private Timer mTimer;
    private List<Task> mTaskMapList = new ArrayList<>();
    private NotificationCompat.Builder notification;
    private static int started;

    public static int getStarted() {
        return started;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mTimer = new Timer();
        mTimer.schedule(timerTask, 5000, 10 * 1000);
        started = 1;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        mTimer.cancel();
        timerTask.cancel();
        started = 0;
        super.onDestroy();

    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
           NoviTaskovi();
        }
    };

    public void NoviTaskovi()
    {
        String token = getApplicationContext().getSharedPreferences("GMTEL", Context.MODE_PRIVATE).getString("Token", "");
        new LoadJSONTask(this).execute(getResources().getString(R.string.ProdukcijaSajt) + "DnevnikPrevoza/NoviTaskovi?token="+ token);
    }


    @Override
    public void onLoaded(JSONArray arr) {

        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
        mTaskMapList.clear();

        for (int i =0; i<arr.length(); i++)
        {
            try {
                int     IdTask          = arr.getJSONObject(i).getInt("IdTask");
                String SerijskiBroj     = arr.getJSONObject(i).getString("SerijskiBroj");
                String Vozilo           = arr.getJSONObject(i).getString("Vozilo");
                String Istovar          = arr.getJSONObject(i).getString("Istovar");
                String Roba             = arr.getJSONObject(i).getString("Roba");
                String Status           = arr.getJSONObject(i).getString("Status");
                String DatumAzuriranja  = arr.getJSONObject(i).getString("DatumAzuriranja");
                String Utovar           = arr.getJSONObject(i).getString("Utovar");
                String UvoznaSpedicija = arr.getJSONObject(i).getString("UvoznaSpedicija");
                String IzvoznaSpedicija = arr.getJSONObject(i).getString("IzvoznaSpedicija");
                String Uvoznik = arr.getJSONObject(i).getString("Uvoznik");
                String Izvoznik = arr.getJSONObject(i).getString("Izvoznik");
                String Napomena = arr.getJSONObject(i).getString("Napomena");
                String RefBroj = arr.getJSONObject(i).getString("RefBroj");
                String Pregledano = arr.getJSONObject(i).getString("Pregledano");

                mTaskMapList.add(new Task(IdTask, SerijskiBroj, Vozilo, Istovar, Roba, Status, DatumAzuriranja, Utovar, UvoznaSpedicija, IzvoznaSpedicija, Uvoznik, Izvoznik, Napomena, RefBroj, Pregledano));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        for (int i=0; i< mTaskMapList.size(); i++ )
        {
            boolean postiji = false;

            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            StatusBarNotification[] notifications = nm.getActiveNotifications();
            for (StatusBarNotification notification : notifications) {
                if (notification.getId() == mTaskMapList.get(i).getIdTask()) {
                    postiji = true;
                }
            }

            if(!postiji) {

                Intent intent = new Intent(this, TaskDetalji.class);   //TaskPrevozi.class
                intent.putExtra("task", mTaskMapList.get(i));
                PendingIntent pendingIntet = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                notification.setSmallIcon(R.drawable.ic_launcher);
                notification.setTicker("This is a ticker");
                notification.setWhen(System.currentTimeMillis());
                notification.setContentTitle("Prevoz " + mTaskMapList.get(i).getSerijskiBroj());
                notification.setContentText("Utovar " + mTaskMapList.get(i).getUtovar());
                notification.setDefaults(Notification.DEFAULT_SOUND);
                notification.setContentIntent(pendingIntet);

                nm.notify(mTaskMapList.get(i).getIdTask(), notification.build());
            }

        }
    }



    @Override
    public void onError() {

    }
}
