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
