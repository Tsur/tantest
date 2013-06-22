package com.scripturesos.tantest.main;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
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
	private ListView chatsListView;
	private Map<String,View> chatViews= new HashMap<String,View>();
	private String current_client;
	private String last_current_client;
	private RelativeLayout chatContainer;
	private RelativeLayout container;
	private EditText sender;
	public static IOSocket server;
	public static Drawable default_dr;
	
	private ArrayList<IOMessage> messages_offine = new ArrayList<IOMessage>();
	private HashMap<String, ArrayList<ChatMessage>> chatMessages = new HashMap<String, ArrayList<ChatMessage>>();
	private ArrayList<String> chats = new ArrayList<String>();
	private String contacts_serialized;
	private boolean canSave = false;
	private MediaPlayer sound;
	private boolean writing = false;
	private boolean firstActionUp = true;
	private DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		/* INIT CONTENT VIEW */
		//Log.i("tantest","CREATE HOME ACTIVITY");
		
		setContentView(R.layout.activity_home);
		
		loader = (ProgressBar) findViewById(R.id.home_progressbar);
		
		handler = new HomeActivityHandler(this);
	
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
					
					ContactUtil.createAgenda(HomeActivity.this, client_id, country_id);

					/*while(cursor.moveToNext())
					{
						
					}*/
					try 
					{
						if(cursor.moveToNext())
						{
							//Log.i("tantest","Tenemos chat messages");
							
							chatMessages = (HashMap<String, ArrayList<ChatMessage>>) HttpUtil.fromString(cursor.getString(0));
							
							if(cursor.moveToNext())
							{
								//Log.i("tantest","Tenemos chats");
								chats = (ArrayList<String>) HttpUtil.fromString(cursor.getString(0));
							}

							if(chats.size() > 0)
							{
								//Log.i("tantest","tamaño mayor que uno");
								
								String scontacts = "[";
								
								for(String contact: chats)
								{
									scontacts += "\""+contact+"\",";
									//Log.i("tantest","Contacto en chats: "+contact);
								}
								
								scontacts = scontacts.substring(0, scontacts.length()-1);
								
								scontacts += "]";
								
								//Log.i("tantest","Contacto server: "+scontacts);
								
								ContactUtil.createContacts(scontacts);
								
								initChatViews();
							}

							if(cursor.moveToNext())
							{
								//Log.i("tantest","Tenemos messages offine");
								messages_offine = (ArrayList<IOMessage>) HttpUtil.fromString(cursor.getString(0));
								
								db.execSQL("DELETE FROM options WHERE key=4");
								
							}
						}
						
					}
					catch (Exception e) 
					{
						
					}
					
					cursor.close();
					
					db.close();
					
					//Log.i("tantest","phone: "+client_id);
					//Log.i("tantest","country: "+country_id);
					
				}

				server = new IOSocket(client_id, new MessageCallback() {
					  
					  @Override
					  public void onConnect() 
					  {
						  // Handle events
						  //Log.i("tantest","Conectado");
						  
						  //Crear lista temporal primero y luego borrar para tener
						  //mensajes que aun fallen
						  (new Thread(){
							  
							  public void run() 
							  {
								  if(!messages_offine.isEmpty())
								  {
									  ArrayList<IOMessage> messages_offine_temp = new ArrayList<IOMessage>();
								  
									  for(IOMessage message: messages_offine)
									  {
										  messages_offine_temp.add(message);
									  }
									  
									  messages_offine.clear();
									  
									  for(IOMessage message: messages_offine_temp)
									  {
										  server.send(message);  
									  }
									  
									  messages_offine_temp.clear();
									  messages_offine_temp = null;
								  }
								  
								  //Miramops base de datos para mensjaes que no se hayan enviado
								  JSONObject response;
								  
								  try 
								  {
									  response = HttpUtil.post(HttpUtil.GET_UNCHEKED_MSG,new String[]{client_id});
									
									  JSONArray unconfirmed = response.getJSONArray("response");
									  
									  for(int c=0; c<unconfirmed.length(); c++)
									  {
										  handlerConfirmation(unconfirmed.getJSONObject(c));
									  }
									
								  } 
								  catch(Exception e) 
								  {
									
								  }
								  
								  try 
								  {
									  response = HttpUtil.post(HttpUtil.GET_UNREAD_MSG,new String[]{client_id});
									
									  JSONArray unread = response.getJSONArray("response");
									  
									  for(int r=0; r<unread.length(); r++)
									  {
										  handlerReceivedMessage(unread.getJSONObject(r));
									  }
									
								  } 
								  catch(Exception e) 
								  {
									
								  }

							  }
							  
						  }).start();
					  }
					  
					  @Override
					  public void onConnectFailure() 
					  {
						  //Log.i("tantest","Problema al conectar, no recibe handshake o server no disponible");
					  }
					  
					  @Override
					  public void onDisconnect() 
					  {
					    // Handle JSON messages
						//Log.i("tantest","Nos desconectamos, hemos perdido conexion o el servidor no disponible");
					  }
					  
					  @Override
					  public void onMessage(IOMessage msg) 
					  {						  
						  final IOMessage message = msg;
						  
						  (new Thread(){
							  
							  public void run() 
							  {
									  Message handlerMSG = new Message();
									  
									  switch(message.getType())
									  {
										  	case MessageCallback.CHAT_MESSAGE:
										  		//Log.i("tantest","Recibimos mensaje del servidor");
										  		handlerMSG.what = 0;
										  		
										  		break;
										  	case MessageCallback.CHAT_CONFIRMATION:
										  		//Log.i("tantest","Recibimos CONfirmacion del servidor");
										  		handlerMSG.what = 1;
										  		break;
										  	case MessageCallback.CHAT_HAS_GONE:
										  		//Log.i("tantest","Recibimos usuario ha salido HAS GONE");
										  		handlerMSG.what = 7;
										  		break;
										  	case MessageCallback.CHAT_HAS_CHANGED:
										  		//Log.i("tantest","Recibimos usuario ha cambiado");
										  		handlerMSG.what = 8;
										  		break;
										  	case MessageCallback.CHAT_IS_WRITING:
										  		//Log.i("tantest","Recibimos usuario escribiendo");
										  		handlerMSG.what = 9;
										  		break;
										  	case MessageCallback.CHAT_IN_TEST:
										  		//Log.i("tantest","Recibimos usuario en test");
										  		handlerMSG.what = 10;
										  		break;
										  	case MessageCallback.CHAT_ONLINE:
										  		//Log.i("tantest","Recibimos usuario online");
										  		handlerMSG.what = 11;
										  		break;
									  }	
									  
									  handlerMSG.obj = message.getMessageData();
								  	  handler.sendMessage(handlerMSG);
							   }
							  
						  }).start();
							
					  }

					  @Override
					  public void onMessageFailure(IOMessage msg) 
					  {
						  //Log.i("tantest","No hemos podido enviar mensaje al servidor");
						  messages_offine.add(msg);
					  }
					}, true);
				
	
				server.connect();
				
				sound = MediaPlayer.create(HomeActivity.this, R.raw.alert);
				/*sound.setOnCompletionListener(new OnCompletionListener() {

		            @Override
		            public void onCompletion(MediaPlayer mp) {
		                // TODO Auto-generated method stub
		                Log.i("tantest","Sonido!");
		            	//mp.release();
		            }

		        });*/   
				
				//Segun lo que sea, mostrar mensaje o mostrar listView 
				if(chats.size() > 0)
				{
					for(String contact: chats)
					{
						if(server != null)
						{
							server.send(MessageCallback.CHAT_CREATE, contact);
						}
					}
					
					Message msg = new Message();
					msg.what = 6;
					handler.sendMessage(msg);
				}
				else
				{
					Message msg = new Message();
					msg.what = 3;
					handler.sendMessage(msg);	
				}

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
    	//Log.i("tantest","RESUME HOM ACTIVITY");
    	
    	/*if(chatsListView != null)
		{
    		chatsListView.setVisibility(View.VISIBLE);
    		//loader.setVisibility(View.GONE);
		}*/
    	
    	if(client_id != null && server != null && !chats.isEmpty())
		{
			server.send(MessageCallback.CHAT_ONLINE);
		}
    	
    	super.onResume();
    }
    
	@Override
	protected void onPause() 
	{
		//server.close();
		super.onPause();
		
		//Log.i("tantest", "On Pause");
		if(canSave)
		{
			//Log.i("tantest", "Saving");
			
			//Actualizar base de datos
			(new Thread(){
				
				public void run()
				{
					canSave = false;
					
					try 
					{
						SQLiteDatabase dbw = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
					
						synchronized(chatMessages)
						{
							if(chatMessages.size() > 0)
							{
								dbw.execSQL("INSERT INTO options (key, value) VALUES (2,\""+HttpUtil.toString(chatMessages)+"\")");
								
								synchronized(chats)
								{
									dbw.execSQL("INSERT INTO options (key, value) VALUES (3,\""+HttpUtil.toString(chats)+"\")");
								
									synchronized(messages_offine)
									{
										if(messages_offine.size() > 0)
										{
											dbw.execSQL("INSERT INTO options (key, value) VALUES (4,\""+HttpUtil.toString(messages_offine)+"\")");
										}
									}
									
								}
							}
						}
					
						dbw.close();
					
					} 
					catch (Exception e) 
					{
						canSave = true;
					}
				}
				
			}).start();
		}
	}
	
    // Alternative variant for API 5 and higher
    @Override
    public void onBackPressed() 
    {
    	chatsListView.setVisibility(View.VISIBLE);
    	
    	if(current_client != null)
    	{
    		last_current_client = current_client;
    		current_client = null;
    		//chatContainer.setVisibility(View.GONE);
    		//chatActions.setVisibility(View.GONE);
    		container.setVisibility(View.GONE);
    		//chatContainer.removeAllViews();
    		
    	}
    	else
    	{
    		//mismo efecto que si pulsa el boton home: moveTaskToBackGround
    		if(client_id != null && server != null && !chats.isEmpty())
    		{
    			server.send(MessageCallback.CHAT_HAS_GONE);
    		}
			
    		moveTaskToBack(true);
    		//super.onBackPressed();
    	}
    }
	
    public void init()
    {
    	chatContainer = (RelativeLayout) findViewById(R.id.act_home_chat_container);
		//chatActions = (RelativeLayout) findViewById(R.id.act_home_chat_actions);
    	container = (RelativeLayout) findViewById(R.id.act_home_container);
		sender = (EditText) findViewById(R.id.act_home_chat_input);
		sender.setOnKeyListener(new OnKeyListener(){           

			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent event) 
			{
				if(event.getAction()==KeyEvent.ACTION_DOWN) 
				{
					if(current_client!= null && !writing)
					{
						server.send(MessageCallback.CHAT_IS_WRITING,current_client,"on");
						writing = true;
					}
					
					return false;  
	            }
				
				if (event.getAction()==KeyEvent.ACTION_UP) 
				{
					if(firstActionUp && writing)
					{
						firstActionUp = false;
						(new Thread(){
							
							public void run() 
							{	
								try 
								{
									Thread.sleep(2500);
								} 
								catch(Exception e)
								{
								}
								
								if(current_client!= null)
								{
									server.send(MessageCallback.CHAT_IS_WRITING,current_client,"off");
									
								}
								else
								{
									server.send(MessageCallback.CHAT_IS_WRITING,last_current_client,"off");
								}
								
								writing = false;
								firstActionUp = true;
						   }
						}).start();
					}
					/*if(writing)
					{
						if(current_client!= null)
						{
							server.send(MessageCallback.CHAT_IS_WRITING,current_client,"off");
							
						}
						else
						{
							server.send(MessageCallback.CHAT_IS_WRITING,last_current_client,"off");
						}
						
						writing = false;
					}*/

					return false;  
	            }
				 
				return true;
			}
        });
		
		chatsListView = (ListView) findViewById(R.id.act_home_lv);
		chatsListView.setAdapter(new ContactListAdapterHome(HomeActivity.this, chats));
		chatsListView.setOnItemClickListener(new OnItemClickListener()
		{
				@Override 
				public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3)
			    { 
					chatsListView.setVisibility(View.GONE);
					
					ContactItemListView contact = ContactUtil.Cache.contacts.get(((ContactListAdapterHome) chatsListView.getAdapter()).getContacts().get(position));

					current_client = contact.getID();
					
					makeChatVisible();
			    }
		});
		
    }
    
    public void initChatViews()
    {
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View chatView;
		ContactItemListView contactlv;
		ListView lv;
		ArrayList<ChatMessage> lcm;
		ArrayList<String> chats_temp = new ArrayList<String>();
		
		for(String contact: chats)
		{
			chatView = inflater.inflate(R.layout.chat, null);
			
			if(ContactUtil.Cache.contacts.get(contact) == null)
	        {
				continue;
	        }
			else
			{
				contactlv = ContactUtil.Cache.contacts.get(contact);
			}

			((ImageView) chatView.findViewById(R.id.chat_img)).setImageDrawable(ContactUtil.Cache.images.get(contact));
            
			//((TextView) header.findViewById(R.id.chat_lv_name)).setText(contact.getName());
			((TextView) chatView.findViewById(R.id.chat_name)).setText(contactlv.getName());
			((TextView) chatView.findViewById(R.id.chat_points)).setText(contactlv.getPoints());

            lv = (ListView) chatView.findViewById(R.id.chat_lv);
            
            lcm = chatMessages.get(contact);
            
            if(lcm == null)
            {
            	lcm = new ArrayList<ChatMessage>();			
            	chatMessages.put(contact,lcm);
            }
            
            lv.setAdapter(new MessageListAdapter(HomeActivity.this,lcm));
            
			chatViews.put(contact, chatView);
			chats_temp.add(contact);

		}
		
		if(chats.size() != chats_temp.size())
		{
			chats = chats_temp;
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
    	
		//Log.i("tantes","iniciando actividad");
		startActivityForResult(cintent,0);
    }
    
	public boolean onOptionsItemSelected(MenuItem item)
	{
		
		switch(item.getItemId())
		{
			case R.id.menu_header_test:
				
				//Mostramos ajax cargando
				//Log.i("tantes","creando actividad");
				Intent tintent = new Intent(this, TestOptionsActivity.class);
				startActivityForResult(tintent,1);
				break;
				
			case R.id.menu_header_social:
				Intent cintent = new Intent(this, ContactsActivity.class);
				//Log.i("tantes","iniciando actividad");
				
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
            		//Log.i("tantest","Tenemos datos serializados");
            	}
            	
            	if(b.containsKey("chat"))
            	{
            		String chat = b.getString("chat");
            		
            		//createChat((ContactItemListView)HttpUtil.fromString(chat));
					createChat(chat,null, true);
            	}

            }
            break;
            case 1:
            	if (resultCode == RESULT_OK) 
                {  
                	Bundle b = data.getExtras();
                	
                	//Problema: Como actualizamos cuando cambie foto o estado?
                	if(b.containsKey("configuration"))
                	{
                		Intent tintent = new Intent(this, TestActivity.class);
        				tintent.putExtras(b);
        				startActivityForResult(tintent,2);
                	}
                }
            	break;
            	
            case 2:
            	if (resultCode == RESULT_OK) 
                {  
                	Bundle b = data.getExtras();
                	//Log.i("tantest","recibimos datos de TestActiviy");
                	try 
                	{
						createChat(b.getString("chat"), null, true);
						ComposeSentCustomMessage(b.getString("test"), true);
						
                		//shareTest((TestGrade)HttpUtil.fromString(b.getString("test")));
					} 
                	catch (Exception e) 
                	{
                		//Log.i("tantest","ShareTest Exception");
					}    	
                }
            	break;
               
        }
        
    }
	
    @Override
    public void onConfigurationChanged(Configuration newConfig) 
    {
        super.onConfigurationChanged(newConfig);
    }
	
	public void createChat(String contact, final String[] options, boolean makeVisible)
	{
		//Ocultamos y luego mostraremos al volver atras
		chatsListView.setVisibility(View.GONE);
		
		if(chats.contains(contact))
		{
			//chatsListView.setVisibility(View.GONE);
			if(makeVisible)
			{
				current_client = contact;
				makeChatVisible();
			}
			
			//Hemos recibido mensaje de alguien
			if(options != null)
			{
				composeReceivedMessage(options[0],options[1], options[2]);
				//Notificamos
				createNotification(options[0],options[1]);
			}
			
		}
		else
		{
			//chatsListView.setVisibility(View.GONE);
			
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
					
					String client = chats.get(chats.size()-1);
					//current_client = chats.get(chats.size()-1);
					
					ContactItemListView contact = null;
					
					if(ContactUtil.Cache.contacts.get(client) == null)
			        {
						
						ContactUtil.createContacts("[\""+client+"\"]");
						
						contact = ContactUtil.Cache.contacts.get(client);

			        }
					else
					{
						contact = ContactUtil.Cache.contacts.get(client);
					}
					 
					((ImageView) chatView.findViewById(R.id.chat_img)).setImageDrawable(ContactUtil.Cache.images.get(client));
		            
					//((TextView) header.findViewById(R.id.chat_lv_name)).setText(contact.getName());
		            //((TextView) chatView.findViewById(R.id.chat_status)).setText(contact.getStatus());
					((TextView) chatView.findViewById(R.id.chat_name)).setText(contact.getName());
					((TextView) chatView.findViewById(R.id.chat_points)).setText(contact.getPoints());

		            ListView lv = (ListView) chatView.findViewById(R.id.chat_lv);
		            
		            ArrayList<ChatMessage> lcm = new ArrayList<ChatMessage>();
		            
		            lv.setAdapter(new MessageListAdapter(HomeActivity.this,lcm));
		            
					chatViews.put(client, chatView);
					chatMessages.put(client,lcm);
					
					Message msg = new Message();
					
					if(server != null)
					{
						server.send(MessageCallback.CHAT_CREATE, client);
					}
					
					//Cuando yo inicio
					if(options == null)
					{
						current_client = client;
						msg.what = 4;
					}
					//Cuando lo inician otros
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
		JSONObject response;
		
		switch(msg.what) 
        {
        	case 0:
        		handlerReceivedMessage((JSONObject) msg.obj);
        		break;//ifError("Tenemos algunos problemillas... pero tu sonrie que Dios te ama");break;
        	case 1:
        		handlerConfirmation((JSONObject) msg.obj);
        		break;//ifError("Revise su conexión a Internet y sonrie que Dios te ama");break;
        	case 2: 
        		ifError("Tenemos algunos problemillas... pero tu sonrie que Dios te ama");
        		break;
        	case 3: 
        		init();
        		TextView no_chats = (TextView) findViewById(R.id.home_text);
        		no_chats.setVisibility(View.VISIBLE);
        		loader.setVisibility(View.GONE);
        		break;
        	case 4:
        		//RelativeLayout r = (RelativeLayout) findViewById(R.id.act_home_container);
        		//r.addView((LinearLayout) msg.obj);
        		canSave = true;
        		((ContactListAdapterHome)chatsListView.getAdapter()).notifyDataSetChanged();
        		//chatsListView.setVisibility(View.VISIBLE);
        		makeChatVisible();
        		break;
        	case 5:
        		((ContactListAdapterHome)chatsListView.getAdapter()).notifyDataSetChanged();
        		//makeChatVisible();
        		//Notificamos
        		String[] options = (String[])msg.obj;
        		composeReceivedMessage(options[0],options[1],options[2]);
        		//Notificamos
        		createNotification(options[0],options[1]);
        		chatsListView.setVisibility(View.VISIBLE);
        		loader.setVisibility(View.GONE);
        		break;
        	case 6: 
        		init();
        		chatsListView.setVisibility(View.VISIBLE);
        		loader.setVisibility(View.GONE);
        		break;
        		//Has gone
        	case 7:
        		
        		response = (JSONObject) msg.obj;
        		
        		try
        		{
        			String who = response.getString("from");

    				String date = formatter.format(new Date());
        			
        			((TextView) chatViews.get(who).findViewById(R.id.chat_status)).setText("última vez: "+date);
        			
        		}
        		catch(Exception e)
        		{
        			
        		}

        		break;
        	//Has changed
        	case 8: 

        		response = (JSONObject) msg.obj;
        		
        		try
        		{
        			final String who = response.getString("from");
        			final String value = response.getString("value");
        			
        			switch(response.getInt("type"))
        			{
        				//PHOTO
        				case 0:
        					(new Thread(){
        					    
        						public void run() 
        						{
        							
        							if(ContactUtil.updateContactImg(who, value))
        							{
        								ListView lv = (ListView) chatViews.get(who).findViewById(R.id.chat_lv);
        								((MessageListAdapter)lv.getAdapter()).notifyDataSetChanged();
        								((BaseAdapter) chatsListView.getAdapter()).notifyDataSetChanged();
        							}

        					   }
        					}).start();
        					break;
        				//Status
        				case 1:
        					(new Thread(){
        					    
        						public void run() 
        						{
        							
        							if(ContactUtil.Cache.contacts.get(who) != null)
        							{
        								ContactUtil.Cache.contacts.get(who).setStatus(value);
        								ListView lv = (ListView) chatViews.get(who).findViewById(R.id.chat_lv);
        								((MessageListAdapter)lv.getAdapter()).notifyDataSetChanged();
        								((BaseAdapter) chatsListView.getAdapter()).notifyDataSetChanged();
        							}

        					   }
        					}).start();
        					break;
        				//Points
        				case 2:
        					(new Thread(){
        					    
        						public void run() 
        						{
        							
        							if(ContactUtil.Cache.contacts.get(who) != null)
        							{
        								ContactUtil.Cache.contacts.get(who).setPoints(value);
        								ListView lv = (ListView) chatViews.get(who).findViewById(R.id.chat_lv);
        								((MessageListAdapter)lv.getAdapter()).notifyDataSetChanged();
        								((BaseAdapter) chatsListView.getAdapter()).notifyDataSetChanged();
        							}

        					   }
        					}).start();
        					break;
        				default:
        					break;
        			}
        			
        		}
        		catch(Exception e)
        		{
        			
        		}
        		break;
        	case 9: 
        		
        		response = (JSONObject) msg.obj;
        		
        		try
        		{
        			String who = response.getString("from");

        			if(response.getBoolean("on"))
        			{
        				((TextView) chatViews.get(who).findViewById(R.id.chat_status)).setText(R.string.chat_writing);
        			}
        			else
        			{
        				((TextView) chatViews.get(who).findViewById(R.id.chat_status)).setText(R.string.chat_online);
        			}
        			
        		}
        		catch(Exception e)
        		{
        			
        		}
        		break;
        	case 10: 

        		response = (JSONObject) msg.obj;
        		
        		try
        		{
        			String who = response.getString("from");

        			if(response.getBoolean("on"))
        			{
        				((TextView) chatViews.get(who).findViewById(R.id.chat_status)).setText(R.string.chat_intest);
        			}
        			else
        			{
        				((TextView) chatViews.get(who).findViewById(R.id.chat_status)).setText(R.string.chat_online);
        			}
        			
        		}
        		catch(Exception e)
        		{
        			
        		}
        		break;
        	//Has welcome
        	case 11:
        		
        		response = (JSONObject) msg.obj;
        		
        		try
        		{
        			String who = response.getString("from");
        			
        			((TextView) chatViews.get(who).findViewById(R.id.chat_status)).setText(R.string.chat_online);
        			
        		}
        		catch(Exception e)
        		{
        			
        		}
        		break;
        	default:
        		break;
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
			String from = response.getString("client_from");
			String message = response.getString("message");
			String message_id = response.getString("message_id");
			
			if(current_client != null && current_client.equals(from))
			{
				composeReceivedMessage(current_client,message,message_id);
				//Emitimos pitido
				sound.start();
			}
			else
			{
				createChat(from, new String[]{from, message,message_id}, false);
			}
		} 
		catch (JSONException e) 
		{
			
		}
	}
	
	public void createNotification(final String client, final String msg)
	{
	    
		(new Thread(){
		    
			public void run() 
			{
				// Prepare intent which is triggered if the
			    // notification is selected
			    Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
			    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			    
			    /*Bundle bundle = new Bundle();
				bundle.putString("client",client);
				
				intent.putExtras(bundle);*/
			    intent.putExtra("client", client);
			    
			    PendingIntent pIntent = PendingIntent.getActivity(HomeActivity.this, (int) System.currentTimeMillis(), intent, 0);
			
			    Drawable d = ContactUtil.Cache.images.get(client);  
			    Bitmap bitmap = null;
			    if(d instanceof BitmapDrawable)
			    {
			    	bitmap = ((BitmapDrawable)d).getBitmap();
			    }
			    else
			    {
			    	try 
			    	{
						bitmap = BitmapFactory.decodeStream((InputStream) new URL(ContactUtil.Cache.contacts.get(client).getImg()).getContent());
					}
			    	catch (Exception e) 
			    	{


					}
			    }
			    // Build notification
			    // Actions are just fake
			    Notification noti = (new NotificationCompat.Builder(HomeActivity.this))
			        .setContentTitle(getString(R.string.act_home_notification)+" "+ContactUtil.Cache.contacts.get(client).getName())
			        .setContentText((msg.length() > 65 ? msg.substring(0,64)+"...":msg))
			        .setSmallIcon(R.drawable.notification)
			        .setLargeIcon(bitmap)
			        .setContentIntent(pIntent)
			        //.addAction(R.drawable.icon, "Call", pIntent)
			        .build();
			    
			    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			    // Hide the notification after its selected
			    noti.flags |= Notification.FLAG_AUTO_CANCEL;

			    notificationManager.notify(0, noti);
			    
			    sound.start();

		   }
		}).start();
		
	}
	
	@Override
	public void onNewIntent(Intent intent) 
	{
		 
	    //SharedPreferences prefs = getSharedPreferences("twitter", 0);
		Bundle extras = intent.getExtras();

		if(extras!=null && extras.containsKey("client"))
		{
			String client = extras.getString("client");
			current_client = client;
			makeChatVisible();
		}
		
		super.onNewIntent(intent);
		
	}
	
	public void handlerConfirmation(JSONObject response)
	{
		try 
		{
			String from = response.getString("client_from");
			String id = response.getString("message_id");
			
			if(chatMessages.containsKey(from))
			{
				ArrayList<ChatMessage> lcm = chatMessages.get(from);
				
				for(ChatMessage cm : lcm)
				{
					if(cm.id.equals(id))
					{
						//Log.i("tantest","confirmacion identificada");
						cm.confirmed = true;
						canSave = true;
						
						break;
					}
				}
				
				String client = current_client;
				
				if(current_client == null)
				{
					client = last_current_client;
				}
				
				ListView lv = (ListView) chatViews.get(client).findViewById(R.id.chat_lv);
				((MessageListAdapter)lv.getAdapter()).notifyDataSetChanged();
				
				ContactItemListView contact  = ContactUtil.Cache.contacts.get(client);
				if( contact != null)
				{
					 contact.setLastDate(new Date());
					 ((BaseAdapter) chatsListView.getAdapter()).notifyDataSetChanged();
				}
				
				/*(new Thread(){
				    
					public void run() 
					{
						//Guarda en base de datos
						
						//Envia mensaje al otro usuario
						Log.i("tantest","actualizando");
						
						String client = current_client;
						
						if(current_client == null)
						{
							client = last_current_client;
						}
						
						ListView lv = (ListView) chatViews.get(client).findViewById(R.id.chat_lv);
						((MessageListAdapter)lv.getAdapter()).notifyDataSetChanged();
						
						ContactItemListView contact  = ContactUtil.Cache.contacts.get(client);
						if( contact != null)
						{
							 contact.setLastDate(new Date());
							 ((BaseAdapter) chatsListView.getAdapter()).notifyDataSetChanged();
						}
				   }
				}).start();*/
				
				
			}
			
		} 
		catch (JSONException e) 
		{
			
		}

	}
	
	public void makeChatVisible()
	{
		loader.setVisibility(View.GONE);
		chatsListView.setVisibility(View.GONE);
		
		chatContainer.removeAllViews();
		chatContainer.addView(chatViews.get(current_client));
		
		//chatContainer.setVisibility(View.VISIBLE);
		//chatActions.setVisibility(View.VISIBLE);
		container.setVisibility(View.VISIBLE);
	}
	
	public void composeReceivedMessage(final String from, String message, final String message_id)
	{

		//Log.i("tantest", "mensaje recibido: "+ message);
		canSave = true;
		
		ListView lv = (ListView) chatViews.get(from).findViewById(R.id.chat_lv);
		
		if(message_id.endsWith("_root"))
		{
			chatMessages.get(from).add(new ChatMessage(true, message, true, message_id));

		}
		else
		{
			chatMessages.get(from).add(new ChatMessage(true, message, false, message_id));

		}
		
		((MessageListAdapter)lv.getAdapter()).notifyDataSetChanged();
		
		server.send(MessageCallback.CHAT_CONFIRMATION, from, message_id, client_id);
		
		ContactItemListView contact  = ContactUtil.Cache.contacts.get(from);
		if( contact != null)
		{
			 contact.setLastDate(new Date());
			 ((BaseAdapter) chatsListView.getAdapter()).notifyDataSetChanged();
		}
		
		/*
		(new Thread(){
		    
			public void run() 
			{
				//Guarda en base de datos
				
				//Envia mensaje al otro usuario
				Log.i("tantest","Envio confirmacion");
				
				server.send(MessageCallback.CHAT_CONFIRMATION, from, message_id, client_id);
				
				ContactItemListView contact  = ContactUtil.Cache.contacts.get(from);
				if( contact != null)
				{
					 contact.setLastDate(new Date());
					 ((BaseAdapter) chatsListView.getAdapter()).notifyDataSetChanged();
				}
		   }
		}).start();
		*/
	}
	
	public void ComposeSentMessage(View view)
	{
		ComposeSentCustomMessage(sender.getText().toString(), false);
	}
	
	public void ComposeSentCustomMessage(String message, boolean root)
	{
		
		if(!message.equals(""))
		{
			canSave = true;
			
			ListView lv = (ListView) chatViews.get(current_client).findViewById(R.id.chat_lv);
			
			String id = UUID.randomUUID().toString();
			
			if(root)
			{
				 id += "_root";
			}
			
			chatMessages.get(current_client).add(new ChatMessage(false, message, root, id));
			//((ContactListAdapter)chatsListView.getAdapter()).add(contact);
			((MessageListAdapter)lv.getAdapter()).notifyDataSetChanged();
			
			sender.setText("");
			
			(new Thread(){
			    
				public void run() 
				{
					//Guarda en base de datos chatMessages
					int last = chatMessages.get(current_client).size() -1;
					
					//Cuidado si envio mas de uno muy rapido puede haber error con last
					ChatMessage cm = chatMessages.get(current_client).get(last);
					
					//String message_id = UUID.randomUUID().toString();
					//Log.i("tantest", "mensaje enviado: "+ StringEscapeUtils.escapeJava(cm.message));
					//Cuidado!! mirar cuando usuairo envia mensaje que contiene caracteres raros como comillas
					server.send(MessageCallback.CHAT_MESSAGE, current_client, StringEscapeUtils.escapeJava(cm.message), cm.id);
				}
				
			}).start();
		
		}
	}
	

}
