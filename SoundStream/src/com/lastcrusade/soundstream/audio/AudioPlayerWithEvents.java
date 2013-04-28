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
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.util.Log;

import com.lastcrusade.soundstream.SoundStreamExternalControlClient;
import com.lastcrusade.soundstream.service.PlaylistService;
import com.lastcrusade.soundstream.util.BroadcastIntent;
import com.lastcrusade.soundstream.util.ClassUtils;
import com.lastcrusade.soundstream.util.LogUtil;

public class AudioPlayerWithEvents implements IPlayer {

    private static final String TAG = AudioPlayerWithEvents.class.getSimpleName();

    private IPlayer player;
    private Context context;
    private IDuckable duckable;

    private SoundStreamExternalControlClient externalControlClient;

    private boolean registered;

    //NOTE: this is static, because music focus is a global attribute that belongs to the phone
    //TODO: this may be better off put in a global settings object...
    private static boolean hasFocus;
    
    public AudioPlayerWithEvents(IPlayer player, Context context) {
        this.player  = player;
        this.duckable = ClassUtils.getIfAvailable(player, IDuckable.class);
        this.context = context;
        this.externalControlClient = new SoundStreamExternalControlClient(this.context);
        this.registered = false;
        hasFocus = false;
    }
    
    private void registerExternalControlClient() {
        externalControlClient.registerClient();
        this.registered = true;
    }

    private void unregisterExternalControlClient() {
        this.registered = false;
        externalControlClient.unregisterClient();
    }
    
    private boolean isExternalControlClientRegistered() {
        return this.registered;
    }

    private void requestAudio() {
        //NOTE: we need to request the audio for the remote controls to work, but
        // we also want to handle things like audio ducking and pausing here.
        AudioManager myAudioManager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
        if (!hasFocus) {
            int response = myAudioManager.requestAudioFocus(new OnAudioFocusChangeListener() {
    
                @Override
                public void onAudioFocusChange(int focusChange) {
                    //TODO: duck audio or pause in other cases where focus has changed.
                    switch (focusChange) {
                    //handle loss of focus, which includes when a phonecall is coming in
                    case AudioManager.AUDIOFOCUS_LOSS:
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        handleAudioFocusLoss(focusChange == AudioManager.AUDIOFOCUS_LOSS);
                        hasFocus = false;
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        handleAudioFocusCanDuck();
                        //NOTE: this will not change the audio focus permanently, so
                        // we dont change hasFocus
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN:
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                        handleAudioFocusReturned(focusChange == AudioManager.AUDIOFOCUS_GAIN);
                        hasFocus = true;
                        break;
                    }
                }

            }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            hasFocus = response == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            if (hasFocus) {
                registerExternalControlClient();
            }
        }
    }

    /**
     * Handle loss of audio focus.  In both permanent and temporary cases,
     * we want to unregister our external control client and pause the music.
     * 
     * No further cleanup is required for permanent loss, as the user may come
     * back and use the app and expect state to be maintained.
     * 
     * @param focusChange
     */
    private void handleAudioFocusLoss(boolean permanent) {
        if (LogUtil.isLogAvailable()) {
            Log.d(TAG, String.format("Audio focus lost%s", permanent ? "" : " (transient)"));
        }
        //unregister the external control client when focus is lost, as per best practice
        unregisterExternalControlClient();
        //NOTE: this calls the the pause method directly, because we want instant action.
        pause();
    }

    /**
     * Handle ducking due to loss of audio focus.  This is not really a
     * loss of focus (compared to other focus loss events) so all we
     * need to do is duck the player volume.
     * 
     */
    private void handleAudioFocusCanDuck() {
        if (LogUtil.isLogAvailable()) {
            Log.d(TAG, "Audio focus lost (can duck)");
        }
        duck();
        //NOTE: don't touch the external client
        //...we have technically not lost focus
    }

    /**
     * Handle focus returning after a loss or a duck.  In all cases
     * we will register the external client
     * @param focusChange
     */
    private void handleAudioFocusReturned(boolean permanent) {
        if (LogUtil.isLogAvailable()) {
            Log.d(TAG, String.format("Audio focus returned%s", permanent ? "" : " (transient)"));
        }
        //register the external control client when we gain focus
        // but only if we're unregistered
        if (!isExternalControlClientRegistered()) {
            registerExternalControlClient();
        }
        unduck();
        resume();
    }

    /**
     * Duck the volume of the player, if the player supports
     * audio ducking.
     */
    protected void duck() {
        if (this.duckable != null) {
            this.duckable.duck();
        }
    }

    /**
     * Unduck the volume of the player, if the player supports
     * audio ducking.
     */
    protected void unduck() {
        if (this.duckable != null) {
            this.duckable.unduck();
        }
    }

    @Override
    public boolean isPaused() {
        return this.player.isPaused();
    }

    @Override
    public boolean isPlaying() {
        return this.player.isPlaying();
    }

    @Override
    public void play() {
        requestAudio();
        this.player.play();
        new BroadcastIntent(PlaylistService.ACTION_PLAYING_AUDIO).send(this.context);
    }

    @Override
    public void pause() {
        this.player.pause();
        new BroadcastIntent(PlaylistService.ACTION_PAUSED_AUDIO).send(this.context);
    }

    @Override
    public void resume() {
        requestAudio();
        this.player.resume();
        new BroadcastIntent(PlaylistService.ACTION_PLAYING_AUDIO).send(this.context);
    }

    @Override
    public void skip() {
        this.player.skip();
        new BroadcastIntent(PlaylistService.ACTION_SKIPPING_AUDIO).send(this.context);
    }
}
