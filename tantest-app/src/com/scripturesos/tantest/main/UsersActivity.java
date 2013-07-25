package com.scripturesos.tantest.main;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;

import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.actionbarsherlock.view.Menu;


public class UsersActivity extends Application {

	//TextView debug;
	private ListView contactsListView;
	private ProgressBar progress;
	private UserGameSurface userGameView;
	//private Map<String,String> phonesList;
	//private ArrayList<ContactItemListView> contactItems;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("tantest","Creating Activity Users");
		
		setTitle(R.string.act_contacts_title);
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_users);

		Display display = getWindowManager().getDefaultDisplay();
		
		Log.i("tantest","Activity Width:" + display.getWidth());
		Log.i("tantest","Activity Height:" + display.getHeight());
		
		userGameView = new UserGameSurface(this);
		userGameView.getThread().setSurfaceSize(display.getWidth(), display.getHeight());
		
		((RelativeLayout) findViewById(R.id.users_container)).addView(userGameView);
			
		((ProgressBar) findViewById(R.id.act_contacts_loader)).setVisibility(View.GONE);

       
		/* INIT CONTENT VIEW */
		//setContentView(R.layout.activity_users);

		//progress = (ProgressBar) findViewById(R.id.act_contacts_loader);
		
		/*Bundle extras = getIntent().getExtras();
		
		if(extras != null && extras.containsKey("contacts"))
    	{
			try 
			{
				Log.i("tantest","no pasamos por todo el proceso!");
				
				displayContactsList((ArrayList<String>) HttpUtil.fromString(extras.getString("contacts")));
			} 
			catch (Exception e) 
			{
				Log.i("tantest","pasamos por todo el proceso!");
				
				(new Thread() {
				    
					public void run() 
					{
						getContacts();
				    }
				}).start();
			}

    	}
		else
		{
			Log.i("tantest","pasamos por todo el proceso!");
			
			(new Thread() {
			    
				public void run() 
				{
					getContacts();
			    }
			}).start();
		}*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_contacts, menu);
		return true;
	}
}
