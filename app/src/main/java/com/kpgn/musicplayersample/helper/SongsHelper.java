package com.kpgn.musicplayersample.helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.kpgn.musicplayersample.entity.Song;

import java.util.ArrayList;
import java.util.List;

public abstract class SongsHelper {

    private SongsHelper() {
        // Do Nothing...
    }

    public static List<Song> getSongList(Context context) {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Uri selectionUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA
        };

        Cursor cursor = context.getContentResolver().query(selectionUri, projection, selection, null, sortOrder);

        List<Song> songs = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.setSongID(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                song.setSongArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                song.setSongTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                song.setSongDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                song.setSongPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                songs.add(song);
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return songs;
    }
}
