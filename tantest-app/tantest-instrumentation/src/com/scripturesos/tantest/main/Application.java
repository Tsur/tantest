package com.scripturesos.tantest.main;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.scripturesos.tantest.main.R;

public class Application extends SherlockActivity 
{
	protected Typeface fontTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		/* ACTION BAR CONFIGURATION */
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);

		LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = (View) inflator.inflate(R.layout.main_actionbar_title, null);
		
		fontTitle = Typeface.createFromAsset(getAssets(), "fuentes/kg.ttf");
		TextView title = (TextView)v.findViewById(R.id.main_actionbar_textTitle);
		title.setText(getTitle());
		title.setTypeface(fontTitle);

		//assign the view to the actionbar
		getSupportActionBar().setCustomView(v);

	}
	
	/*protected void onCreate(Bundle savedInstanceState, int title)
	{
		setTitle(title);
		
		onCreate(savedInstanceState);
	}*/

}
