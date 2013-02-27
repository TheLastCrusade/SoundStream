package com.lastcrusade.fanclub.net.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.lastcrusade.fanclub.model.UserList;

public class UserListMessage implements IMessage {
	private final String TAG = UserListMessage.class.getName();
	private UserList userList;
	
	@Override
	public void deserialize(InputStream input) throws IOException {
		byte[] bytes = new byte[1024];
		int read = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while((read = input.read(bytes)) > 0) {
			out.write(bytes, 0, read);
		}
		//this.setUserList(out.); //wait for Jesse's changes to be made
	}
	
	@Override
	public void serialize(OutputStream output) throws IOException {
		output.write(this.getUserList().toString().getBytes());		
	}

	public UserList getUserList() {
		return userList;
	}

	public void setUserList(UserList userList) {
		this.userList = userList;
	}
	

}
