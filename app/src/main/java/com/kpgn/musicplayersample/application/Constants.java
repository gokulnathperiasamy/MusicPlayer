package com.kpgn.musicplayersample.application;

import com.kpgn.musicplayersample.BuildConfig;

public abstract class Constants {

    public static final String PLAY_ACTION = "PLAY_ACTION";
    public static final String PAUSE_ACTION = "PAUSE_ACTION";

    public static final String NOTIFICATION_CHANNEL_ID = "MUSIC_PLAYER_12345";
    public static final String NOTIFICATION_PLAYER = BuildConfig.APPLICATION_ID + ".NOTIFICATION_PLAYER";
    public static final String NOTIFICATION_PREVIOUS = BuildConfig.APPLICATION_ID + ".NOTIFICATION_PREVIOUS";
    public static final String NOTIFICATION_STOP = BuildConfig.APPLICATION_ID + ".NOTIFICATION_STOP";
    public static final String NOTIFICATION_NEXT = BuildConfig.APPLICATION_ID + ".NOTIFICATION_NEXT";

}
