package com.lastcrusade.fanclub.model;

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
