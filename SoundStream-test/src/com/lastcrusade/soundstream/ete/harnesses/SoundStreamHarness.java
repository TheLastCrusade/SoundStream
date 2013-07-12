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

import android.widget.ImageView;

import com.jayway.android.robotium.solo.Solo;

/**
 * The base harness for the soundstream application. Contains all methods
 * necessary for interacting with and verifying the state of the application as
 * a whole, including handles to all sub harnesses.
 * 
 * @author Taylor Wrobel
 * 
 */
public class SoundStreamHarness extends AbstractHarness {
	
	public SoundStreamHarness(Solo solo){
		super(solo);
	}

	public ConnectHarness hConnect() {
		return new ConnectHarness(solo);
	}
	
	public MenuHarness hMenu() {
		return new MenuHarness(solo);
	}

	public void pressMenuButton(){
		// FIXME: Find better way to ID button when pressing.  Currently works, but is sketchy.
		solo.clickOnImage(0);
	}
	
	public void assertMenuButtonActive(boolean expected){
		// FIXME: Doesn't work
		ImageView menuButton = solo.getImage(0);
		assertEquals("Menu not correctly enabled/disabled", expected, menuButton.isEnabled());
	}
	
}
