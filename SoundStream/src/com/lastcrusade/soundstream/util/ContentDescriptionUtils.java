package com.lastcrusade.soundstream.util;

import com.lastcrusade.soundstream.model.SongMetadata;

public class ContentDescriptionUtils {
    public static String addSongTitleToContentDescription(String conDesc, SongMetadata song) {
        return conDesc + "_" + song.getTitle();
    }
}
