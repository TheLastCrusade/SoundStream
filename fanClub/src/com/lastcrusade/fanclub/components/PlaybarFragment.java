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
import com.lastcrusade.fanclub.service.AudioPlayerService;
import com.lastcrusade.fanclub.util.BroadcastIntent;

public class PlaybarFragment extends Fragment {

    /**
     * Broadcast action sent when the Audio Player service is paused.
     * 
     */
    public static final String ACTION_PAUSE = AudioPlayerService.class.getName() + ".action.Paused";
    
    /**
     * Broadcast action sent when the Audio Player service starts playing.
     * 
     */
    public static final String ACTION_PLAY  = AudioPlayerService.class.getName() + ".action.Playing";

    /**
     * Broadcast action sent when the Audio Player service is asked to skip a song.
     * 
     */
    public static final String ACTION_SKIP  = AudioPlayerService.class.getName() + ".action.Skipping";
    
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
        new BroadcastIntent(ACTION_PLAY).send(this.getActivity());
    }

    public void pause() {
        this.playing = false;
        new BroadcastIntent(ACTION_PAUSE).send(this.getActivity());
    }
    
    public void skip() {
        new BroadcastIntent(ACTION_SKIP).send(this.getActivity());
    }
}
