package com.kpgn.musicplayersample.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;

import com.kpgn.musicplayersample.entity.Song;
import com.kpgn.musicplayersample.helper.SongsHelper;
import com.kpgn.musicplayersample.notification.NotificationPanel;

import java.util.List;

public class MusicPlayerData extends AndroidViewModel {

    private List<Song> songList;
    private MediaPlayer mediaPlayer;
    private int selectedSongPosition;
    private int seekPosition;
    private NotificationPanel notificationPanel;
    private boolean isSongPlaying;

    public MusicPlayerData(@NonNull Application application) {
        super(application);
        songList = SongsHelper.getSongList(application);
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public int getSelectedSongPosition() {
        return selectedSongPosition;
    }

    public void setSelectedSongPosition(int selectedSongPosition) {
        this.selectedSongPosition = selectedSongPosition;
    }

    public int getSeekPosition() {
        return seekPosition;
    }

    public void setSeekPosition(int seekPosition) {
        this.seekPosition = seekPosition;
    }

    public NotificationPanel getNotificationPanel() {
        return notificationPanel;
    }

    public void setNotificationPanel(NotificationPanel notificationPanel) {
        this.notificationPanel = notificationPanel;
    }

    public boolean isSongPlaying() {
        return isSongPlaying;
    }

    public void setSongPlaying(boolean songPlaying) {
        isSongPlaying = songPlaying;
    }
}
