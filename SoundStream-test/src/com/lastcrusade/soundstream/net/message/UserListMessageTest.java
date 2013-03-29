package com.lastcrusade.soundstream.net.message;


import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.lastcrusade.soundstream.model.UserList;
import com.lastcrusade.soundstream.net.message.UserListMessage;

public class UserListMessageTest extends SerializationTest<UserListMessage> {
		
	@Test
	public void testSerializeUserListMessage() throws Exception {
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
