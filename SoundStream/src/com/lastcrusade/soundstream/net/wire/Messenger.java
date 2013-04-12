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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;
import android.util.SparseArray;

import com.lastcrusade.soundstream.net.message.IFileMessage;
import com.lastcrusade.soundstream.net.message.IMessage;
import com.lastcrusade.soundstream.net.message.MessageFormat;

/**
 * This class is the main entry point to send and receive messages in Sound Stream.  It implements a protocol
 * that represent serializable messages and file data in a series of packets.  This protocol is constructed
 * to allow multiple messages to be in transit at a time.  This means that control messages can be sent
 * while also transmitting large amounts of file data, which with proper prioritization should enable
 * the application to feel responsive and still accomplish the job of moving music around the network.
 * 
 * See MessageFormat and PacketFormat for specific information about those formats.
 * 
 * This protocol supports stacking multiple messages (in that same format) into the same stream.  Each message will
 * have a length that describes that message.
 * 
 * @author Jesse Rosalia
 *
 */
public class Messenger {

    private static final String TAG = Messenger.class.getSimpleName();

    //package protected, so they can be accessed from the unit test
    
    private static final int SIZE_LEN = 4;

    private int messageLength;

    private ByteArrayOutputStream inputBuffer = new ByteArrayOutputStream();

    private SparseArray<WireRecvOutputStream> activeTransfers   = new SparseArray<WireRecvOutputStream>();

    private List<IMessage>                    receivedMessages = new LinkedList<IMessage>();

    //    private ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();

    /**
     * Maximum size in bytes to read from a socket at a time.
     * 
     */
    private static final int MAX_READ_SIZE_BYTES = 1024;
    private byte[] inBytes = new byte[MAX_READ_SIZE_BYTES];

    /**
     * Maximum size in bytes to write to a socket at a time.
     * 
     */
   // private static final int MAX_WRITE_SIZE_BYTES = 1024;
   // private byte[] outBytes = new byte[MAX_WRITE_SIZE_BYTES];

    private boolean canLog;

    private int sendPacketSize;

    private int nextMessageNo = 0;

    private File tempFolder;
    
    public Messenger(File tempFolder) {
        this.tempFolder = tempFolder;
        //test to see if we can log (i.e. if the logger exists on the classpath)
        //...this is required because we run unit tests using the android junit runner, which will remove
        // android classes, such as Log, from the classpath.
        try {
            Log.v(TAG, "Creating messenger");
            this.canLog = true;
        } catch (NoClassDefFoundError e) {
            this.canLog = false;
        }
    }
    
    /**
     * Serialize a message into the output buffer.  This will append to the output
     * buffer, to stack multiple messages next to each other.  See clearOutputBuffer
     * to clear this buffer.
     * 
     * @param message
     * @throws IOException
     */
    public InputStream serializeMessage(IMessage message) throws IOException {
        MessageFormat format = new MessageFormat(message);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        format.serialize(baos);
        // if this is a file message, open the file and prepare it for the write
        // operation
        InputStream fileStream = null;
        if (isFileMessage(message)) {
            FileFormat fileFormat = new FileFormat((IFileMessage) message, this.tempFolder);
            fileStream = fileFormat.getWrappedInputStream();
        }
        return new WireSendInputStream(this.sendPacketSize, this.nextMessageNo++, new ByteArrayInputStream(baos.toByteArray()), fileStream);
    }

    /**
     * @param message
     * @return
     */
    private boolean isFileMessage(IMessage message) {
        return message instanceof IFileMessage;
    }

    /**
     * Deserialize a message in the input stream, and store the result in the receivedMessage field.
     * This may be called multiple times with partial messages (in case the message is not all here yet).
     * 
     * Only one received message may be held at a time, so be prepared to call getReceivedMessage if this
     * method returns true.
     * 
     * This is designed to block until a full message is received, and will throw an exception if the
     * socket is closed unexpectedly.
     * 
     * @param input
     * @return
     * @throws Exception If the message class does not exist, or is not defined properly, or
     * if the stream closes prematurely.
     */
    public boolean deserializeMessage(InputStream input) throws Exception {
        boolean processed = false;
        do {
            //always check to see if we have more message data waiting...this is so we can process
            // grouped/batched messages without having to wait on the call to readNext
            if (inputBuffer.size() > 0) {
                //check to see if we can process this message
                processed = processAndConsumePacket();
            }
            
            //if we don't have a message processed, attempt to read new data and loop back around
            if (!processed) {
                readNext(input);
            }
            //loop back around if we havent processed a message yet
        } while (!processed);
        return processed;
    }

