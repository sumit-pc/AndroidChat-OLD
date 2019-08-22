package com.example.aymen.androidchat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import static com.example.aymen.androidchat.ChatBoxActivity.decrypt;

public class AppBackgroundService extends Service {
    public static boolean isRunning;
    private Thread backgroundThread;
   // private Socket mSocket;
    private String Nickname;
    private String SECRET_KEY;



    private final IBinder mBinder = new MyLocalBinder();
    //ChatBoxActivity activity;

    NotificationManager mNotifyManager;

    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    private int count = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyLocalBinder extends Binder {
        AppBackgroundService getService() {
            return AppBackgroundService.this;
        }
    }

    @Override
    public void onCreate() {
        this.isRunning = false;
        try {
            // instantiate socket connection
            //mSocket = IO.socket("http://203.193.171.133:56455");
            ClientSocket.socketIO= IO.socket("http://203.193.171.133:56455");
            // establish the socket connection
            //mSocket.connect();
            ClientSocket.socketIO.connect();


        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        this.backgroundThread = new Thread(myTask);

    }

    private Runnable myTask = new Runnable() {
        @Override
        public void run() {
            // do something in here

            Log.i("INFO", "SOCKET BACKGROUND SERVICE IS RUNNING");
            ClientSocket.socketIO.on("message", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        //extract data from fired event
                        String nickname = data.getString("senderNickname");
                        String message = data.getString("message");

                        try{
                            message = decrypt(message);
                            Message m = new Message(nickname,message);

                            Log.i("INFO", message);

                            if(!nickname.equals(ChatBoxActivity.Nickname))
                            {
                                count++;
                                sendNotification(nickname, message);
                            }
                        }
                        catch (Exception e)
                        {}

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });


            //we're not stopping self as we want this service to be continuous
            //stopSelf();
        }
    };

    public void createNotificationChannel() {

        // Define notification manager object.
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Job Service notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifications from Job Service");

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    public void sendNotification(String nickname, String message)
    {
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (this, 0, new Intent(this, ChatBoxActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (this, PRIMARY_CHANNEL_ID)
                .setContentTitle(nickname)
                .setContentText(message)
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_android)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});;

        mNotifyManager.notify(count, builder.build());
    }


    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //activity = new ChatBoxActivity();
        createNotificationChannel();
        Nickname=intent.getExtras().getString(MainActivity.NICKNAME);
        SECRET_KEY = intent.getExtras().getString(MainActivity.SECRET_KEY);
        Log.i("Secret_key_debug",SECRET_KEY);
        if( !this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
            //Toast.makeText(getApplicationContext(),Nickname,Toast.LENGTH_LONG).show();
            ClientSocket.socketIO.emit("join", Nickname);
            //if(ClientSocket.socketIO.connected()){

        }
        if(!ClientSocket.socketIO.connected())
        {
            try {
                ClientSocket.socketIO= IO.socket("http://203.193.171.133:56455");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            ClientSocket.socketIO.connect();
        }
        Intent intent2 = new Intent(this,ChatBoxActivity.class);
        intent2.putExtra(MainActivity.NICKNAME,Nickname);
        intent2.putExtra(MainActivity.SECRET_KEY,SECRET_KEY);
        Log.i("Secret_key_debug",SECRET_KEY);
        startActivity(intent2);
        //}
        return START_STICKY;


    }

    public Socket getSocket() {
        return ClientSocket.socketIO;
    }


}
