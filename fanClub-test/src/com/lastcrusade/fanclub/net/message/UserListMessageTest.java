package com.lastcrusade.fanclub.net.message;


import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.lastcrusade.fanclub.model.UserList;
import com.lastcrusade.fanclub.net.message.UserListMessage;

public class UserListMessageTest extends SerializationTest<UserListMessage> {
		
	@Test
	public void testSerializeUserListMessage() throws IOException {
		UserList userList = new UserList();
		populateTestUserList(userList);
		
		List<String> usernames = userList.getUsernames();
		
		UserListMessage preSerializationUserListMessage = new UserListMessage();
		preSerializationUserListMessage.setUserList(userList);
		UserListMessage postSerializationUserListMessage = 
				super.testSerializeMessage(preSerializationUserListMessage);
		
		for(int i = 0; i < usernames.size(); i++) {
			assertEquals(usernames.get(i), postSerializationUserListMessage.getUserList().getUsernames().get(i));
		}
	}
	
	/**
	 * @return A UserList populated with data used to test UserList serialization
	 */
	public UserList populateTestUserList(UserList userList) {
		userList.addUser("David");
		userList.addUser("Jesse");
		userList.addUser("Lizziemom");
		userList.addUser("Sills");
		userList.addUser("Reid");
		
		return userList;
	}
}
