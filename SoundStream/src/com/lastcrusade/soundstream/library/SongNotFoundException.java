package com.lastcrusade.soundstream.library;

import java.io.IOException;

public class SongNotFoundException extends IOException {

    public SongNotFoundException(String message) {
        super(message);
    }
}
