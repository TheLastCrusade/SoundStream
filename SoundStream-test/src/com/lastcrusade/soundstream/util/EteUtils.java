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

	/**
	 * Returns a boolean as to whether the given fragment is visible or not.
	 * 
	 * Assumes that the current activity must be core activity (currently our only activity),
	 * and that there is only one instance of each type of fragment ever in existance.  Currently
	 * these assumptions hold true, but may vary in the future.
	 * 
	 * @param solo - The solenium object from the robotium testing architecture
	 * @param fragment - The 
	 * @return Whether the specified fragment is visible or not
	 * @throws IllegalStateException - If the activity has switched to something other than the core activity
	 */
	public static boolean isFragmentVisible(Solo solo, Class<? extends Fragment> fragment) throws IllegalStateException{
		// If we switched to another activity something broke
		if(!(solo.getCurrentActivity() instanceof CoreActivity)){
			throw new IllegalStateException("Current Activity isn't CoreActivity, as expected");
		}
		
		CoreActivity core = (CoreActivity)solo.getCurrentActivity();
		
		// Condition for if the fragment is visible or not, to be updated with logic below
		boolean vis = false;
		
		for(Fragment f : core.getAttachedFragments()){
			if (f.getClass().equals(fragment)) {
				// The menu fragment is always listed as visible, even when it's
				// not on screen. This uses the menu's own method to determine
				// visibility.
				if (f.getClass().equals(MenuFragment.class)) {
					vis |= core.getSlidingMenu().isMenuShowing();
				} else{
					// Set visiblity to true only if the fragment is listed as
					// visible, and not currently being removed (which will make
					// it not visible shortly)
					vis |= f.isVisible() && !f.isRemoving();
				}
			}
		}
		
		// Return overall visiblity condition for the given fragment
		return vis;
	}
	
}
