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

package com.lastcrusade.soundstream.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lastcrusade.soundstream.R;
import com.lastcrusade.soundstream.model.User;
import com.lastcrusade.soundstream.model.UserList;

public class UserListAdapter extends BaseAdapter {
    private static final String TAG = UserListAdapter.class.getSimpleName();

    private Context mContext;
    private UserList users;
    //variable to keep track of whether the list is displaying on the
    //menu or not - if it is, the text color changes
    private boolean isOnMenu;
    
    public UserListAdapter(Context mContext, UserList users, boolean isOnMenu){
        this.users = users;
        this.mContext = mContext;
        this.isOnMenu = isOnMenu;
    }
    
    @Override
    public int getCount() {
        return users.getUsers().size();
    }

    @Override
    public Object getItem(int index) {
        return users.getUsers().get(index);
    }

    @Override
    public long getItemId(int index) {
        return users.getUsers().get(index).getBluetoothID().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View userView = convertView;
        
        if(userView == null){
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            userView = inflater.inflate(R.layout.user_item, null);
        }
        
        View userColor = (View) userView.findViewById(R.id.user_color); 
        TextView username = (TextView)userView.findViewById(R.id.bluetoothID);

        
        userColor.setBackgroundColor( ((User)getItem(position)).getColor() );
        
        if(isOnMenu){
            username.setTextColor(mContext.getResources().getColor(R.color.white));
        }
        username.setText(((User)getItem(position)).getBluetoothID());
        
        return userView;
    }
    
    public void updateUsers(UserList users){
        this.users = users;
        notifyDataSetChanged();
    }

}
