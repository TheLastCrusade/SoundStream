package com.lastcrusade.fanclub.util;

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lastcrusade.fanclub.R;
import com.lastcrusade.fanclub.model.UserList;

public class UserListAdapter extends BaseAdapter {

    private Context mContext;
    private String[] usernames;
    private Hashtable<String, String> users;
    
    public UserListAdapter(Context mContext, String[] usernames){
        this.usernames = usernames;
        users = UserList.getUsers();
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        return usernames.length;
    }

    @Override
    public Object getItem(int index) {
        return usernames[index];
    }

    @Override
    public long getItemId(int index) {
        return usernames[index].hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View element = convertView;
        
        if(element == null){
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            element = inflater.inflate(R.layout.user_item, null);
        }
        
        View userColor = (View) element.findViewById(R.id.userColor); 
        TextView username = (TextView)element.findViewById(R.id.username);

        

        userColor.setBackgroundColor(Color.parseColor(users.get(usernames[position])));
        username.setText(usernames[position]);


        
        return element;
    }

}
