package com.lastcrusade.fanclub.components;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.audio.SingleFileAudioPlayer;
import com.lastcrusade.fanclub.service.PlaylistService;
import com.lastcrusade.fanclub.service.PlaylistService.PlaylistServiceBinder;
import com.lastcrusade.fanclub.service.ServiceLocator;
import com.lastcrusade.fanclub.service.ServiceNotBoundException;
import com.lastcrusade.fanclub.util.IBroadcastActionHandler;

public class PlaybarFragment extends Fragment {

    private static final String TAG = PlaybarFragment.class.getName();
    
    private ServiceLocator<PlaylistService> playlistServiceLocator;
    //TODO PlaybarFragment floods logcat with errors if the screen is rotated

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.playlistServiceLocator = new ServiceLocator<PlaylistService>(
                this.getActivity(), PlaylistService.class, PlaylistServiceBinder.class);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        playlistServiceLocator.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_playbar, container, false);
    
        final ImageButton playPause = ((ImageButton) view.findViewById(R.id.btn_play_pause));

        playPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlaylistService service = playlistServiceLocator.getService();
                    if (service.isPlaying()) {
                        service.pause();
                        playPause.setImageDrawable(
                                getResources().getDrawable(R.drawable.av_play));
                        Log.w(TAG, "pause called");
                    } else {
                        service.play();
                        playPause.setImageDrawable(
                                getResources().getDrawable(R.drawable.av_pause));
                        Log.w(TAG, "play called");
                        }
                }catch (ServiceNotBoundException e) {
                    Log.wtf(TAG, e);
                }
            }
        });

        ((ImageButton) view.findViewById(R.id.btn_skip))
            .setOnClickListener(new OnClickListener() {
    
                @Override
                public void onClick(View v) {
                    try {
                        PlaylistService service = playlistServiceLocator.getService();
                        service.skip();
                    } catch (ServiceNotBoundException e) {
                        Log.wtf(TAG, e);
                    }
                }
            });

        return view;
    }
    //TODO add register recievers with ACTION_SONG_FINISHED
}
