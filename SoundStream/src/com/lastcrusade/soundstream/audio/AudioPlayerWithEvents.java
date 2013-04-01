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

import com.lastcrusade.soundstream.service.PlaylistService;
import com.lastcrusade.soundstream.util.BroadcastIntent;

public class AudioPlayerWithEvents implements IPlayer {

    private IPlayer player;
    private Context context;

    public AudioPlayerWithEvents(IPlayer player, Context context) {
        this.player  = player;
        this.context = context;
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
        this.player.resume();
        new BroadcastIntent(PlaylistService.ACTION_PLAYING_AUDIO).send(this.context);
    }

    @Override
    public void skip() {
        this.player.skip();
        new BroadcastIntent(PlaylistService.ACTION_SKIPPING_AUDIO).send(this.context);
    }
}
