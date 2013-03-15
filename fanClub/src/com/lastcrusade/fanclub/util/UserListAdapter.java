package com.lastcrusade.fanclub.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.model.User;
import com.lastcrusade.fanclub.model.UserList;

public class UserListAdapter extends BaseAdapter {

    private Context mContext;
    private UserList users;
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

}
