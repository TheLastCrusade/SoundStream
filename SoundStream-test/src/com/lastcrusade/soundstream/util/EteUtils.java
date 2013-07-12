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
package com.lastcrusade.soundstream.util;

import android.support.v4.app.Fragment;

import com.jayway.android.robotium.solo.Solo;
import com.lastcrusade.soundstream.CoreActivity;
import com.lastcrusade.soundstream.components.MenuFragment;

/**
 * @author Taylor
 *
 */
public class EteUtils {

	public static boolean isFragmentVisible(Solo solo, Class<? extends Fragment> fragment) throws IllegalStateException{
		if(!(solo.getCurrentActivity() instanceof CoreActivity)){
			throw new IllegalStateException("Current Activity isn't CoreActivity, as expected");
		}
		
		CoreActivity core = (CoreActivity)solo.getCurrentActivity();
		
		boolean vis = false;
		
		for(Fragment f : core.getAttachedFragments()){
			if(f.getClass().equals(fragment)){
				if(f.getClass().equals(MenuFragment.class)){
					vis |= core.getSlidingMenu().isMenuShowing();
				} else{
					vis |= f.isVisible() && !f.isRemoving();
				}
			}
		}
		
		return vis;
	}
	
}
