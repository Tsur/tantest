package com.scripturesos.tantest.main;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.scripturesos.tantest.connection.HttpUtil;


public class UsersActivity extends Application  //implements SensorEventListener  
{
	public static class UsersActivityHandler extends Handler 
	{
        private UsersActivity parent;

        public UsersActivityHandler(UsersActivity parent) 
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
        //JSONObject response = (JSONObject) msg.obj;
        
		switch(msg.what) 
        {
			case 0: findContact((Drawable) msg.obj);break;
        	case 1: error(null);break;
			case 2: displayUserFound((JSONObject) msg.obj);break;
			case 10: error("Problema de conectividad"); break;
        	default:break;
        }
    }
	
	public UsersActivityHandler handler;
	
	//TextView debug;
	private RelativeLayout gameView, loadingView, contactView;
	private ListView contactsListView;
	private ProgressBar progress;
	private UserGameSurface userGameView;
	
	
	private Vibrator vib;
	private long[] vib_pattern = { 0, 1000, 0};

	//private Map<String,String> phonesList;
	//private ArrayList<ContactItemListView> contactItems;
	
	
	
	/* Sensor Datas */
	/*
	SensorManager m_sensorManager;
    float []m_lastMagFields;
    float []m_lastAccels;
    private float[] m_rotationMatrix = new float[16];
    private float[] m_remappedR = new float[16];
    private float[] m_orientation = new float[4];

    final static int AVERAGE_BUFFER = 30;
    float []m_prevPitch = new float[AVERAGE_BUFFER];
    float m_lastPitch = 0.f;
    float m_lastYaw = 0.f;

    int m_pitchIndex = 0;
    float []m_prevRoll = new float[AVERAGE_BUFFER];
    float m_lastRoll = 0.f;

    int m_rollIndex = 0;

    private float m_tiltCentreX = 0.f;
    private float m_tiltCentreY = 0.f;
    private float m_tiltCentreZ = 0.f;
    */
    /* End Sensor Datas */
    
    
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("tantest","Creating Activity Users");
		
