package com.lastcrusade.soundstream.components;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
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
            
            /*
             * Calculates the number of additional lines needed to contain the text
             */
            Rect titleBounds = new Rect();
            title.getPaint().getTextBounds(title.getText().toString(), 0, title.length(), titleBounds);
            int titleLines = titleBounds.width()/title.getWidth();
           
            Rect artistBounds = new Rect();
            artist.getPaint().getTextBounds(artist.getText().toString(), 0, artist.length(), artistBounds);
            int artistLines = artistBounds.width()/artist.getWidth();
            
            Rect albumBounds = new Rect();
            album.getPaint().getTextBounds(album.getText().toString(), 0, album.length(), albumBounds);
            /*
             *  +1 accounts for the fact that since album wraps its contents, it is almost
             * always exactly as big as it needs to b. This makes album think it is a pixel
             * larger than it is, which eliminates the possibility of thinking we have
             * to add another line when in reality it is just a perfect match
             */
            int albumLines = albumBounds.width()/(album.getWidth()+1);
            
            // determines whether artist or album is longer, that way expansion can
            // reference the correct one
            int bottomLines = artistLines;
            if(albumLines > bottomLines){
                bottomLines = albumLines;
            }
            
            //calculate the total height of the expanded view
            int viewHeight = (int) getResources().getDimension(R.dimen.song_height)
                    + titleLines*title.getLineHeight() + bottomLines*artist.getLineHeight();
            
            
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
