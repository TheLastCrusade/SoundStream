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

package com.lastcrusade.soundstream.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;

import com.lastcrusade.soundstream.service.PlaylistService;
import com.lastcrusade.soundstream.util.BroadcastIntent;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;
import com.lastcrusade.soundstream.util.LogUtil;

/**
 * Handle external music control events that the Android OS may
 * send to a music player.  Unlike most of our broadcast receivers,
 * this one is defined in the Android Manifest and is always listening.
 * 
 * @author Jesse Rosalia
 *
 */
public class ExternalMusicControlHandler extends BroadcastReceiver {

    private static final String TAG = ExternalMusicControlHandler.class.getSimpleName();
    
    @Override
    public void onReceive(Context context, Intent intent) {
        //headphones disconnected, or other "audio becoming noisy" events
        if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            if (LogUtil.isLogAvailable()) {
                Log.d(TAG, "Audio becoming noisy");
            }
            new BroadcastIntent(PlaylistService.ACTION_PAUSE).send(context);
        }
        //media button or disconnnected headset or ICS (or greater) remote control
        else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                    if (LogUtil.isLogAvailable()) {
                        Log.d(TAG, "Headset hook button pressed");
                    }
                    new BroadcastIntent(PlaylistService.ACTION_PAUSE).send(context);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if (LogUtil.isLogAvailable()) {
                        Log.d(TAG, "External play/pause button pressed");
                    }
                    new BroadcastIntent(PlaylistService.ACTION_PLAY_PAUSE).send(context);
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    if (LogUtil.isLogAvailable()) {
                        Log.d(TAG, "External skip button pressed");
                    }
                    new BroadcastIntent(PlaylistService.ACTION_SKIP).send(context);
                    break;
//                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
//                        // TODO: ensure that doing this in rapid succession actually plays the
//                        // previous song
//                        context.startService(new Intent(MusicService.ACTION_REWIND));
//                        break;
            }
        }
    }
}
