package com.lastcrusade.fanclub.net.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UserListMessage implements IMessage {
	private final String TAG = UserListMessage.class.getName();
	private String userListString;
	
	@Override
	public void deserialize(InputStream input) throws IOException {
		byte[] bytes = new byte[1024];
		int read = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while((read = input.read(bytes)) > 0) {
			out.write(bytes, 0, read);
		}
		this.setUserListString(out.toString());
	}
	
	@Override
	public void serialize(OutputStream output) throws IOException {
		output.write(this.getUserListString().getBytes());		
	}
	
	public String getUserListString() {
		return userListString;
	}
	
	public void setUserListString(String userListString) {
		this.userListString = userListString;
	}
	

}
