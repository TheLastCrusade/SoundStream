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

package com.lastcrusade.soundstream.model;

public class UserColors {
    //color set not finished yet - might change some or all colors
    private ColorEntry[] colors = new ColorEntry[] {
        
        new ColorEntry(0xFF02AD8C),
        new ColorEntry(0xFFB3ED00),
        new ColorEntry(0xFFFFC600),
        new ColorEntry(0xFFC10091),
        new ColorEntry(0xFFF99500),
        new ColorEntry(0xFF91B22C),
        new ColorEntry(0xFFF94000),
        new ColorEntry(0xFF1E7766),
        new ColorEntry(0xFFCAA72B),
        new ColorEntry(0xFF9B217D),
        new ColorEntry(0xFFC88C32)
    };
    
    //gets the next color that is not taken and marks it as such
    public int getNextAvailableColor(){
        for(ColorEntry c:colors){
            if(!c.isTaken()){
                c.setTaken(true);
                return c.getColor();
            }
        }
        
        //if we have run out of colors in our list, return black
        return 0xff000000;
        
    }
    
    // adds the color back into the pool - called from userlist's remove
    // when a user is no longer connected to the network
    public void returnColor(int color){
        for(ColorEntry c:colors){
            if(c.getColor() == color){
                c.setTaken(false);
            }
        }
    }

    public void clear() {
        for (ColorEntry entry : colors) {
            entry.setTaken(false);
        }
    }
    
    //defines an entry in the color pallet - needed because we want to keep track
    //of what colors are currently being used, and when someone leaves the app the color
    //needs to become available again
    private class ColorEntry{
        private int color;
        private boolean taken;
        
        public ColorEntry(int color){
            this.color = color;
            taken = false;
        }
        
        public boolean isTaken(){
            return taken;
        }
        
        public void setTaken(boolean taken){
            this.taken = taken;
        }
        
        public int getColor(){
            return color;
        }
    }
}
