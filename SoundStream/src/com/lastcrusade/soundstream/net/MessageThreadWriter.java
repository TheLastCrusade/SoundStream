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
        QueueEntry qe = queue.poll();
        if (qe != null) {
            Log.i(TAG, "Message " + qe.messageNo + " written, it's a " + qe.type.getSimpleName());
            this.outStream.write(qe.bytes);
            Log.i(TAG, "Message " + qe.messageNo + " finished writing");
        }
    }
}
