package com.lastcrusade.fanclub.components;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.util.ITitleable;

public abstract class MusicListFragment extends SherlockListFragment implements ITitleable{

    private final int SHORT_VIEW = 1;
    private final int EXPANDED_VIEW = 10;
    
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
            title.setMaxLines(SHORT_VIEW);
            album.setMaxLines(SHORT_VIEW);
            artist.setMaxLines(SHORT_VIEW);
            
            //set the height of the color bar to the standard song element height
            v.findViewById(R.id.user_color).setMinimumHeight((int) getResources().getDimension(R.dimen.song_height));
        }
        //otherwise, expand the view
        else{
            title.setMaxLines(EXPANDED_VIEW);
            album.setMaxLines(EXPANDED_VIEW);
            artist.setMaxLines(EXPANDED_VIEW);
            
            //get the additional height taken up by the expanded words
            int titleHeight = (title.getLineCount()-1)*title.getLineHeight();
            int artistHeight =  (artist.getLineCount()-1)*artist.getLineHeight();
            int albumHeight = (album.getLineCount()-1)*album.getLineHeight();
            
            //calculate the total height of the expanded view
            int viewHeight = (int) getResources().getDimension(R.dimen.song_height)
                    + titleHeight + artistHeight + albumHeight;
            
            //set the height of the color bar to the new view height
            v.findViewById(R.id.user_color).setMinimumHeight(viewHeight);
        }
    }

    @Override
    public abstract String getTitle();
    
    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(getTitle());
    }
}
