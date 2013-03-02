package com.lastcrusade.fanclub.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An abstract message class for messages that send and receive data.  This class contains
 * helper methods to read and write basic data types that may be sent.
 * 
 * @author Jesse Rosalia
 *
 */
public abstract class ADataMessage implements IMessage {

    protected String readString(InputStream input) throws IOException {
        int length = readInteger(input);
        //TODO: should put an upper bound here, and use a ByteArrayOutputStream to accumulate bytes
        byte[] buffer = new byte[length];
        input.read(buffer, 0, length);
        return new String(buffer);
    }

    protected int readInteger(InputStream input) throws IOException {
        int value = input.read();
        value    |= (input.read() << 8);
        value    |= (input.read() << 16);
        value    |= (input.read() << 24);
        
        return value;
    }

    protected void writeString(String string, OutputStream output) throws IOException {
        byte[] bytes = string.getBytes();
        writeInteger(bytes.length, output);
        output.write(bytes);
    }

    protected void writeInteger(int integer, OutputStream output) throws IOException {
        output.write(integer         & 0xFF);
        output.write((integer >> 8)  & 0xFF);
        output.write((integer >> 16) & 0xFF);
        output.write((integer >> 24) & 0xFF);
        
    }

}