    /**
     * Process and consume a packet.  This will look for any active message transfers
     * (or create one if needed) and append the bytes to that message.
     * @return 
     * @return
     * @throws IOException 
     */
    private boolean processAndConsumePacket() throws IOException {
        boolean received = false;
        //REVIEW: character encoding issues may arise, but since we're controlling the class names
        // we should be able to decide how to handle these
        PacketFormat packet = new PacketFormat();
        byte[] bytes = inputBuffer.toByteArray();
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            packet.deserialize(bais);
            //consume this message either way
            int bufferLen = inputBuffer.size();
            inputBuffer.reset();
            byte[] bytes2 = new byte[1024];
            int read;
            while ((read = bais.read(bytes2)) > 0) {
                inputBuffer.write(bytes2, 0, read);
            }
//            inputBuffer.write(bytes, packet.getPacketLength(), bufferLen - packet.getPacketLength());
            this.messageLength = 0;

            WireRecvOutputStream transfer = this.activeTransfers.get(packet.getMessageNo());
            if (transfer == null) {
                transfer = new WireRecvOutputStream(this.tempFolder);
                this.activeTransfers.append(packet.getMessageNo(), transfer);
            }

            transfer.write(packet.getBytes());
            received = transfer.attemptReceive();
            //if we've received the full message, remove it from our active
            // transfer array and add the underlying message to the received messages list
            if (received) {
                this.activeTransfers.remove(packet.getMessageNo());
                this.receivedMessages.add(transfer.getReceivedMessage());
            }
        } catch (MessageNotCompleteException ex) {
            //fall thru
        } finally {
        }
        return received;
    }

    /**
     * True if we're waiting for a new message, false if we're currently processing a message.
     * @return
     */
    private boolean isWaitingForNewMessage() {
        return this.messageLength <= 0 && inputBuffer.size() >= SIZE_LEN;
    }

    /**
     * Read the next set of bytes from the input stream.
     * 
     * NOTE: This will block until data is available, and may throw
     * an exception if the stream is closed while reading.
     * 
     * @param input
     * @throws IOException
     */
    private void readNext(InputStream input) throws IOException {
        //read a chunk at a time...the buffer size was determined through trial and error, and
        // could be optimized more.
        //NOTE: this is so input.read can block, and will throw an exception when the connection
        // goes down.  this is the only way we'll get a notification of a downed client
        int read = input.read(inBytes);
        if (read > 0) {
            inputBuffer.write(inBytes, 0, read);
        } else {
            inputBuffer.write(inBytes);
        }
    }

    /**
     * Read the first 4 bytes off of the input buffer, and remove those bytes
     * from the buffer.
     * 
     * NOTE: This assumes that there is at least 4 bytes (SIZE_LEN bytes) in
     * the buffer.
     * 
     * @return
     */
    private int readAndConsumeLength() {
        byte[] bytes = inputBuffer.toByteArray();
        ByteBuffer bb = ByteBuffer.wrap(bytes, 0, SIZE_LEN);
        //this actually consumes the first 4 bytes (removes it from the stream)
        inputBuffer.reset();
        inputBuffer.write(bytes, SIZE_LEN, bytes.length - SIZE_LEN);
        return bb.getInt();
    }

    /**
     * Get the last received message processed by this messenger.
     * 
     * @return
     */
    public List<IMessage> getReceivedMessages() {
        List<IMessage> toReturn = receivedMessages;
        receivedMessages = new LinkedList<IMessage>();
        return toReturn;
    }

    /**
     * @param maxWriteSizeBytes
     */
    public void setSendPacketSize(int sendPacketSize) {
        this.sendPacketSize = sendPacketSize;
    }
}
