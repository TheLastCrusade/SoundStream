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
package com.thelastcrusade.soundstream.net;

import com.thelastcrusade.soundstream.net.message.IMessage;

/**
 * An interface for connections that defines internal constants and callback methods for
 * connection objects.
 *  
 * @author Jesse Rosalia
 *
 */
public interface IConnectionInternal {

    public static final int MESSAGE_READ = 1;
    public static final String EXTRA_ADDRESS = ConnectionReader.class.getName() + ".extra.Address";
    
    public void messageReceived(IMessage message, String remoteAddress);
    public void messageTransferFinished(int messageNo);
    
}
