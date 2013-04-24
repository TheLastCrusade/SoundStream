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

package com.lastcrusade.soundstream.audio;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lastcrusade.soundstream.CustomApp;
import com.lastcrusade.soundstream.service.IMessagingService;
import com.lastcrusade.soundstream.service.MessagingService;
import com.lastcrusade.soundstream.service.PlaylistService;
import com.lastcrusade.soundstream.service.ServiceLocator;
import com.lastcrusade.soundstream.service.ServiceNotBoundException;
import com.lastcrusade.soundstream.util.LocalBroadcastIntent;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;

public class RemoteAudioPlayer implements IPlayer {

    private static final String TAG = RemoteAudioPlayer.class.getSimpleName();
    
    private boolean paused;
    private boolean playing;
    
    BroadcastRegistrar registrar;
    private Context context;
    private ServiceLocator<MessagingService> messagingService;

    public RemoteAudioPlayer(Context context, ServiceLocator<MessagingService> messagingServiceLocator) {
        this.context = context;
        this.messagingService = messagingServiceLocator;
        this.playing = false;
        this.paused  = false;
        registerReceivers();
    }

    @Override
    public boolean isPaused() {
        return this.paused;
    }
    
    @Override
    public boolean isPlaying() {
        return this.playing && !this.paused;
    }

    @Override
    public void play() {
        //TODO: a hack, because we really should be getting this info from the messaging system (i.e.
        // the host needs to send out a message to say if its playing or paused)
        this.playing = true;
        
        try {
            this.messagingService.getService().sendPlayMessage();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
    }

    @Override
    public void pause() {
        //TODO: see above
        this.playing = false;
        try {
            this.messagingService.getService().sendPauseMessage();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
    }

    @Override
    public void resume() {
        //TODO: see above
        this.playing = true;
        this.paused  = false;
        try {
            //TODO: this should probably send a resume message
            this.messagingService.getService().sendPlayMessage();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
    }

    @Override
    public void skip() {
        try {
            this.messagingService.getService().sendSkipMessage();
        } catch (ServiceNotBoundException e) {
            Log.wtf(TAG, e);
        }
    }
    
    private void registerReceivers() {
    	this.registrar = new BroadcastRegistrar();
    	this.registrar.addAction(MessagingService.ACTION_PLAY_STATUS_MESSAGE,
    			new IBroadcastActionHandler() {
			
			@Override
			public void onReceiveAction(Context context, Intent intent) {
				playing = intent.getBooleanExtra(MessagingService.EXTRA_IS_PLAYING, false);
				if(playing) {
				    paused = false;
					new LocalBroadcastIntent(PlaylistService.ACTION_PLAYING_AUDIO).send(context);
				}
				else {
				    paused = true;
					new LocalBroadcastIntent(PlaylistService.ACTION_PAUSED_AUDIO).send(context);
				}
			}
		}).register(this.context);
    }
}
