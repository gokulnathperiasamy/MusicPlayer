package com.kpgn.musicplayersample.helper;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public abstract class TextHelper {

    private TextHelper() {
        // Do Nothing...
    }

    public static String convertMilliSecondsToHHMMSS(long ms) {
        try {
            long hours = TimeUnit.MILLISECONDS.toHours(ms);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms));
            long seconds = TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms));

            if (hours != 0) {
                return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format(Locale.US, "%02d:%02d", minutes, seconds);
            }
        } catch (Exception e) {
            return "\u2014";
        }
    }

}
