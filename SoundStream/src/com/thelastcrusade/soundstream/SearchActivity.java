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
package com.thelastcrusade.soundstream;


import com.thelastcrusade.soundstream.components.MusicLibraryFragment;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;


public class SearchActivity extends FragmentActivity {
    
    private static final String TAG = SearchActivity.class.getSimpleName();
    public static final String QUERY_KEY = "query_key";
    private String query;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_frame);
        View resultsBar = findViewById(R.id.results_bar);
        resultsBar.setVisibility(View.VISIBLE);
        
        ActionBar bar = getActionBar();
        bar.show();
                
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
          query = intent.getStringExtra(SearchManager.QUERY);
        }
        
        if (savedInstanceState == null) {
            MusicLibraryFragment fragment = getMusicLibraryFragment(query);
            getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).commit();
        }
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);

        return true;
    }
    
    private MusicLibraryFragment getMusicLibraryFragment(String query) {
        Bundle bundle = new Bundle();
        bundle.putString(QUERY_KEY, query);
        MusicLibraryFragment fragment = new MusicLibraryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    
    
}
