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
package com.lastcrusade.soundstream.net.wire;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

import com.lastcrusade.soundstream.net.message.IFileMessage;
import com.lastcrusade.soundstream.net.message.IMessage;
import com.lastcrusade.soundstream.net.message.MessageFormat;
import com.lastcrusade.soundstream.util.InputBuffer;
import com.lastcrusade.soundstream.util.LogUtil;

/**
 * An output stream for receiving incoming message data.  This
 * class allows callers to write message data as it is received.
 * It will buffer data until a full message is received,
 * deserialize the message, and (if present) write file data
 * to a temporary file.
 * 
 * @author Jesse Rosalia
 *
 */
public class WireRecvOutputStream extends OutputStream {

    private static final String TAG = WireRecvOutputStream.class.getSimpleName();

    private InputBuffer buffer = new InputBuffer();
    private IMessage receivedMessage;
    private File tempFolder;
    private FileReceiver fileReceiver;

    /**
     * @param tempFolder
     */
    public WireRecvOutputStream(File tempFolder) {
        //for incoming files
        this.tempFolder = tempFolder;
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int oneByte) throws IOException {
        buffer.write(oneByte);
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] bytes, int offset, int count) throws IOException {
        buffer.write(bytes, offset, count);
    }

    /**
     * Process and consume one message contained in the input buffer.  This will modify the contents of the
     * input buffer when successful and when an error is occurred (the message in error is thrown away).
     * 
     * TODO: this needs better error handling.
     * 
     * @return True if a message was processed, false if not
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws IOException 
     */
    public boolean attemptReceive() throws IOException {
        boolean received = false;
        
        try {
            InputStream is = buffer.getInputStream();
            //NOTE: we must keep the file format for multiple calls, as we expect to receive the file in parts
            //...this is different from the message format, where we'll try and deserialize and throw an exception
            // if its not available
            if (this.fileReceiver != null) {
                received = this.fileReceiver.receive(is);
            } else {
                MessageFormat format = new MessageFormat();
                format.deserialize(is);
                //TODO: consume the bytes in buffer
                this.receivedMessage = format.getMessage();
                if (!isFileMessage(this.receivedMessage)) {
                    //not a file message, we're done
                    received = true;
                } else {
                    //otherwise, we want to attempt to read a file if the message is processed and it is a file message
                    this.fileReceiver = new FileReceiver((IFileMessage) this.receivedMessage, this.tempFolder);
                    //receive any file data that happens to be in the 
                    if (is.available() > 0) {
                        received = this.fileReceiver.receive(is);
                    }
                }
            }
            buffer.consume();
            if (LogUtil.isLogAvailable()) {
                if (buffer.size() > 0) {
                    Log.v(TAG, "Residual buffer data: " + buffer.size()
                            + " bytes left in buffer");
                }
            }

        } catch (MessageNotCompleteException e) {
            //fall thru
        }
        return received;
    }

    /**
     * @param message
     * @return
     */
    private boolean isFileMessage(IMessage message) {
        return message instanceof IFileMessage;
    }

    /**
     * @return
     */
    public IMessage getReceivedMessage() {
        return this.receivedMessage;
    }
}
