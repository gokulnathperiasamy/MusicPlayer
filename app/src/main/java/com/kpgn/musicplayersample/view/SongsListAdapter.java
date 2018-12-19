package com.kpgn.musicplayersample.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kpgn.musicplayersample.R;
import com.kpgn.musicplayersample.entity.Song;

import java.util.ArrayList;
import java.util.List;

public class SongsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SongViewHolder.OnItemSelectedListener {

    private Context context;
    private List<Song> songList;
    SongViewHolder.OnItemSelectedListener listener;

    public SongsListAdapter(SongViewHolder.OnItemSelectedListener listener, Context context, List<Song> list) {
        this.listener = listener;
        this.context = context;
        this.songList = new ArrayList<>();

        for (Song song : list) {
            song.setSelected(false);
            songList.add(song);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SongViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_song, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Song song = songList.get(position);
        ((SongViewHolder) holder).bindView(listener, context, song, position);
        ((SongViewHolder) holder).setChecked(song.isSelected());
    }


    @Override
    public int getItemCount() {
        if (songList != null) {
            return songList.size();
        }
        return 0;
    }

    @Override
    public void onItemSelected(Song song, int songPosition) {
        for (Song s : songList) {
            if (!s.equals(song) && s.isSelected()) {
                s.setSelected(false);
            } else if (s.equals(song) && song.isSelected()) {
                s.setSelected(true);
            }
        }
        notifyDataSetChanged();
        listener.onItemSelected(song, songPosition);
    }
}