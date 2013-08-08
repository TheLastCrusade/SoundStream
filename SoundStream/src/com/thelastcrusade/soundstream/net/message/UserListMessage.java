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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.lastcrusade.soundstream.model.UserList;

public class UserListMessage extends ADataMessage {
    @SuppressWarnings("unused")
	private final String TAG = UserListMessage.class.getName();
	
	private UserList userList; 
	
	/**
	 * Default constructor, required for Messenger.  All other users should use
	 * the other constructor.
	 * 
	 */
	UserListMessage() {
	    this(new UserList());
	}
	
	public UserListMessage(UserList userList) {
	    this.userList = userList;
	}
	
	@Override
	public void deserialize(InputStream input) throws IOException {
		int userListSize = readInteger(input);
		for(int i = 0; i < userListSize; i++) {
			String bluetoothID = readString(input);
			String macAddress = readString(input);
			userList.addUser(bluetoothID, macAddress);
		}
	}
	
	@Override
	public void serialize(OutputStream output) throws IOException {
		List<String> bluetoothIDs = userList.getBluetoothIDs();
		List<String> macAddresses= userList.getMacAddresses();
		
		writeInteger(userList.getUsers().size(), output);
		for(int i = 0; i < bluetoothIDs.size(); i++) {
			writeString(bluetoothIDs.get(i), output);
			writeString(macAddresses.get(i), output);
		}
	}

	public UserList getUserList() {
		return userList;
	}

	public void setUserList(UserList userList) {
		this.userList = userList;
	}
	

}
