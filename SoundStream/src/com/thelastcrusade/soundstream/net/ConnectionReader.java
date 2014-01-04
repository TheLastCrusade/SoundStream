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
package com.thelastcrusade.soundstream.net;

import java.io.InputStream;

import com.thelastcrusade.soundstream.net.message.IMessage;
import com.thelastcrusade.soundstream.net.wire.Messenger;

/**
 * @author Jesse Rosalia
 */
public class ConnectionReader {

    private InputStream inStream;
    private Messenger messenger;
    private String remoteAddress;

    /**
     * @param remoteDevice 
     * @param mmMessenger
     * @param mmInStream
     */
    public ConnectionReader(Messenger messenger, InputStream inStream, String remoteAddress) {
        this.messenger = messenger;
        this.inStream = inStream;
        this.remoteAddress = remoteAddress;
    }

    /**
     * @throws Exception 
     * 
     */
    public void readAvailable(MessageReceiver receiver) throws Exception {
        //attempt to deserialize from the socket input stream
        boolean messageRecvd = messenger.deserializeMessage(inStream);
        if (messageRecvd) {
            for (IMessage message : messenger.getReceivedMessages()) {
                //dispatch the message to the handler
                receiver.messageReceived(message, remoteAddress);
            }
            
            messenger.clearReceivedMessages();
            messenger.clearExpiredCanceledMessages();
        }
    }    
}
