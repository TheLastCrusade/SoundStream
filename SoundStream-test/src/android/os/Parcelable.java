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

package android.os;

/**
 * .net.message tests are run as standard JUnit tests, and because this
 * is an Android test project, the tests must be run with the Android
 * test launcher.  This removes Android JAR files from the class path
 * which removes runtime access to this interface, which is required
 * by several of our model objects.  Ergo, this hack is required
 * so we can run our tests within eclipse without running them as
 * Android tests, which requires an emulator or device.
 * 
 * tl;dr THIS ONLY EXISTS BECAUSE ECLIPSE SUCKS.
 * 
 * @author Jesse Rosalia
 *
 */
public interface Parcelable {

    public interface Creator<T> {
        public T createFromParcel(Parcel in);

        public T[] newArray(int size);
    }

    public int describeContents();

    public void writeToParcel(Parcel dest, int flags);
}
