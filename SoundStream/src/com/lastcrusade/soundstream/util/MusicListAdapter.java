package com.lastcrusade.soundstream.util;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.model.User;
import com.lastcrusade.soundstream.model.UserList;

public class MusicListAdapter extends BaseAdapter {
    private final String TAG = MusicListAdapter.class.getName();

    private Context mContext;
    private List<SongMetadata> metadataList;
    private UserList users;
    
    public MusicListAdapter(
            Context mContext,
            List<SongMetadata> metadataList,
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
    public SongMetadata getItem(int position) {
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
        TextView title = (TextView)element.findViewById(R.id.title);
        TextView artist = (TextView) element.findViewById(R.id.artist);
        TextView album = (TextView) element.findViewById(R.id.album);

        String macAddress = metadataList.get(position).getMacAddress();
        
        User user = users.getUserByMACAddress(macAddress);
        if (user != null) {
            userColor.setBackgroundColor(user.getColor());
        } else {
            Log.wtf(TAG, "User with mac address " + macAddress + " not found.  Using default color.");
            userColor.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }

        ImageButton addButton = (ImageButton) element.findViewById(R.id.btn_add_to_playlist);
        addButton.setContentDescription(addButton.getContentDescription().toString()
                + "_" + metadataList.get(position).getTitle().replace(" ", ""));

        addButton.setTag(position);

        title.setText(metadataList.get(position).getTitle());
        artist.setText(metadataList.get(position).getArtist());
        album.setText(metadataList.get(position).getAlbum());
        
        artist.setMaxWidth(parent.getWidth()/2);

        return element;
    }
    
    //updates the music shown and notifies the attached view that it needs to redraw
    public void updateMusic(List<SongMetadata> metadataList){
        this.metadataList = metadataList;
        notifyDataSetChanged();
    }
    
    public void updateUsers(UserList users){
        this.users = users;
        notifyDataSetChanged();
    }
}
