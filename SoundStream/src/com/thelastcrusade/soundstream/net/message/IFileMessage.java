/*
 * Copyright 2013 The Last Crusade ContactLastCrusade@gmail.com
 * 
 * This file is part of SoundStream.
 * 
 * SoundStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SoundStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SoundStream.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lastcrusade.soundstream.net.message;


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
