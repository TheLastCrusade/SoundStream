package com.lastcrusade.fanclub.service;

import java.util.List;

import com.lastcrusade.fanclub.model.SongMetadata;

public interface IMessagingService {

    public void sendLibraryMessage(List<SongMetadata> library);
    public void sendFindNewFansMessage();
    public void sendStringMessage(String message);
}
