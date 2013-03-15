package com.lastcrusade.soundstream.audio;

/**
 * A generic media player interface.  This will be used both to implement the actual media player, and
 * anything that acts as an interface into the media player (e.g. the play bar).
 * 
 * @author Jesse Rosalia
 *
 */
public interface IPlayer {

    public boolean isPlaying();
    
    public void play();

    public void pause();
    
    public void skip();

}
