package com.lastcrusade.soundstream.net.message;

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
    private final String TAG = StringMessage.class.getName();
    private String string;

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
