package com.scripturesos.tantest;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
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
		View vi=convertView;
		         
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.contacts_listview, null);
		}
		             
		ContactItemListView contact = contacts.get(position);
		         
		ImageView image = (ImageView) vi.findViewById(R.id.contacts_lv_img);
		
		//InputStream is = (InputStream) new URL(contact.getImg()).getContent();
        //Drawable d = Drawable.createFromStream(is, contact.getImg());
        //image.setImageDrawable(d);
		 
		int imageResource = activity.getResources().getIdentifier(contact.getImg(), null, activity.getPackageName());
		image.setImageDrawable(activity.getResources().getDrawable(imageResource));
		         
		TextView name = (TextView) vi.findViewById(R.id.contacts_lv_name);
		name.setText(contact.getName());
		         
		TextView status = (TextView) vi.findViewById(R.id.contacts_lv_status);
		status.setText(contact.getStatus());
		
		TextView points = (TextView) vi.findViewById(R.id.contacts_lv_points);
		points.setText(contact.getPoints());
		 
		return vi;
	  }
}