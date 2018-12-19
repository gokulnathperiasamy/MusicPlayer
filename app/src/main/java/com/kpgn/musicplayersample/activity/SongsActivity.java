package com.kpgn.musicplayersample.activity;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kpgn.musicplayersample.R;
import com.kpgn.musicplayersample.application.Constants;
import com.kpgn.musicplayersample.application.MusicPlayerApplication;
import com.kpgn.musicplayersample.entity.Song;
import com.kpgn.musicplayersample.helper.LogHelper;
import com.kpgn.musicplayersample.helper.TextHelper;
import com.kpgn.musicplayersample.notification.NotificationPanel;
import com.kpgn.musicplayersample.receiver.MediaPlayerBroadcastReceiver;
import com.kpgn.musicplayersample.service.AppService;
import com.kpgn.musicplayersample.view.SongViewHolder;
import com.kpgn.musicplayersample.view.SongsListAdapter;
import com.kpgn.musicplayersample.viewmodel.MusicPlayerData;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SongsActivity extends AppCompatActivity implements SongViewHolder.OnItemSelectedListener {

    private static final int PERMISSIONS_REQUEST_READ_STORAGE = 12345;

    @BindView(R.id.empty_container)
    View mEmptyContainer;

    @BindView(R.id.song_container)
    View mSongContainer;

    @BindView(R.id.playback_container)
    View mPlaybackContainer;

    @BindView(R.id.tv_error_message)
    TextView mErrorMessage;

    @BindView(R.id.tv_grant_permission)
    TextView mGrantPermission;

    @BindView(R.id.rv_song_list)
    RecyclerView mSongList;

    @BindView(R.id.iv_play_pause)
    ImageView mPlayPause;

    @BindView(R.id.tv_track_name)
    TextView mTrackName;

    @BindView(R.id.tv_track_duration)
    TextView mTrackDuration;

    @BindView(R.id.sb_track)
    SeekBar mTrackSeekBar;

    @BindView(R.id.indicator_playing)
    AVLoadingIndicatorView mPlayingIndicator;

    private SongsListAdapter songsListAdapter;
    private MusicPlayerData musicPlayerData;

    private MediaPlayerBroadcastReceiver mediaPlayerBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        ButterKnife.bind(this);
        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNotification(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mediaPlayerBroadcastReceiver);
    }

    @Override
    public void finish() {
        super.finish();
        cancelNotification();
        unregisterReceiver(mediaPlayerBroadcastReceiver);
    }

    /*********************************** Request Permission ***************************************/

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_STORAGE);
        } else {
            initData();
            setupData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initData();
                setupData();
            } else {
                showView(true, true);
            }
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.tv_grant_permission)
    public void ctaGrantPermission(View view) {
        checkPermissions();
    }

    /******************************** Load Data and Setup View ************************************/

    private void initData() {
        musicPlayerData = ViewModelProviders.of(this).get(MusicPlayerData.class);
        MusicPlayerApplication.songsActivity = this;
        initServiceAndReceiver();
    }

    private void setupData() {
        if (musicPlayerData.getSongList() == null || musicPlayerData.getSongList().isEmpty()) {
            showView(true, false);
        } else {
            showSongList();
        }
    }

    private void showSongList() {
        showView(false, false);
        mTrackSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        setupRecyclerView();
        if (!musicPlayerData.isSongPlaying()) {
            mTrackName.setText("");
            mTrackDuration.setText("");
        } else {
            restoreData();
        }
    }

    private void restoreData() {
        Song song = musicPlayerData.getSongList().get(musicPlayerData.getSelectedSongPosition());
        mTrackName.setText(song.getSongTitle());
        mTrackDuration.setText(TextHelper.convertMilliSecondsToHHMMSS(song.getSongDuration()));
        mPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white));
        mPlayPause.setTag(Constants.PAUSE_ACTION);
        mPlayingIndicator.setVisibility(View.VISIBLE);
        if (musicPlayerData.getMediaPlayer() != null) {
            mTrackSeekBar.setMax(musicPlayerData.getMediaPlayer().getDuration());
        }
        mTrackDuration.post(mUpdateTime);
        selectedSongPosition(false, musicPlayerData.getSelectedSongPosition());
        onItemSelected(null, musicPlayerData.getSelectedSongPosition());
    }

    private void showView(boolean showEmptyContainer, boolean isPermissionDenied) {
        if (showEmptyContainer) {
            mSongList.setVisibility(View.GONE);
            mPlaybackContainer.setVisibility(View.GONE);
            mSongContainer.setVisibility(View.GONE);
            mGrantPermission.setVisibility(View.GONE);
            mEmptyContainer.setVisibility(View.VISIBLE);

        } else {
            mEmptyContainer.setVisibility(View.GONE);
            mGrantPermission.setVisibility(View.GONE);
            mSongList.setVisibility(View.VISIBLE);
            mPlaybackContainer.setVisibility(View.VISIBLE);
            mSongContainer.setVisibility(View.VISIBLE);
        }

        if (isPermissionDenied) {
            mErrorMessage.setText(getString(R.string.need_permission));
            mGrantPermission.setVisibility(View.VISIBLE);
        } else {
            mErrorMessage.setText(getString(R.string.empty_list));
            mGrantPermission.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        songsListAdapter = new SongsListAdapter(this, this, musicPlayerData.getSongList());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mSongList.setLayoutManager(mLayoutManager);
        mSongList.setItemAnimator(new DefaultItemAnimator());
        mSongList.setAdapter(songsListAdapter);
        songsListAdapter.notifyDataSetChanged();
    }

    /************************************** Playback Controls *************************************/

    @SuppressWarnings("unused")
    @OnClick(R.id.iv_previous)
    public void ctaPrevious(View view) {
        int currentPosition = musicPlayerData.getSelectedSongPosition();
        musicPlayerData.setSelectedSongPosition(--currentPosition);
        if (musicPlayerData.getSelectedSongPosition() < 0) {
            musicPlayerData.setSelectedSongPosition(musicPlayerData.getSongList().size() - 1);
        }
        musicPlayerData.setSeekPosition(0);
        playSong();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.iv_stop)
    public void ctaStop(View view) {
        musicPlayerData.setSeekPosition(0);
        musicPlayerData.setSongPlaying(false);
        mTrackSeekBar.setProgress(0);
        mPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_white));
        mPlayPause.setTag(Constants.PLAY_ACTION);
        resetMediaPlayer();
        resetSongSelection();
        songsListAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.iv_play_pause)
    public void ctaPlayPause(View view) {
        if (Constants.PLAY_ACTION.equals(mPlayPause.getTag())) {
            playSong(musicPlayerData.getSeekPosition());
        } else if (Constants.PAUSE_ACTION.equals(mPlayPause.getTag())) {
            musicPlayerData.setSeekPosition(musicPlayerData.getMediaPlayer().getCurrentPosition());
            musicPlayerData.setSongPlaying(false);
            resetMediaPlayer();
        } else {
            playSong();
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.iv_next)
    public void ctaNext(View view) {
        int currentPosition = musicPlayerData.getSelectedSongPosition();
        musicPlayerData.setSelectedSongPosition(++currentPosition);
        if (musicPlayerData.getSelectedSongPosition() >= musicPlayerData.getSongList().size()) {
            musicPlayerData.setSelectedSongPosition(0);
        }
        musicPlayerData.setSeekPosition(0);
        playSong();
    }

    /************************************ Notification Controls ***********************************/

    private void initServiceAndReceiver() {
        startService(new Intent(this, AppService.class));
        mediaPlayerBroadcastReceiver = new MediaPlayerBroadcastReceiver();
        registerReceiver(mediaPlayerBroadcastReceiver, mediaPlayerBroadcastReceiver.getIntentFilter());
    }

    private void cancelNotification() {
        if (musicPlayerData.getNotificationPanel() != null) {
            musicPlayerData.getNotificationPanel().notificationCancel();
        }
    }

    private void handleNotification(Intent intent) {
        if (intent != null && intent.hasExtra(Constants.NOTIFICATION_PLAYER)) {
            String action = intent.getStringExtra(Constants.NOTIFICATION_PLAYER);
            if (Constants.NOTIFICATION_PREVIOUS.equals(action)) {
                ctaPrevious(null);
            } else if (Constants.NOTIFICATION_STOP.equals(action)) {
                ctaStop(null);
            } else if (Constants.NOTIFICATION_NEXT.equals(action)) {
                ctaNext(null);
            }
        }
    }

    /************************************ MediaPlayer Controls ************************************/

    public void playSong() {
        playSong(0);
    }

    public void playSong(int seekPosition) {
        if (!musicPlayerData.getSongList().isEmpty() && musicPlayerData.getSelectedSongPosition() <= musicPlayerData.getSongList().size()) {
            Song song = musicPlayerData.getSongList().get(musicPlayerData.getSelectedSongPosition());
            String songPath = song.getSongPath();
            try {
                resetMediaPlayer();
                musicPlayerData.setMediaPlayer(new MediaPlayer());
                musicPlayerData.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_white));
                        mPlayPause.setTag(Constants.PLAY_ACTION);
                        ctaNext(null);
                    }
                });
                musicPlayerData.getMediaPlayer().setDataSource(songPath);
                musicPlayerData.getMediaPlayer().prepare();
                mTrackSeekBar.setMax(musicPlayerData.getMediaPlayer().getDuration());
                musicPlayerData.getMediaPlayer().start();
                mPlayingIndicator.setVisibility(View.VISIBLE);
                musicPlayerData.setNotificationPanel(new NotificationPanel(this, song.getSongTitle()));
                musicPlayerData.setSongPlaying(true);
                onItemSelected(null, musicPlayerData.getSelectedSongPosition());
                mTrackName.setText(song.getSongTitle());
                if (seekPosition > 0) {
                    musicPlayerData.getMediaPlayer().seekTo(seekPosition);
                }
                mTrackDuration.post(mUpdateTime);
                mPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white));
                mPlayPause.setTag(Constants.PAUSE_ACTION);
            } catch (Exception e) {
                LogHelper.log(e);
            }
        }
    }

    private void resetMediaPlayer() {
        if (musicPlayerData.getMediaPlayer() != null) {
            musicPlayerData.getMediaPlayer().stop();
            musicPlayerData.getMediaPlayer().release();
            musicPlayerData.setMediaPlayer(null);
        }
        if (Constants.PLAY_ACTION.equals(mPlayPause.getTag())) {
            mTrackName.setText("");
            mTrackDuration.setText("");
        }
        mPlayingIndicator.setVisibility(View.GONE);
        mPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_white));
        mPlayPause.setTag(Constants.PLAY_ACTION);
        songsListAdapter.notifyDataSetChanged();
    }

    private Runnable mUpdateTime = new Runnable() {
        public void run() {
            long currentDuration;
            long totalDuration;
            if (musicPlayerData.getMediaPlayer() != null && musicPlayerData.getMediaPlayer().isPlaying()) {
                currentDuration = musicPlayerData.getMediaPlayer().getCurrentPosition();
                totalDuration = musicPlayerData.getMediaPlayer().getDuration();
                mTrackSeekBar.setProgress((int) currentDuration);
                mTrackDuration.setText(TextHelper.convertMilliSecondsToHHMMSS(totalDuration - currentDuration));
                mTrackDuration.postDelayed(this, 1000);
            } else {
                mTrackDuration.removeCallbacks(this);
            }
        }
    };

    /******************************* RecyclerView Song Highlight **********************************/

    public void selectedSongPosition(boolean songSelected, int position) {
        musicPlayerData.setSelectedSongPosition(position);
        if (!songSelected && musicPlayerData.getMediaPlayer() != null && musicPlayerData.getMediaPlayer().getCurrentPosition() > 0) {
            playSong(musicPlayerData.getMediaPlayer().getCurrentPosition());
        } else {
            playSong();
        }
    }

    @Override
    public void onItemSelected(Song song, int songPosition) {
        if (song != null) {
            List<Song> selectedSongList = new ArrayList<>();
            for (Song s : musicPlayerData.getSongList()) {
                if (s.equals(song)) {
                    s.setSelected(true);
                } else {
                    s.setSelected(false);
                }
                selectedSongList.add(s);
            }
            musicPlayerData.setSongList(selectedSongList);
        } else {
            resetSongSelection();
            musicPlayerData.getSongList().get(songPosition).setSelected(true);
        }
        songsListAdapter.notifyDataSetChanged();
        mSongList.smoothScrollToPosition(musicPlayerData.getSelectedSongPosition());
    }

    private void resetSongSelection() {
        if (musicPlayerData.getSongList() != null) {
            for (Song s : musicPlayerData.getSongList()) {
                s.setSelected(false);
            }
        }
    }
}