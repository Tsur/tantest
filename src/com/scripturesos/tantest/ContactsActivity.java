package com.scripturesos.tantest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.scripturesos.tantest.connection.ClientResponse;
import com.scripturesos.tantest.connection.ClientSocket;

public class ContactsActivity extends ActionBarActivity {

	//TextView debug;
	private ListView contactsListView;
	private ProgressBar progress;
	private Map<String,String> phonesList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTitle(R.string.act_contacts_title);
		
		super.onCreate(savedInstanceState);
		
		/* INIT CONTENT VIEW */
		setContentView(R.layout.activity_contacts);
	
		handler = new ContactsActivityHandler(ContactsActivity.this);
		
		progress = (ProgressBar) findViewById(R.id.act_contacts_loader);
		
		(new Thread() {
		    
			public void run() 
			{
				getContacts();
		    }
		}).start();
		
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
			
			String contactId =
					cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			
			String name =
					cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	        //
	        //  Get all phone numbers.
	        //
			Cursor phones = cr.query(Phone.CONTENT_URI, null,
		            Phone.CONTACT_ID + " = " + contactId, null, null);

			
			while(phones.moveToNext())
			{
				
				String number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
				
				phonesList.put(number,name);

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
		       
		}
		
		//Tenemos en phoneList un diccionario/ mapa de numeros -> nombre
		cursor.close();
		
		//Conectamos con el servidor y mandamos telefonos
		if(phonesList.size() > 0)
		{
			ClientSocket
			.getInstance()
			.send("getContacts", phonesList.keySet(), new ClientResponse(handler,0));	
		}
		else
		{
			
			Message msg = new Message();
			
			msg.what = 1;
			
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
        						
        						/*
        						ClientSocket
        						.getInstance()
        						.send("getContacts", phonesList.keySet(), new ClientResponse(handler,0));
        						*/	
        					}
        					else
        					{
        						
        						Message msg = new Message();
        						
        						msg.what = 2;
        						
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
        				}
        		    }
        		}).start();
            }           
            break;
        }
        
    }
	
	public void displayContacts(JSONObject serverContacts)
	{

		progress.setVisibility(View.GONE);
		
		contactsListView = (ListView) findViewById(R.id.act_contacts_lv);
		
		ArrayList<ContactItemListView> contactItems = new ArrayList<ContactItemListView>();
		JSONArray contacts;
		JSONObject jsonContact;
		String id = null;
		ContactItemListView contact;
		
		//Recorremos respuesta del servidor y generamos el listView
		try 
		{
			contacts = serverContacts.getJSONArray("response");
			
			for(int i = 0; i < contacts.length(); i++)
			{
			
				jsonContact = contacts.getJSONObject(i);
				id = jsonContact.getString("id");
				
				if(phonesList.containsKey(id) == true)
				{
					contact = new ContactItemListView(
							Long.parseLong(id), 
							jsonContact.getString("name").equals("") ? phonesList.get(id) : jsonContact.getString("name"), 
							jsonContact.getString("status"), 
							jsonContact.getString("points"), 
							jsonContact.getString("photo"));
					
					contactItems.add(contact);
				}
				
			} 
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    ContactListAdapter adapter = new ContactListAdapter(this, contactItems);
	         
	    contactsListView.setAdapter(adapter);
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
        	case 0: displayContacts((JSONObject)msg.obj);break;
        	case 1: displayNoAgenda(null);break;
        	case 2: displayNoAgenda(getString(R.string.contacts_no_agenda_import));break;
            default:break;
        }
    }
	
	public ContactsActivityHandler handler;

}
