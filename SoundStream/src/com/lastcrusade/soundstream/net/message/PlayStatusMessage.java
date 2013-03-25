package com.lastcrusade.soundstream.net.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.lastcrusade.soundstream.model.SongMetadata;

import android.util.Log;


/**
 * TODO: Potential refactor
 * @theJenix believes that PlayStatus should be a boolean. 
 */
public class PlayStatusMessage extends ADataMessage {

    public static final String PLAY_MESSAGE = "Play";
    public static final String PAUSE_MESSAGE = "Pause";

    private final String TAG = PlayStatusMessage.class.getName();
    private String string = "";

    public PlayStatusMessage() {}

    public PlayStatusMessage(String playStatusMessage) {
        this(playStatusMessage, new SongMetadata());
    }

    public PlayStatusMessage(String playStatusMessage, SongMetadata currentSong) {
        if(playStatusMessage.equals(PLAY_MESSAGE) || playStatusMessage.equals(PAUSE_MESSAGE)) {
            this.setString(playStatusMessage);
		}
        else {
            Log.wtf(TAG, "Status msg passed not Play or Pause");
        }
    }

    @Override
    public void deserialize(InputStream input) throws IOException {
        byte[] bytes = new byte[1024];
        int read = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while((read = input.read(bytes)) > 0) {
            out.write(bytes, 0, read);
        }
        this.setString(out.toString());
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        output.write(getString().getBytes());
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
