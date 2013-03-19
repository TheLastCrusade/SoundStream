package com.lastcrusade.soundstream.components;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.util.ITitleable;

public abstract class MusicListFragment extends SherlockListFragment implements ITitleable{
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.list, container, false);
        return v;
    }
    
    @Override
    public void onListItemClick(ListView lv, View v, int position, long id){
        super.onListItemClick(lv, v, position, id);
        toggleViewSize(v);
    }
    
    
    private void toggleViewSize(View v){
        TextView title = (TextView)v.findViewById(R.id.title); 
        TextView album = (TextView)v.findViewById(R.id.album);
        TextView artist = (TextView)v.findViewById(R.id.artist);
        
        //if the view height is larger than the standard element, set it back to the standard
        if(v.getHeight()>getResources().getDimension(R.dimen.song_height)){
            
            title.setSingleLine(true);
            artist.setSingleLine(true);
            album.setSingleLine(true);
            
            //set the height of the color bar to the standard song element height
            v.findViewById(R.id.user_color).setMinimumHeight((int) getResources().getDimension(R.dimen.song_height));
        }
        //otherwise, expand the view
        else{            
            title.setSingleLine(false);
            artist.setSingleLine(false);
            album.setSingleLine(false);
            
            //get the additional height taken up by the expanded words
            int titleHeight = title.getHeight() - title.getLineHeight();
            int artistHeight = artist.getHeight() - artist.getLineHeight();
            int albumHeight = album.getHeight() - album.getLineHeight();
            
            //calculate the total height of the expanded view
            int viewHeight = (int) getResources().getDimension(R.dimen.song_height)
                    + titleHeight + artistHeight + albumHeight;
            
            //set the height of the color bar to the new view height
            v.findViewById(R.id.user_color).setMinimumHeight(viewHeight);
        }
    }

    @Override
    public abstract int getTitle();
    
    @Override
    public void onResume(){
        super.onResume();
    }
}
