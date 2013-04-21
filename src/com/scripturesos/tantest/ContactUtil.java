package com.scripturesos.tantest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import a_vcard.android.syncml.pim.PropertyNode;
import a_vcard.android.syncml.pim.VDataBuilder;
import a_vcard.android.syncml.pim.VNode;
import a_vcard.android.syncml.pim.vcard.VCardParser;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

import com.scripturesos.tantest.connection.HttpUtil;

public class ContactUtil 
{

	public static void createAgenda(Activity act, String client, String country)
	{
		ContactUtil.Cache.agenda.clear();
		
		ContentResolver cr = act.getContentResolver();
		 
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		
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
					ContactUtil.Cache.agenda.put("\""+number+"\"",name);
	
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
		
		if(ContactUtil.Cache.agenda.containsKey("\""+client+"\""))
		{
			ContactUtil.Cache.agenda.remove("\""+client+"\"");
		}
	}
	
	public static void createAgendaFromVCard(File file, String client, String country)
	{
		//Read the file
		ContactUtil.Cache.agenda.clear();
		
		try 
		{
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

		            if(!phone.startsWith("+"))
					{
		            	phone = country+phone;
					}
		            
		            phone = phone.replace("-","");
		            phone = phone.replace(")","");
		            phone = phone.replace("(","");
					phone = phone.replace(" ","");
					
					Log.i("tantest", "Name: " + name);
		            Log.i("tantest", "phone: " + phone);
		            
					ContactUtil.Cache.agenda.put("\""+phone+"\"",name);
	            }
	            
	        }	
	        
	        if(ContactUtil.Cache.agenda.containsKey("\""+client+"\""))
			{
				ContactUtil.Cache.agenda.remove("\""+client+"\"");
			}
		}
		catch (Exception e) 
		{
			
		}
	}
	
    public static ArrayList<String> createContacts(String scontacts)
    {
    	JSONObject jsonContact;
    	ArrayList<String> contactItems = new ArrayList<String>();
    	
    	try 
		{
			jsonContact = HttpUtil.post(HttpUtil.GET_CONTACTS,new String[]{scontacts});

	    	JSONArray contacts = jsonContact.getJSONArray("response");
			
			String id;
	 		ContactItemListView ccontact;
	 		String name;
	 		
	        for(int i = 0; i < contacts.length(); i++)
	        {
	        	jsonContact = contacts.getJSONObject(i);
	        	id = jsonContact.getString("client");
	        	
	        	if(ContactUtil.Cache.agenda.containsKey("\""+id+"\""))
	    		{
	    			name = ContactUtil.Cache.agenda.get("\""+id+"\"");
	    		}
	        	else
	        	{
	        		name = "("+id+")";
	        	}
	        	
	        	try 
				{
	        		if(ContactUtil.Cache.contacts.get(id) != null)
	        		{
	        			ccontact = new ContactItemListView(
	            				id,
	    						jsonContact.getString("photo"),
	    						jsonContact.getString("name").equals("") ? name : jsonContact.getString("name"),
	    						//name,
	    						jsonContact.getString("status"), 
	    						jsonContact.getString("points"),
	    						ContactUtil.Cache.contacts.get(id).getLastDate()
	    					);
	        		}
	        		else
	        		{
	        			ccontact = new ContactItemListView(
	            				id,
	    						jsonContact.getString("photo"),
	    						jsonContact.getString("name").equals("") ? name : jsonContact.getString("name"),
	    						//name,
	    						jsonContact.getString("status"), 
	    						jsonContact.getString("points")
	    					);
	        		}
	        		
	        		contactItems.add(id);
	        		
	        		ContactUtil.Cache.contacts.put(id,ccontact);
					InputStream ins = (InputStream) new URL(ccontact.getImg()).getContent();
					ContactUtil.Cache.images.put(id, Drawable.createFromStream(ins, null));	
				}
	        	catch(Exception e)
	        	{
	        		ContactUtil.Cache.images.put(id, HomeActivity.default_dr);
	        	}
	        }
		}
    	catch (Exception e)
    	{
			
		}
    	
    	return contactItems;
    }
	
    public static boolean updateContactImg(String contact, String url)
    {

    	ContactItemListView ccontact = ContactUtil.Cache.contacts.get(contact);
    	
    	try 
		{
    		if(ccontact != null)
    		{
    			ccontact.setImg(url);
    			
    		}
    		else
    		{
    			return false;
    		}
    		
    		ContactUtil.Cache.contacts.put(contact,ccontact);
			InputStream ins = (InputStream) new URL(ccontact.getImg()).getContent();
			ContactUtil.Cache.images.put(contact, Drawable.createFromStream(ins, null));
			
			return true;
		}
    	catch(Exception e)
    	{
    		return false;
    	}

    }
    
	 static public class Cache
	 {
	 	//static SparseArray<Drawable> images = new SparseArray<Drawable>();
	 	static public Map<String, ContactItemListView> contacts = new HashMap<String, ContactItemListView>();
	 	static public Map<String, Drawable> images = new HashMap<String, Drawable>();
	 	static public Map<String, String> agenda = new HashMap<String, String>();
	 }
}
