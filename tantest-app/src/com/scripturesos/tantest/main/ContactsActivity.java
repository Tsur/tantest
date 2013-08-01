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

public class ContactsActivity extends Application {

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
		
		handler = new ContactsActivityHandler(ContactsActivity.this);
		
		/* INIT CONTENT VIEW */
		setContentView(R.layout.activity_contacts);

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
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		
		switch(item.getItemId())
		{
			case R.id.menu_contacts_import: importVCF(null);break;
			default:break;
		}
		
		return true;
	}
	
    @Override
    public void onConfigurationChanged(Configuration newConfig) 
    {
        super.onConfigurationChanged(newConfig);
    }
	
	
	private void getContacts()
	{
		ContactUtil.createAgenda(this, "client_id", HomeActivity.country_id);

		if(ContactUtil.Cache.agenda.size() > 0)
		{
			
			try 
			{
				Message msg = new Message();
				msg.what = 0;
				msg.obj = ContactUtil.createContacts(ContactUtil.Cache.agenda.keySet().toString());
				handler.sendMessage(msg);
				/*Message msg = new Message();
				msg.what = 0;
				msg.obj = HttpUtil.post(HttpUtil.GET_CONTACTS,new String[]{phonesList.keySet().toString()});
				handler.sendMessage(msg);*/
			}
			catch (Exception e) 
			{
				//Error diversas causas: conexion, parseo, ...
				Message msg = new Message();
				msg.what = 12;
				handler.sendMessage(msg);
			}

		}
		else
		{
			//Le decimos que importe desde fichero
			Message msg = new Message();
			msg.what = 11;
			handler.sendMessage(msg);
		}
		
		 
	}
	
	public void displayNoAgenda(String msg)
	{
		
		progress.setVisibility(View.GONE);
		
		TextView info = (TextView) findViewById(R.id.act_contacts_text);
		
		if(msg != null)
		{
			info.setText(msg);
		}
		
		info.setVisibility(View.VISIBLE);
	}
	
	
	public void importVCF(View view)
	{
		Intent target = FileUtils.createGetContentIntent();
		//target.setType("file/*"); 
	    Intent intent = Intent.createChooser(target, "Busca tu fichero VCF");
	    
	    try 
	    {
	        startActivityForResult(intent, 0);
	    } 
	    catch (ActivityNotFoundException e) 
	    {
	        // The reason for the existence of aFileChooser
	    }

	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
        super.onActivityResult(requestCode, resultCode, data);
        
        switch (requestCode) 
        {
            case 0:      
            if (resultCode == RESULT_OK) 
            {  
                // Get the Uri of the selected file 
                final Uri uri = data.getData();
                Log.i("tantest", "File Uri: " + uri.toString());
                // Get the path
                progress.setVisibility(View.VISIBLE);
                
                (new Thread() {
        		    
        			public void run() 
        			{
        				try 
        				{
        					// Get the file instance
        	                // File file = new File(path);
        	                // Initiate the upload
        					
        					//String path = FileUtils.getPath(this, uri);
        					//Log.i("tantest", "File Path: " + path);
        					
        					// Create a File from this Uri
        		            File file = FileUtils.getFile(uri);
        		            
        					ContactUtil.createAgendaFromVCard(file, "client_id", HomeActivity.country_id);
        			        
        					if(ContactUtil.Cache.agenda.size() > 0)
        					{
        						
        						Message msg = new Message();
        						msg.what = 0;
        						msg.obj = ContactUtil.createContacts(ContactUtil.Cache.agenda.keySet().toString());
        						handler.sendMessage(msg);

        					}
        					else
        					{
        						//Esta vacio el fichero importado
        						Message msg = new Message();
        						msg.what = 10;
        						handler.sendMessage(msg);
        					}

        				} 
        				catch(Exception e) 
        				{
							e.printStackTrace();
						}
        		    }
        		}).start();
            }           
            break;
        }
        
    }
	
	/*@Override
	public void onBackPressed() 
	{
	    
	     Intent mIntent = new Intent();
	     
	    try 
	    {
	    	if(contactItems != null)
		    {
		    	Bundle bundle = new Bundle();
		    	
				bundle.putString("contacts", HttpUtil.toString(contactItems));
				
				mIntent.putExtras(bundle);
			} 

	    }
	    catch (IOException e) 
    	{
			
		}
	    
	    setResult(RESULT_OK, mIntent);
	    super.onBackPressed();
	}*/
	
	/*public void makeContactsList(JSONObject serverContacts)
	{

		//ArrayList<ContactItemListView> contactItems = new ArrayList<ContactItemListView>();
		final JSONArray contacts;
		//JSONObject jsonContact;
		//String id = null;
		//ContactItemListView contact;
		
		//Recorremos respuesta del servidor y generamos el listView
		try 
		{
			contacts = serverContacts.getJSONArray("response");
			
			if(contacts.length() == 0)
			{
				displayNoAgenda("Ninguno de tus contactos utiliza tantest, qué pena ...");
			}
			else
			{

				(new Thread() {
				    
					public void run() 
					{
						 
						 ArrayList<String> contactItems = new ArrayList<String>();
				 		 JSONObject jsonContact;
				 		 String id = null;
				 		 ContactItemListView contact;
				 		 
				         for(int i = 0; i < contacts.length(); i++)
						 {
				        	
				        	 Drawable img = null;
				        	 contact = null;
				        	 
				        	 try 
								{
									
									jsonContact = contacts.getJSONObject(i);
									id = jsonContact.getString("client");
									if(phonesList.containsKey("\""+id+"\"") == true)
									{
										 
										contact = new ContactItemListView(
												id,
												jsonContact.getString("photo"),
												jsonContact.getString("name").equals("") ? phonesList.get("\""+id+"\"") : jsonContact.getString("name"), 
												jsonContact.getString("status"), 
												jsonContact.getString("points")
											);
									
										contactItems.add(id);
										
										if(ContactListAdapter.Cache.contacts.get(id) == null)
										{
											ContactListAdapter.Cache.contacts.put(id, contact);
										}
										
										InputStream is = (InputStream) new URL(contact.getImg()).getContent();
										img = Drawable.createFromStream(is, null);
										
										if(ContactListAdapter.Cache.images.get(id) == null)
										{
											ContactListAdapter.Cache.images.put(id, img);
										}

									}

								} 
								catch (Exception e) 
								{
									
								}	
						}

						Message msg = new Message();
						msg.what = 3;
						msg.obj = contactItems;
						handler.sendMessage(msg);	

				    }
				}).start();
			}
			
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}*/
	
	public void displayContactsList(ArrayList<String> contactItems)
	{
		Log.i("tantest", "Elementos en adapter"+ contactItems.size());
		ContactListAdapter adapter = new ContactListAdapter(ContactsActivity.this, contactItems);
		 
		contactsListView = (ListView) findViewById(R.id.act_contacts_lv);
		 
		contactsListView.setAdapter(adapter);
		 
        contactsListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override 
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3)
		    { 
				
				Intent mIntent = new Intent();
			    
			    Bundle bundle = new Bundle();
				
				ContactItemListView contact = ContactUtil.Cache.contacts.get(((ContactListAdapter) contactsListView.getAdapter()).getContacts().get(position));
				//bundle.putString("chat", HttpUtil.toString(contact));
				Log.i("tantest", "ID en listView"+  contact.getID());
				
				bundle.putString("chat", contact.getID());
				
				try 
				{
					bundle.putString("contacts", HttpUtil.toString(((ContactListAdapter) contactsListView.getAdapter()).getContacts()));
				} 
				catch (IOException e) 
				{

				}
				//bundle.putString("chat", HttpUtil.toString(contactItems.get(position));
					
				mIntent.putExtras(bundle);
			    
			    setResult(RESULT_OK, mIntent);
			    
			    finish();
		    }
		});
        
	    
        contactsListView.setVisibility(View.VISIBLE);
		    
		progress.setVisibility(View.GONE);
	}
	
    @Override
    public void onResume()
    {
    	Log.i("tantest","RESUME CONTACTS ACTIVITY");
    	
    	//ClientSocket.getInstance().init("phone","country");
    	
    	super.onResume();
    }
	
	public class ContactsActivityHandler extends Handler 
	{
        private ContactsActivity parent;

        public ContactsActivityHandler(ContactsActivity parent) 
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
        switch(msg.what) 
        {
        	//case 0: makeContactsList((JSONObject)msg.obj);break;
        	case 0:
        		ArrayList<String> contacts = (ArrayList<String>)msg.obj;
        		if(contacts.size() == 0)
    			{
    				displayNoAgenda("Ninguno de tus contactos utiliza tantest, qué pena ...");
    			}
        		else
        		{
        			displayContactsList(contacts);
        		}
        		break;
        	case 10: displayNoAgenda(getString(R.string.contacts_no_agenda_import));break;
        	case 11: displayNoAgenda(getString(R.string.contacts_no_agenda));break;
        	case 12: ifError("Inténtalo de nuevo más tarde por favor");
        	//case 3: displayContactsList((ArrayList<String>)msg.obj);break;
            default:break;
        }
    }
	
	public void ifError(String txt)
	{
		Toast.makeText(ContactsActivity.this, txt, Toast.LENGTH_SHORT).show();
	}
	
	public ContactsActivityHandler handler;

    @Override
    public void onBackPressed() 
    {
    	if(contactsListView != null)
    	{
    		Intent mIntent = new Intent();
		    
		    Bundle bundle = new Bundle();
			
			try 
			{
				bundle.putString("contacts", HttpUtil.toString(((ContactListAdapter) contactsListView.getAdapter()).getContacts()));
			} 
			catch (IOException e) 
			{

			}
			//bundle.putString("chat", HttpUtil.toString(contactItems.get(position));
				
			mIntent.putExtras(bundle);
		    
		    setResult(RESULT_OK, mIntent);
		    
		    //finish();
    	}
    	
    	super.onBackPressed();
    }
}
