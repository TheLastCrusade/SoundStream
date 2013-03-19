package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A message sent from a guest to the host
 * to find new guests for the network.
 * 
 * @author Jesse Rosalia
 *
 */
public class FindNewGuestsMessage implements IMessage {

    @Override
    public void deserialize(InputStream input) throws IOException {
        //nothing to do
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        //nothing to do
    }
}
