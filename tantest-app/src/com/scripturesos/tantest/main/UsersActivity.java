package com.scripturesos.tantest.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.scripturesos.tantest.connection.HttpUtil;

public class UsersActivity extends Application {

	//TextView debug;
	private ListView contactsListView;
	private ProgressBar progress;
	//private Map<String,String> phonesList;
	//private ArrayList<ContactItemListView> contactItems;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("tantest","Creating contacts");
		
		setTitle(R.string.act_contacts_title);
		
		super.onCreate(savedInstanceState);

		/* INIT CONTENT VIEW */
		setContentView(R.layout.activity_users);

		progress = (ProgressBar) findViewById(R.id.act_contacts_loader);
		
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
