package com.lastcrusade.fanclub.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lastcrusade.fanclub.model.SongMetadata;

public class LibraryService extends Service{
    private List<SongMetadata> libraryData;
    
    
    public void addToLibrary(List<SongMetadata> libraryData){
        this.libraryData.addAll(libraryData);
    }
    
    @Override
    public void onCreate(){
        super.onCreate();
        libraryData = new ArrayList<SongMetadata>();
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    
    

}
