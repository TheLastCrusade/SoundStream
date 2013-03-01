package com.lastcrusade.fanclub.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ConnectFansMessage extends ADataMessage {

    List<String> addresses = new ArrayList<String>();
    
    /**
     * Default constructor, required for Messenger.  All other users should use
     * the other constructor.
     * 
     */
    ConnectFansMessage() {
    }

    public ConnectFansMessage(List<String> addresses) {
        this.addresses.addAll(addresses);
    }

    @Override
    public void deserialize(InputStream input) throws IOException {
        int numAddresses = readInteger(input);
        for (int ii = 0; ii < numAddresses; ii++) {
            addresses.add(readString(input));
        }
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        writeInteger(addresses.size(), output);
        for (String address : addresses) {
            writeString(address, output);
        }
    }
    
    public List<String> getAddresses() {
        return addresses;
    }
}
