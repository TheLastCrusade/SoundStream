package com.lastcrusade.fanclub.components;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.service.PlaylistService;
import com.lastcrusade.fanclub.service.PlaylistService.PlaylistServiceBinder;
import com.lastcrusade.fanclub.service.ServiceLocator;
import com.lastcrusade.fanclub.service.ServiceNotBoundException;

public class PlaybarFragment extends Fragment {

    private static final String TAG = PlaybarFragment.class.getName();
    
    private ServiceLocator<PlaylistService> playlistServiceLocator;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.playlistServiceLocator = new ServiceLocator<PlaylistService>(this.getActivity(), PlaylistService.class, PlaylistServiceBinder.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_playbar, container, false);
    
        ((ImageButton) view.findViewById(R.id.btn_play_pause))
            .setOnClickListener(new OnClickListener() {
    
                @Override
                public void onClick(View v) {
                    try {
                        PlaylistService service = playlistServiceLocator.getService();
                        if (service.isPlaying()) {
                            service.pause();
                        } else {
                            service.play();
                        }
                    } catch (ServiceNotBoundException e) {
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
}
