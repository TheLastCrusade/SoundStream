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
package com.lastcrusade.soundstream.components;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.lastcrusade.soundstream.CoreActivity;
import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.util.ITitleable;

/**
 * @author Elizabeth
 *
 */
public class AboutFragment extends SherlockFragment implements ITitleable {
    private final String TAG = AboutFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        
        final TextView 
        	repoLinkText = (TextView)v.findViewById(R.id.repo_link),
        	SlidingMenuLinkText = (TextView)v.findViewById(R.id.thanks_SlidingMenu),
        	ABSLinkText = (TextView)v.findViewById(R.id.thanks_ABS),
        	emailLinkText = (TextView)v.findViewById(R.id.email_link);
        
        listenToHttpLink(repoLinkText, "https://github.com/TheLastCrusade/SoundStream");
        listenToHttpLink(SlidingMenuLinkText, "https://github.com/jfeinstein10/SlidingMenu");
        listenToHttpLink(ABSLinkText, "https://github.com/JakeWharton/ActionBarSherlock");
        
        emailLinkText.setOnClickListener(new AdapterView.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "SoundStreamHelp@gmail.com", null));
				i.putExtra(Intent.EXTRA_SUBJECT, "[SoundStream Beta]");
				startActivity(Intent.createChooser(i, "Send email..."));
			}
        });

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        ((CoreActivity)getActivity()).getTracker().sendView(TAG);
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(getTitle());
    }

    @Override
    public int getTitle() {
        return R.string.about;
    }

    public void listenToHttpLink(TextView linkText, final String url)
    {
    	linkText.setOnClickListener(new AdapterView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(i);				
			}
    	});
    }
}
