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

import com.lastcrusade.soundstream.components.AboutFragment;
import com.lastcrusade.soundstream.components.MenuFragment;
import com.lastcrusade.soundstream.components.MusicLibraryFragment;
import com.lastcrusade.soundstream.components.NetworkFragment;
import com.lastcrusade.soundstream.components.PlaylistFragment;

/**
 * @author Taylor
 *
 */
public class SmokeTest extends EteBaseTest {

	public void testOpenAllFragments() throws Exception {
		ssh.hConnect().assertVisible(true);
		ssh.assertFragmentVisible(MenuFragment.class, false);

		ssh.hConnect().pressCreateButton();
		
		ssh.hMenu().openPlaylist();
		ssh.assertFragmentVisible(PlaylistFragment.class, true);
		
		ssh.hMenu().openMusicLibrary();
		ssh.assertFragmentVisible(MusicLibraryFragment.class, true);
		
		ssh.hMenu().openAbout();
		ssh.assertFragmentVisible(AboutFragment.class, true);
		
		ssh.hMenu().openNetwork();
		ssh.assertFragmentVisible(NetworkFragment.class, true);

		solo.clickOnText("Disconnect");
		solo.clickOnText("Disconnect");
	}
	
}
