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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.analytics.tracking.android.Log;
import com.thelastcrusade.soundstream.CustomApp;

/**
 * @author Taylor
 *
 */
public class SoundStreamPrefs {

    private static final String prefsLocation = "app.conf";
    private static final Map<String, String> rawPrefs = new HashMap<String, String>();
    private static final String acraName = "ACRA_REPORT_USER", acraPassword = "ACRA_REPORT_PASSWORD";
    
    static{
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    CustomApp.getAssetManager().open(prefsLocation)));
            String line = "";
            // Very simplistic config file parsing, to avoid needing an additional library.
            while((line = reader.readLine()) != null){
                // Super simple comment stripping
                if(line.contains("#")){
                    line = line.substring(0, line.indexOf("#"));
                }
                // All configs must be in the format ***=*** on their own line
                if(line.matches("^.+=.+$")){
                    String[] sections = line.split("=");
                    rawPrefs.put(sections[0].trim(), sections[1].trim());
                }
            }
            reader.close();
        } catch (IOException ioe){
            Log.e("Error when loading prefs file.");
            Log.e(ioe.getLocalizedMessage());
        }
    }
    
    public static String getAcraUsername(){
        return parseStringPref(acraName, "");
    }
    
    public static String getAcraPassword(){
        return parseStringPref(acraPassword, "");
    }
    
    private static String parseStringPref(String key, String defaultValue){
        if(rawPrefs.containsKey(key)){
            return rawPrefs.get(key);
        } else {
            return defaultValue;
        }
    }
}
