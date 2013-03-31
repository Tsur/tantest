package com.scripturesos.tantest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;

public class ContactsActivity extends ActionBarActivity {

	//TextView debug;
	ListView contactsListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTitle(R.string.act_contacts_title);
		
		super.onCreate(savedInstanceState);
		
		/* INIT CONTENT VIEW */
		setContentView(R.layout.activity_contacts);
		
		contactsListView = (ListView) findViewById(R.id.act_contacts_lv);
		
		getContacts();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_contacts, menu);
		return true;
	}
	
	private void getContacts()
	{
		ContentResolver cr = getContentResolver();
		 
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		
		Map<String,String> phonesList = new HashMap<String,String>();
		
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
		
		cursor.close();

		//debug.setText(contacts);
		
		//ListView lv = new ListView(this);
		
		//Conectamos con el servidor y mandamos telefonos
		
		ArrayList<ContactItemListView> contactItems = new ArrayList<ContactItemListView>();
		JSONObject jsonContact = null;
		String id = null;
		ContactItemListView contact;
		
		//Recorremos respuesta del servidor y generamos el listView
		try 
		{
			JSONArray serverContacts = getContactsFromServer(phonesList.keySet());
			
			for(int i = 0; i < serverContacts.length(); i++)
			{
			
				jsonContact = serverContacts.getJSONObject(i);
				id = jsonContact.getString("id");
				
				if(phonesList.containsKey(id) == true)
				{
					contact = new ContactItemListView(Long.parseLong(id), phonesList.get(id), jsonContact.getString("status"), jsonContact.getString("points"), jsonContact.getString("photo"));
					
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
	
	private JSONArray getContactsFromServer(Collection<String> contacts) throws JSONException
	{
		
		//Server.setCommand("getContacts", contacts);
		
		try 
		{
			return new JSONArray("[]");//new JSONArray(Server.send());
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new JSONArray("[]");
	}

}
