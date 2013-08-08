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
package com.thelastcrusade.soundstream.util;

import com.google.analytics.tracking.android.Tracker;

/**
 * This class wraps the Google Analytics Android tracker in a desciptive API
 * to avoid clutter in our application code.
 * 
 * To add a new tracking event, simply define a label, within an established
 * action and category, and define what values are expected for that label. Add
 * a new method to this class that accepts those values and calls sendEvent on the
 * tracker and you're done.
 * 
 * @author Jesse Rosalia
 *
 */
public class TrackerAPI {

    private Trackable trackable;

    /**
     * @param tracker The google analytics tracker to use
     */
    public TrackerAPI(Trackable trackable) {
        this.trackable = trackable;
    }

    /**
     * @return The tracker to use to send events
     */
    private Tracker getTracker() {
        return this.trackable.getTracker();
    }

    public void trackAddMembersEvent() {
        getTracker().sendEvent(
                "ui_action",  // Category
                "click",      // Action
                "add_members",// Label
                0L);          // Value doesn't matter
    }

    public void trackDisconnectEvent(boolean userPressedOK) {
        getTracker().sendEvent(
                "ui_action",              // Category
                "click",                  // Action
                "disconnect",             // Label
                userPressedOK ? 1L : 0L); // Value
    }

    public void trackDisbandEvent(boolean userPressedOK) {
        getTracker().sendEvent(
                "ui_action",              // Category
                "click",                  // Action
                "disband",                // Label
                userPressedOK ? 1L : 0L); // Value
    }

    public void trackFoundGuestsEvent(int numGuests) {
        getTracker().sendEvent(
                "ui_action",             // Category
                "MultiSelectListDialog", // Action
                "found_guests",          // Label
                (long)numGuests);        // Value length of list
    }

    public void trackPlayPauseEvent(boolean playing) {
        getTracker().sendEvent(
                "ui_action",        // Category
                "click",            // Action
                "play_pause",       // Label
                playing ? 1L : 0L); // Value
    }

    public void trackSkipEvent() {
        getTracker().sendEvent(
                "ui_action",  // Category
                "click",      // Action
                "skip",       // Label
                0L);          // Value doesn't matter
    }
}
