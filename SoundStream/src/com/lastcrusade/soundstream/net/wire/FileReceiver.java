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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.util.Log;

import com.lastcrusade.soundstream.net.core.AComplexDataType;
import com.lastcrusade.soundstream.net.message.IFileMessage;
import com.lastcrusade.soundstream.util.LogUtil;

/**
 * This class manages receiving files.  It is not a serializable object
 * (e.g. we do not expect the whole thing to be read in at once).
 * Instead, we will receive file data one chunk at a time and will
 * write that data immediately to a temporary file.
 * 
 * @author Jesse Rosalia
 * 
 */
public class FileReceiver extends AComplexDataType {

    private static final String TAG = FileReceiver.class.getSimpleName();

    private IFileMessage message;

    private File tempFolder;

    private int fileBytesLeft;

    private OutputStream fileStream;
    
    private ByteArrayOutputStream fileBuffer = new ByteArrayOutputStream();

    private byte[] byteBuffer;
    
    /**
     * Min size in bytes to read in before flushing to file. Used for the
     * incoming buffer, to avoid frequent writes to disk
     * 
     */
    private static final int MIN_BYTES_READ_IN = 65536;

    private static final int READ_BUFFER_SIZE = 1024;
    /**
     * @param message
     * @param tempFolder
     */
    public FileReceiver(IFileMessage message, File tempFolder) {
        this.message = message;
        this.tempFolder = tempFolder;

        this.byteBuffer = new byte[READ_BUFFER_SIZE];
    }

    /**
     * @return
     * @throws FileNotFoundException
     */
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(message.getFilePath());
    }

    /**
     * Receive bytes for the incoming file. This will read all available bytes
     * on the input stream and write them to a temporary file, which will be
     * specified in the file message.
     * 
     * @param input
     *            An input stream with bytes to write to this file. Note that
     *            this assumes that the input stream only has bytes for this
     *            file, and will consume all bytes available in the stream.
     * @return
     * @throws IOException
     */
    public boolean receive(InputStream input)
            throws MessageNotCompleteException, IOException {

        if (this.fileBytesLeft <= 0 && input.available() >= SIZEOF_INTEGER) {
            this.fileBytesLeft = readInteger(input);
            // write the file data to a temporary file...this is so we don't
            // need to hold the data
            // in memory, and instead can just pass around a file path
            openRandomInFile();
        }

        int read;
        while ((read = input.read(byteBuffer)) > 0) {
            fileStream.write(byteBuffer, 0, read);
            this.fileBytesLeft -= read;
        }
        if (LogUtil.isLogEnabled()) {
            Log.d(TAG, "Writing " + read + " bytes to incoming file (" + this.fileBytesLeft + " bytes left)");
        }

        boolean readComplete = isInFileComplete();
        
//        if (isBufferFilled() || readComplete) {
//            flushBuffer();
//        }

        if (readComplete) {
            closeInFile();
        }
        return readComplete;
    }

    private boolean isBufferFilled() {
        int available = fileBuffer.size();
        return available > 0 && available >= Math.min(MIN_BYTES_READ_IN, this.fileBytesLeft);
    }

    private void flushBuffer() throws IOException {
        Log.d(TAG, "Writing " + fileBuffer.size() + " bytes to incoming file (" + this.fileBytesLeft + " bytes left)");

        fileStream.write(fileBuffer.toByteArray());
        fileBuffer.reset();
    }

    /**
     * Open a random file and initialize the incoming file stream.
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void openRandomInFile() throws IOException, FileNotFoundException {
        File outFile = createRandomTempFile();
        this.message.setFilePath(outFile.getCanonicalPath());
        this.fileStream = new FileOutputStream(outFile);
    }

    /**
     * Close the incoming file stream.
     * 
     * @throws IOException
     */
    private void closeInFile() {
        try {
            this.fileStream.close();
        } catch (Exception e) {
            // don't care, we're closing
        } finally {
            this.fileStream = null;
        }
    }

    /**
     * Test to see if the incoming file is fully loaded
     * 
     * @return
     */
    private boolean isInFileComplete() {
        return this.fileStream != null && this.fileBytesLeft == 0;
    }

    /**
     * Helper method to create a temporary file.
     * 
     * TODO: Android doesn't really have temporary files...it will proactively
     * clear the cache folder, but we should be better citizens and clean up
     * after ourself. This is not an immediate (read: alpha) concern, because we
     * consume all cache files in PlaylistDataManager (which will clean up after
     * itself) but before this gets to final, we should make sure all bases are
     * covered.
     * 
     * @param message
     * @return
     * @throws IOException
     */
    private File createRandomTempFile() throws IOException {
        String filePrefix = UUID.randomUUID().toString().replace("-", "");
        String extension = ".dat";
        File outputFile = File
                .createTempFile(filePrefix, extension, tempFolder);
        return outputFile;
    }

}
