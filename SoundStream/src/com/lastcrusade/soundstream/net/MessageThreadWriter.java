package com.lastcrusade.soundstream.net;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.PriorityQueue;

import android.content.Context;
import android.util.Log;

import com.lastcrusade.soundstream.net.message.IFileMessage;
import com.lastcrusade.soundstream.net.message.IMessage;
import com.lastcrusade.soundstream.net.message.Messenger;
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
        private int messageNo;
        private int score;
        public IMessage message;
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
    }

    public void enqueue(int messageNo, IMessage message) {
        QueueEntry qe = new QueueEntry();
        qe.messageNo = messageNo;
        qe.score     = messageNo * (TransferSongMessage.class.isAssignableFrom(message.getClass()) ? 100 : 1);
        qe.message   = message;
        queue.add(qe);
    }

    public void writeOne() throws IOException {
        QueueEntry qe = queue.poll();
        if (qe != null) {
            int len = messenger.serializeMessage(qe.message);
            Log.i(TAG, "Message " + qe.messageNo + " written, it's a " + qe.message.getClass().getSimpleName() + ", " + len + " bytes in length");
            messenger.writeToOutputStream(outStream);
            messenger.reset();
            Log.i(TAG, "Message " + qe.messageNo + " finished writing");
        }
    }
}
