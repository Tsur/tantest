package com.scripturesos.tantest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ContactListAdapter extends BaseAdapter
{
	
	protected Activity activity;
	protected ArrayList<ContactItemListView> contacts;
	         
	public ContactListAdapter(Activity activity, ArrayList<ContactItemListView> contacts) 
	{
		    this.activity = activity;
		    this.contacts = contacts;
	}
	 
	@Override
	public int getCount() 
	{
		  return contacts.size();
	}
	 
	@Override
	public Object getItem(int position) 
	{
		  return contacts.get(position);
	}
	 
	@Override
	public long getItemId(int position) 
	{
		  return contacts.get(position).getId();
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
            //Save holder
            vi.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) vi.getTag();
		}
		             
		ContactItemListView contact = contacts.get(position);
		         
		//ImageView image = (ImageView) vi.findViewById(R.id.contacts_lv_img);

		//Save image to cache
        /*if(Cache.images.get(position) == null)
        {
            InputStream is;
            Drawable img = null;
			try 
			{
				Log.i("tantes",contact.getImg());
				
				
				is = (InputStream) new URL(contact.getImg()).getContent();
				img = Drawable.createFromStream(is, "lo que sea");
			} 
			catch (MalformedURLException e) 
			{
				// TODO Auto-generated catch block
				img = activity.getResources().getDrawable(R.drawable.profile);
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				img = activity.getResources().getDrawable(R.drawable.profile);
			}
            
            Cache.images.put(position, img);
        }
        */
		
        if(Cache.images.get(position) == null)
        {
            Cache.images.put(position, activity.getResources().getDrawable(R.drawable.profile));
        }
        
		//int imageResource = activity.getResources().getIdentifier(contact.getImg(), null, activity.getPackageName());
		//holder.image.setImageDrawable(activity.getResources().getDrawable(imageResource));
        holder.image.setImageDrawable(Cache.images.get(position));
		         
		//TextView name = (TextView) vi.findViewById(R.id.contacts_lv_name);
		holder.name.setText((contact.getName().length() > 32) ? contact.getName().substring(0, 29)+ "..." : contact.getName());
		         
		//TextView status = (TextView) vi.findViewById(R.id.contacts_lv_status);
		holder.status.setText((contact.getStatus().length() > 32) ? contact.getStatus().substring(0, 29)+ "..." : contact.getStatus());
		
		//TextView points = (TextView) vi.findViewById(R.id.contacts_lv_points);
		holder.points.setText(contact.getPoints());
		 
		return vi;
	  }
	
	 class ViewHolder
	 {
	        TextView name;
	        TextView status;
	        TextView points;
	        ImageView image;
	 }
	 
	 static class Cache
	 {
	 	static SparseArray<Drawable> images = new SparseArray<Drawable>();
	 }
	 

}