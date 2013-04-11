package com.scripturesos.tantest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import a_vcard.android.syncml.pim.PropertyNode;
import a_vcard.android.syncml.pim.VDataBuilder;
import a_vcard.android.syncml.pim.VNode;
import a_vcard.android.syncml.pim.vcard.VCardException;
import a_vcard.android.syncml.pim.vcard.VCardParser;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.scripturesos.tantest.connection.HttpUtil;

public class ContactsActivity extends Application {

	//TextView debug;
	private ListView contactsListView;
	private ProgressBar progress;
	private Map<String,String> phonesList;
	//private ArrayList<ContactItemListView> contactItems;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTitle(R.string.act_contacts_title);
		
		super.onCreate(savedInstanceState);
		
		Log.i("tantest","Creating contacts");
		
		/* INIT CONTENT VIEW */
		setContentView(R.layout.activity_contacts);
	
		handler = new ContactsActivityHandler(ContactsActivity.this);
		
		progress = (ProgressBar) findViewById(R.id.act_contacts_loader);
		
		Bundle extras = getIntent().getExtras();
		
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
		}

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
	
	/*
	 * Obtenemos contactos de la Agenda
	 */
	private void getContacts()
	{
		ContentResolver cr = getContentResolver();
		 
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		
		phonesList = new HashMap<String,String>();
		
		if(cursor.moveToFirst())
		{
			
			while(!cursor.isAfterLast())
			{
				
				String contactId =
						cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				
				String name =
						cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		        //
		        //  Get all phone numbers.
		        //
				Cursor phones = cr.query(Phone.CONTENT_URI, null,
			            Phone.CONTACT_ID + " = " + contactId, null, null);
	
				String country = HomeActivity.country_id;
				
				while(phones.moveToNext())
				{
					
					String number = phones.getString(phones.getColumnIndex(Phone.NUMBER)).trim();
					
					if(!number.startsWith("+"))
					{
						number = country+number;
					}
					
					number = number.replace("-","");
					number = number.replace(")","");
					number = number.replace("(","");
					number = number.replace(" ","");
					
					Log.i("tantest",number);
					phonesList.put("\""+number+"\"",name);
	
					/*int type = phones.getInt(phones.getColumnIndex(Phone.TYPE));
					
					switch (type) {
			                case Phone.TYPE_HOME:
			                    // do something with the Home number here...
			                    break;
			                case Phone.TYPE_MOBILE:
			                    // do something with the Mobile number here...
			                    break;
			                case Phone.TYPE_WORK:
			                    // do something with the Work number here...
			                    break;
			    	}*/
				}
				
			    phones.close();
			    cursor.moveToNext();
			}
			
		}
		
		//Tenemos en phoneList un diccionario/ mapa de numeros -> nombre
		cursor.close();
		
		if(phonesList.containsKey("\""+HomeActivity.client_id+"\""))
		{
			phonesList.remove("\""+HomeActivity.client_id+"\"");
		}
		
		
		//Conectamos con el servidor y mandamos telefonos
		if(phonesList.size() > 0)
		{
			
			try 
			{
				Message msg = new Message();
				msg.what = 0;
				msg.obj = HttpUtil.post(HttpUtil.GET_CONTACTS,new String[]{phonesList.keySet().toString()});
				handler.sendMessage(msg);
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
        		            
        					//Read the file
        					BufferedReader reader = new BufferedReader(new InputStreamReader(
        			                new FileInputStream(file), "UTF-8"));
        			        
        			        String vcardString = "";
        			        
        			        String line;
        			        
        			        while ((line = reader.readLine()) != null) 
        			        {
        			            vcardString += line + "\n";
        			        }
        			        
        			        reader.close();
        			        
        			        Log.i("tantest", "File readed");
        			        
        			        VCardParser parser = new VCardParser();
        			        VDataBuilder builder = new VDataBuilder();
        			        
        			      //parse the string
        			        boolean parsed = parser.parse(vcardString, "UTF-8", builder);
        			        
        			        if(!parsed) 
        			        {
        			        	Log.i("tantest", "Could not parse vCard file: ");
        			        	return;
        			        }

        			        Log.i("tantest", "File parsed");
        			        
        			        //get all parsed contacts
        			        List<VNode> contacts = builder.vNodeList;

        			        //do something for all the contacts
        			        for (VNode contact : contacts) 
        			        {
        			            ArrayList<PropertyNode> props = contact.propList;

        			            //contact name - FN property
        			            String name = null;
        			            String phone = null;
        			            
        			            for(PropertyNode prop : props) 
        			            {
        			                if("FN".equals(prop.propName)) 
        			                {
        			                    name = prop.propValue;
        			                    
        			                }
        			                
        			                if ("TEL".equals(prop.propName)) 
        			                {
        			                    phone = prop.propValue.replace("-", "");
        			                    break;
        			                }
        			            }

        			            //similarly for other properties (N, ORG, TEL, etc)
        			            if(name != null && phone != null)
        			            {
        			            	phonesList.put(phone,name);
        			            	Log.i("tantest", "Name: " + name);
            			            Log.i("tantest", "phone: " + phone);
        			            }
        			            
        			        }
        			        
        			        if(phonesList.size() > 0)
        					{
        			        	Message msg = new Message();
        						msg.what = 0;
        						msg.obj = HttpUtil.post(HttpUtil.GET_CONTACTS,new String[]{phonesList.keySet().toString()});
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
        				catch (UnsupportedEncodingException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				} catch (FileNotFoundException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				} catch (IOException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				} catch (VCardException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				} catch (JSONException e) {
							// TODO Auto-generated catch block
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
	
	public void makeContactsList(JSONObject serverContacts)
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
								
									/*HttpURLConnection connection = (HttpURLConnection)new URL(contact.getImg()).openConnection();
								    connection.setRequestProperty("User-agent","Mozilla/4.0");
	
								    connection.connect();
								    InputStream input = connection.getInputStream();*/

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
	
	}
	
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
				
				ContactItemListView contact = ContactListAdapter.Cache.contacts.get(((ContactListAdapter) contactsListView.getAdapter()).getContacts().get(position));
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
        	case 0: makeContactsList((JSONObject)msg.obj);break;
        	case 10: displayNoAgenda("El fichero importado no contiene información suficiente. Por favor, utilize otro");break;
        	case 11: displayNoAgenda(getString(R.string.contacts_no_agenda_import));break;
        	case 12: ifError("Inténtalo de nuevo más tarde por favor");
        	case 3: displayContactsList((ArrayList<String>)msg.obj);break;
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
		    
		    finish();
    	}
    	
    	super.onBackPressed();
    }
}
