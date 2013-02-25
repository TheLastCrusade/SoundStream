package com.lastcrusade.fanclub.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SkipMessage implements IMessage {
	private final String SKIP_SERVICE = "Skip";

	@Override
	public void deserialize(InputStream input) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serialize(OutputStream output) throws IOException {
		output.write(SKIP_SERVICE.getBytes());		
	}

}
