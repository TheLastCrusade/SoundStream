package com.lastcrusade.fanclub.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FindNewFansMessage implements IMessage {

    @Override
    public void deserialize(InputStream input) throws IOException {
        //nothing to do
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        //nothing to do
    }
}
