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

import com.lastcrusade.soundstream.components.ConnectFragment;
import com.lastcrusade.soundstream.components.MenuFragment;
import com.lastcrusade.soundstream.util.BluetoothUtils;

/**
 * A set of tests to verify functionality of the ConnectFragment
 * 
 * @author Taylor Wrobel
 * 
 */
public class ConnectFragmentTest extends EteBaseTest {

	public void testConnectText() {
		ssh.assertFragmentVisible(ConnectFragment.class, true);
		ssh.hConnect().assertCreateText("Create");
		String localname = BluetoothUtils.getLocalBluetoothName();
		ssh.hConnect().assertJoinText("Join as \"" + localname + "\"");
	}

	public void testCreateNetwork() {
		ssh.hConnect().assertVisible(true);
		ssh.hConnect().assertCreateButtonEnabled(true);
		ssh.hConnect().pressCreateButton();
		ssh.assertFragmentVisible(MenuFragment.class, true);
		ssh.hConnect().assertVisible(false);
	}

}
