package com.lastcrusade.fanclub.net.message;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.lastcrusade.fanclub.model.*;
import com.lastcrusade.fanclub.net.message.UserListMessage;

public class UserListMessageTest extends SerializationTest<UserListMessage> {
	
	// NOTE: this relies on the default UserList constructor which currently
	// populates with hardcoded user data
	private UserList userList = new UserList();
	
	@Test
	public void testSerializeUserListMessage() throws IOException {
		List<String> usernames = userList.getUsernames();
		
		UserListMessage preSerializationUserListMessage = new UserListMessage();
		preSerializationUserListMessage.setUserList(userList);
		UserListMessage postSerializationUserListMessage = 
				super.testSerializeMessage(preSerializationUserListMessage);
		
		for(int i = 0; i < usernames.size(); i++) {
			assertEquals(usernames.get(i), postSerializationUserListMessage.getUserList().getUsernames().get(i));
		}
	}
}
