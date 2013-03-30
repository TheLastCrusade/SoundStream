package com.lastcrusade.soundstream.net.message;

import java.io.File;

/**
 * A file message is used to transmit a file across the wire.  These
 * are handled specially, as we want to avoid loading the whole file
 * into memory and passing it around through the app.  Instead
 * we want to read and write from a file at the lowest possible level,
 * and let senders/receivers specify the file or use the file
 * as they need.
 * 
 * @author Jesse Rosalia
 *
 */
public interface IFileMessage extends IMessage {

    public String getFilePath();

    public void setFilePath(String filePath);
}
