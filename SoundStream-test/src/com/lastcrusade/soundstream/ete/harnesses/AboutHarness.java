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
import com.lastcrusade.soundstream.util.EteUtils;

/**
 * @author Taylor
 *
 */
public class AboutHarness extends AbstractHarness {

	public AboutHarness(Solo solo){
		super(solo);
	}
	
	public void navigateTo(){
		if(!EteUtils.isFragmentVisible(solo, AboutFragment.class)){
			new SoundStreamHarness(solo).hMenu().openAbout();
		}
	}
	
	public void assertVisible(boolean expected){
		assertFragmentVisible(AboutFragment.class, expected);
	}	
	
}
