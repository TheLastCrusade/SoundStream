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

import com.thelastcrusade.soundstream.audio.AudioPlayerWithEvents;

/**
 * Utilities for working with Class objects and their effects on
 * other objects.
 * 
 * @author Jesse Rosalia
 *
 */
public class ClassUtils {

    /**
     * Get a class or interface from an object, if available.  This is useful
     * when an object may implement optional interfaces, and we want to
     * get those interfaces.
     * 
     * As an example, see {@link AudioPlayerWithEvents}.
     * 
     * @param object the object to inspect
     * @param desiredClass the class to get
     * @return
     */
    public static <T> T getIfAvailable(Object object, Class<T> desiredClass) {
        return (desiredClass.isAssignableFrom(object.getClass()))
                    ? desiredClass.cast(object)
                    : null;
    }
}
