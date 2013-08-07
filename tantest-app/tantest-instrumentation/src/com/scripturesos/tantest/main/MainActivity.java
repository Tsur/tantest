package com.scripturesos.tantest.main;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.scripturesos.tantest.main.R;
import com.scripturesos.connection.DatabaseHelper;
import com.scripturesos.connection.HttpUtil;

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
			case 1: goHome((JSONObject) msg.obj, true);break;
			case 2: requestCode((JSONObject) msg.obj);break;
			case 3: verifyCode((JSONObject) msg.obj);break;
			case 4: 
				JSONObject response = (JSONObject) msg.obj;
				
				try 
				{
					if(response.getInt("access") == 0)
					{
						info("Hemos vuelto a enviar un cÛdigo de verificaciÛn a su correo electrÛnico, introd˙zcalo y pulse verificar para validar su cuenta, por favor.", false);
					}
					else
					{
						 ifError("° Tenemos un problema Houston ! IntÈntelo m·s tarde");break;
					}
				} 
				catch (JSONException e) 
				{
					 ifError("° Tenemos un problema Houston ! IntÈntelo m·s tarde");break;
				}
				
				resend_text.setEnabled(true);
				break;
			case 5:
				loginGUI();
				break;
        	//Errors
        	case 10: ifError("° Tenemos un problema Houston ! IntÈntelo m·s tarde");break;
        	case 11: 
        		resend_text.setEnabled(true);
        		break;
        	case 12: ifError("Revise su conexion a internet");break;
        	
        	//Database
        	//case 20: loginGUI();break;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Log.i("tantest", "ONCREATE MAIN ACTIVITY");
		
		setContentView(R.layout.activity_main);

		handler = new MainActivityHandler(this);
		
		(new Thread() {
		    
			public void run() 
			{
				SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getReadableDatabase();
				
				Cursor cursor = db.rawQuery("SELECT value FROM options WHERE key=0", null);
				
				if(cursor.getCount() > 0)
				{
					cursor.moveToFirst();
					
					try 
					{
						goHome(new JSONObject(cursor.getString(0)),false);
					} 
					catch (Exception e) 
					{
						
					}
				}
				else
				{
					
				    Message msg = new Message();
					msg.what = 5;
					
					handler.sendMessage(msg);
				}
				
				cursor.close();
				
				db.close();
				
		    }
		}).start();
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
	
	public void loginGUI(/*Bundle data*/)
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
		
		email_input.setText("zuriebu@gmail.com");
		password_input.setText("dei97");
		
		/*if(data == null)
		{
			return;
		}
		
		if(data.containsKey("email"))
		{
			email_input.setText(data.getString("email"));
		}
		
		try {
			
			if(data.containsKey("validation"))
			{
				JSONObject response = new JSONObject();
				
				response.put("error", 0);

				requestCode(response);
				
				if(data.containsKey("code"))
				{
					code_input.setText(data.getString("code"));
				}
			}
			else
			{
				if(data.containsKey("password"))
				{
					password_input.setText(data.getString("password"));
				}
			}
		} 
		catch (JSONException e) 
		{

		}*/
	}
	
	public void connectButtom(View view)
	{
		
		Log.i("tantest", "Pulsado acceder");
		view.setVisibility(View.GONE);
		loader.setVisibility(View.VISIBLE);
		
		email = email_input.getText().toString();
		password = password_input.getText().toString();
		
		if(email.equals("") || password.equals("") || !HttpUtil.isValidEmail(email))
		{
			//Display error
			ifError("Por favor, rellene todos los campos seg˙n proceda");
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
				
				info("La contrase√±a introducida es incorrecta. En caso de la p√©rdida de la misma, puede solicitar una nueva desde aqu√≠", false);
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
				
				info("Hemos enviado un c√≥digo de verificaci√≥n a su correo electr√≥nico, introd√∫zcalo y pulse verificar para validar su cuenta, por favor.", false);
				
				validating = true;
			}
			
		} 
		catch (Exception e) 
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

	public void verifyCodeButtom(View view)
	{
		
		//code_text.setVisibility(View.GONE);
		resend_text.setVisibility(View.GONE);
		view.setVisibility(View.GONE);//.setEnabled(false);
		loader.setVisibility(View.VISIBLE);

		if(code_input.getText().toString().equals("") || code_input.getText().toString().length() != 8)
		{
			ifError("Por favor, introduzca el c√≥digo de verificaci√≥n");
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
				ifError("El Codigo de validaci√≥n es incorrecto");
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
				    		goHome(response.getJSONObject("result"),true);
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
			ifError("El Codigo de validaci√≥n es incorrecto");
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
	
	public void goHome(JSONObject user, boolean db)
	{
		
		//Base de datos
		if(db)
		{
			SQLiteDatabase dbw = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
		
			dbw.execSQL("INSERT INTO options (key, value) VALUES (0,'"+user.toString()+"')");
		}
		
		UsersUtil.initUser(user);
		Intent i = new Intent(this, HomeActivity.class);
		startActivity(i);
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
	
	/*
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) 
	{
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		  
		//Save Email
		if(!email.equals(""))
		{
			savedInstanceState.putString("email", email);
		}
		
		//Save State
		if(validating)
		{
			savedInstanceState.putBoolean("validation", true);
			
			String code = code_input.getText().toString();
			
			if(!code.equals(""))
			{
				savedInstanceState.putString("code", code);
			}
		}
		else
		{
			if(!password.equals(""))
			{
				savedInstanceState.putString("password", password);
			}
		}
			 
	}
	*/
	
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
    	/*if(validating)
    	{
    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
     
			//Set title
			alertDialogBuilder.setTitle("Cancelar cuenta");
     
			// set dialog message
			alertDialogBuilder
			.setMessage("¬øDesea cancelar su nueva cuenta y salir?")
			.setCancelable(true)
			.setPositiveButton("Si",new DialogInterface.OnClickListener(){
				
				public void onClick(DialogInterface dialog, int id) 
				{
					//Volvemos a estado inicial
					((ViewGroup)email_input.getParent()).setVisibility(View.VISIBLE);
					((ViewGroup)password_input.getParent()).setVisibility(View.VISIBLE);
					connect_bt.setVisibility(View.VISIBLE);
					
					((ViewGroup)code_input.getParent()).setVisibility(View.GONE);
					validate_bt.setVisibility(View.GONE);
					resend_text.setVisibility(View.GONE);
					code_text.setVisibility(View.GONE);
					
					email_input.setText("");
					code_input.setText("");
				}
				
			})
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog,int id) 
				{
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
				}
			});
 
    		// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
 
			// show it
			alertDialog.show();
    		
    	}
    	else
    	{
    		super.onBackPressed();
    	}*/
    	moveTaskToBack(true);
    	
    }
	
}