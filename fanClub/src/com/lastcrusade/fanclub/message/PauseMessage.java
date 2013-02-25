package com.lastcrusade.fanclub.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PauseMessage implements IMessage {
	private final String PAUSE_MESSAGE = "Pause";

	@Override
	public void deserialize(InputStream input) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serialize(OutputStream output) throws IOException {
		output.write(PAUSE_MESSAGE.getBytes());		
	}
}
