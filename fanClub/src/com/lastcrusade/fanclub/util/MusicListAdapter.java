package com.lastcrusade.fanclub.util;

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.model.Song;
import com.lastcrusade.fanclub.model.UserList;

public class MusicListAdapter extends BaseAdapter {
    private Context mContext;
    private Song[] songs;
    private Hashtable<String,String> users;
    
    public MusicListAdapter(Context mContext, Song[] songs){
        this.mContext = mContext;
        this.songs = songs;
        users = UserList.getUsers();
    }
    
    
    @Override
    public int getCount() {
        return songs.length;
    }

    @Override
    public Object getItem(int position) {
        return songs[position];
    }

    @Override
    public long getItemId(int position) {
        return songs[position].getMetadata().getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View element = convertView;
        
        if(element == null){
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            element = inflater.inflate(R.layout.song_item, null);
        }
        
        View userColor = (View) element.findViewById(R.id.userColor); 
        TextView title = (TextView)element.findViewById(R.id.title);
        TextView artist = (TextView) element.findViewById(R.id.artist);
        TextView album = (TextView) element.findViewById(R.id.album);
        

        userColor.setBackgroundColor(Color.parseColor(users.get(songs[position].getMetadata().getUsername())));
        title.setText(songs[position].getMetadata().getTitle());
        artist.setText(songs[position].getMetadata().getArtist());
        album.setText(songs[position].getMetadata().getAlbum());

        
        return element;
    }

}
