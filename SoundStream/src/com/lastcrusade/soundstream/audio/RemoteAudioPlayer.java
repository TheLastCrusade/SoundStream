package com.lastcrusade.soundstream.audio;

import android.content.Context;

import com.lastcrusade.soundstream.CustomApp;
import com.lastcrusade.soundstream.service.IMessagingService;

public class RemoteAudioPlayer implements IPlayer {

    private CustomApp application;
    private boolean playing;

    public RemoteAudioPlayer(CustomApp application) {
        this.application = application;
        this.playing = false;
    }

    @Override
    public boolean isPlaying() {
        return this.playing;
    }

    @Override
    public void play() {
        //TODO: a hack, because we really should be getting this info from the messaging system (i.e.
        // the host needs to send out a message to say if its playing or paused)
        this.playing = true;
        this.application.getMessagingService().sendPlayMessage();
    }

    @Override
    public void pause() {
        //TODO: see above
        this.playing = false;
        this.application.getMessagingService().sendPauseMessage();
    }

    @Override
    public void skip() {
        this.application.getMessagingService().sendSkipMessage();
    }
}
