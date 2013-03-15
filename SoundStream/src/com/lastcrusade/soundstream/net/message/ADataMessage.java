package com.lastcrusade.soundstream.net.message;

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
        if(length > 0) {
	        byte[] buffer = new byte[length];
	        input.read(buffer, 0, length);
	        return new String(buffer);
        }
        else {
        	return null;
        }
    }

    protected int readInteger(InputStream input) throws IOException {
        int value = input.read();
        value    |= (input.read() << 8);
        value    |= (input.read() << 16);
        value    |= (input.read() << 24);
        
        return value;
    }
    
    protected long readLong(InputStream input) throws IOException {
    	long value = input.read();
    	value 	  |= (input.read() << 8);
    	value 	  |= (input.read() << 16);
    	value 	  |= (input.read() << 24);
    	value 	  |= (input.read() << 32);
    	value 	  |= (input.read() << 40);
    	value 	  |= (input.read() << 48);
    	value 	  |= (input.read() << 56);
    	
    	return value;
    }

    protected void writeString(String string, OutputStream output) throws IOException {
        byte[] bytes = null;
    	
    	if(string == null) {
    		writeInteger(0, output);
        }
        else {
        	bytes = string.getBytes();
        	writeInteger(bytes.length, output);
        	output.write(bytes);
        }
    }

    protected void writeInteger(int integer, OutputStream output) throws IOException {
        output.write(integer         & 0xFF);
        output.write((integer >> 8)  & 0xFF);
        output.write((integer >> 16) & 0xFF);
        output.write((integer >> 24) & 0xFF);
    }
    
    protected void writeLong(long value, OutputStream output) throws IOException {
    	output.write((int)(value		 & 0xFF));
    	output.write((int)((value >> 8)  & 0xFF));
    	output.write((int)((value >> 16) & 0xFF));
    	output.write((int)((value >> 24) & 0xFF));
    	output.write((int)((value >> 32) & 0xFF));
    	output.write((int)((value >> 40) & 0xFF));
    	output.write((int)((value >> 48) & 0xFF));
    	output.write((int)((value >> 56) & 0xFF));
    }
}
