package com.lastcrusade.fanclub.net.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.junit.Test;

import com.lastcrusade.fanclub.model.*;
import com.lastcrusade.fanclub.net.message.UserListMessage;

public class UserListMessageTest extends SerializationTest<UserListMessage> {
	
	@Test
	public void testSerializeUserListMessage() throws IOException {
		UserListMessage oldMessage = new UserListMessage();
		UserListMessage newMessage = super.testSerializeMessage(oldMessage);
		
		//assertEquals( , newMessage.getUserListString());
	}
}
