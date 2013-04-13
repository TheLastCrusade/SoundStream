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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

import com.lastcrusade.soundstream.net.core.AComplexDataType;
import com.lastcrusade.soundstream.net.core.ISerializable;
import com.lastcrusade.soundstream.net.wire.MessageNotCompleteException;

/**
 * A format for all messages.  All messages consists of:
 *  4 byte length of the message, not including the length (using java.nio.ByteBuffer)
 *  4 byte messenger version
 *  class name (in bytes)
 *  \n character to terminate the class name
 *  n bytes of message payload

 * @author Jesse Rosalia
 *
 */
public class MessageFormat extends AComplexDataType implements ISerializable {

    private static final String TAG = MessageFormat.class.getSimpleName();
    private static final int MESSENGER_VERSION = 1;
    private static final char END_OF_CLASS_CHAR = '\n';

    private IMessage message;
    private int length;

    /**
     * 
     */
    public MessageFormat() {
    }

    /**
     * @param size
     * @param class1
     */
    public MessageFormat(IMessage message) {
        this.message = message;
    }

    @Override
    public void deserialize(InputStream input) throws IOException, MessageNotCompleteException {
        //we assume that there is at least a length in the input stream
        if (this.length == 0) {
            this.length = readInteger(input);
        }

        //if we havent read in the whole message, throw an exception
        if (this.length > input.available()) {
            throw new MessageNotCompleteException();
        }

        @SuppressWarnings("unused")
        int messengerVersion = readInteger(input);

        //TODO: actually do something with the messengerVersion
        byte[] classBytes = readBytesUntil(input, END_OF_CLASS_CHAR);
        //REVIEW: character encoding issues may arise, but since we're controlling the class names
        // we should be able to decide how to handle these
        String messageName = new String(classBytes);

        IMessage message = instantiateMessage(messageName);
        message.deserialize(input);
        this.message = message;
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        //serialize the message into a separate buffer
        ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();
        message.serialize(messageBuffer);
        
        //build the output message
        byte[] classBytes = message.getClass().getCanonicalName().getBytes();
        //write the length
        writeInteger(messageBuffer.size() + classBytes.length + SIZEOF_INTEGER + 1, output);
        writeInteger(MESSENGER_VERSION, output);
        //write the class name and end of class char
        // (this is used to reconstruct the message on the remote side)
        writeBytes(classBytes, output);
        writeBytes(new byte[] {END_OF_CLASS_CHAR}, output);
        //write the message
        writeBytes(messageBuffer.toByteArray(), output);        
    }
    
    /**
     * @return the message
     */
    public IMessage getMessage() {
        return message;
    }

    /**
     * @param messageName
     * @return 
     */
    private IMessage instantiateMessage(String messageName) {
        try {
            return (IMessage) Class.forName(messageName).newInstance();
//            if (obj instanceof IMessage) {
//                return (IMessage)obj;
//            } else {
//                //otherwise, it's a WTF
////                if (this.canLog) {
////                }
//            }
        } catch (ClassCastException ex) {
            Log.wtf(TAG, "Received message '" + messageName + "', but it does not implement IMessage");
            throw new RuntimeException("Unable to instantiate message...this is a critical error.", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to instantiate message...this is a critical error.", ex);
        }
    }

    /**
     * @param input
     * @param endChar
     * @return
     * @throws IOException 
     */
    private byte[] readBytesUntil(InputStream input, char endChar) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte read;
        while ((read = (byte) input.read()) != endChar) {
            baos.write(read);
        }
        return baos.toByteArray();
    }
}
