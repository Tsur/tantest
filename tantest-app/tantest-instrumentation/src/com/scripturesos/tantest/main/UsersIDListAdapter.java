package com.scripturesos.tantest.main;

import java.util.ArrayList;

import com.scripturesos.tantest.main.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
//import android.util.SparseArray;


public class UsersIDListAdapter extends BaseAdapter
{
	
	protected Activity activity;
	protected ArrayList<String> users;
	         
	public UsersIDListAdapter(Activity activity, ArrayList<String> users) 
	{
		    this.activity = activity;
		    this.users = users;
	}
	 
	public ArrayList<String> getUsers()
	{
		return users;
	}
	
	public void add(String client)
	{
		users.add(client);
		//notifyDataSetChanged();
	}
	
	public int getCount() 
	{
		  return users.size();
	}
	 
	public Object getItem(int position) 
	{
		  return users.get(position);
	}
	 
	public long getItemId(int position) 
	{
		  return position;
	}
	 
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View vi = convertView;
		ViewHolder holder = null;
		
		if(vi == null || !( vi.getTag() instanceof ViewHolder))
		{
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.users_listview, null);
			
			//holder
			holder = new ViewHolder();
            holder.image = (ImageView) vi.findViewById(R.id.users_lv_img);
            holder.email =(TextView) vi.findViewById(R.id.users_lv_name);
            holder.deno =  (TextView) vi.findViewById(R.id.users_lv_deno);
            holder.level = (TextView) vi.findViewById(R.id.users_lv_level);
            //Save holder
            vi.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) vi.getTag();
		}
		             
		UserItemListView user = UsersUtil.contactsCache.get(users.get(position));
		      
		//Log.i("tantest","name: "+contact.getName());

        holder.image.setImageDrawable(UsersUtil.imagesCache.get(user.getEmail()));
		         
		//holder.email.setText((contact.getName().length() > 32) ? contact.getName().substring(0, 29)+ "..." : contact.getName());
		holder.email.setText(user.getEmail().toString());
		         
		holder.deno.setText(user.getDeno().toString());
		
		holder.level.setText(Integer.toString(user.getLevel()));
	    
		return vi;
		
	  }
	
	 class ViewHolder
	 {
	        TextView email;
	        TextView deno;
	        TextView level;
	        ImageView image;
	 }

}