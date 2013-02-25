package com.lastcrusade.fanclub.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SkipMessage implements IMessage {
	private String string;
	private final String SKIP_MESSAGE= "Skip";

	@Override
	public void deserialize(InputStream input) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serialize(OutputStream output) throws IOException {
		output.write(SKIP_MESSAGE.getBytes());		
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
	
	public String getSkipMessage() {
		return SKIP_MESSAGE;
	}

}
