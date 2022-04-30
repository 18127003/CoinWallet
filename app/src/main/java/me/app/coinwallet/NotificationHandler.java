package me.app.coinwallet;

import android.app.Notification;
import android.content.Context;
import android.media.RingtoneManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHandler {
    public static void sendNotification(Context ctx, String title, String body){
        Notification notification =  new NotificationCompat.Builder(ctx, Constants.NOTIFICATION_CHANNEL_ID).setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(Notification.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .build();

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(ctx);
        managerCompat.notify(1, notification);
    }
}
