package com.lastcrusade.soundstream.net.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.lastcrusade.soundstream.model.SongMetadata;

public class SongAddedMessage extends ADataMessage{

    private SongMetadata song;
    
    public SongAddedMessage() {
        this(new SongMetadata());
    }
    
    public SongAddedMessage(SongMetadata song){
        this.song = song;
    }
    
    
    public SongMetadata getSong(){
        return song;
    }

    @Override
    public void deserialize(InputStream input) throws IOException {
        
        
    }

    @Override
    public void serialize(OutputStream output) throws IOException {
        // TODO Auto-generated method stub
        
    }
}
