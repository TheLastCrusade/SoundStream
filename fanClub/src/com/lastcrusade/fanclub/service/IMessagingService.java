package com.lastcrusade.fanclub.service;

import java.util.List;

import com.lastcrusade.fanclub.model.SongMetadata;

public interface IMessagingService {

    public void sendLibraryMessage(List<SongMetadata> library);
    public void sendFindNewFansMessage();
    public void sendPauseMessage();
    public void sendPlayMessage();
    public void sendSkipMessage();
    public void sendStringMessage(String message);
}
