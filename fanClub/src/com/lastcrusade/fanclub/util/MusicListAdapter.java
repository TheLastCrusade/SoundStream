package com.lastcrusade.fanclub.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.model.Song;

public class MusicListAdapter extends BaseAdapter {
    private Context mContext;
    private Song[] songs;
    
    public MusicListAdapter(Context mContext, Song[] songs){
        this.mContext = mContext;
        this.songs = songs;
    }
    
    
    @Override
    public int getCount() {
        return songs.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return songs[position];
    }

    @Override
    public long getItemId(int position) {
        return songs[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View element = convertView;
        
        if(element == null){
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            element = inflater.inflate(R.layout.song_item, null);
        }
        
        VerticalTextView username = (VerticalTextView) element.findViewById(R.id.username); 
        TextView title = (TextView)element.findViewById(R.id.title);
        TextView artist = (TextView) element.findViewById(R.id.artist);
        TextView album = (TextView) element.findViewById(R.id.album);
        
        username.setText(songs[position].getUsername());
        title.setText(songs[position].getTitle());
        artist.setText(songs[position].getArtist());
        album.setText(songs[position].getAlbum());
        
        return element;
    }

}
