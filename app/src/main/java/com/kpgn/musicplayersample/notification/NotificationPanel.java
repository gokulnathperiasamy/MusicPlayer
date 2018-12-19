package com.kpgn.musicplayersample.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.kpgn.musicplayersample.R;
import com.kpgn.musicplayersample.activity.SongsActivity;
import com.kpgn.musicplayersample.application.Constants;

public class NotificationPanel {

    private static final int NOTIFICATION_ID = 98765;

    private Context context;
    private String title;
    private NotificationManager notificationManager;

    public NotificationPanel(Context context, String title) {
        this.context = context.getApplicationContext();
        this.title = title;

        createView();
    }

    private void createView() {
        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(false)
                .setOngoing(true)
                .setVibrate(null);


        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.view_notification);
        setContent(remoteView);
        //setListeners(remoteView); --> This will open application on click!
        setBroadcastListeners(remoteView);
        notificationBuilder.setContent(remoteView);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID);
            CharSequence name = context.getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void setContent(RemoteViews view) {
        view.setTextViewText(R.id.tv_track_name, title);
    }

    private void setListeners(RemoteViews view) {
        setPendingIntentListener(view, Constants.NOTIFICATION_PREVIOUS, 0, R.id.iv_previous);
        setPendingIntentListener(view, Constants.NOTIFICATION_STOP, 1, R.id.iv_stop);
        setPendingIntentListener(view, Constants.NOTIFICATION_NEXT, 2, R.id.iv_next);
    }

    private void setPendingIntentListener(RemoteViews view, String notificationType, int requestCode, int controlID) {
        Intent intent = new Intent(context, SongsActivity.class);
        intent.putExtra(Constants.NOTIFICATION_PLAYER, notificationType);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(controlID, pendingIntent);
    }

    private void setBroadcastListeners(RemoteViews view) {
        setBroadcastIntentListeners(view, Constants.NOTIFICATION_PREVIOUS, 0, R.id.iv_previous);
        setBroadcastIntentListeners(view, Constants.NOTIFICATION_STOP, 1, R.id.iv_stop);
        setBroadcastIntentListeners(view, Constants.NOTIFICATION_NEXT, 2, R.id.iv_next);
    }

    private void setBroadcastIntentListeners(RemoteViews view, String actionType, int requestCode, int controlID) {
        Intent intent = new Intent(actionType);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        view.setOnClickPendingIntent(controlID, pendingIntent);
    }

    public void notificationCancel() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}    