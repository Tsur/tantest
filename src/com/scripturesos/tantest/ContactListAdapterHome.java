package com.scripturesos.tantest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
//import android.util.SparseArray;


public class ContactListAdapterHome extends BaseAdapter
{
	
	protected Activity activity;
	protected ArrayList<String> clients;
	         
	public ContactListAdapterHome(Activity activity, ArrayList<String> chats) 
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
	
	@Override
	public int getCount() 
	{
		  return clients.size();
	}
	 
	@Override
	public Object getItem(int position) 
	{
		  return clients.get(position);
	}
	 
	@Override
	public long getItemId(int position) 
	{
		  return position;
	}
	 
	@Override
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
            holder.name =(TextView) vi.findViewById(R.id.contacts_lv_name);
            holder.status =  (TextView) vi.findViewById(R.id.contacts_lv_status);
            holder.points = (TextView) vi.findViewById(R.id.contacts_lv_points);
            holder.date = (TextView) vi.findViewById(R.id.contacts_lv_date);
            //Save holder
            vi.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) vi.getTag();
		}
		             
		ContactItemListView contact = ContactUtil.Cache.contacts.get(clients.get(position));
		      
		//Log.i("tantest","name: "+contact.getName());
		
        if( ContactUtil.Cache.images.get(contact.getID()) == null)
        {
        	ContactUtil.Cache.images.put(contact.getID(), activity.getResources().getDrawable(R.drawable.profile));
        }
        
		//int imageResource = activity.getResources().getIdentifier(contact.getImg(), null, activity.getPackageName());
		//holder.image.setImageDrawable(activity.getResources().getDrawable(imageResource));
        holder.image.setImageDrawable( ContactUtil.Cache.images.get(contact.getID()));
		         
		//TextView name = (TextView) vi.findViewById(R.id.contacts_lv_name);
		holder.name.setText((contact.getName().length() > 32) ? contact.getName().substring(0, 29)+ "..." : contact.getName());
		         
		//TextView status = (TextView) vi.findViewById(R.id.contacts_lv_status);
		holder.status.setText((contact.getStatus().length() > 32) ? contact.getStatus().substring(0, 29)+ "..." : contact.getStatus());
		
		//TextView points = (TextView) vi.findViewById(R.id.contacts_lv_points);
		holder.points.setText(contact.getPoints());
	    
		if(contact.getLastDate() != null)
		{
			
			Calendar c1 = Calendar.getInstance(); // today
			Calendar c2 = Calendar.getInstance();
			c2.setTime(contact.getLastDate()); // your date

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
				
				holder.date.setText(formatter.format(contact.getLastDate()));
			}
					
		}

		return vi;
		
	  }
	
	 class ViewHolder
	 {
	        TextView name;
	        TextView status;
	        TextView points;
	        TextView date;
	        ImageView image;
	 }

}