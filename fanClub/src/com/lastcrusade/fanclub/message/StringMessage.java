package com.lastcrusade.fanclub.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A simple implementation of IMessage for sending strings back and forth.
 * 
 * @author Jesse Rosalia
 *
 */
public class StringMessage implements IMessage {

    private static final int STRING_MESSAGE_VERSION = 1;
    private String string;

    @Override
    public int getMessageVersion() {
        return STRING_MESSAGE_VERSION;
    }

    @Override
    public void deserialize(InputStream input) throws IOException {
        byte[] bytes = new byte[1024];
        int read = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while ((read = input.read(bytes)) > 0) {
            out.write(bytes, 0, read);
        }
        this.setString(out.toString());
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        output.write(this.getString().getBytes());
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
