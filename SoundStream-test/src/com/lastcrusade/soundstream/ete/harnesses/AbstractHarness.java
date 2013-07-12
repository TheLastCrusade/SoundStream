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

import junit.framework.Assert;

import android.support.v4.app.Fragment;

import com.jayway.android.robotium.solo.Condition;
import com.jayway.android.robotium.solo.Solo;
import com.lastcrusade.soundstream.util.EteUtils;

/**
 * @author Taylor
 *
 */
public abstract class AbstractHarness extends Assert{

	protected final Solo solo;
	
	public AbstractHarness(Solo solo){
		this.solo = solo;
	}
	
	public void assertFragmentVisible(final Class<? extends Fragment> fragment,
			final boolean expected) {
		Condition con = new Condition() {
			@Override
			public boolean isSatisfied() {
				return EteUtils.isFragmentVisible(solo, fragment) == expected;
			}
		};

		boolean correct = solo.waitForCondition(con, 2000);

		if (!correct) {
			assertEquals("Fragment " + fragment.getCanonicalName()
					+ " incorrectly visible.", expected, !expected);
		}
	}
}
