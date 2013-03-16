package com.lastcrusade.soundstream.components;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.CoreActivity;
import com.lastcrusade.soundstream.CustomApp;
import com.lastcrusade.soundstream.model.SongMetadata;
import com.lastcrusade.soundstream.service.ConnectionService;
import com.lastcrusade.soundstream.service.IMessagingService;
import com.lastcrusade.soundstream.service.MusicLibraryService;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
import com.lastcrusade.soundstream.service.MusicLibraryService.MusicLibraryServiceBinder;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.ITitleable;
import com.lastcrusade.soundstream.util.Transitions;

/*
 * This fragment should be what is first presented to the user when
 * they enter the app and are not connected to any network
 */
public class ConnectFragment extends SherlockFragment implements ITitleable{
    
    private static final String TAG = ConnectFragment.class.getName();

    private BroadcastRegistrar broadcastRegistrar;
    private Button connectButton;
    private ServiceLocator<MusicLibraryService> musicLibraryLocator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceivers();

        musicLibraryLocator = new ServiceLocator<MusicLibraryService>(this.getActivity(),
                MusicLibraryService.class, MusicLibraryServiceBinder.class);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connect, container, false);

        Button create = (Button)v.findViewById(R.id.btn_create);
        create.setOnClickListener( new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //TODO: these will go away once Elizabeth completes her transition singleton
                ((CoreActivity)getActivity()).onConnected();
                Transitions.transitionToNetwork((CoreActivity)getActivity());
            }
        });
        
        this.connectButton = (Button)v.findViewById(R.id.btn_connect);
        this.connectButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                connectButton.setEnabled(false);
                getConnectionService().broadcastFan(getActivity());
            }
        });

        return v;
    }

    @Override
    public void onDestroy() {
        unregisterReceivers();
        super.onDestroy();
    }
    
    private ConnectionService getConnectionService() {
        CustomApp app = (CustomApp) getActivity().getApplication();
        return app.getConnectionService();
    }
    
    private IMessagingService getMessagingService() {
        CustomApp app = (CustomApp) getActivity().getApplication();
        return app.getMessagingService();
    }
    
    private MusicLibraryService getMusicLibraryService() throws ServiceNotBoundException {
        return this.musicLibraryLocator.getService();
    }
    
    private void registerReceivers() {
        this.broadcastRegistrar = new BroadcastRegistrar();
        this.broadcastRegistrar
            .addAction(ConnectionService.ACTION_HOST_CONNECTED, new IBroadcastActionHandler() {

                    @Override
                    public void onReceiveAction(Context context, Intent intent) {
                        connectButton.setEnabled(true);
                        //send the library to the connected host
                        try {
                            List<SongMetadata> metadata = getMusicLibraryService().getMyLibrary();
                            getMessagingService().sendLibraryMessage(metadata);
                        } catch (ServiceNotBoundException e) {
                            Log.wtf(TAG, e);
                        }
                        ((CoreActivity)getActivity()).onConnected();
                        //switch 
                        Transitions.transitionToHome((CoreActivity)getActivity());
                    }
                })
            .register(this.getActivity());
    }

    private void unregisterReceivers() {
        this.broadcastRegistrar.unregister();
    }

    @Override
    public int getTitle() {
        return R.string.connect;
    }
}
