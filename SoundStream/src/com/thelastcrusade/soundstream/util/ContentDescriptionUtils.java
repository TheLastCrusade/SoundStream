package com.thelastcrusade.soundstream.util;

import com.thelastcrusade.soundstream.model.SongMetadata;

public class ContentDescriptionUtils {

    public static final String CREATE = "Create";
    public static final String CONNECT = "Connect";

    public static final String NAVIGATE_UP = "Navigate up";
    public static final String MUSIC_LIBRARY = "Music Library";
    public static final String PLAYLIST = "Playlist";
    public static final String NETWORK = "Network";
    public static final String ABOUT = "About";

    public static final String PLAY_PAUSE = "Play/Pause";
    public static final String SKIP = "Skip";

    public static final String ADD_TO_PLAYLIST = "Add To Playlist";

    public static String addToPlaylistAppendSongTitle(SongMetadata song) {
        return ADD_TO_PLAYLIST + "_" + song.getTitle();
    }
}
