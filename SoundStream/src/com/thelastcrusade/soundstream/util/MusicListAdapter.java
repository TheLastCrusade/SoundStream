/*
 * Copyright 2013 The Last Crusade ContactLastCrusade@gmail.com
 * 
 * This file is part of SoundStream.
 * 
 * SoundStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SoundStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SoundStream.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lastcrusade.soundstream.util;

import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.model.User;
import com.lastcrusade.soundstream.model.UserList;

public class MusicListAdapter<T extends SongMetadata> extends BaseAdapter {
    private final String TAG = MusicListAdapter.class.getSimpleName();

    private Context mContext;
    private List<T> metadataList;
    private UserList users;
    
    public MusicListAdapter(
            Context mContext,
            List<T> metadataList,
            UserList users
            ){
        this.mContext = mContext;
        this.metadataList = metadataList;
        this.users = users;
    }
    
    
    @Override
    public int getCount() {
        return metadataList.size();
    }

    @Override
    public T getItem(int position) {
        return metadataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return metadataList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View element = convertView;
        
        if(element == null){
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            element = inflater.inflate(R.layout.song_item, null);
        }
        
               View userColor = (View) element.findViewById(R.id.user_color); 
               TextView title = (TextView) element.findViewById(R.id.title);
               TextView album = (TextView) element.findViewById(R.id.album);
              TextView artist = (TextView) element.findViewById(R.id.artist);
        ImageButton addButton = (ImageButton) element.findViewById(R.id.btn_add_to_playlist);

        String macAddress = metadataList.get(position).getMacAddress();
        
        User user = users.getUserByMACAddress(macAddress);
        if (user != null) {
            userColor.setBackgroundColor(user.getColor());
        } else {
            Log.wtf(TAG, "User with mac address " + macAddress + " not found.  Using default color.");
            userColor.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }
        
        //set the default sizes
        userColor.setMinimumHeight((int) mContext.getResources().getDimension(R.dimen.song_height));
        title.setSingleLine(true);
        artist.setSingleLine(true);
        album.setSingleLine(true);

        addButton.setTag(position);
        addButton.setContentDescription(ContentDescriptionUtils
                .addToPlaylistAppendSongTitle(metadataList.get(position)));

        title.setText(metadataList.get(position).getTitle());
        artist.setText(metadataList.get(position).getArtist());
        album.setText(metadataList.get(position).getAlbum());
        
        artist.setMaxWidth(parent.getWidth()/2);
        /*
        final GestureDetectorCompat songGesture = new GestureDetectorCompat(mContext, new SongGestureListener(element));
        element.setOnTouchListener(new View.OnTouchListener() {       
            @Override
            public boolean onTouch(View v, MotionEvent event) {
              return songGesture.onTouchEvent(event);
            }
        });*/
        return element;
    }
    
    //updates the music shown and notifies the attached view that it needs to redraw
    public void updateMusic(List<T> metadataList){
        this.metadataList = metadataList;
        notifyDataSetChanged();
    }
    
    public void updateUsers(UserList users){
        this.users = users;
        notifyDataSetChanged();
    }
    
        

}
