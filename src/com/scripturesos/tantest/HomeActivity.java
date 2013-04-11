package com.scripturesos.tantest;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.scripturesos.tantest.connection.DatabaseHelper;
import com.scripturesos.tantest.connection.HttpUtil;
import com.scripturesos.tantest.connection.IOMessage;
import com.scripturesos.tantest.connection.IOSocket;
import com.scripturesos.tantest.connection.MessageCallback;

public class HomeActivity extends Application {

	private ProgressBar loader;
	public static String client_id;
	public static String country_id;
	private String contacts_serialized;
	
	private ArrayList<String> chats = new ArrayList<String>();
	private ListView chatsListView;
	private Map<String,View> chatViews= new HashMap<String,View>();
	private Map<String, ArrayList<ChatMessage>> chatMessages= new HashMap<String, ArrayList<ChatMessage>>();
	private String current_client;
	private RelativeLayout chatContainer;
	private RelativeLayout chatActions;
	private EditText sender;
	
	private IOSocket server;
	
	private static Drawable default_dr;
	
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
		sender = (EditText) findViewById(R.id.act_home_chat_input);
		
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
		
		default_dr = getResources().getDrawable(R.drawable.profile);
				
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
				
				server = new IOSocket(client_id, new MessageCallback() {
					  
					  @Override
					  public void onConnect() 
					  {
					    // Handle events
						  Log.i("tantest","Conectado");
						  
						  //Miramops base de datos para mensjaes que no se hayan enviado
					  }
					  
					  @Override
					  public void onConnectFailure() 
					  {
						  Log.i("tantest","Problema al conectar, no recibe handshake o server no disponible");
					  }
					  
					  @Override
					  public void onDisconnect() 
					  {
					    // Handle JSON messages
						Log.i("tantest","Nos desconectamos, hemos perdido conexion o el servidor no disponible");
					  }
					  
					  @Override
					  public void onMessage(IOMessage msg) 
					  {
						  Log.i("tantest","Recibimos mensaje del servidor");						  
						  final IOMessage message = msg;
						  
						  (new Thread(){
							  
							  public void run() 
							  {
									  Message handlerMSG = new Message();
									  
									  switch(message.getType())
									  {
									  	case IOMessage.CHAT_MESSAGE:
									  		handlerMSG.what = 0;
									  		handlerMSG.obj = message.getMessageData();
									  		handler.sendMessage(handlerMSG);
									  		break;
									  	case IOMessage.CHAT_CONFIRMATION:
									  		handlerMSG.what = 1;
									  		handlerMSG.obj = message.getMessageData();
									  		handler.sendMessage(handlerMSG);
									  		break;
									  }	
							   }
							  
						  }).start();
							
					  }

					  @Override
					  public void onMessageFailure(IOMessage msg) 
					  {
					    
						  Log.i("tantest","No hemos podido enviar mensaje al servidor");
					  }
					}, true);
				
	
				server.connect();
				
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
		
