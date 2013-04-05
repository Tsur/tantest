package com.scripturesos.tantest;

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

public class HomeActivity extends Application {

	private ProgressBar loader;
	private String client_id;
	private String country_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		/* INIT CONTENT VIEW */
		Log.i("tantest","CREATE HOME ACTIVITY");
		
		setContentView(R.layout.activity_home);
		
		loader = (ProgressBar) findViewById(R.id.home_progressbar);
		
		handler = new HomeActivityHandler(this);
		
		ClientSocket.getInstance()
		.getHandlers().put("onServerError", new ClientResponse(handler,0));
		
		ClientSocket.getInstance()
		.getHandlers().put("onConnectionError", new ClientResponse(handler,1));
		
		ClientSocket.getInstance()
		.getHandlers().put("OnTimeoutError", new ClientResponse(handler,2));
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
    	
    	(new Thread() {
		    
			public void run() 
			{
				if(client_id == null || country_id == null)
				{
					SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getReadableDatabase();
					
					Cursor cursor = db.rawQuery("SELECT value FROM options WHERE key=0 OR key=1", null);
					
					cursor.moveToFirst();
					
					client_id = cursor.getString(0);
					
					cursor.moveToNext();
					
					country_id = cursor.getString(0);
					
					cursor.close();
					
					db.close();
					
					Log.i("tantest","phone: "+client_id);
					Log.i("tantest","country: "+country_id);
					
				}
				
				ClientSocket.getInstance().init(client_id,country_id);
				
		    }
		}).start();
    	
    	
    	
    	super.onResume();
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
		Log.i("tantes","iniciando actividad");
		startActivity(cintent);
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

				
				//RelativeLayout rl = (RelativeLayout) findViewById(R.id.main_logoimg);
				//loader = new MYGIFView(this);
				//rl.addView(loader);
				
				Intent intent = new Intent(this, TestActivity.class);
				Log.i("tantes","iniciando actividad");
				startActivity(intent);
				

				break;
			case R.id.menu_header_social:
				Intent cintent = new Intent(this, ContactsActivity.class);
				Log.i("tantes","iniciando actividad");
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
            	
            	Log.i("tantest","ContactsActivity me manda saludos: "+b.getString("saludos"));
            	//Guardo estado que viene en data
            }
               
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
        JSONObject response = (JSONObject) msg.obj;
        
		switch(msg.what) 
        {
        	case 0: ifError("Tenemos algunos problemillas... pero tu sonrie que Dios te ama");break;
        	case 1: ifError("Revise su conexión a Internet y sonrie que Dios te ama");break;
        	case 2: ifError("Tenemos algunos problemillas... pero tu sonrie que Dios te ama");break;
        	default:break;
        }
    }
	
	public void ifError(String txt)
	{
		Toast.makeText(HomeActivity.this, txt, Toast.LENGTH_SHORT).show();
	}

}
