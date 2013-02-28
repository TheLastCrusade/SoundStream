package com.lastcrusade.fanclub.components;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.lastcrusade.fanclub.R;

public class PlaybarFragment extends Fragment {

    /**
     * Implement this interface to receive events when the user presses the play/pause button
     * 
     * @author thejenix
     *
     */
    public static interface PlayControlListener {
        /**
         * Called when the user presses the play button.  Note that this should never get called if music is already playing.
         * 
         */
        public void onPlay();
        
        /**
         * Called when the user presses the pause button.  Note that this should never get called if music is already paused.
         * 
         */
        public void onPause();
        
        /**
         * Called when the user presses the skip button.
         * 
         */
        public void onSkip();
        
    }
   
    private static final PlayControlListener NO_OP_PLAY_CONTROL_LISTENER = new PlayControlListener() {
        
        @Override
        public void onSkip() {}
        
        @Override
        public void onPlay() {}
        
        @Override
        public void onPause() {}
    };
    
    private PlayControlListener playControlListener = NO_OP_PLAY_CONTROL_LISTENER;

    private boolean playing;
    
    public PlaybarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_playbar, container, false);
    
        ((ImageButton) view.findViewById(R.id.btn_play_pause))
        .setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (PlaybarFragment.this.isPlaying()) {
                    PlaybarFragment.this.pause();
                } else {
                    PlaybarFragment.this.play();
                }                
            }
        });

        ((ImageButton) view.findViewById(R.id.btn_skip))
            .setOnClickListener(new OnClickListener() {
    
                @Override
                public void onClick(View v) {
                    PlaybarFragment.this.skip();
                }
            });

        return view;
    }

    private boolean isPlaying() {
        return playing;
    }

    public void play() {
        this.playing = true;
        this.playControlListener.onPlay();
    }

    public void pause() {
        this.playing = false;
        this.playControlListener.onPause();
    }
    
    public void skip() {
        this.playControlListener.onSkip();        
    }

    public void setPlayControlListener(PlayControlListener listener) {
        this.playControlListener = listener;
    }
}
