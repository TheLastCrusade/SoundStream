package com.lastcrusade.fanclub.net;

import java.io.IOException;

public class UnableToCreateSocketException extends IOException {

    public UnableToCreateSocketException(Throwable tr) {
        super(tr);
    }
}