package me.app.coinwallet;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationHandler {
    private final NotificationManagerCompat managerCompat;

    public NotificationHandler(Context context){
        managerCompat = NotificationManagerCompat.from(context);
        NotificationChannelCompat syncChannel = new NotificationChannelCompat.Builder(
                Constants.NOTIFICATION_CHANNEL_ID_SERVICE, NotificationManager.IMPORTANCE_LOW)
                .setName(Constants.NOTIFICATION_CHANNEL_ID_SERVICE)
                .build();
        NotificationChannelCompat receiveChannel = new NotificationChannelCompat.Builder(
                Constants.NOTIFICATION_CHANNEL_ID_RECEIVE, NotificationManager.IMPORTANCE_HIGH)
                .setName(Constants.NOTIFICATION_CHANNEL_ID_RECEIVE)
                .build();
        managerCompat.createNotificationChannelsCompat(Arrays.asList(syncChannel, receiveChannel));
    }

    public void sendNotification(int id, Notification notification){
        managerCompat.notify(id, notification);
    }

    public static Notification buildNotification(Context ctx, String title, String body){
        NotificationCompat.Builder builder =  builder(ctx, title, body, Constants.NOTIFICATION_CHANNEL_ID_RECEIVE);
        return builder.build();
    }

    private static NotificationCompat.Builder builder(Context ctx, String title, String body, String channel){
        return new NotificationCompat.Builder(ctx, channel)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body));
    }

    public static Notification buildServiceNotification(Context ctx, String title, String body){
        return builder(ctx, title, body, Constants.NOTIFICATION_CHANNEL_ID_SERVICE)
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
    }
}
