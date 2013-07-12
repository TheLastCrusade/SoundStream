/*
 * Copyright 2013 The Last Crusade ContactLastCrusade@gmail.com
 * 
 * This file is part of SoundStream.
 * 
 * SoundStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SoundStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SoundStream.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.lastcrusade.soundstream.ete.tests;

/**
 * Smoke test to assure that core functionality of the application is retained.
 * 
 * @author Taylor Wrobel
 * 
 */
public class SmokeTest extends EteBaseTest {

	/**
	 * This test simply opens all fragments that can be opened from a single
	 * phone network.
	 * 
	 */
	public void testOpenAllFragments() {
		ssh.hConnect().assertVisible(true);
		ssh.hMenu().assertVisible(false);

		ssh.hConnect().pressCreateButton();

		ssh.hMenu().openPlaylist();

		ssh.hMenu().openMusicLibrary();

		ssh.hMenu().openAbout();

		ssh.hMenu().openNetwork();

		solo.clickOnText("Disconnect");
		solo.clickOnText("Disconnect");
	}

}
