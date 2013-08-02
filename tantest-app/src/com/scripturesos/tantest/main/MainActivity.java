package com.scripturesos.tantest.main;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.scripturesos.tantest.connection.HttpUtil;
//import android.annotation.TargetApi;
//import android.widget.RelativeLayout;
//import com.google.i18n.phonenumbers.NumberParseException;
//import com.google.i18n.phonenumbers.PhoneNumberUtil;
//import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
//import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class MainActivity extends Activity 
{

	public static class MainActivityHandler extends Handler 
	{
        private MainActivity parent;

        public MainActivityHandler(MainActivity parent) 
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
        	//Http
			/*case 0: goHome(true);break;
			
        	case 2: requestCode(response);break;
        	
        	*/
			case 1: goHome((JSONObject) msg.obj);break;
			case 2: requestCode((JSONObject) msg.obj);break;
			case 3: verifyCode((JSONObject) msg.obj);break;
			case 4: 
				JSONObject response = (JSONObject) msg.obj;
				
				try 
				{
					if(response.getInt("access") == 0)
					{
						info("Hemos vuelto a enviar un código de verificación a su correo electrónico, introdúzcalo y pulse verificar para validar su cuenta, por favor.", false);
					}
					else
					{
						 ifError("¡ Tenemos un problema Houston ! Inténtelo más tarde");break;
					}
				} 
				catch (JSONException e) 
				{
					 ifError("¡ Tenemos un problema Houston ! Inténtelo más tarde");break;
				}
				
				resend_text.setEnabled(true);
				break;
        	//Errors
        	case 10: ifError("¡ Tenemos un problema Houston ! Inténtelo más tarde");break;
        	case 11: 
        		resend_text.setEnabled(true);
        		break;
        	case 12: ifError("Revise su conexion a internet");break;
        	
        	//Database
        	case 20: loginGUI();break;
        	default:break;
        }
    }
	
	/******************
	Class Members
	*******************/

	public MainActivityHandler handler;

	private ProgressBar loader;
	
	private ImageButton validate_bt;
	private ImageButton connect_bt;
	
	private EditText email_input;
	private EditText password_input;	
	private EditText code_input;	
	
	private TextView code_text;	
	private TextView resend_text;
	
	private String email;
	private String password;
	private boolean validating = false;
	
	//@TargetApi(11)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Log.i("tantest", "CREANDO");
		
		/*if(Build.VERSION.SDK_INT >= 11)
		{
			
		}*/
		
		setContentView(R.layout.activity_main);

		handler = new MainActivityHandler(this);
		
		/*(new Thread() {
		    
			public void run() 
			{
				SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getReadableDatabase();
				
				Cursor cursor = db.rawQuery("SELECT value FROM options WHERE key=0", null);
				
				if(cursor.getCount() > 0)
				{
					goHome(false);
				}
				else
				{
					
				    Message msg = new Message();
					msg.what = 20;
					
					handler.sendMessage(msg);
				}
				
				cursor.close();
				
				db.close();
				
		    }
		}).start();*/
		
		loginGUI();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		
		switch(item.getItemId())
		{
			case R.id.menu_main_adddevice:
				
				break;
			default:break;
		}
		
		return true;
	}
	
	public void loginGUI()
	{
		//Get Loader
		loader = (ProgressBar) findViewById(R.id.main_progressbar);
		//loader.setIndeterminate(true);
		
		//Get buttons
		validate_bt = (ImageButton) findViewById(R.id.main_validate);
		connect_bt = (ImageButton) findViewById(R.id.main_connect);
		
		//Get inputs
		email_input = (EditText) findViewById(R.id.main_email);
		password_input = (EditText) findViewById(R.id.main_password);
		code_input = (EditText) findViewById(R.id.main_code);
		
		//TextView for displaying information messages on top
		code_text = (TextView) findViewById(R.id.main_validate_text);
		resend_text = (TextView) findViewById(R.id.main_validate_resend);
		
		//Hide pre-progress bar and display login view
		//((ProgressBar) findViewById(R.id.main_initProgressbar)).setVisibility(View.GONE);
		//((RelativeLayout) findViewById(R.id.login_view)).setVisibility(View.VISIBLE);
		
	}
	
	public void connectButtom(View view)
	{
		Log.i("tantest", "Pulsado acceder");
		view.setVisibility(View.GONE);
		loader.setVisibility(View.VISIBLE);
		
		email = email_input.getText().toString();
		password = password_input.getText().toString();
		
		Log.i("tantest", "email es: "+ email);
		Log.i("tantest", "password es: "+ password);
		
		if(email.equals("") || password.equals("") || !HttpUtil.isValidEmail(email))
		{
			//Display error
			ifError("Por favor, rellene todos los campos según proceda");
			return;
		}
		
		//view.setEnabled(false);
		
		(new Thread() {
		    
			public void run() 
			{
				try 
				{
					//Comprobamos si email ha sido registrado anteriormente
					
					JSONObject response = HttpUtil.get(HttpUtil.getURL(HttpUtil.ACCESS, new String[]{email,password}));
					Message msg = new Message();
					
					if(response.getInt("access") == 1)
					{
						//Enter 
						msg.what = 1;
					}
					else
					{
						//Validate
						msg.what = 2;
					};
					
					msg.obj = response.getJSONObject("result");

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
	}
	
	public void test(View view)
	{
		loader.setVisibility(View.VISIBLE);
		
		Intent intent = new Intent(MainActivity.this, TestActivity.class);
		Log.i("tantest","iniciando actividad");
		//overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
		startActivity(intent);
	}
	
	public void requestCode(JSONObject response)
	{
		
		try
		{
			//Clear password
			password_input.setText("");
			loader.setVisibility(View.GONE);
			
			if(response.getInt("error") == 1)
			{
				//Data are wrong
				Log.i("tantest","Input is wrong");
				
				//loader.setVisibility(View.GONE);
				connect_bt.setVisibility(View.VISIBLE);//setEnabled(true);
				
				info("La contraseña introducida es incorrecta. En caso de la pérdida de la misma, puede solicitar una nueva desde aquí", false);
			}
			else
			{
				//Validate
				Log.i("tantest","No account");
				
				//Hide Elements
				((ViewGroup)email_input.getParent()).setVisibility(View.GONE);
				((ViewGroup)password_input.getParent()).setVisibility(View.GONE);
				connect_bt.setVisibility(View.GONE);
				
				((ViewGroup)code_input.getParent()).setVisibility(View.VISIBLE);
				validate_bt.setVisibility(View.VISIBLE);
				resend_text.setVisibility(View.VISIBLE);
				
				info("Hemos enviado un código de verificación a su correo electrónico, introdúzcalo y pulse verificar para validar su cuenta, por favor.", false);
			}
			
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
	
	private void info(String text, boolean showLoader)
	{
		Animation out = new TranslateAnimation(0, 0, -50, 0);
		out.setFillAfter(true);
		out.setDuration(1300);
		
		out.setAnimationListener(new AnimationListener() {

		    public void onAnimationEnd(Animation animation) {

		    	code_text.clearAnimation();
		    	//code_text.setVisibility(View.GONE);

		    }

			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}

		});
		
		if(text != null)
		{
			code_text.setText(text);
		}
		
		code_text.startAnimation(out);
		code_text.setVisibility(View.VISIBLE);

		if(!showLoader)
		{
			loader.setVisibility(View.GONE);
		}
	}

	
	@Override
	protected void onDestroy() 
	{	
		super.onDestroy();
	}
	
	public void verifyCodeButtom(View view)
	{
		
		//code_text.setVisibility(View.GONE);
		resend_text.setVisibility(View.GONE);
		view.setVisibility(View.GONE);//.setEnabled(false);
		loader.setVisibility(View.VISIBLE);
		
		validating = true;
		
		if(code_input.getText().toString().equals("") || code_input.getText().toString().length() != 8)
		{
			ifError("Por favor, introduzca el código de verificación");
			return;
		}

		(new Thread() {
		    
			public void run() 
			{
				try 
				{

					Message msg = new Message();
					msg.what = 3;
					msg.obj = HttpUtil.get(HttpUtil.getURL(HttpUtil.VALIDATE_CODE, new String[]{email, code_input.getText().toString()}));
					
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
	}
	
	public void verifyCode(final JSONObject response)
	{

		loader.setVisibility(View.GONE);
		
		try 
		{
			
			if(response.getInt("validate") == 0)
			{
				code_input.setText("");
				validate_bt.setVisibility(View.VISIBLE);//.setEnabled(true);
				resend_text.setVisibility(View.VISIBLE);
				ifError("El Codigo de validación es incorrecto");
			}
			else
			{
				Animation out = new TranslateAnimation(0, 0, -50, 0);
				out.setFillAfter(true);
				out.setDuration(700);
				
				out.setAnimationListener(new AnimationListener() {

				    public void onAnimationEnd(Animation animation) {

				    	code_text.clearAnimation();
				    	
				    	try
				    	{
				    		goHome(response.getJSONObject("result"));
				    	}
				    	catch(Exception e)
				    	{
				    		ifError("Un problema ha ocurrido");
				    		resend_text.setVisibility(View.VISIBLE);
				    	}

				    }

					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
						
					}

					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
						
					}

				});
				
				code_text.setText(R.string.act_main_right_code);
				//code_text.setVisibility(View.VISIBLE);
				code_text.startAnimation(out);
			}

		} 
		catch (JSONException e) 
		{
			code_input.setText("");
			validate_bt.setVisibility(View.VISIBLE);
			resend_text.setVisibility(View.VISIBLE);
			ifError("El Codigo de validación es incorrecto");
		}
		
	}
	
	public void sendCodeButtom(View view)
	{
		
		view.setEnabled(false);
		
		(new Thread() {
		    
			public void run() 
			{
				try 
				{
					Message msg = new Message();
					msg.what = 4;
					msg.obj = HttpUtil.get(HttpUtil.getURL(HttpUtil.ACCESS, new String[]{email,password}));

					handler.sendMessage(msg);
				}
				catch(Exception e)
				{
					
					Message msg = new Message();
					msg.what = 11;
					
					handler.sendMessage(msg);
				}
		    }
		}).start();
	}
	
	public void goHome(JSONObject user)
	{
		
		//Base de datos
		/*SQLiteDatabase dbw = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
		
		dbw.execSQL("INSERT INTO options (key, value) VALUES (0,'"+email+"')");*/
		
		UsersUtil.initUser(user);
		startActivity(new Intent(MainActivity.this, HomeActivity.class));
		finish();
	}
	
	public void ifError(String txt)
	{
		loader.setVisibility(View.GONE);
		
		Toast.makeText(MainActivity.this, txt, Toast.LENGTH_SHORT).show();
		
		if(validating)
		{
			validate_bt.setVisibility(View.VISIBLE);
			resend_text.setVisibility(View.VISIBLE);
		}
		else
		{
			connect_bt.setVisibility(View.VISIBLE);
			code_text.setVisibility(View.GONE);
		}
	}
	
    @Override
    public void onResume()
    {
    	if(loader != null)
		{
    		loader.setVisibility(View.GONE);
		}
    	
    	super.onResume();
    }
    
    @Override
    public void onBackPressed() 
    {
    	//if(validating){}
    	moveTaskToBack(false);
    	super.onBackPressed();
    }

}