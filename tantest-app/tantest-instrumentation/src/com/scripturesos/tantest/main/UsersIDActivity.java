package com.scripturesos.tantest.main;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.scripturesos.connection.HttpUtil;

public class UsersIDActivity extends Application
{
	//TextView debug;
	private ListView usersList;
	private ProgressBar progress;
	private EditText searchbox;
	private ImageButton searchbtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTitle(R.string.act_usersID_title);
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_usersid);
		
		searchbox = (EditText) findViewById(R.id.act_usersID_searchbox);
		progress = (ProgressBar) findViewById(R.id.act_usersID_loader);
		usersList =(ListView) findViewById(R.id.act_usersID_results);
		searchbtn = (ImageButton) findViewById(R.id.act_usersID_searchbtn);
		handler = new UsersIDActivityHandler(this);
	}
	
	public void search(View view)
	{
		view.setEnabled(false);
		
		if(searchbox.getText().toString().equals(""))
		{
			error("Introduzca datos requeridos");
			return;
		}
		
		progress.setVisibility(View.VISIBLE);
		usersList.setVisibility(View.GONE);
		
		(new Thread() {
		    
			public void run() 
			{
				try 
				{
					String id = searchbox.getText().toString();
					JSONObject response;
					
					if(HttpUtil.isValidEmail(id))
					{
						response = HttpUtil.get(HttpUtil.getURL(HttpUtil.ID, new String[]{"email",id}));
					}
					else if(HttpUtil.isValidPhone(id))
					{
						response = HttpUtil.get(HttpUtil.getURL(HttpUtil.ID, new String[]{"phone",id}));
					}
					else if(id.startsWith("#"))
					{
						response = HttpUtil.get(HttpUtil.getURL(HttpUtil.ID, new String[]{"tid",id}));
					}
					else if(id.startsWith("<<e>>"))
					{
						id = id.replace("<<e>>", "");
						response = HttpUtil.get(HttpUtil.getURL(HttpUtil.ID, new String[]{"email",id}));
					}
					else if(id.startsWith("<<p>>"))
					{
						id = id.replace("<<p>>", "");
						response = HttpUtil.get(HttpUtil.getURL(HttpUtil.ID, new String[]{"phone",id}));
					}
					else
					{
						response = HttpUtil.get(HttpUtil.getURL(HttpUtil.ID, new String[]{"alias",id}));
					}
					
					Message msg = new Message();
					
					if(response.getInt("error") == 1)
					{
						//Error not found 
						msg.what = 1;
					}
					else
					{
						//Found
						msg.what = 2;
						
						JSONArray users = response.getJSONArray("users");
						msg.obj = users;
						
						for(int i=0;i<users.length();i++)
						{
							UsersUtil.saveUser(users.getJSONObject(i));
						}
					}
					
					handler.sendMessage(msg);	
				}
				catch(Exception e)
				{	
					Message msg = new Message();
					msg.what = 10;
					handler.sendMessage(msg);
				}
				
		    }
			
		}).start();
	}

	public void displayUsersFound(JSONArray users)
	{
		progress.setVisibility(View.GONE);
		
		JSONObject user;
		
		try 
		{
			ArrayList<String> usr = new ArrayList<String>();
			
	        for(int i=0;i<users.length();i++)
			{
				user = users.getJSONObject(i);	
				usr.add(user.getString("email"));
			}
	        
			UsersIDListAdapter adapter = new UsersIDListAdapter(UsersIDActivity.this, usr);
			 
			usersList.setAdapter(adapter);
			 
			usersList.setOnItemClickListener(new OnItemClickListener()
			{

				public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3)
			    { 
					
					Intent mIntent = new Intent();
				    
				    Bundle bundle = new Bundle();
					
					UserItemListView user = UsersUtil.contactsCache.get(((UsersIDListAdapter) usersList.getAdapter()).getUsers().get(position));
					
					bundle.putString("userID", user.getEmail());

					mIntent.putExtras(bundle);
				    
				    setResult(RESULT_OK, mIntent);
				    
				    finish();
			    }
			});
	        
	        
	        usersList.setVisibility(View.VISIBLE);
		} 
		catch (JSONException e) 
		{

		}
		finally
		{
			searchbtn.setEnabled(true);
		}
	}
	
	public void error(String txt)
	{	
		searchbtn.setEnabled(true);
		progress.setVisibility(View.GONE);
		Toast.makeText(UsersIDActivity.this, txt, Toast.LENGTH_SHORT).show();
	}
	
	public static class UsersIDActivityHandler extends Handler 
	{
        private UsersIDActivity parent;

        public UsersIDActivityHandler(UsersIDActivity parent) 
        {
            this.parent = parent;
        }

        public void handleMessage(Message msg) 
        {
            parent.handleMessage(msg);
        }
    }

	public void handleMessage(Message msg) 
	{
        //JSONObject response = (JSONObject) msg.obj;
		switch(msg.what) 
        {
			case 1: error("No se ha encontrado ningún resultado");break;
        	case 2: displayUsersFound((JSONArray) msg.obj);break;
        	case 10: error("Problema de Conexión.");break;
        	default:break;
        }
    }
	
	public UsersIDActivityHandler handler;
}
