package com.lastcrusade.fanclub.net.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Hashtable;

import org.junit.Test;

import com.lastcrusade.fanclub.model.*;
import com.lastcrusade.fanclub.net.message.UserListMessage;

public class UserListMessageTest extends SerializationTest<UserListMessage> {
	
	private Hashtable<String,String> connectedUsers = new Hashtable<String,String>();
	
	@Test
	public void testSerializeUserListMessage() throws IOException {
		UserListMessage oldMessage = new UserListMessage();
		oldMessage.setUserListString(connectedUsers.toString());
		UserListMessage newMessage = super.testSerializeMessage(oldMessage);
		
		assertEquals(connectedUsers.toString(), newMessage.getUserListString());
	}
}
