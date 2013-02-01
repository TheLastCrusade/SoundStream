package com.lastcrusade.fanclub.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import android.util.Log;

public class Messenger {

    private static final String TAG = "Messenger";

    private static final int SIZE_LEN = 4;

    private IMessage receivedMessage;

    private boolean messageStarted = false;
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private int messageLength;

    public OutputStream serializeMessage(IMessage message) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(message.getClass().getCanonicalName().getBytes());
        baos.write('\n');
        message.serialize(baos);
        return baos;
    }

    /**
     * NOTE: this may block if InputStream blocks on read, and doesnt support available properly
     * 
     * @param input
     * @return
     * @throws IOException
     */
    public boolean deserializeMessage(InputStream input) throws IOException {
        
        boolean processed = false;
        //read all we can
        while(input.available() > 0) {
            buffer.write(input.read());
        }

        //if we need to, consume the message length (to make sure we read until we have a complete message)
        if (this.messageLength <= 0 && buffer.size() >= SIZE_LEN) {
            byte[] bytes = buffer.toByteArray();
            ByteBuffer bb = ByteBuffer.wrap(bytes, 0, SIZE_LEN);
            //this actually consumes the first 4 bytes (removes it from the stream)
            buffer.reset();
            buffer.write(bytes, SIZE_LEN, bytes.length - SIZE_LEN);
            this.messageLength = bb.getInt();
        }
        //check to see if we can process this message
        if (this.messageLength > 0 && buffer.size() >= this.messageLength) {
            processed = processAndConsumeMessage();
        }
        return processed;
    }
    
    private boolean processAndConsumeMessage() {
        boolean processed = false;
        //REVIEW: character encoding issues may arise, but since we're controlling the class names
        // we should be able to decide how to handle these
        byte[] bytes = buffer.toByteArray();
        try {
            int nameEnd;
            for (nameEnd = 0; nameEnd < bytes.length && bytes[nameEnd] != '\n'; nameEnd++) {}
            String messageName = new String(bytes, 0, nameEnd);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes, nameEnd + 1, this.messageLength - (nameEnd + 1));

            Object obj = Class.forName(messageName).newInstance();
            if (obj instanceof IMessage) {
                //we've created the message...deserialize it and store it
                ((IMessage)obj).deserialize(bais);
                this.receivedMessage = ((IMessage)obj);
                processed = true;
            } else {
                //otherwise, it's a WTF
                Log.wtf(TAG, "Received message '" + messageName + "', but it does not implement IMessage");
            }
        } catch (InstantiationException e) {
            
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            
        } finally {
            //consume this message either way
            int bufferLen = buffer.size();
            buffer.reset();
            buffer.write(bytes, this.messageLength, bufferLen - this.messageLength);
            this.messageLength = 0;
        }
        return processed;
    }

    public IMessage getReceivedMessage() {
        return receivedMessage;
    }
}
