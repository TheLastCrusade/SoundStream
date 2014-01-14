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

import java.io.IOException;

import com.thelastcrusade.soundstream.net.message.IMessage;

/**
 * A helper class to enqueue messages to be written by the connection writer.
 * 
 * This class also defines the behavior of the Future and the cancel method,
 * for canceling outstanding messages.
 * 
 * @author Jesse Rosalia
 *
 */
public class MessageEnqueuer {

    private int mmOutMessageNumber = 1;
    private ConnectionWriter writer;

    /**
     * 
     */
    public MessageEnqueuer(ConnectionWriter writer) {
        this.writer = writer;
    }
    /**
     * @param message
     * @return
     * @throws IOException 
     */
    public MessageFuture enqueueMessage(IMessage message) throws IOException {
        //enqueue this message
        final int messageNo = mmOutMessageNumber++;
        MessageFuture future = new MessageFuture() {

            @Override
            public void cancel() throws IOException {
                writer.cancel(messageNo);
            }
            
        };
        this.writer.enqueue(messageNo, message, future);
        return future;
    }
}
