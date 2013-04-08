package com.scripturesos.tantest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.scripturesos.tantest.connection.ClientResponse;
import com.scripturesos.tantest.connection.ClientSocket;
import com.scripturesos.tantest.connection.DatabaseHelper;
import com.scripturesos.tantest.connection.HttpUtil;

public class HomeActivity extends Application {

	private ProgressBar loader;
	private String client_id;
	private String country_id;
	private String contacts_serialized;
	
	private ArrayList<String> chats = new ArrayList<String>();
	private ListView chatsListView;
	private Map<String,LinearLayout> chatViews= new HashMap<String,LinearLayout>();
	private String current_client;
	private RelativeLayout chatContainer;
	private RelativeLayout chatActions;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		/* INIT CONTENT VIEW */
		Log.i("tantest","CREATE HOME ACTIVITY");
		
		setContentView(R.layout.activity_home);
		
		loader = (ProgressBar) findViewById(R.id.home_progressbar);
		
		handler = new HomeActivityHandler(this);
		
		chatContainer = (RelativeLayout) findViewById(R.id.act_home_container);
		chatActions = (RelativeLayout) findViewById(R.id.act_home_chat_actions);
		
		chatsListView = (ListView) findViewById(R.id.act_home_lv);
		chatsListView.setAdapter(new ContactListAdapter(HomeActivity.this, chats));
		chatsListView.setOnItemClickListener(new OnItemClickListener()
		{
				@Override 
				public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3)
			    { 
					chatsListView.setVisibility(View.GONE);
					
					ContactItemListView contact = ContactListAdapter.Cache.contacts.get(((ContactListAdapter) chatsListView.getAdapter()).getContacts().get(position));

					current_client = contact.getID();
					
					makeChatVisible();
			    }
		});
		
		(new Thread() {
		    
			public void run() 
			{
				if(client_id == null || country_id == null)
				{
					SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getReadableDatabase();
					
					Cursor cursor = db.rawQuery("SELECT value FROM options", null);
					//Cursor cursor = db.rawQuery("SELECT value FROM options WHERE key=0 OR key=1", null);
					
					cursor.moveToFirst();
					
					client_id = cursor.getString(0);
					
					cursor.moveToNext();
					
					country_id = cursor.getString(0);
					
					cursor.close();
					
					db.close();
					
					Log.i("tantest","phone: "+client_id);
					Log.i("tantest","country: "+country_id);
					
				}
				
				/*ClientSocket.getInstance()
				.getHandlers().put("onServerError", new ClientResponse(handler,10));
				
				ClientSocket.getInstance()
				.getHandlers().put("onConnectionError", new ClientResponse(handler,11));
				
				ClientSocket.getInstance()
				.getHandlers().put("onTimeoutError", new ClientResponse(handler,12));
				*/
				ClientSocket.getInstance()
				.getHandlers().put("sendMsg", new ClientResponse(handler,0));
				
				ClientSocket.getInstance()
				.getHandlers().put("confirmMsg", new ClientResponse(handler,1));
				
				ClientSocket.getInstance().clientID = client_id;
				ClientSocket.getInstance().countryCode = country_id;
				
				ClientSocket.getInstance().start();
				
				//Segun lo que sea, mostrar mensaje o mostrar listView
				Message msg = new Message();
				msg.what = 3;
				handler.sendMessage(msg);	
				
		    }
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}
	

    @Override
    public void onResume()
    {
    	Log.i("tantest","RESUME HOM ACTIVITY");
    	
    	if(loader != null)
		{
    		loader.setVisibility(View.GONE);
		}
    	
    	super.onResume();
    }
    
	@Override
	protected void onDestroy() 
	{
		
		ClientSocket.getInstance().close();
		
		super.onDestroy();
	}
	
	public void changeText(View view)
	{
		TextView text = (TextView) findViewById(R.id.home_text);
		
		text.setText("BOTON HA SIDO PULSADO");
		
	}
	
    // Alternative variant for API 5 and higher
    @Override
    public void onBackPressed() 
    {
    	if(current_client != null)
    	{
    		current_client = null;
    		chatContainer.setVisibility(View.GONE);
    		chatActions.setVisibility(View.GONE);
    		chatContainer.removeAllViews();
    		chatsListView.setVisibility(View.VISIBLE);
    	}
    	else
    	{
    		//mismo efecto que si pulsa el boton home: moveTaskToBackGround
    		moveTaskToBack(true);
    		//super.onBackPressed();
    	}
    	
    }
	
    public void showContacts(View v)
    {
    	Intent cintent = new Intent(this, ContactsActivity.class);
    	
    	if(contacts_serialized != null)
	    {
	    	Bundle bundle = new Bundle();
	    	bundle.putString("contacts", contacts_serialized);
	    	cintent.putExtras(bundle);
	    }
    	
		Log.i("tantes","iniciando actividad");
		startActivityForResult(cintent,0);
    }
    
	public boolean onOptionsItemSelected(MenuItem item)
	{
		
		loader.setVisibility(View.VISIBLE);
		loader.setIndeterminate(true);
		
		switch(item.getItemId())
		{
			case R.id.menu_header_test:
				
				//Mostramos ajax cargando
				
				Log.i("tantes","creando actividad");
				
				Intent intent = new Intent(this, TestActivity.class);
				Log.i("tantes","iniciando actividad");
				startActivity(intent);
				break;
			case R.id.menu_header_social:
				Intent cintent = new Intent(this, ContactsActivity.class);
				Log.i("tantes","iniciando actividad");
				
				if(contacts_serialized != null)
			    {
			    	Bundle bundle = new Bundle();
			    	bundle.putString("contacts", contacts_serialized);
			    	cintent.putExtras(bundle);
			    }
				
				startActivityForResult(cintent,0);
				break;
			case R.id.menu_settings:

				break;
			default:
		}
		
		return true;
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
        super.onActivityResult(requestCode, resultCode, data);
        
        switch(requestCode) 
        {
            case 0:      
            if (resultCode == RESULT_OK) 
            {  
            	Bundle b = data.getExtras();
            	
            	//Problema: Como actualizamos cuando cambie foto o estado?
            	if(b.containsKey("contacts"))
            	{
            		contacts_serialized = b.getString("contacts");
            		
            		//Los guardamos en BD
            		Log.i("tantest","Tenemos datos serializados");
            	}
            	
            	if(b.containsKey("chat"))
            	{
            		String chat = b.getString("chat");
            		
            		//createChat((ContactItemListView)HttpUtil.fromString(chat));
					createChat(chat);
            	}

            }
            break;
               
        }
        
    }
	
	public void createChat(String contact)
	{
		//Si existe simplemente mostramos vista del chat
		if(chats.contains(contact))
		{
			chatsListView.setVisibility(View.GONE);
			current_client = contact;
			makeChatVisible();
		}
		else
		{
			chatsListView.setVisibility(View.GONE);
			
			TextView no_chats = (TextView) findViewById(R.id.home_text);
			no_chats.setVisibility(View.GONE);
			
			loader.setVisibility(View.VISIBLE);
			
			//Añadir a listView del HomeActivity
			chats.add(contact);
			//((ContactListAdapter)chatsListView.getAdapter()).add(contact);
			((ContactListAdapter)chatsListView.getAdapter()).notifyDataSetChanged();
			
			(new Thread() {
			    
				public void run() 
				{
					//Crear nueva vista de chat -> RelativeLayout
					LinearLayout chatview = new LinearLayout(HomeActivity.this);
					//LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
					
					//llParams.setMargins(0, 0, 0, 40);
					
					//chatview.setLayoutParams(llParams);
					chatview.setOrientation(LinearLayout.VERTICAL);
					//chatview.setBackgroundResource(R.drawable.chatbg);
					//chatview.setVisibility(View.GONE);
					//chatview.setId(id)
					
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View header = inflater.inflate(R.layout.chat_header, null);
					
					current_client = chats.get(chats.size()-1);
					
					if(ContactListAdapter.Cache.contacts.get(current_client) == null)
			        {
						
						try {
							
							JSONObject jsonContact = HttpUtil.post(HttpUtil.GET_CONTACTS,new String[]{"[\""+current_client+"\"]"});
						
							ContactItemListView contact = new ContactItemListView(
									current_client,
									jsonContact.getString("photo"),
									jsonContact.getString("name"), 
									jsonContact.getString("status"), 
									jsonContact.getString("points")
							);
							
							InputStream is = (InputStream) new URL(jsonContact.getString("photo")).getContent();
							ContactListAdapter.Cache.images.put(current_client, Drawable.createFromStream(is, jsonContact.getString("photo")));
							ContactListAdapter.Cache.contacts.put(current_client,contact);
				        
						} 
						catch (Exception e) 
						{
							
						}
			        }
					
					((ImageView) header.findViewById(R.id.chat_lv_img)).setImageDrawable(ContactListAdapter.Cache.images.get(current_client));
		            
					ContactItemListView contact = ContactListAdapter.Cache.contacts.get(current_client);
					 
					//((TextView) header.findViewById(R.id.chat_lv_name)).setText(contact.getName());
		            ((TextView) header.findViewById(R.id.chat_lv_status)).setText(contact.getStatus());
		            ((TextView) header.findViewById(R.id.chat_lv_points)).setText(contact.getPoints());

		            chatview.addView(header);
					
					ScrollView sv = new ScrollView(HomeActivity.this);
					
					sv.addView(new LinearLayout(HomeActivity.this));

					chatViews.put(current_client, chatview);
					
					//Actualizar base de datos
					SQLiteDatabase dbw = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
					
					try 
					{
						dbw.execSQL("INSERT INTO options (key, value) VALUES (2,'"+HttpUtil.toString(chats)+"')");
					} 
					catch (Exception e) 
					{
						
					}
					
					Message msg = new Message();
					msg.what = 4;
					handler.sendMessage(msg);	
			    }
			}).start();
			
		}
		
	}
	
	public class HomeActivityHandler extends Handler 
	{
        private HomeActivity parent;

        public HomeActivityHandler(HomeActivity parent) 
        {
            this.parent = parent;
        }

        public void handleMessage(Message msg) 
        {
            parent.handleMessage(msg);
        }
    }
	
	public HomeActivityHandler handler;
	
	public void handleMessage(Message msg) 
	{
        
		switch(msg.what) 
        {
        	case 0: 
        		handlerMessage((JSONObject) msg.obj);break;//ifError("Tenemos algunos problemillas... pero tu sonrie que Dios te ama");break;
        	case 1: 
        		handlerConfirmation((JSONObject) msg.obj);break;//ifError("Revise su conexión a Internet y sonrie que Dios te ama");break;
        	case 2: 
        		ifError("Tenemos algunos problemillas... pero tu sonrie que Dios te ama");break;
        	case 3: 
        		TextView no_chats = (TextView) findViewById(R.id.home_text);
        		no_chats.setVisibility(View.VISIBLE);
        		loader.setVisibility(View.GONE);
        		break;
        	case 4:
        		//RelativeLayout r = (RelativeLayout) findViewById(R.id.act_home_container);
        		//r.addView((LinearLayout) msg.obj);
        		makeChatVisible();
        		break;
        	default:break;
        }
    }
	
	public void ifError(String txt)
	{
		Toast.makeText(HomeActivity.this, txt, Toast.LENGTH_SHORT).show();
	}
	
	public void handlerMessage(JSONObject response)
	{
		try 
		{
			String from = response.getString("from");
			String message = response.getString("message");
			String message_ig = response.getString("message_id");
			
			if(current_client.equals(from))
			{
				
			}
			else
			{
				createChat(from);
			}
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void handlerConfirmation(JSONObject response)
	{
		
	}
	
	public void makeChatVisible()
	{
		loader.setVisibility(View.GONE);
		
		chatContainer.removeAllViews();
		chatContainer.addView(chatViews.get(current_client));
		
		chatContainer.setVisibility(View.VISIBLE);
		chatActions.setVisibility(View.VISIBLE);
	}

}
