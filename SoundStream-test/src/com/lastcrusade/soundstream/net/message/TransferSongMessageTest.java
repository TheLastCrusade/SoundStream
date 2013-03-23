package com.lastcrusade.soundstream.net.message;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

public class TransferSongMessageTest extends SerializationTest<TransferSongMessage> {
    
    @Test
    public void testSerializePlayMessage() throws IOException {
        File file = new File("./assets/Jesse_normal_trimmed.wav");
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[fis.available()];
        fis.read(bytes);
        TransferSongMessage oldMessage = new TransferSongMessage(132452L, file.getName(), bytes);
        TransferSongMessage newMessage = super.testSerializeMessage(oldMessage);

        assertEquals(oldMessage.getSongId(),       newMessage.getSongId());
        assertEquals(oldMessage.getSongFileName(), newMessage.getSongFileName());
        assertTrue(Arrays.equals(oldMessage.getSongData(), newMessage.getSongData()));
    }
}
