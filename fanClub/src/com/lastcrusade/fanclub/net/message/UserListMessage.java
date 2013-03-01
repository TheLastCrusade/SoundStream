package com.lastcrusade.fanclub.net.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;

import com.lastcrusade.fanclub.model.UserList;

public class UserListMessage extends ADataMessage {
	private final String TAG = UserListMessage.class.getName();
	
	// NOTE: this relies on the default UserList constructor which currently
	// populates with hardcoded user data
	private UserList userList = new UserList(); 
	
	public UserListMessage() {}
	
	public UserListMessage(UserList userList) {
		
	}
	
	@Override
	public void deserialize(InputStream input) throws IOException {
		int userListSize = readInteger(input);
		for(int i = 0; i < userListSize; i++) {
			String username = readString(input);
			String color = readString(input);
			
			userList.addUser(username, color);
		}
	}
	
	@Override
	public void serialize(OutputStream output) throws IOException {
		List<String> usernames = userList.getUsernames();
		
		writeInteger(userList.getUsers().size(), output);
		for(int i = 0; i < usernames.size(); i++) {
			writeString(usernames.get(i), output);
			writeString(userList.getUsers().get(usernames.get(i)), output);
		}
	}

	public UserList getUserList() {
		return userList;
	}

	public void setUserList(UserList userList) {
		this.userList = userList;
	}
	

}
