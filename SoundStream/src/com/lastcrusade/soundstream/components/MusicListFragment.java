package com.lastcrusade.soundstream.components;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;
import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.util.ITitleable;

public abstract class MusicListFragment extends SherlockListFragment implements ITitleable{
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.list, container, false);
        return v;
    }

    @Override
    public abstract int getTitle();
    
    @Override
    public void onResume(){
        super.onResume();
    }
}
