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
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.view.Menu;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.scripturesos.tantest.MainActivity.MainActivityHandler;
import com.scripturesos.tantest.connection.ClientResponse;
import com.scripturesos.tantest.connection.ClientSocket;
import com.scripturesos.tantest.connection.MainClientSocketController;

public class ContactsActivity extends ActionBarActivity {

	//TextView debug;
	private ListView contactsListView;
	private ProgressBar loader;
	private Map<String,String> phonesList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTitle(R.string.act_contacts_title);
		
		super.onCreate(savedInstanceState);
		
		/* INIT CONTENT VIEW */
		setContentView(R.layout.activity_contacts);
		
		loader = (ProgressBar) findViewById(R.id.contacts_loader);
		loader.setIndeterminate(true);
		
		Thread contacts = new Thread() {
		    
			public void run() 
			{
				contactsListView = (ListView) findViewById(R.id.act_contacts_lv);
				handler = new ContactsActivityHandler(ContactsActivity.this);
				getContacts();
		    }
		};
		
		contacts.start();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_contacts, menu);
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
		ClientSocket
		.getInstance()
		.send("getContacts", phonesList.keySet(), new ClientResponse(handler,0));	
		 
	}
	
	public void displayContacts(JSONObject serverContacts)
	{

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
        	case 0: displayContacts((JSONObject)msg.obj);
            break;
        }
    }
	
	public ContactsActivityHandler handler;

}
