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
import java.io.OutputStream;
import java.util.Comparator;
import java.util.PriorityQueue;

import android.util.Log;

import com.lastcrusade.soundstream.net.message.IMessage;
import com.lastcrusade.soundstream.net.message.TransferSongMessage;

/**
 * A class to manage writing messages from the MessageThread.  This class
 * maintains a priority queue the prioritizes command messages over transfer
 * data messages, to ensure the user interface is snappy.
 * 
 * @author Jesse Rosalia
 *
 */
public class MessageThreadWriter {

    private static String TAG = MessageThreadWriter.class.getSimpleName();

    class QueueEntry {
        private byte[] bytes;
        private int messageNo;
        private int score;
        public Class<?> type;
    }

    PriorityQueue<QueueEntry> queue = new PriorityQueue<QueueEntry>(11, new Comparator<QueueEntry>() {

        @Override
        public int compare(QueueEntry lhs, QueueEntry rhs) {
            return lhs.score - rhs.score;
        }
    });

    private OutputStream outStream;
    
    public MessageThreadWriter(OutputStream outStream) {
        this.outStream = outStream;
    }

    public void enqueue(int messageNo, Class<?> messageType, byte[] bytes) {
        QueueEntry qe = new QueueEntry();
        qe.bytes     = bytes;
        qe.messageNo = messageNo;
        qe.score     = messageNo * (TransferSongMessage.class.isAssignableFrom(messageType) ? 100 : 1);
        qe.type      = messageType;
        queue.add(qe);
    }

    public void writeOne() throws IOException {
        int maxWriteSize = 8192;
        QueueEntry qe = queue.poll();
        if (qe != null) {
            Log.i(TAG, "Message " + qe.messageNo + " written, it's a " + qe.type.getSimpleName() + ", " + qe.bytes.length + " bytes in length");
            //TODO: this may or may not be needed...for now it does not appear it is, but I'd like to leave this in until I
            // finish with all of the transfer song debugging -- Jesse Rosalia, 03/24/13
//            int written = 0;
//            for (int bufPos = 0; bufPos < qe.bytes.length; bufPos += written) {
//                int writeSize = Math.min(qe.bytes.length - bufPos, maxWriteSize);
//                Log.d(TAG, "Writing " + writeSize + " bytes...");
//                this.outStream.write(qe.bytes, bufPos, writeSize);
//                written = writeSize;
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                }
//            }
            outStream.write(qe.bytes);
            Log.i(TAG, "Message " + qe.messageNo + " finished writing");
        }
    }
}
