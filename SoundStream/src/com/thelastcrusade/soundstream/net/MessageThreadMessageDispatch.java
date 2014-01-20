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

import java.util.HashMap;
import java.util.Map;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.thelastcrusade.soundstream.net.message.IMessage;

/**
 * An Android Handler used as a SoundStream network message dispatch.  This will receive
 * Android Message objects that represent network messages, and dispatch them to the
 * appropriate handler.
 * 
 * @author Jesse Rosalia
 *
 */
public class MessageThreadMessageDispatch extends Handler {

    public interface IMessageHandler<T extends IMessage> {
        /**
         * Handle an incoming message from a remote connection.
         * 
         * @param messageNo A monotonically increasing message counter
         * @param message A message object.  The type of this object is specified
         *                through generic type parameters 
         * @param fromAddr The MAC address of the remote device (e.g. the
         *                 Bluetooth address).  It will be in the form
         *                 xx:yy:zz:aa:bb:cc, made up of hexadecimal digits.
         */
        public void handleMessage(int messageNo, T message, String fromAddr);
    }

    private static final String TAG = "MessageThreadMessageDispatch";

    //hold a map of messages to handlers for those messages.  The registerHandler method
    // ensures that the handler is appropriate for the type of class passed in, since we
    // cannot define arbitrary bounds     
    private Map<Class<? extends IMessage>, IMessageHandler<? extends IMessage>> dispatchMap =
            new HashMap<Class<? extends IMessage>, IMessageHandler<? extends IMessage>>();

    private IMessageHandler<IMessage> defaultHandler = null;

    /**
     * Register a message handler with the dispatch.  This handler will get called
     * when a message is received.
     * 
     * NOTE: only one handler per message, per dispatch.
     * 
     * @param messageClass
     * @param handler
     */
    public <T extends IMessage> void registerHandler(Class<T> messageClass, IMessageHandler<T> handler) {
        dispatchMap.put(messageClass, handler);
    }
    
    public void setDefaultHandler(IMessageHandler<IMessage> defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    @Override
    public void handleMessage(Message msg) {
        IMessage        message   = null;
        int             messageNo = 0;
        String          fromAddr  = null;
        
        if (msg.what == ConnectionConstants.MESSAGE_READ) {
            messageNo = msg.arg1;
            message   = (IMessage)msg.obj;
            fromAddr  = msg.getData().getString(ConnectionConstants.EXTRA_ADDRESS);
            handleMessage(messageNo, message, fromAddr);
        } else {
            // default...call the base class
            super.handleMessage(msg);
        }
    }

    public <T extends IMessage> void handleMessage(int messageNo, T message, String fromAddr) {
        //NOTE: this is OK, because we enforce type safety when we register the handler
        @SuppressWarnings("unchecked")
        IMessageHandler<T> handler   = (IMessageHandler<T>) dispatchMap.get(message.getClass());
        
        if (handler != null) {
            Log.w(TAG, "Message received: " + messageNo + ", it's a " + message.getClass().getSimpleName());
            handler.handleMessage(messageNo, message, fromAddr);
        } else if (defaultHandler != null) {
            this.defaultHandler.handleMessage(messageNo, message, fromAddr);
        } else {
            Log.wtf(TAG, "Handler not registered for '" + message.getClass() + "'");            
        }
    }
}
