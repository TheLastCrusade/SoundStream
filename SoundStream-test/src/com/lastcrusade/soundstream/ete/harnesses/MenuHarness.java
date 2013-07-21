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
package com.lastcrusade.soundstream.ete.harnesses;

import com.jayway.android.robotium.solo.Solo;
import com.lastcrusade.soundstream.components.AboutFragment;
import com.lastcrusade.soundstream.components.ConnectFragment;
import com.lastcrusade.soundstream.components.MenuFragment;
import com.lastcrusade.soundstream.components.MusicLibraryFragment;
import com.lastcrusade.soundstream.components.NetworkFragment;
import com.lastcrusade.soundstream.components.PlaylistFragment;
import com.lastcrusade.soundstream.util.EteUtils;
import com.squareup.spoon.Spoon;

/**
 * Automation harness for the MenuFragment. Contains all methods necessary
 * for interacting with and verifying the state of the fragment.
 * 
 * @author Taylor Wrobel
 *
 */
public class MenuHarness extends AbstractHarness{
	
	public MenuHarness(Solo solo){
		super(solo);
	}

	public void navigateTo(){
		assertFragmentVisible(ConnectFragment.class, false);
		if(!EteUtils.isFragmentVisible(solo, MenuFragment.class)){
			new SoundStreamHarness(solo).pressMenuButton();
		}
		assertVisible(true);
	}
	
	public void openPlaylist(){
		navigateTo();
		solo.clickOnButton("Playlist");
		assertVisible(false);
		assertFragmentVisible(PlaylistFragment.class, true);
		Spoon.screenshot(solo.getCurrentActivity(), "Changed_to_Playlist");
	}
	
	public void openMusicLibrary(){
		navigateTo();
		solo.clickOnButton("Music Library");
		assertVisible(false);
		assertFragmentVisible(MusicLibraryFragment.class, true);
		Spoon.screenshot(solo.getCurrentActivity(), "Changed_to_Music_Library");
	}
	
	public void openAbout(){
		navigateTo();
		solo.clickOnButton("About");
		assertVisible(false);
		assertFragmentVisible(AboutFragment.class, true);
		Spoon.screenshot(solo.getCurrentActivity(), "Changed_to_About");
	}
	
	public void openNetwork(){
		navigateTo();
		solo.clickOnButton("Network");
		assertVisible(false);
		assertFragmentVisible(NetworkFragment.class, true);
		Spoon.screenshot(solo.getCurrentActivity(), "Changed_to_Network");
	}
	
	public void assertVisible(boolean expected){
		assertFragmentVisible(MenuFragment.class, expected);
	}
	
}
