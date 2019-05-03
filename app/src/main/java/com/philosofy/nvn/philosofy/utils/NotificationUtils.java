package com.philosofy.nvn.philosofy.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Action;
import androidx.core.content.ContextCompat;

import com.philosofy.nvn.philosofy.EditorActivity;
import com.philosofy.nvn.philosofy.QuotesActivity;
import com.philosofy.nvn.philosofy.R;
import com.philosofy.nvn.philosofy.service.ShareQodIntentService;

public class NotificationUtils {

    private static final int QOD_NOTIFICATION_ID = 1212;
    private static final String QOD_NOTIFICATION_CHANNEL_ID = "qod_channel";

    private static final int EDIT_QOD_PENDING_INTENT_ID = 321;
    private static final int SHARE_QOD_PENDING_INTENT_ID = 322;
    private static final int QOD_ACTIVITY_PENDING_INTENT_ID = 333;

    public static void showQodNotification(Context context, String quote, String author) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    QOD_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.qod_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription(context.getString(R.string.notification_channel_description));
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, QOD_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setContentTitle(author)
                .setContentText(quote)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(quote))
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setContentIntent(qodContentIntent(context))
                .addAction(quoteAction(context, quote))
                .addAction(shareAction(context, quote));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(QOD_NOTIFICATION_ID, builder.build());

        //PreferencesUtils.saveLastNotificationTime(context, System.currentTimeMillis());
    }

    private static Action quoteAction(Context context, String quote) {
        Intent editorIntent = new Intent(context, EditorActivity.class);
        editorIntent.putExtra(Intent.EXTRA_TEXT, quote);

        PendingIntent editorPendingIntent = PendingIntent.getActivity(
                context,
                EDIT_QOD_PENDING_INTENT_ID,
                editorIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Action editQodAction = new Action(R.drawable.ic_pen, "EDIT", editorPendingIntent);

        return editQodAction;
    }

    private static Action shareAction(Context context, String quote) {
        Intent shareIntent = new Intent(context, ShareQodIntentService.class);
        shareIntent.putExtra(Intent.EXTRA_TEXT, quote);

        PendingIntent sharePendingIntent = PendingIntent.getService(
                context,
                SHARE_QOD_PENDING_INTENT_ID,
                shareIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Action shareQodAction = new Action(R.drawable.ic_share, "SHARE", sharePendingIntent);

        return shareQodAction;
    }

    private static PendingIntent qodContentIntent(Context context) {
        Intent qodIntent = new Intent(context, QuotesActivity.class);
        PendingIntent qodPendingIntent = PendingIntent.getActivity(
                context,
                QOD_ACTIVITY_PENDING_INTENT_ID,
                qodIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return qodPendingIntent;
    }

    public static void clearQodNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(QOD_NOTIFICATION_ID);
    }
}
