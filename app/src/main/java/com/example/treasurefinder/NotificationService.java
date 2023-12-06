package com.example.treasurefinder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class NotificationService extends Service {

    //String used to hold the channel ID for the notifications
    private static final String CHANNEL_ID = "Foreground Location Channel";

    //Integer for the notificaton ID
    public static final int NOTIFICATION_ID = 222;

    //Debug tag
    public static final String TAG = "NotifServiceTag";

    SharedPreferences sharedPref;

    String id = "";

    //Socket used for communicating with the server
    private Socket mSocket;
    {
        try {
            //Creates an IO socket using server route
            mSocket = IO.socket("https://treasurefinderbackend.onrender.com");
        } catch (URISyntaxException e) {}
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Create a shared preference to pass userID between views
        sharedPref = this.getSharedPreferences("user", Context.MODE_PRIVATE);

        if(sharedPref.contains("id")) {

            String s = sharedPref.getString("id", "0");

            if(s.equals("0") == false) {
                id = s;
            }
        }

        // Create a notification for running in the foreground
        Notification foregroundNotification = createForegroundNotification();

        // Start the service in the foreground and show the notification
        startForeground(NOTIFICATION_ID, foregroundNotification);

        //Creates a "Listener" for the socket
        //(When the client socket recieves the message "newGarageSale" from server it will run function fn)
        mSocket.on("newGarageSale", fn ->{

            //Debug
            Log.d(TAG, "New Garage Sale from server");

            //Create a notification
            Notification notification = new NotificationCompat.Builder(NotificationService.this,CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.star_big_on)
                    .setContentTitle("New garage sale near you!")
                    .setContentText("Tap to open App")
                    .setContentIntent(setOnTapAction())
                    .setDeleteIntent(setOnDismissAction())
                    .build();

            //Instantiate an instance of notification manager
            NotificationManager manager = getSystemService(NotificationManager.class);

            //Send the notification to the phone
            manager.notify(NOTIFICATION_ID,notification);
        });

        //Creates a "Listener" for the socket
        //(When the client socket recieves the message "newItemRequest" from server it will run function fn)
        mSocket.on("newItemRequest_" + id, fn ->{

            //Debug
            Log.d(TAG, "New Item Request from server");

            //Create a notification
            Notification notification = new NotificationCompat.Builder(NotificationService.this,CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.star_big_on)
                    .setContentTitle("A User Has Requested Your Item!")
                    .setContentText("Tap to open App")
                    .setContentIntent(setOnTapAction())
                    .setDeleteIntent(setOnDismissAction())
                    .build();

            //Instantiate an instance of notification manager
            NotificationManager manager = getSystemService(NotificationManager.class);

            //Send the notification to the phone
            manager.notify(NOTIFICATION_ID,notification);
        });

        //Creates a "Listener" for the socket
        //(When the client socket recieves the message "deleteRequest" from server it will run function fn)
        mSocket.on("deleteRequest_"+id, fn ->{

            //Debug
            Log.d(TAG, "New Requested Item Sold from server");

            //Create a notification
            Notification notification = new NotificationCompat.Builder(NotificationService.this,CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.star_big_on)
                    .setContentTitle("An Item You Requested Has Been Sold!")
                    .setContentText("Tap to open App")
                    .setContentIntent(setOnTapAction())
                    .setDeleteIntent(setOnDismissAction())
                    .build();

            //Instantiate an instance of notification manager
            NotificationManager manager = getSystemService(NotificationManager.class);

            //Send the notification to the phone
            manager.notify(NOTIFICATION_ID,notification);
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        //When onDestroy is triggered, shut down the socket and turn off the listener
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("newGarageSale");
    }

    public PendingIntent setOnTapAction(){
        //Pending intent setOnTapAction used to give the notification the ability to open to the main activity to log in when clicked

        //Create intent for the login page (MainActivity)
        Intent i = new Intent(this, MainActivity.class);

        //Create pending intent for notification to use
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                NOTIFICATION_ID,
                i,
                PendingIntent.FLAG_IMMUTABLE
        );

        //Return the pending intent
        return pendingIntent;
    }

    public PendingIntent setOnDismissAction(){
        //Pending intent setOnDismissAction used to delete the pending intent

        //Create intent with action to delete pending intent
        Intent i = new Intent("dismiss_broadcast");

        //Create pending intent for notification to use
        //(This pending intent just stops pending intents)
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                NOTIFICATION_ID,
                i,
                PendingIntent.FLAG_IMMUTABLE
        );

        //Return the pending intent
        return pendingIntent;
    }


    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Calls method to create a notification channel
        createNotificationChannel();

        //Creates a new dismiss receiver
        DismissReceiver receiver = new DismissReceiver();

        //creates new intent filter for dismissing a notification
        IntentFilter filter = new IntentFilter("dismiss_broadcast");

        //register dismissReceiver so pending intent can be closed
        registerReceiver(receiver,filter);

        //Attempts to connect client socket to server socket
        mSocket.connect();
    }

    public void createNotificationChannel() {

        //Creates a new notification channel called channel to send notification through
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Notify App Main Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        //Creates a new instance of notificationManager
        NotificationManager manager = getSystemService(NotificationManager.class);

        //Create notificationChannel using channel
        manager.createNotificationChannel(channel);

        //Debug
        Log.d(TAG, "Main Channel created");
    }

    class DismissReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case "dismiss_broadcast":
                    Toast.makeText(context,"Notification was dismissed",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private Notification createForegroundNotification() {
        // Create a notification channel if necessary
        createNotificationChannel();

        // Create the notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service Running")
                .setContentText("Listening for garage sale updates...")
                .setSmallIcon(android.R.drawable.star_big_on)
                .build();

        //Return the notification
        return notification;
    }
}