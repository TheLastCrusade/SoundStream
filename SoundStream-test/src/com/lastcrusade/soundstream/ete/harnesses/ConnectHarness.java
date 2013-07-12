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

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Condition;
import com.jayway.android.robotium.solo.Solo;
import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.components.ConnectFragment;
import com.lastcrusade.soundstream.util.EteUtils;

/**
 * @author Taylor
 * 
 */
public class ConnectHarness extends AbstractHarness {

	public ConnectHarness(Solo solo) {
		super(solo);
	}

	public void navigateTo() {
		if (!EteUtils.isFragmentVisible(solo, ConnectFragment.class)) {
			throw new IllegalStateException(
					"Not possible to get to the ConnectFragment from current state.");
		}
	}

	public void pressCreateButton() {
		solo.clickOnView(solo.getView(R.id.create_network_id));
		assertVisible(false); // Should navigate away from fragment
	}

	public void pressJoinButton() {
		solo.clickOnView(solo.getView(R.id.join_network_id));
	}

	public void answerBluetoothDialog(boolean respondYes) {
		// FIXME: Doesn't work
//		assertBluetoothDialogShowing(true);
		solo.clickOnButton(respondYes ? "Yes" : "No");
	}

	public void joinNetwork() {
		pressJoinButton();
		answerBluetoothDialog(true);
	}

	public void assertBluetoothDialogShowing(final boolean expected) {
		Condition con = new Condition() {
			@Override
			public boolean isSatisfied() {
				//FIXME: Doesn't work
				return solo.getText("Bluetooth permission request").isShown() == expected;
			}
		};

		if (!solo.waitForCondition(con, 1000)) {
			assertEquals("Bluetooth dialog visibility not as expected",
					expected, !expected);
		}
	}

	public void assertCreateButtonEnabled(final boolean expected) {
		View createBtn = solo.getView(R.id.create_Img);
		assertTrue("Found view of type that isn't ImageView",
				createBtn instanceof ImageView);
		assertEquals("Create incorrectly enabled/disabled", expected,
				((ImageView)createBtn).isEnabled());
	}

	public void assertJoinButtonEnabled(final boolean expected) {
		View joinBtn = solo.getView(R.id.join_Img);
		assertTrue("Found view of type that isn't ImageView",
				joinBtn instanceof ImageView);
		assertEquals("Join incorrectly enabled/disabled", expected,
				((ImageView)joinBtn).isEnabled());
	}

	public void assertCreateText(final String expected) {
		View createText = solo.getView(R.id.create_network_id);
		assertTrue("Found view of type that isn't TextView",
				createText instanceof TextView);
		assertEquals("Create button text different than expected.", expected,
				((TextView) createText).getText().toString());
	}

	public void assertJoinText(final String expected) {
		View createText = solo.getView(R.id.join_network_id);
		assertTrue("Found view of type that isn't TextView",
				createText instanceof TextView);
		assertEquals("Create button text different than expected.", expected,
				((TextView) createText).getText().toString());
	}
	
	public void assertVisible(boolean expected){
		assertFragmentVisible(ConnectFragment.class, expected);
	}
}
