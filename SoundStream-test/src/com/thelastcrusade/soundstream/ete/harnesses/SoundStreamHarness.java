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
package com.thelastcrusade.soundstream.ete.harnesses;

import android.annotation.SuppressLint;
import android.view.View;

import com.jayway.android.robotium.solo.Solo;
import com.squareup.spoon.Spoon;

/**
 * The base harness for the soundstream application. Contains all methods
 * necessary for interacting with and verifying the state of the application as
 * a whole, including handles to all sub harnesses.
 * 
 * @author Taylor Wrobel
 * 
 */
public class SoundStreamHarness extends AbstractHarness {

	public SoundStreamHarness(Solo solo) {
		super(solo);
	}

	public ConnectHarness hConnect() {
		return new ConnectHarness(solo);
	}

	public MenuHarness hMenu() {
		return new MenuHarness(solo);
	}

	public AboutHarness hAbout() {
		return new AboutHarness(solo);
	}

	public void spoonShot(String tag) {
		Spoon.screenshot(solo.getCurrentActivity(), tag);
	}

	@SuppressLint("InlinedApi")
	public void pressMenuButton() {
		solo.clickOnView(solo.getView(android.R.id.home));
	}

	public void assertMenuButtonActive(boolean expected) {
		// FIXME: Doesn't work
		View menuButton = solo.getView(android.R.id.home);
		assertEquals("Menu not correctly enabled/disabled", expected,
				menuButton.isEnabled());
	}

	public void assertTitleEquals(String expectedTitle) {
		// FIXME: Doesn't grab title as it should. Should pull from UI directly.
		String actual = solo.getCurrentActivity().getTitle().toString();
		assertTrue("Title \"" + actual + "\" doesn't match expected \""
				+ expectedTitle + "\"", expectedTitle.equals(actual));
	}

}