		server.close();
		
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
					createChat(chat,null);
            	}

            }
            break;
               
        }
        
    }
	
	public void createChat(String contact, final String[] options)
	{
		//Si existe simplemente mostramos vista del chat
		if(chats.contains(contact))
		{
			chatsListView.setVisibility(View.GONE);
			current_client = contact;
			makeChatVisible();
			
			if(options != null)
			{
				composeReceivedMessage(options[0],options[1]);
			}
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

			(new Thread() {
			    
				public void run() 
				{
					
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View chatView = inflater.inflate(R.layout.chat, null);
					
					current_client = chats.get(chats.size()-1);
					
					ContactItemListView contact = null;
					
					if(ContactListAdapter.Cache.contacts.get(current_client) == null)
			        {
						
						try {
							
							JSONObject jsonContact = HttpUtil.post(HttpUtil.GET_CONTACTS,new String[]{"[\""+current_client+"\"]"});
						
							jsonContact = jsonContact.getJSONArray("response").getJSONObject(0);
									
							contact = new ContactItemListView(
									current_client,
									jsonContact.getString("photo"),
									jsonContact.getString("name"), 
									jsonContact.getString("status"), 
									jsonContact.getString("points")
							);
							
							ContactListAdapter.Cache.contacts.put(current_client,contact);
							InputStream ins = (InputStream) new URL(contact.getImg()).getContent();
							Log.i("tantest","Contacto es "+ jsonContact.getString("photo"));
							ContactListAdapter.Cache.images.put(current_client, Drawable.createFromStream(ins, null));
						} 
						catch (Exception e) 
						{
							Log.i("tantest","error es "+ e.getMessage());
							Log.i("tantest",e.toString());
							ContactListAdapter.Cache.images.put(current_client, HomeActivity.default_dr);
						}

			        }
					else
					{
						contact = ContactListAdapter.Cache.contacts.get(current_client);
					}
					 
					((ImageView) chatView.findViewById(R.id.chat_img)).setImageDrawable(ContactListAdapter.Cache.images.get(current_client));
		            
					//((TextView) header.findViewById(R.id.chat_lv_name)).setText(contact.getName());
		            ((TextView) chatView.findViewById(R.id.chat_status)).setText(contact.getStatus());
		            ((TextView) chatView.findViewById(R.id.chat_points)).setText(contact.getPoints());

		            ListView lv = (ListView) chatView.findViewById(R.id.chat_lv);
		            
		            ArrayList<ChatMessage> lcm = new ArrayList<ChatMessage>();
		            
		            lv.setAdapter(new MessageListAdapter(HomeActivity.this,lcm));
		            
					chatViews.put(current_client, chatView);
					chatMessages.put(current_client,lcm);
					
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
					
					if(options == null)
					{
						msg.what = 4;
					}
					else
					{
						msg.what = 5;
						msg.obj = options;
					}
					 
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
        		handlerReceivedMessage((JSONObject) msg.obj);break;//ifError("Tenemos algunos problemillas... pero tu sonrie que Dios te ama");break;
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
        		((ContactListAdapter)chatsListView.getAdapter()).notifyDataSetChanged();
        		makeChatVisible();
        		break;
        	case 5:
        		//RelativeLayout r = (RelativeLayout) findViewById(R.id.act_home_container);
        		//r.addView((LinearLayout) msg.obj);
        		((ContactListAdapter)chatsListView.getAdapter()).notifyDataSetChanged();
        		makeChatVisible();
        		String[] options = (String[])msg.obj;
        		composeReceivedMessage(options[0],options[1]);
        		break;
        	default:break;
        }
    }
	
	public void ifError(String txt)
	{
		Toast.makeText(HomeActivity.this, txt, Toast.LENGTH_SHORT).show();
	}
	
	public void handlerReceivedMessage(JSONObject response)
	{
		try 
		{
			String from = response.getString("from");
			String message = response.getString("message");
			String message_id = response.getString("message_id");
			
			if(current_client != null && current_client.equals(from))
			{
				composeReceivedMessage(message,message_id);
			}
			else
			{
				//Notificar de nuevo mensaje
				
				createChat(from, new String[]{message,message_id});
				//composeReceivedMessage(message, message_id);
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
	
	public void composeReceivedMessage(String message, final String message_id)
	{

		ListView lv = (ListView) chatViews.get(current_client).findViewById(R.id.chat_lv);
		
		chatMessages.get(current_client).add(new ChatMessage(true, message));

		((MessageListAdapter)lv.getAdapter()).notifyDataSetChanged();
		
		/*(new Thread(){
		    
			public void run() 
			{
				//Guarda en base de datos
				
				//Envia mensaje al otro usuario
				ClientSocket.getInstance().sendConfirmation(message_id, current_client);
		   }
		}).start();*/
		
	}
	
	public void ComposeSentMessage(View view)
	{
		
		final String message = sender.getText().toString();
		
		if(!message.equals(""))
		{

			ListView lv = (ListView) chatViews.get(current_client).findViewById(R.id.chat_lv);
	
			
			chatMessages.get(current_client).add(new ChatMessage(false, message ));
			//((ContactListAdapter)chatsListView.getAdapter()).add(contact);
			((MessageListAdapter)lv.getAdapter()).notifyDataSetChanged();
			
			sender.setText("");
			
			//message = null;
			
			/*
			myListView.post(new Runnable() {
		        @Override
		        public void run() {
		            // Select the last row so it will scroll into view...
		            myListView.setSelection(myListAdapter.getCount() - 1);
		        }
		    });
			*/
			
			(new Thread(){
			    
				public void run() 
				{
					//Guarda en base de datos chatMessages
					
					String message_id = UUID.randomUUID().toString();
					
					//Cuidado!! mirar cuando usuairo envia mensaje que contiene caracteres raros como comillas
					server.send(IOMessage.CHAT_MESSAGE, current_client, message, message_id);
				}
				
			}).start();
		
		}
	}

}
