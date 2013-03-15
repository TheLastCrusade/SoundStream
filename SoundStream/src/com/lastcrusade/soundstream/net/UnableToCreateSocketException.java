package com.lastcrusade.soundstream.net;

import java.io.IOException;

public class UnableToCreateSocketException extends IOException {

    public UnableToCreateSocketException(Throwable tr) {
        super(tr);
    }
}
