package com.lastcrusade.fanclub.audio;

import android.content.Context;

import com.lastcrusade.fanclub.CustomApp;
import com.lastcrusade.fanclub.service.IMessagingService;

public class RemoteAudioPlayer implements IPlayer {

    private Context context;
    private CustomApp application;

    public RemoteAudioPlayer(CustomApp application) {
        this.application = application;
    }

    @Override
    public boolean isPlaying() {
        //TODO: this won't be accurate until we implement some kind of host->fan broadcast that it's playing or paused
        return false;
    }

    @Override
    public void play() {
        this.application.getMessagingService().sendPlayMessage();
    }

    @Override
    public void pause() {
        this.application.getMessagingService().sendPauseMessage();
    }

    @Override
    public void skip() {
        this.application.getMessagingService().sendSkipMessage();
    }
}
