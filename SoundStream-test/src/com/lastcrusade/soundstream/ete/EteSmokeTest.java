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
package com.lastcrusade.soundstream.ete;

import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Condition;
import com.jayway.android.robotium.solo.Solo;
import com.lastcrusade.soundstream.CoreActivity;
import com.lastcrusade.soundstream.components.ConnectFragment;
import com.lastcrusade.soundstream.components.MenuFragment;
import com.lastcrusade.soundstream.components.NetworkFragment;
import com.lastcrusade.soundstream.util.EteUtils;

/**
 * @author Taylor
 *
 */
public class EteSmokeTest extends ActivityInstrumentationTestCase2<CoreActivity> {
	private Solo solo;

	public EteSmokeTest() {
		super(CoreActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		//setUp() is run before a test case is started. 
		//This is where the solo object is created.
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		//tearDown() is run after a test case has finished. 
		//finishOpenedActivities() will finish all the activities that have been opened during the test execution.
		solo.finishOpenedActivities();
	}

	public void testOpenClose() throws Exception {
		solo.waitForActivity(CoreActivity.class);
		
		assertUtils.assertFragmentVisible(solo, ConnectFragment.class, true);
		
		solo.clickOnText("Create");

		assertUtils.assertFragmentVisible(solo, ConnectFragment.class, false);
		assertUtils.assertFragmentVisible(solo, MenuFragment.class, true);
		
		solo.clickOnButton("Network");

		assertUtils.assertFragmentVisible(solo, MenuFragment.class, false);
		assertUtils.assertFragmentVisible(solo, NetworkFragment.class, true);
		
		solo.clickOnText("Disconnect");

		solo.clickOnText("Disconnect");
	}
	
	public static class assertUtils {
		
		public static void assertFragmentVisible(final Solo solo, final Class<? extends Fragment> fragment, final boolean expected){
			Condition con = new Condition() {
				@Override
				public boolean isSatisfied() {
					return EteUtils.isFragmentVisible(solo, fragment) == expected;
				}
			};
			
			boolean correct = solo.waitForCondition(con, 1000);
			
			if(!correct){
				assertEquals("Fragment " + fragment.getCanonicalName() + " incorrectly visible.", expected, !expected);
			}
		}
	}
}
