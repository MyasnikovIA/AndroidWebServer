package ru.sgk.webserver.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Base64;
import android.widget.Toast;

import java.util.HashMap;

import ru.sgk.webserver.AndroidLib.WebServerAndroid;
import ru.sgk.webserver.Lib.Sys;

public class ServiceExample extends Service {


    public static int INTERVAL = 10000; // 10 sec
    public static final int FIRST_RUN = 5000; // 5 seconds
    int REQUEST_CODE = 11223344;
    HashMap<String, String> Setup = new HashMap<String, String>();
    private HttpSrv http;
    private AlarmManager alarmManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Setup = Sys.readFile(this, "conf.ini");

        if (Setup.size() > 0) {
            String Author = new String(Base64.decode((Setup.get("Author")).getBytes(), Base64.DEFAULT));
            Setup.put("UserName", Author.split(":")[0]);
            Setup.put("UserPass", Author.split(":")[1]);
            INTERVAL = Integer.valueOf(Setup.get("Interval"));
        } else {
            Setup.put("UserName", "user");
            Setup.put("UserPass", "123");
            Setup.put("UserPort", "9090");
            Setup.put("Interval", "10000");
            Setup.put("DefaultHost","");
            INTERVAL = 10000;
            Setup.put("run", "1");
        }
        http = new HttpSrv(this);
        http.Start(Setup);

        startServiceAlyarm();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (alarmManager != null) {
            Intent intent = new Intent(this, RepeatingAlarmService.class);
            alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0));
        }
        http.Stop();
        Toast.makeText(this, "Service http "+ WebServerAndroid.ApplicationName+" stoped.", Toast.LENGTH_LONG).show();
    }

    private void startServiceAlyarm() {
        Intent intent = new Intent(this, RepeatingAlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + FIRST_RUN,
                INTERVAL,
                pendingIntent);
        Toast.makeText(this, "Service http "+ WebServerAndroid.ApplicationName+" started.", Toast.LENGTH_LONG).show();
        // Toast.makeText(this, "Service http "+ WebServerAndroid.ApplicationName+" started." + Setup.toString(), Toast.LENGTH_LONG).show();
        // Log.v(this.getClass().getName(), "AlarmManger started at " + new java.sql.Timestamp(System.currentTimeMillis()).toString());
    }


}
