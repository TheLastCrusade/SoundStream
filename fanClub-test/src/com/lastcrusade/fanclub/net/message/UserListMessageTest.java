package com.lastcrusade.fanclub.net.message;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Hashtable;
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
		Hashtable<String, String> users = userList.getUsers();
		
		UserListMessage oldMessage = new UserListMessage();
		oldMessage.setUserList(userList);
		UserListMessage newMessage = super.testSerializeMessage(oldMessage);
		
		for(int i = 0; i < userList.getUsers().size(); i++) {
			System.out.println(i);
			assertEquals(usernames.get(i), newMessage.getUserList().getUsernames().get(i));
			// charlie foxtrot
			assertEquals(users.get(usernames.get(i)), 
					newMessage.getUserList().getUsers().get(newMessage.getUserList().getUsernames().get(i)));
			
		}
	}
}
