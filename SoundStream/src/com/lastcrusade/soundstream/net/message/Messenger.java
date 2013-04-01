package com.lastcrusade.soundstream.net.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

import android.util.Log;

/**
 * This class prepares message data to be sent or received.  It implements the following simple protocol:
 *  4 byte length of the message, not including the length (using java.nio.ByteBuffer)
 *  String canonical class name
 *  \n character
 *  Message data
 *  ...
 *  
 * This protocol supports stacking multiple messages (in that same format) into the same stream.  Each message will
 * have a length that describes that message.
 * 
 * @author Jesse Rosalia
 *
 */
public class Messenger {

    private static final char END_OF_CLASS_CHAR = '\n';

    private static final String TAG = Messenger.class.getName();

    //package protected, so they can be accessed from the unit test
    
    static final int SIZE_LEN = 4;
    static final int VERSION_LEN = 4;

    static final int MESSENGER_VERSION = 1;


    private IMessage receivedMessage;

    private int messageLength;

    private ByteArrayOutputStream inputBuffer = new ByteArrayOutputStream();

    private ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();

    private static final int MAX_READ_SIZE = 1024*1024;
    private byte[] inBytes = new byte[MAX_READ_SIZE];

    private static final int MAX_WRITE_SIZE = 1024;
    private byte[] outBytes = new byte[MAX_WRITE_SIZE];

    private File tempFolder;

    private int fileLength;

    private File outFile;

    private FileOutputStream inFileStream;

    private FileInputStream outFileStream;
    
    public Messenger(File tempFolder) {
        this.tempFolder = tempFolder;
    }
    
    /**
     * Serialize a message into the output buffer.  This will append to the output
     * buffer, to stack multiple messages next to each other.  See clearOutputBuffer
     * to clear this buffer.
     * 
     * @param message
     * @throws IOException
     */
    public int serializeMessage(IMessage message) throws IOException {

        ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();
        message.serialize(messageBuffer);
        
        byte[] classBytes = message.getClass().getCanonicalName().getBytes();
        int start = outputBuffer.size();
        //write the length
        writeLength(outputBuffer, messageBuffer.size() + classBytes.length + 1);
        writeVersion(outputBuffer);
        //write the class name and end of class char
        // (this is used to reconstruct the message on the remote side)
        outputBuffer.write(classBytes);
        outputBuffer.write(END_OF_CLASS_CHAR);
        //write the message
        outputBuffer.write(messageBuffer.toByteArray());

        //if this is a file message, open the file and prepare it for the write operation
        if (message instanceof IFileMessage) {
            this.outFileStream = new FileInputStream(((IFileMessage)message).getFilePath());
        }
        
        //return the whole message size...note this isnt written until
        // writeToOutputStream is called.
        return (outputBuffer.size() - start) + (this.outFileStream != null ? this.outFileStream.available() : 0);
    }

    /**
     * Clear the output buffer of all messages.
     * 
     */
    public void reset() {
        outputBuffer.reset();
        outFileStream = null;
    }

//    /**
//     * Get the output bytes for this messenger.  This should contain all of the serialized messages
//     * since the last time the messenger was cleared.
//     * 
//     * NOTE: this method will not clear the messenger itself.
//     * 
//     * @return
//     */
//    public byte[] getOutputBytes() {
//        return outputBuffer.toByteArray();
//    }
//
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
        boolean firstTime = true;

