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

package com.lastcrusade.soundstream.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.PriorityQueue;

import android.util.Log;

import com.lastcrusade.soundstream.net.message.IMessage;
import com.lastcrusade.soundstream.net.message.TransferSongMessage;
import com.lastcrusade.soundstream.net.wire.Messenger;
import com.lastcrusade.soundstream.util.LogUtil;

/**
 * A class to manage writing messages from the MessageThread.  This class
 * maintains a priority queue the prioritizes command messages over transfer
 * data messages, to ensure the user interface is snappy.
 * 
 * @author Jesse Rosalia
 *
 */
public class MessageThreadWriter {

    /**
     * The normal multiplier uses the score as is.
     */
    private static final int NORMAL_SCORE_MULTIPLIER = 1;

    /**
     * A multiplier to lower the priority of transfer song messages,
     * so control messages will jump the queue to be sent quicker.
     */
    private static final int TRANSFER_SONG_SCORE_MULTIPLIER = 100;

    private static String TAG = MessageThreadWriter.class.getSimpleName();

    /**
     * Maximum size in bytes to write to a socket at a time.
     * 
     */
    private byte[] outBytes;

    class QueueEntry {
        private int messageNo;
        private int score;
        public Class<? extends IMessage> messageClass;
        public InputStream messageStream;
    }

    PriorityQueue<QueueEntry> queue = new PriorityQueue<QueueEntry>(11, new Comparator<QueueEntry>() {

        @Override
        public int compare(QueueEntry lhs, QueueEntry rhs) {
            return lhs.score - rhs.score;
        }
    });

    private OutputStream outStream;

    private Messenger messenger;
    
    public MessageThreadWriter(Messenger messenger, OutputStream outStream) {
        this.outStream = outStream;
        this.messenger = messenger;
        this.outBytes = new byte[messenger.getSendPacketSize()];
    }

    public void enqueue(int messageNo, IMessage message) throws IOException {
        QueueEntry qe = new QueueEntry();
        qe.messageNo     = messageNo;
        qe.score         = computeMessageScore(messageNo, message);
        qe.messageClass  = message.getClass();
        qe.messageStream = messenger.serializeMessage(message);
        if (LogUtil.isLogAvailable()) {
            Log.i(TAG, "Message " + qe.messageNo + " enqueued, it's a "
                        + qe.messageClass.getSimpleName() + ", "
                        + qe.messageStream.available() + " bytes in length");
        }
        queue.add(qe);
    }

    /**
     * Compute the score of the message.  Messages are held in a min-first priority
     * queue.  This, along with the partial transfer of song messages, allows commands
     * such as play or add to "jump ahead" of any transferring song data, thus
     * maintaining a usable system while shipping around large amounts of data.
     * 
     * @param messageNo
     * @param message
     * @return
     */
    private int computeMessageScore(int messageNo, IMessage message) {
        int mult = NORMAL_SCORE_MULTIPLIER;
        //transfer song has a higher score, so it is a lower priority message
        if (TransferSongMessage.class.isAssignableFrom(
                                message.getClass())) { 
            mult = TRANSFER_SONG_SCORE_MULTIPLIER;
        }
        //compute the score by multiplying the message no by a multiplier
        // to push some messages down in priority
        return messageNo * mult;
    }

    public boolean canWrite() {
        return !queue.isEmpty();
    }

    /**
     * Write one message (or part of a message) to the connected output stream.
     * 
     * This may write a partial message, if the message is bigger than our output buffer.
     * If this is the case, the queue entry is readded to the priority queue and given
     * another chance to write more data.  This is repeated until the message is
     * completely sent.  Note that this means other, higher priority messages may
     * jump the line while this message is in the middle of sending its data.  This
     * is ok, and how we allow the system to send command messages when long transfer
     * messages are in progress.
     * 
     * @throws IOException
     */
    public void writeOne() throws IOException {
        QueueEntry qe = queue.poll();
        if (qe != null) {
            int read = qe.messageStream.read(outBytes);
            if (LogUtil.isLogAvailable()) {
                Log.d(TAG, "Message " + qe.messageNo + " written, it's a " + qe.messageClass.getSimpleName() + ", " + read + " bytes in length");
            }
            outStream.write(outBytes, 0, read);
            int left = qe.messageStream.available();
            //if there are bytes left to write, add this message back into the queue
            // to write at the next opportunity
            if (left > 0) {
                if (LogUtil.isLogAvailable()) {
                    Log.d(TAG, "Message " + qe.messageNo + ", " + left + " bytes left to write");
                }
                queue.add(qe);
            } else {
                //otherwise, we're done
                if (LogUtil.isLogAvailable()) {
                    Log.i(TAG, "Message " + qe.messageNo + " finished writing");
                }
            }
        }
    }
}
