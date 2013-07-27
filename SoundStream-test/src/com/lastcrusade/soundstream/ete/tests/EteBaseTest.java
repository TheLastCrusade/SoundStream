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

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.lastcrusade.soundstream.CoreActivity;
import com.lastcrusade.soundstream.ete.harnesses.SoundStreamHarness;

/**
 * Base End-to-End test, which sets up robotium, and makes a default base Sound
 * Stream Harness for use in further automation.
 * 
 * @author Taylor Wrobel
 * 
 */
public abstract class EteBaseTest extends
		ActivityInstrumentationTestCase2<CoreActivity> {

	protected Solo solo;
	protected SoundStreamHarness ssh;

	public EteBaseTest() {
		super(CoreActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		// setUp() is run before a test case is started.
		// This is where the solo object is created.
		solo = new Solo(getInstrumentation(), getActivity());
		ssh = new SoundStreamHarness(solo);

		solo.waitForActivity(CoreActivity.class); // Wait for activity to start
													// fully
		
		//Handle if welcome screen is up
		boolean welcomeUp = solo.searchText("Welcome to SoundStream!");
		if (welcomeUp) {
			solo.clickOnButton("Ok");
		}
	}

	@Override
	public void tearDown() throws Exception {
		// tearDown() is run after a test case has finished.
		// finishOpenedActivities() will finish all the activities that have
		// been opened during the test execution.
		solo.finishOpenedActivities();
	}

}
