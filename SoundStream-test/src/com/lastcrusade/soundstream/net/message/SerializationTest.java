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

package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.junit.Test;

import com.lastcrusade.soundstream.net.message.IMessage;
import com.lastcrusade.soundstream.net.message.Messenger;

public class SerializationTest<T extends IMessage> {
    
    public T testSerializeMessage(T message) throws Exception {
        Messenger messenger = new Messenger();
        
        messenger.serializeMessage(message);
        InputStream is = simulateSendAndReceive(messenger.getOutputBytes());
        
        Messenger rcvMessenger = new Messenger();
        //attempt to deserialize the second message
        assertTrue(rcvMessenger.deserializeMessage(is));
        
        //and check the deserialized message
        IMessage rcvMessage = rcvMessenger.getReceivedMessage();
        assertNotNull(rcvMessage);
        assertEquals(message.getClass(), rcvMessage.getClass());
        
        //make sure all bytes are consumed
        assertEquals(0, is.available());
        return (T)rcvMessage;
    }
    
    public T testDeserializeMessage(T message) throws Exception {
        Messenger messenger = new Messenger();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        
        //attempt to deserialize it
        assertTrue(messenger.deserializeMessage(bais));
        
        //and check the deserialized message
        IMessage rcvMessage = messenger.getReceivedMessage();
        assertNotNull(rcvMessage);
        assertEquals(message.getClass(), rcvMessage.getClass());
        
        //make sure all bytes are consumed
        assertEquals(0, bais.available());
        return (T)rcvMessage;
    }
    
    private InputStream simulateSendAndReceive(
            byte[] outputBytes) {
        return new ByteArrayInputStream(outputBytes);
    }
    
    /**
     * NOTE: keep this separate, so we have independent verification of the Messenger.  This lets
     * us use the independently verified side to test the other side of the messenger.
     * @param className
     * @param testMessage
     * @param baos
     * @throws IOException
     */
    private void appendMessage(String className,
            String testMessage, ByteArrayOutputStream baos) throws IOException {
        //only write the length bytes the first time through
        int start = baos.size();
        baos.write(new byte[4]);
        baos.write(className.getBytes());
        baos.write('\n');
        baos.write(testMessage.getBytes());
        byte[] bytes = baos.toByteArray();
        ByteBuffer bb = ByteBuffer.wrap(bytes, start, 4);
        int len = baos.size() - start - 4;
        bb.putInt(len);
        
        baos.reset();
        baos.write(bytes);
    }

}
