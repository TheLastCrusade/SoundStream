package com.lastcrusade.soundstream.net.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PlayStatusMessage extends ADataMessage {
	
	private final String TAG = PlayStatusMessage.class.getName();
	private final String PLAY_MESSAGE = "Play";
	private final String PAUSE_MESSAGE = "Pause";
	private String string = "";

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
		output.write(getString().getBytes());
		/*if(output.toString() == PLAY_MESSAGE) {
			output.write(PLAY_MESSAGE.getBytes());
		}
		else if(output.toString() == PAUSE_MESSAGE) {
			output.write(PAUSE_MESSAGE.getBytes());
		}*/
	}
	
	public String getPlayMessage() {
		return PLAY_MESSAGE;
	}
	
	public String getPauseMessage() {
		return PAUSE_MESSAGE;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
}
