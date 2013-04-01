package com.lastcrusade.soundstream.net.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    /**
     * Min size in bytes to read in before flushing to file.
     * Used for the incoming buffer, to avoid frequent writes to disk
     * 
     */
    private static final int MIN_BYTES_READ_IN = 102400;
    
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
    private static final int MAX_WRITE_SIZE_BYTES = 1024;
    private byte[] outBytes = new byte[MAX_WRITE_SIZE_BYTES];

    private File tempFolder;

    private int fileBytesLeft;

    private FileOutputStream inFileStream;

    private FileInputStream outFileStream;

    private String outFilePath;

    private boolean canLog;
    
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
    public int serializeMessage(IMessage message) throws IOException {

        //serialize the message into a separate buffer
        ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();
        message.serialize(messageBuffer);
        
        //build the output message
        byte[] classBytes = message.getClass().getCanonicalName().getBytes();
        int start = outputBuffer.size();
        //write the length
        writeLength(outputBuffer, messageBuffer.size() + classBytes.length + VERSION_LEN + 1);
        writeVersion(outputBuffer);
        //write the class name and end of class char
        // (this is used to reconstruct the message on the remote side)
        outputBuffer.write(classBytes);
        outputBuffer.write(END_OF_CLASS_CHAR);
        //write the message
        outputBuffer.write(messageBuffer.toByteArray());

        //if this is a file message, open the file and prepare it for the write operation
        if (isFileMessage(message)) {
            this.outFilePath   = ((IFileMessage)message).getFilePath();
            openOutFile();
        }
        
        //return the whole message size...note this isnt written until
        // writeToOutputStream is called.
        return (outputBuffer.size() - start) + (this.outFileStream != null ? this.outFileStream.available() : 0);
    }

    /**
     * @param message
     * @return
     */
    private boolean isFileMessage(IMessage message) {
        return message instanceof IFileMessage;
    }

    /**
     * Clear the output buffer of all messages.
     * 
     */
    public void reset() {
        outputBuffer.reset();
        closeOutFile();
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
        boolean readingFile = false;
        do {
            //always check to see if we have more message data waiting...this is so we can process
            // grouped/batched messages without having to wait on the call to readNext
            if (inputBuffer.size() > 0) {
                //if we need to, consume the message length (to make sure we read until we have a complete message)
                if (isWaitingForNewMessage()) {
                    this.messageLength = readAndConsumeLength();
                    this.receivedMessage = null;
                    if (this.canLog) {
                        Log.i(TAG, "Receiving " + this.messageLength + " byte message");
                    }
                }
                //check to see if we can process this message
                if (this.messageLength > 0 && inputBuffer.size() >= this.messageLength && this.receivedMessage == null) {
                    processed = processAndConsumeMessage();
                    //we want to attempt to read a file if the message is processed and it is a file message
                    readingFile = processed && isFileMessage(this.receivedMessage);
                }
                if (readingFile) {
                    processed = readAndConsumeFile();
                    readingFile = !processed;
                }
                if (this.canLog) {
                    Log.d(TAG, "Residual buffer data: " + inputBuffer.size() + " bytes left in buffer");
                }
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
     * True if we're waiting for a new message, false if we're currently processing a message.
     * @return
     */
    private boolean isWaitingForNewMessage() {
        return this.messageLength <= 0 && inputBuffer.size() >= SIZE_LEN && this.inFileStream == null;
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
     * Read and consume an incoming file.  This may be
     * read in parts if it is a large file.
     * 
     * fileLength will keep track of the number of bytes
     * left to read...when it == 0, the file is read completely
     * 
     * @return
     * @throws IOException
     */
    private boolean readAndConsumeFile() throws IOException {
        if (this.fileBytesLeft <= 0 && inputBuffer.size() >= SIZE_LEN) {
            this.fileBytesLeft = readAndConsumeLength();
            //write the file data to a temporary file...this is so we don't need to hold the data
            // in memory, and instead can just pass around a file path
            openRandomInFile();
        }
        
        boolean readComplete = isInFileComplete();
        int available = inputBuffer.size();
        if (!readComplete && isInFileBufferFilled()) {
            byte[] bytes = inputBuffer.toByteArray();
            int read = Math.min(available,  this.fileBytesLeft);
            writeInFileData(bytes, read);
            available -= read;
            inputBuffer.reset();
            if (available > 0) {
                inputBuffer.write(bytes, read, available);
            }
            readComplete = isInFileComplete();
        }
        
        if (readComplete) {
            closeInFile();
        }
        return readComplete;
    }

    private boolean isInFileBufferFilled() {
        int available = inputBuffer.size();
        return available > 0 && available >= Math.min(MIN_BYTES_READ_IN, this.fileBytesLeft);
    }

    /**
     * Open a random file and initialize the incoming file stream.
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void openRandomInFile() throws IOException, FileNotFoundException {
        File outFile = createRandomTempFile();
        ((IFileMessage)this.receivedMessage).setFilePath(outFile.getCanonicalPath());
        this.inFileStream = new FileOutputStream(outFile);
    }

    /**
     * Close the incoming file stream.
     * 
     * @throws IOException
     */
    private void closeInFile() {
        try {
            this.inFileStream.close();
        } catch (Exception e) {
            //don't care, we're closing
        } finally {
            this.inFileStream = null;
        }
    }

    /**
     * Test to see if the incoming file is fully loaded
     * @return
     */
    private boolean isInFileComplete() {
        return this.inFileStream != null && this.fileBytesLeft == 0;
    }
    
    /**
     * Write bytes to the incoming file stream, and decrement
     * the fileBytesLeft variable.
     * 
     * @param bytes
     * @param read
     * @throws IOException
     */
    private void writeInFileData(byte[] bytes, int read) throws IOException {
        if (this.canLog) {
            Log.d(TAG, "Writing " + read + " bytes to incoming file");
        }
        this.inFileStream.write(bytes, 0, read);
        this.fileBytesLeft -= read;
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
                if (this.canLog) {
                    Log.wtf(TAG, "Received message '" + messageName + "', but it does not implement IMessage");
                }
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
        // is, but I'd like to leave this in until I finish with all of the
        // transfer song debugging -- Jesse Rosalia, 03/24/13
        // int written = 0;
        // for (int bufPos = 0; bufPos < qe.bytes.length; bufPos += written) {
        //      int writeSize = Math.min(qe.bytes.length - bufPos, maxWriteSize);
        //      Log.d(TAG, "Writing " + writeSize + " bytes...");
        //      this.outStream.write(qe.bytes, bufPos, writeSize);
        //      written = writeSize;
        //      try {
        //          Thread.sleep(10);
        //      } catch (InterruptedException e) {
        // }
        outStream.write(outputBuffer.toByteArray());
        if (!isOutFileFinished()) {
            writeLength(outStream, this.outFileStream.available());
            int read;
            while ((read = this.outFileStream.read(outBytes)) > 0) {
                outStream.write(outBytes, 0, read);
            }
            openOutFile();
//            this.outFileStream.reset();
        }
    }

    private void closeOutFile() {
        try {
            this.outFileStream.close();
        } catch (Exception e) {
            //dont care, we're closing
        } finally {
            this.outFileStream = null;
            this.outFilePath = null;
        }
    }

    private boolean isOutFileFinished() throws IOException {
        return this.outFileStream == null || this.outFileStream.available() <= 0;
    }

    private void openOutFile() throws FileNotFoundException {
        this.outFileStream = new FileInputStream(this.outFilePath);
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
