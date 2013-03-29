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


import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.lastcrusade.soundstream.model.UserList;
import com.lastcrusade.soundstream.net.message.UserListMessage;

public class UserListMessageTest extends SerializationTest<UserListMessage> {
		
	@Test
	public void testSerializeUserListMessage() throws IOException {
		UserList userList = new UserList();
		populateTestUserList(userList);
		
		List<String> bluetoothIDs = userList.getBluetoothIDs();
		List<String> macAddresses = userList.getMacAddresses();
		
		UserListMessage preSerializationUserListMessage = new UserListMessage();
		preSerializationUserListMessage.setUserList(userList);
		UserListMessage postSerializationUserListMessage = 
				super.testSerializeMessage(preSerializationUserListMessage);
		
		for(int i = 0; i < bluetoothIDs.size(); i++) {
			assertEquals(bluetoothIDs.get(i), postSerializationUserListMessage.getUserList().getBluetoothIDs().get(i));
			assertEquals(macAddresses.get(i), postSerializationUserListMessage.getUserList().getMacAddresses().get(i));
		}
	}
	
	/**
	 * @return A UserList populated with data used to test UserList serialization
	 */
	public UserList populateTestUserList(UserList userList) {
		userList.addUser("David","1");
		userList.addUser("Jesse","2");
		userList.addUser("Lizziemom","3");
		userList.addUser("Sills","4");
		userList.addUser("Reid","5");
		
		return userList;
	}
}
