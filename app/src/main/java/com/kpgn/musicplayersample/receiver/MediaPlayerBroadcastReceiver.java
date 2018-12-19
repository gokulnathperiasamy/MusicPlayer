package com.kpgn.musicplayersample.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.kpgn.musicplayersample.application.Constants;
import com.kpgn.musicplayersample.application.MusicPlayerApplication;

public class MediaPlayerBroadcastReceiver extends BroadcastReceiver {

    private IntentFilter intentFilter;

    public MediaPlayerBroadcastReceiver() {
        // Do Nothing...
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && MusicPlayerApplication.songsActivity != null) {
            String action = intent.getAction();
            if (Constants.NOTIFICATION_PREVIOUS.equals(action)) {
                MusicPlayerApplication.songsActivity.ctaPrevious(null);
            } else if (Constants.NOTIFICATION_STOP.equals(action)) {
                MusicPlayerApplication.songsActivity.ctaStop(null);
            } else if (Constants.NOTIFICATION_NEXT.equals(action)) {
                MusicPlayerApplication.songsActivity.ctaNext(null);
            }
        }
    }

    public IntentFilter getIntentFilter() {
        if (intentFilter == null) {
            createIntentFilter();
        }
        return intentFilter;
    }

    private void createIntentFilter() {
        if (intentFilter == null) {
            intentFilter = new IntentFilter();
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            intentFilter.addAction(Constants.NOTIFICATION_NEXT);
            intentFilter.addAction(Constants.NOTIFICATION_PREVIOUS);
            intentFilter.addAction(Constants.NOTIFICATION_STOP);
        }
    }
}