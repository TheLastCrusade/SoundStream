package com.lastcrusade.fanclub.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public interface IMessage {

    /**
     * Deserialize a message that has been received in an input stream.  This will be
     * called if a message of this type is received.
     * 
     * NOTE: it can be assumed that this input stream has all required data and therefore
     * will not block.  The framework will ensure this.
     * 
     * @param input
     * @throws IOException 
     */
    public void deserialize(InputStream  input) throws IOException;

    public void serialize(  OutputStream output) throws IOException;
}
