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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.thelastcrusade.soundstream.net.message.IMessage;

/**
 * A helper class to receive messages from the wire and dispatch them through the Android
 * system.
 * 
 * @author Jesse Rosalia
 *
 */
public class MessageReceiver {

    private int inMessageNumber  = 1;
    private Handler handler;

    /**
     * 
     */
    public MessageReceiver(Handler handler) {
        this.handler = handler;
    }
    
    private Message obtainAndroidMessage(int type, Object obj) {
        return handler.obtainMessage(type, this.inMessageNumber++, 0, obj);
    }    

    /**
     * Send the network message to the appropriate handler.
     * 
     * @param message
     * @param remoteAddr
     */
    public void messageReceived(IMessage message, String remoteAddr) {
        Message androidMsg = obtainAndroidMessage(ConnectionConstants.MESSAGE_READ, message);
        Bundle bundle = new Bundle();
        bundle.putString(ConnectionConstants.EXTRA_ADDRESS, remoteAddr);
        androidMsg.setData(bundle);
        androidMsg.sendToTarget();
    }
}