        boolean processed = false;
        boolean readingFile = false;
        do {
            //read a chunk at a time...the buffer size was determined through trial and error, and
            // could be optimized more.
            //NOTE: this is so input.read can block, and will throw an exception when the connection
            // goes down.  this is the only way we'll get a notification of a downed client
            if (!firstTime) {
                int read = input.read(inBytes);
                if (read > 0) {
                    inputBuffer.write(inBytes, 0, read);
                } else {
                    inputBuffer.write(inBytes);
                }
            }

//            Log.d(TAG, read + " bytes read into buffer");

            if (readingFile) {
                processed = processAndConsumeFile();
                readingFile = !processed;
            } else {
                //if we need to, consume the message length (to make sure we read until we have a complete message)
                if (this.messageLength <= 0 && inputBuffer.size() >= SIZE_LEN) {
                    this.messageLength = readAndConsumeLength();
                    Log.i(TAG, "Receiving " + this.messageLength + " byte message");
                }
                //check to see if we can process this message
                if (this.messageLength > 0 && inputBuffer.size() >= this.messageLength) {
                    processed = processAndConsumeMessage();
                }
                if (processed && this.receivedMessage instanceof IFileMessage) {
                    processed = processAndConsumeFile();
                    readingFile = !processed;
                }
            }
            firstTime = false;
//            Log.d(TAG, "Residual buffer data: " + inputBuffer.size() + " bytes left in buffer");
            //loop back around if we havent processed a message yet
        } while (!processed);
        return processed;
    }

    /**
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
        
    
    private boolean processAndConsumeFile() throws IOException {
        if (this.fileLength <= 0 && inputBuffer.size() >= SIZE_LEN) {
            this.fileLength = readAndConsumeLength();
            //write the file data to a temporary file...this is so we don't need to hold the data
            // in memory, and instead can just pass around a file path
            File outFile = createRandomTempFile();
            ((IFileMessage)this.receivedMessage).setFilePath(outFile.getCanonicalPath());
            this.inFileStream = new FileOutputStream(outFile);
        }
        
        boolean processed = this.outFileStream != null && this.fileLength == 0;
        int available = inputBuffer.size();
        if (!processed && available > 0) {
            byte[] bytes = inputBuffer.toByteArray();
            int read = Math.min(available,  this.fileLength);
            this.inFileStream.write(bytes, 0, read);
            this.fileLength -= read;
            available -= read;
            inputBuffer.reset();
            if (available > 0) {
                //TODO: NEED TO TEST THIS
                inputBuffer.write(bytes, read, available);
            }
            processed = this.fileLength == 0;
        }
        
        if (processed) {
            this.inFileStream.close();
        }
        return processed;
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
    private boolean processAndConsumeMessage() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
        boolean processed = false;
        //REVIEW: character encoding issues may arise, but since we're controlling the class names
        // we should be able to decide how to handle these
        byte[] bytes = inputBuffer.toByteArray();
        try {
            int start = 0;
            ByteBuffer bb = ByteBuffer.wrap(bytes, 0, VERSION_LEN);
            int messengerVersion = bb.getInt();
            //TODO: actually do something with the messengerVersion
            start += VERSION_LEN;
            int nameEnd = start;
            for (; nameEnd < bytes.length && bytes[nameEnd] != END_OF_CLASS_CHAR; nameEnd++) {}
            String messageName = new String(bytes, start, nameEnd - start);
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
        } finally {
            //consume this message either way
            int bufferLen = inputBuffer.size();
            inputBuffer.reset();
            inputBuffer.write(bytes, this.messageLength, bufferLen - this.messageLength);
            this.messageLength = 0;
        }
        return processed;
    }

    /**
     * Get the last received message processed by this messenger.
     * 
     * @return
     */
    public IMessage getReceivedMessage() {
        return receivedMessage;
    }
    
    
    
    /**
     * Helper method to create a temporary file.
     * 
     * TODO: Android doesn't really have temporary files...it will proactively clear the cache folder,
     * but we should be better citizens and clean up after ourself.  This is not an immediate (read: alpha)
     * concern, because we consume all cache files in PlaylistDataManager (which will clean up after itself)
     * but before this gets to final, we should make sure all bases are covered.
     * 
     * @param message
     * @return
     * @throws IOException
     */
    private File createRandomTempFile()
            throws IOException {
        String filePrefix = UUID.randomUUID().toString().replace("-", "");
//        int inx = message.getSongFileName().lastIndexOf(".");
//        String extension = message.getSongFileName().substring(inx + 1);
        String extension = "dat";
        File outputFile = File.createTempFile(filePrefix, extension, tempFolder);
        return outputFile;
    }

    public void writeToOutputStream(OutputStream outStream) throws IOException {
        // TODO: this may or may not be needed...for now it does not appear it
        // is, but I'd like to leave this in until I
        // finish with all of the transfer song debugging -- Jesse Rosalia,
        // 03/24/13
        // int written = 0;
        // for (int bufPos = 0; bufPos < qe.bytes.length; bufPos += written) {
        // int writeSize = Math.min(qe.bytes.length - bufPos, maxWriteSize);
        // Log.d(TAG, "Writing " + writeSize + " bytes...");
        // this.outStream.write(qe.bytes, bufPos, writeSize);
        // written = writeSize;
        // try {
        // Thread.sleep(10);
        // } catch (InterruptedException e) {
        // }
        // }
        outStream.write(outputBuffer.toByteArray());
        if (this.outFileStream != null && this.outFileStream.available() > 0) {
            writeLength(outStream, this.outFileStream.available());
            int read;
            while ((read = this.outFileStream.read(outBytes)) > 0) {
                outStream.write(outBytes, 0, read);
            }
//            this.outFileStream.reset();
        }
    }

    /**
     * @param outStream
     * @param len
     * @throws IOException
     */
    private void writeLength(OutputStream outStream, int len)
            throws IOException {
        byte[] bytes = new byte[4];
        ByteBuffer bb = ByteBuffer.wrap(bytes, 0, 4);
        bb.putInt(len);
        outStream.write(bytes);
    }
    private void writeVersion(OutputStream outStream)
            throws IOException {
        byte[] bytes = new byte[4];
        ByteBuffer bb = ByteBuffer.wrap(bytes, 0, 4);
        bb.putInt(MESSENGER_VERSION);
        outStream.write(bytes);
    }
}