		setTitle(R.string.act_contacts_title);
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_users);

		//m_sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //registerListeners();
		
		Display display = getWindowManager().getDefaultDisplay();
		
		//Log.i("tantest","Activity Width:" + display.getWidth());
		//Log.i("tantest","Activity Height:" + display.getHeight());
		
		handler = new UsersActivityHandler(this);
		vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		userGameView = new UserGameSurface(this);
		userGameView.setHandler(handler);
		userGameView.getThread().setSurfaceSize(display.getWidth(), display.getHeight());
		userGameView.requestFocus();
		
		gameView = (RelativeLayout) findViewById(R.id.act_users_game);
		gameView.addView(userGameView);
		
		loadingView = (RelativeLayout) findViewById(R.id.act_users_loading);
		contactView = (RelativeLayout) findViewById(R.id.act_users_contact);
       
		/* INIT CONTENT VIEW */
		//setContentView(R.layout.activity_users);

		//progress = (ProgressBar) findViewById(R.id.act_contacts_loader);
		
		/*Bundle extras = getIntent().getExtras();
		
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
		}*/

	}
	
	private void registerListeners() 
	{
        //m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
        //m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }
	
	@Override
    public void onDestroy() 
	{
		//m_sensorManager.unregisterListener(this);
        super.onDestroy();
    }
	
	@Override
	protected void onResume()
    {
		//registerListeners();
		super.onResume();
    }
	
	@Override
	protected void onPause() 
	{
		//m_sensorManager.unregisterListener(this);
		super.onPause();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_contacts, menu);
		return true;
	}
	
	public void findContact(Drawable bg)
	{
		//Mostramos un loading
		//userGameView.clearFocus();
		((RelativeLayout) findViewById(R.id.users_container)).setBackgroundDrawable(bg);
		
		Log.i("tantest","hiding surfaceView");
		gameView.setVisibility(View.GONE);
		userGameView.invalidate();
		
		vib.vibrate(vib_pattern,-1);
		
		loadingView.setVisibility(View.VISIBLE);
		
		(new Thread() {
		    
			public void run() 
			{
				try 
				{
					//Comprobamos si email ha sido registrado anteriormente
					JSONObject response = HttpUtil.get(HttpUtil.getURL(HttpUtil.RANDOM, new String[]{}));
					Message msg = new Message();
					
					if(response.getInt("error") == 1)
					{
						//Error not found 
						msg.what = 1;
					}
					else
					{
						//Found
						msg.what = 2;
						
						JSONObject user = response.getJSONObject("users");
						msg.obj = user;
						
						//Download image
						if(user.has("img"))
						{
							UsersUtil.downloadImg(user.getString("email"), user.getString("img"));
						}
						else
						{
							UsersUtil.imagesCache.put(user.getString("email"), HomeActivity.default_dr);
						}
					}
					
					handler.sendMessage(msg);	
				}
				catch(Exception e)
				{	
					Message msg = new Message();
					msg.what = 10;
					
					handler.sendMessage(msg);
				}
				
		    }
			
		}).start();

		//loadingView.requestFocus();
	}
	
	public void error(String txt)
	{
		loadingView.setVisibility(View.GONE);
		contactView.setVisibility(View.GONE);
		
		if(txt !=null)
		{
			Toast.makeText(UsersActivity.this, txt, Toast.LENGTH_SHORT).show();
		}
		
		userGameView.requestLayout();
		Log.i("tantest","mostrando surfaceview");
		userGameView.getThread().restart();
		gameView.setVisibility(View.VISIBLE);
		Log.i("tantest","mostrando surfaceview 2");
	}
	
	public void displayUserFound(JSONObject user)
	{
		loadingView.setVisibility(View.GONE);
		
		try 
		{
			String id = user.getString("email").toString();
			
			if(user.has("alias"))
			{
				((TextView) contactView.findViewById(R.id.act_users_contact_name)).setText(user.getString("alias").toString());
			}
			else
			{
				((TextView) contactView.findViewById(R.id.act_users_contact_name)).setText(id.substring(0, id.indexOf("@")));
			}
			
			if(user.has("gender"))
			{
				if(user.getString("gender").equals("m"))
				{
					((ImageView) contactView.findViewById(R.id.act_users_contact_gender)).setImageDrawable(HomeActivity.gender_m);
				}
				else
				{
					((ImageView) contactView.findViewById(R.id.act_users_contact_gender)).setImageDrawable(HomeActivity.gender_f);
				}
			}
			
			if(user.has("age"))
			{
				
			}
			
			if(user.has("whereabout"))
			{
				
			}
			
			if(user.has("status"))
			{
				((TextView) contactView.findViewById(R.id.act_users_contact_status)).setText(user.getString("status").toString());	
			}
			
			if(user.has("deno"))
			{
				((TextView) contactView.findViewById(R.id.act_users_contact_deno)).setText(user.getString("deno").toString());	
			}
			
			((ImageView) contactView.findViewById(R.id.act_users_contact_img)).setImageDrawable(UsersUtil.imagesCache.get(id));
			
			Animation out = new TranslateAnimation(0, 0, -(getWindowManager().getDefaultDisplay().getHeight()/2),0);
			out.setFillAfter(true);
			out.setDuration(1500);
			
			out.setAnimationListener(new AnimationListener() {

			    public void onAnimationEnd(Animation animation) {

			    	contactView.clearAnimation();
			    	//((RelativeLayout) findViewById(R.id.users_container)).setClickable(true);
					
			    }

				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}

				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					
				}

			});
			
			contactView.startAnimation(out);
			contactView.setVisibility(View.VISIBLE);
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void rebootCanvas(View view)
	{
		error(null);
	}
	
	public void startChat(View view)
	{
		error(null);
	}
	/*
	public void onAccuracyChanged(Sensor sensor, int accuracy) 
	{
		// TODO Auto-generated method stub
		
	}

    public void onSensorChanged(SensorEvent event) 
	{

    	if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) 
		{
    		Log.i("tantest","acc");
    		accel(event);
        }
		
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) 
        {
        	Log.i("tantest","mag!");
        	mag(event);
        }
    }
    
    private void accel(SensorEvent event) 
    {
        if (m_lastAccels == null) 
        {
            m_lastAccels = new float[3];
        }

        System.arraycopy(event.values, 0, m_lastAccels, 0, 3);

        if (m_lastMagFields != null) 
        {
            computeOrientation();
        }
    }

    private void mag(SensorEvent event) 
    {
        if (m_lastMagFields == null) 
        {
            m_lastMagFields = new float[3];
        }

        System.arraycopy(event.values, 0, m_lastMagFields, 0, 3);

        if (m_lastAccels != null) 
        {
            computeOrientation();
        }
    }
    
    private void computeOrientation() 
    {
        if (SensorManager.getRotationMatrix(m_rotationMatrix, null,  m_lastAccels, m_lastMagFields)) 
        {
            
        	SensorManager.getOrientation(m_rotationMatrix, m_orientation);

            // 1 radian = 57.2957795 degrees 
            // [0] : yaw, rotation around z axis
            // [1] : pitch, rotation around x axis
            // [2] : roll, rotation around y axis
            float yaw = m_orientation[0] * 57.2957795f;
            float pitch = m_orientation[1] * 57.2957795f;
            float roll = m_orientation[2] * 57.2957795f;

            m_lastYaw = m_filters[0].append(yaw);
            m_lastPitch = m_filters[1].append(pitch);
            m_lastRoll = m_filters[2].append(roll);
           
            Log.i("tantest","azi z: " + m_lastYaw);
            Log.i("tantest","pitch x: " + m_lastPitch);
            Log.i("tantest","roll y: " + m_lastRoll);
        }
    }
    
    Filter [] m_filters = { new Filter(), new Filter(), new Filter() };

    private class Filter 
    {
        static final int AVERAGE_BUFFER = 10;
        float []m_arr = new float[AVERAGE_BUFFER];
        int m_idx = 0;

        public float append(float val) 
        {
            m_arr[m_idx] = val;
            m_idx++;
            if (m_idx == AVERAGE_BUFFER)
                m_idx = 0;
            return avg();
        }
        
        public float avg() 
        {
            float sum = 0;
            for (float x: m_arr)
                sum += x;
            return sum / AVERAGE_BUFFER;
        }
    }
	*/
}
