package com.example.treasurefinder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;

public class NotificationService extends Service {

    //String used to hold the channel ID for the notifications
    private static final String CHANNEL_ID = "Foreground Location Channel";

    //Integer for the new garage sale notification ID
    public static final int GARAGE_NOTIFICATION_ID = 1;

    //Integer for the new request notification ID
    public static final int REQUEST_NOTIFICATION_ID = 2;

    //Integer for the item sale notification ID
    public static final int SOLD_NOTIFICATION_ID = 3;

    //Debug tag
    public static final String TAG = "NotifServiceTag";

    //Shared preference editor
    SharedPreferences sharedPref;

    //clients id
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

        //if shared preference contains id tag
        if(sharedPref.contains("id")) {

            //Set id to id contained in shared preference
            id = sharedPref.getString("id", "0");
        }

        // Create a notification for running in the foreground
        Notification foregroundNotification = createForegroundNotification();

        // Start the service in the foreground and show the notification
        startForeground(GARAGE_NOTIFICATION_ID, foregroundNotification);

        //Creates a "Listener" for the socket
        //(When the client socket recieves the message "newGarageSale" from server it will run function fn)
        mSocket.on("newGarageSale", fn ->{

            //Debug
            Log.d(TAG, "New Garage Sale from server");

            //Create a notification
            Notification notification = new NotificationCompat.Builder(NotificationService.this,CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.star_big_on)
                    .setContentTitle("A User Has Posted A New garage Sale!")
                    .setContentText("Tap to open App")
                    .setContentIntent(setOnGarageTapAction())
                    .setDeleteIntent(setOnGarageDismissAction())
                    .build();

            //Instantiate an instance of notification manager
            NotificationManager manager = getSystemService(NotificationManager.class);

            //Send the notification to the phone
            manager.notify(GARAGE_NOTIFICATION_ID,notification);
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
                    .setContentIntent(setOnRequestTapAction())
                    .setDeleteIntent(setOnRequestDismissAction())
                    .build();

            //Instantiate an instance of notification manager
            NotificationManager manager = getSystemService(NotificationManager.class);

            //Send the notification to the phone
            manager.notify(REQUEST_NOTIFICATION_ID,notification);
        });

        //Creates a "Listener" for the socket
        //(When the client socket recieves the message "deleteRequest" from server it will run function fn)
        Log.d(TAG, id);
        mSocket.on("deleteRequest_"+id, fn ->{

            //Debug
            Log.d(TAG, "New Requested Item Sold from server");

            //Create a notification
            Notification notification = new NotificationCompat.Builder(NotificationService.this,CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.star_big_on)
                    .setContentTitle("An Item You Requested Has Been Sold!")
                    .setContentText("Tap to open App")
                    .setContentIntent(setOnSoldTapAction())
                    .setDeleteIntent(setOnSoldDismissAction())
                    .build();

            //Instantiate an instance of notification manager
            NotificationManager manager = getSystemService(NotificationManager.class);

            //Send the notification to the phone
            manager.notify(SOLD_NOTIFICATION_ID,notification);
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        //When onDestroy is triggered, shut down the socket and turn off the listener
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("newGarageSale");
        mSocket.off("newItemRequest_"+id);
        mSocket.off("deleteRequest_"+id);
    }

    public PendingIntent setOnGarageTapAction(){
        //Pending intent setOnGarageTapAction used to give the notification the ability to open to the main activity to log in when clicked

        //Create intent for the login page (MainActivity)
        Intent i = new Intent(this, MainActivity.class);

        //Create pending intent for notification to use
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                GARAGE_NOTIFICATION_ID,
                i,
                PendingIntent.FLAG_IMMUTABLE
        );

        //Return the pending intent
        return pendingIntent;
    }

    public PendingIntent setOnGarageDismissAction(){
        //Pending intent setOnGarageDismissAction used to delete the pending intent

        //Create intent with action to delete pending intent
        Intent i = new Intent("dismiss_broadcast");

        //Create pending intent for notification to use
        //(This pending intent just stops pending intents)
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                GARAGE_NOTIFICATION_ID,
                i,
                PendingIntent.FLAG_IMMUTABLE
        );

        //Return the pending intent
        return pendingIntent;
    }

    public PendingIntent setOnRequestTapAction(){
        //Pending intent setOnRequestTapAction used to give the notification the ability to open to the main activity to log in when clicked

        //Create intent for the login page (MainActivity)
        Intent i = new Intent(this, MainActivity.class);

        //Create pending intent for notification to use
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                REQUEST_NOTIFICATION_ID,
                i,
                PendingIntent.FLAG_IMMUTABLE
        );

        //Return the pending intent
        return pendingIntent;
    }

    public PendingIntent setOnRequestDismissAction(){
        //Pending intent setOnRequestDismissAction used to delete the pending intent

        //Create intent with action to delete pending intent
        Intent i = new Intent("dismiss_broadcast");

        //Create pending intent for notification to use
        //(This pending intent just stops pending intents)
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                REQUEST_NOTIFICATION_ID,
                i,
                PendingIntent.FLAG_IMMUTABLE
        );

        //Return the pending intent
        return pendingIntent;
    }

    public PendingIntent setOnSoldTapAction(){
        //Pending intent setOnSoldTapAction used to give the notification the ability to open to the main activity to log in when clicked

        //Create intent for the login page (MainActivity)
        Intent i = new Intent(this, MainActivity.class);

        //Create pending intent for notification to use
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                SOLD_NOTIFICATION_ID,
                i,
                PendingIntent.FLAG_IMMUTABLE
        );

        //Return the pending intent
        return pendingIntent;
    }

    public PendingIntent setOnSoldDismissAction(){
        //Pending intent setOnSoldDismissAction used to delete the pending intent

        //Create intent with action to delete pending intent
        Intent i = new Intent("dismiss_broadcast");

        //Create pending intent for notification to use
        //(This pending intent just stops pending intents)
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                SOLD_NOTIFICATION_ID,
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


    private Notification createForegroundNotification() {
        // Create a notification channel
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