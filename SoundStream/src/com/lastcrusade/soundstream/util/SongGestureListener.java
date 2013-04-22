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

import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.lastcrusade.soundstream.R;

/**
 * @author Elizabeth
 *
 */

public class SongGestureListener extends GestureDetector.SimpleOnGestureListener{
    protected ListView musicView;
    protected int selectedIndex;
    
    public SongGestureListener(ListView musicView){
        this.musicView = musicView;
    }
    
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if(selectedIndex != -1){
            toggleViewSize(getSelectedView());
        }
        return true;
    }
    @Override
    public boolean onDown(MotionEvent e) {
        int firstPos = musicView.getFirstVisiblePosition();
        int lastPos = musicView.getLastVisiblePosition();
        
        //get the starting x & y location of the music view
        int[] location = new int[2];
        musicView.getLocationOnScreen(location);
        
        selectedIndex = -1;
        
        for(int i = firstPos; i<lastPos+1; i++){
            View song = musicView.getChildAt(i-firstPos);
            Rect box = new Rect();
            song.getHitRect(box);
            
            if(box.contains((int)e.getX(), (int)e.getY())){
                selectedIndex = i;
                break;
            }
        }
       return true;
    }

    public void toggleViewSize(View v){
        TextView title = (TextView)v.findViewById(R.id.title); 
        TextView album = (TextView)v.findViewById(R.id.album);
        TextView artist = (TextView)v.findViewById(R.id.artist);
        //if the view height is larger than the standard element, set it back to the standard
        if(v.getHeight()> v.getContext().getResources().getDimension(R.dimen.song_height)){
            
            title.setSingleLine(true);
            artist.setSingleLine(true);
            album.setSingleLine(true);
            
            //set the height of the color bar to the standard song element height
            v.findViewById(R.id.user_color).setMinimumHeight((int) v.getContext().getResources().getDimension(R.dimen.song_height));
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
            int viewHeight = (int) v.getContext().getResources().getDimension(R.dimen.song_height)
                    + titleLines*title.getLineHeight() + bottomLines*artist.getLineHeight();
            
            
            //set the height of the color bar to the new view height
            v.findViewById(R.id.user_color).setMinimumHeight(viewHeight);
        }
    }
    
    protected View getSelectedView(){
        return musicView.getChildAt(selectedIndex-musicView.getFirstVisiblePosition());
    }
    

}