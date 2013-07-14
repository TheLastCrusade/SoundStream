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
 * @author Taylor
 *
 */
public class AboutFragmentTest extends EteBaseTest{

	public void testTitle(){
		ssh.hConnect().pressCreateButton();
		ssh.hAbout().navigateTo();
		ssh.spoonShot("About_Opened");
		ssh.assertTitleEquals("About");
		ssh.pressMenuButton();
		ssh.spoonShot("About_Closed_Via_Home");
		ssh.assertTitleEquals("SoundStream");
		ssh.pressMenuButton();
		ssh.hAbout().assertVisible(true);
		ssh.hMenu().assertVisible(false);
		ssh.spoonShot("About_Reopened_Via_Home");
		ssh.assertTitleEquals("About");
	}
	
}
