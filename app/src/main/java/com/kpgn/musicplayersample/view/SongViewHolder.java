package com.kpgn.musicplayersample.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kpgn.musicplayersample.R;
import com.kpgn.musicplayersample.activity.SongsActivity;
import com.kpgn.musicplayersample.entity.Song;
import com.kpgn.musicplayersample.helper.TextHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SongViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.song_container)
    View mSongContainer;

    @BindView(R.id.tv_song_detail)
    TextView mSongDetail;

    @BindView(R.id.tv_song_duration)
    TextView mSongDuration;

    private OnItemSelectedListener itemSelectedListener;
    private Context context;
    private Song song;
    private int songPosition;

    public SongViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindView(OnItemSelectedListener listener, Context context, Song song, int songPosition) {
        this.itemSelectedListener = listener;
        this.context = context;
        this.song = song;
        this.songPosition = songPosition;

        mSongDetail.setText(song.getSongTitle());
        mSongDuration.setText(TextHelper.convertMilliSecondsToHHMMSS(song.getSongDuration()));
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.song_container)
    public void ctaSongSelection(View view) {
        ((SongsActivity) context).selectedSongPosition(true, songPosition);
        setSelection();
    }

    private void setSelection() {
        if (song.isSelected()) {
            setChecked(false);
        } else {
            setChecked(true);
        }
        itemSelectedListener.onItemSelected(song, songPosition);
    }

    public void setChecked(boolean isChecked) {
        if (isChecked) {
            mSongContainer.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
        } else {
            mSongContainer.setBackgroundColor(context.getResources().getColor(R.color.colorDeactivated));
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(Song song, int songPosition);
    }
}
