package com.scripturesos.tantest;

import java.io.IOException;

import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		/* INIT CONTENT VIEW */
		Log.i("tantest","CREATE HOME ACTIVITY");
		
		setContentView(R.layout.activity_home);
		
		loader = (ProgressBar) findViewById(R.id.home_progressbar);
		
		handler = new HomeActivityHandler(this);
		
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
					
					cursor.moveToNext();
					
					contacts_serialized = cursor.getString(0);
					
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
    	//mismo efecto que si pulsa el boton home: moveTaskToBackGround
    	moveTaskToBack(true);
    	//super.onBackPressed();
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
            		
            		(new Thread() {
        			    
        				public void run() 
        				{
        					SQLiteDatabase dbw = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        			
        					dbw.execSQL("INSERT INTO options (key, value) VALUES (2,'"+contacts_serialized+"')");
            	
        			    }
        			}).start();
            		
            		return;
            	}
            	
            	if(b.containsKey("chat"))
            	{
            		String chat = b.getString("chat");
            		
            		try 
            		{
						createChat((ContactItemListView)HttpUtil.fromString(chat));
					} 
            		catch (IOException e) 
            		{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
            		catch (ClassNotFoundException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}

            }
            break;
               
        }
        
    }
	
	public void createChat(ContactItemListView contact)
	{
		//Comprobar que no existe ya el chat
		
		//Si existe simplemente mostramos vista del chat
		
		//Añadir a listView del HomeActivity
		
		//Crear nueva vista de chat -> RelativeLayout
		
		//Actualizar base de datos
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
        JSONObject response = (JSONObject) msg.obj;
        
		switch(msg.what) 
        {
        	case 0: handlerMessage(response);break;//ifError("Tenemos algunos problemillas... pero tu sonrie que Dios te ama");break;
        	case 1: handlerConfirmation(response);break;//ifError("Revise su conexión a Internet y sonrie que Dios te ama");break;
        	case 2: ifError("Tenemos algunos problemillas... pero tu sonrie que Dios te ama");break;
        	default:break;
        }
    }
	
	public void ifError(String txt)
	{
		Toast.makeText(HomeActivity.this, txt, Toast.LENGTH_SHORT).show();
	}
	
	public void handlerMessage(JSONObject response)
	{
		
	}
	
	public void handlerConfirmation(JSONObject response)
	{
		
	}

}
