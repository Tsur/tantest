package com.scripturesos.tantest;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CountryListAdapter extends BaseAdapter
{
	
	protected Activity activity;
	         
	public CountryListAdapter(Activity activity) 
	{
		    this.activity = activity;
	}
	 
	@Override
	public int getCount() 
	{
		 return MainActivity.abbreviations.length;
		//return Locale.getISOCountries().length;
	}
	 
	@Override
	public Object getItem(int position) 
	{
		  return position;
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
			vi = inflater.inflate(R.layout.countries_listview, null);
			
			//holder
			holder = new ViewHolder();
            holder.image = (ImageView) vi.findViewById(R.id.countries_lv_img);
            holder.name =(TextView) vi.findViewById(R.id.countries_lv_name);
            //Save holder
            vi.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) vi.getTag();
		}

		//Save image to cache
        if(Cache.images.get(position) == null)
        {
            //Log.i("tantest","img: "+MainActivity.abbreviations[position]);
            
        	int imageResource = activity.getResources().getIdentifier(MainActivity.abbreviations[position].toLowerCase(), "drawable", activity.getPackageName());
			Drawable img = activity.getResources().getDrawable(imageResource);
            Cache.images.put(position, img);
        }
        
		holder.image.setImageDrawable(Cache.images.get(position));
		holder.name.setText(MainActivity.countries[position]);
		
		return vi;
	  }
	
	 class ViewHolder
	 {
	        TextView name;
	        ImageView image;
	 }
	 
	 static class Cache
	 {
	 	static SparseArray<Drawable> images = new SparseArray<Drawable>();
	 }
}