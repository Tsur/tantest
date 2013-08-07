package com.scripturesos.tantest.main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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


public class UsersHomeListAdapter extends BaseAdapter
{
	
	protected Activity activity;
	protected ArrayList<String> clients;
	         
	public UsersHomeListAdapter(Activity activity, ArrayList<String> chats) 
	{
		    this.activity = activity;
		    this.clients = chats;
	}
	 
	public ArrayList<String> getContacts()
	{
		return clients;
	}
	
	public void add(String client)
	{
		clients.add(client);
		//notifyDataSetChanged();
	}
	
	public int getCount() 
	{
		  return clients.size();
	}
	 
	public Object getItem(int position) 
	{
		  return clients.get(position);
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
			vi = inflater.inflate(R.layout.contacts_listview, null);
			
			//holder
			holder = new ViewHolder();
            holder.image = (ImageView) vi.findViewById(R.id.contacts_lv_img);
            holder.email =(TextView) vi.findViewById(R.id.contacts_lv_name);
            holder.deno =  (TextView) vi.findViewById(R.id.contacts_lv_status);
            holder.level = (TextView) vi.findViewById(R.id.contacts_lv_points);
            holder.date = (TextView) vi.findViewById(R.id.contacts_lv_date);
            //Save holder
            vi.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) vi.getTag();
		}
		             
		UserItemListView user = UsersUtil.contactsCache.get(clients.get(position));
	      
		//Log.i("tantest","name: "+contact.getName());

        holder.image.setImageDrawable(UsersUtil.imagesCache.get(user.getEmail()));
        
        holder.email.setText(user.getEmail().toString());
        
		holder.deno.setText(user.getDeno().toString());
		
		holder.level.setText(Integer.toString(user.getLevel()));
	    
		if(user.getLastDate() != null)
		{
			
			Calendar c1 = Calendar.getInstance(); // today
			Calendar c2 = Calendar.getInstance();
			c2.setTime(user.getLastDate()); // your date

			if(c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
			   && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR))
			{
				holder.date.setText("HOY");
				
				return vi;
			}
			
			c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday
			
			if(c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
					   && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR))
			{
				holder.date.setText("AYER");
			}
			else
			{
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
				
				holder.date.setText(formatter.format(user.getLastDate()));
			}
					
		}

		return vi;
		
	  }
	
	 class ViewHolder
	 {
	        TextView email;
	        TextView deno;
	        TextView level;
	        ImageView image;
	        TextView date;
	 }

}