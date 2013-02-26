package com.lastcrusade.fanclub.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SkipMessage implements IMessage {
	private String string;
	private final String SKIP_MESSAGE= "Skip";

	@Override
	public void deserialize(InputStream input) throws IOException {
		byte[] bytes = new byte[1024];
		int read = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while((read = input.read(bytes)) > 0) {
			out.write(bytes, 0, read);
		}
		this.setString(out.toString());
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
