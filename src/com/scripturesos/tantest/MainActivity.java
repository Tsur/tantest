package com.scripturesos.tantest;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.scripturesos.tantest.connection.ClientResponse;
import com.scripturesos.tantest.connection.ClientSocket;

public class MainActivity extends Activity 
{

	public class MainActivityHandler extends Handler 
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
	
	public MainActivityHandler handler;
	
	public void handleMessage(Message msg) 
	{
        JSONObject response = (JSONObject) msg.obj;
        
		switch(msg.what) 
        {
        	case 0: requestCodeSMS(response);break;
        	case 1: verifyCode(response);break;
        	case 2: requestCode(response);break;
        	case 3: verifyCodeSMS(response);break;
        	case 4: Toast.makeText(this, "¡ Tenemos un problema Houston !, espere mientras lo resolvemos ... ", Toast.LENGTH_SHORT).show();
			loader.setVisibility(View.GONE);
			((ImageButton) findViewById(R.id.main_connect)).setEnabled(true);
			break;
        	default:break;
        }
    }
	
	private ProgressBar loader;
	private ListView countriesContainer;
	private Button country;
	private ImageButton connect;
	private String abbr = "";
	private String phone;
	private String phone_country;
	private String code;
	private EditText phone_input;
	private EditText phone_code;
	private TextView textCode;
	private boolean correct = false;
	private boolean sms_mode;
	private int what = 0;
	
	private BroadcastReceiver bcr_sent;
	private BroadcastReceiver bcr_received;
	
	@SuppressWarnings("deprecation")
	@TargetApi(16)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("tantest", "CREANDO");
		super.onCreate(savedInstanceState);
		
		/*
		 * AQUI miramos si en base de datos existe entrada de cuenta ya confirmada
		 * en tal caso, directemente creamos actividad home:
		 * 
		 * Intent intent = new Intent(MainActivity.this, HomeActivity.class);
		   intent.putExtra("extra", "load");//Indica a la actividad Home que carga los chats abiertos
		   startActivity(intent);
		 */
		handler = new MainActivityHandler(this);
		
		/* INIT CONTENT VIEW */
		setContentView(R.layout.activity_main);
		
		if (Build.VERSION.SDK_INT >= 11)
		{
			getActionBar().hide();
		}
		
		loader = (ProgressBar) findViewById(R.id.main_progressbar);
		loader.setIndeterminate(true);
		
		country = (Button) findViewById(R.id.main_country);
		connect = (ImageButton) findViewById(R.id.main_connect);
		
		phone_input = (EditText) findViewById(R.id.main_phone);
		phone_code = (EditText) findViewById(R.id.main_code);
		
		textCode = (TextView) findViewById(R.id.main_verify_text);
		
		countriesContainer = (ListView) findViewById(R.id.main_lv);
		
		countriesContainer.setAdapter(new CountryListAdapter(this));
		
		countriesContainer.setOnItemClickListener(new OnItemClickListener()
		{
			@Override 
			public void onItemClick(AdapterView<?> arg0, View view,int position, long arg3)
		    { 
		    	abbr = MainActivity.abbreviations[position];
		    	
		    	//abbr = Locale.getISOCountries()[position];
		    	countriesContainer.setVisibility(View.GONE);
		    	connect.setVisibility(View.VISIBLE);
		    	
		    	Log.i("tantes","nuevo pais es: "+abbr);
		    	
		    	if (Build.VERSION.SDK_INT >= 16)
		    		country.setBackground(CountryListAdapter.Cache.images.get(position));
		    	else
		    		country.setBackgroundDrawable(CountryListAdapter.Cache.images.get(position));
		    }
		});
		
		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		
		try
		{
			abbr = tm.getSimCountryIso();
			Log.i("tantest","abbr: "+abbr);
			
			if(abbr != "")
			{
				int imageResource = getResources().getIdentifier(abbr.toLowerCase(), "drawable", getPackageName());
				Drawable img = getResources().getDrawable(imageResource);
	        
		    	if (Build.VERSION.SDK_INT >= 16)
		    		country.setBackground(img);
		    	else
		    		country.setBackgroundDrawable(img);
			}
		}
		catch(Exception e)
		{
			Log.i("tantest","abbr: nada");
		}
		
		ClientSocket.getInstance()
		.getHandlers().put("error", new ClientResponse(handler,4));
		
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
			case R.id.menu_main_code:
				what = 2;
				connectButtom((ImageButton) findViewById(R.id.main_connect));
				break;
			default:break;
		}
		
		return true;
	}
	
	public void displayCountries(View view)
	{
		assert countriesContainer != null;
		
		if(countriesContainer.isShown())
	    {
		   countriesContainer.setVisibility(View.GONE);
		   connect.setVisibility(View.VISIBLE);
	    }
	    else
	    {
		   countriesContainer.setVisibility(View.VISIBLE);
		   connect.setVisibility(View.GONE);
	    }
	}
	
	public void connectButtom(View view)
	{
		/*Intent cintent = new Intent(this, ContactsActivity.class);
		Log.i("tantes","iniciando actividad");
		startActivity(cintent);*/
		
		Log.i("tantest", "Pulsado connect");
		loader.setVisibility(View.VISIBLE);

		phone = phone_input.getText().toString();
		
		Log.i("tantest", "telefono es: "+ phone);
		Log.i("tantest", "pais es: "+ abbr);
		
		if(phone.equals("") || abbr.equals(""))
		{
			//Display error
			Toast.makeText(this, "El teléfono y el pais son obligatorios", Toast.LENGTH_SHORT).show();
			loader.setVisibility(View.GONE);
			return;
		}
		
		//ImageButton connect = (ImageButton) findViewById(R.id.main_connect);
		
		view.setEnabled(false);
		
		(new Thread() {
		    
			public void run() 
			{
				try 
				{
					
					PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
					PhoneNumber phoneData = phoneUtil.parse(phone, abbr);
		        
					phone = phoneUtil.format(phoneData, PhoneNumberFormat.E164);
					phone_country = "+"+phoneData.getCountryCode();
					
					Log.i("tantest", "Telefono final es: "+phone);
					Log.i("tantest", "El codigo del pais: "+phone_country);
					
					//AQUI TENEMOS QUE CHEQUEAR SI PHONE YA HA RECIBIDO EL MSG CON EL CODIGO
					//EN CUYO CASO LLAMAMOS DIRECTAMENTE A REQUESTCODE
					
					ClientSocket
					.getInstance()
					.init(phone,phone_country)
					.send("createUser", phone, new ClientResponse(handler,what));
					
					what = 0;
	    		
				} 
				catch(NumberParseException e) 
				{
					Log.i("tantest", "Error telefono "+phone);
					Toast.makeText(MainActivity.this, "El teléfono no es correcto", Toast.LENGTH_SHORT).show();
					loader.setVisibility(View.GONE);
				}
		    }
		}).start();

	}
	
	public void test(View view)
	{
		loader.setVisibility(View.VISIBLE);
		
		Thread startTest = new Thread() {
		    
			public void run() 
			{
				Intent intent = new Intent(MainActivity.this, TestActivity.class);
				Log.i("tantest","iniciando actividad");
				//overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
				startActivity(intent);
		    }
		};
		
		startTest.start();
		
	}
	
	public void requestCodeSMS(JSONObject response)
	{
		
		try
		{

			JSONObject result = response.getJSONObject("response");
			
			String code = result.getString("code");
			//boolean sms =  result.getBoolean("sms");
			
			Log.i("tantest","Server envia codigo a requestCodeSMS: "+code);

			sms_mode = true;
			sendSMS(phone,"Tantest codigo: "+code);

			//Guardamos Codigo para posterior verificacion
			this.code = code;
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void requestCode(JSONObject response)
	{
		
		try
		{

			JSONObject result = response.getJSONObject("response");
			
			String code = result.getString("code");
			boolean sms =  result.getBoolean("sms");
			
			Log.i("tantest","Server envia codigo a requestCode: "+code);
			
			//Ocultamos interfaz paso 1
			country.setVisibility(View.GONE);
			phone_input.setVisibility(View.GONE);
			connect.setVisibility(View.GONE);
			
			View line = (View) findViewById(R.id.main_vertical_line);
			line.setVisibility(View.GONE);
			
			ImageButton test = (ImageButton) findViewById(R.id.main_test);
			test.setVisibility(View.GONE);
			
			//Mostramos interfaz paso 2
			phone_code.setVisibility(View.VISIBLE);
			
			ImageButton verify = (ImageButton) findViewById(R.id.main_verify);
			verify.setVisibility(View.VISIBLE);
			
			//if(sms)
			//{
				
				if(phone.equals("+34652905791"))
				{
					info("Normalmene enviamos un sms con el código pero como te conozco, te ahorro el coste del envio del sms. Solamente tienes que pulsar en verificar :)");
					phone_code.setText(code);
				}
				else
				{
					//Enviamos sms
					//sms_mode = false;
					//sendSMS(phone,getString(R.string.act_main_sms_body)+" "+code+". "+getString(R.string.act_main_sms_sign));
					info(getString(R.string.act_main_verify_nosms));
				}

			//}
			//else
			//{
			//	info(getString(R.string.act_main_verify_nosms));
			//}
			
			//Guardamos Codigo para posterior verificacion
			this.code = code;
			
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*Animation out = new AlphaAnimation(1.0f, 0.0f);
		out.setFillAfter(true);
		out.setStartOffset(5000);
		out.setDuration(600);
		
		out.setAnimationListener(new AnimationListener() {

		    @Override
		    public void onAnimationEnd(Animation animation) {

		    	textCode.setVisibility(View.GONE);

		    }

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});
		
		textCode.startAnimation(out);*/	
		
	}
	
	private void info(String text)
	{
		Animation out = new TranslateAnimation(0, 0, -50, 0);
		out.setFillAfter(true);
		out.setDuration(1300);
		
		if(text != null)
		{
			textCode.setText(text);
		}
		
		textCode.startAnimation(out);
		textCode.setVisibility(View.VISIBLE);

		loader.setVisibility(View.GONE);
	}
	
	private void sendSMS(String phoneNumber, String message)
	{
	       
		String SENT = "SMS_SENT";
	    //String DELIVERED = "SMS_DELIVERED";
		String RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

        /*
        <receiver android:name="com.scripturesos.tantest.SMSReceiver"> 
            <intent-filter> 
                <action android:name="android.provider.Telephony.SMS_RECEIVED" /> 
            </intent-filter> 
        </receiver>
         */
		bcr_received = new BroadcastReceiver()
        {
        	@Override
        	public void onReceive(Context arg0, Intent intent) 
        	{
        		if(sms_mode)
                {
                	this.abortBroadcast();
                	
                	Bundle bundle = intent.getExtras();        
        	        SmsMessage[] msgs = null;
        	        
        	        boolean received = false;            
        	        
        	        if(bundle != null)
        	        {
        	            //---retrieve the SMS message received---
        	            Object[] pdus = (Object[]) bundle.get("pdus");
        	            msgs = new SmsMessage[pdus.length]; 
        	            
        	            for(int i=0; i<msgs.length; i++)
        	            {
        	                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]); 
        	                
        	                if(msgs[i].getMessageBody().toString().equals("Tantest codigo: "+code))
        	                {
        	                	received = true;
        	                	break;
        	                }
        	                      
        	            }
        	            
        	            if(received)
        	            {
        	            	ClientSocket
        	            	.getInstance()
        	            	.send("confirmValidationCode", code, new ClientResponse(handler,3));
        	            }

        	        }
        	        
        		    this.clearAbortBroadcast();
                }   
        	}
        };
		
		registerReceiver(bcr_received, new IntentFilter(RECEIVED));
        
		bcr_sent = new BroadcastReceiver()
        {
        	@Override
        	public void onReceive(Context arg0, Intent intent) 
        	{
        		
        		
        		switch (getResultCode())
        		{
                	case Activity.RESULT_OK:
                	
                	/*if(!sms_mode)
                	{
                		info(null);
                	}
                	else
                	{*/
                		info(getString(R.string.act_main_sms_validating));
                	//}
                	
                    break;
                	/*case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(getBaseContext(), "Generic failure", 
                            Toast.LENGTH_SHORT).show();
                    break;
                	case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(getBaseContext(), "No service", 
                            Toast.LENGTH_SHORT).show();
                    break;
                	case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(getBaseContext(), "Null PDU", 
                            Toast.LENGTH_SHORT).show();
                    break;
                	case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(getBaseContext(), "Radio off", 
                            Toast.LENGTH_SHORT).show();
                    break;*/
                    default:
                    	//Se puede utilizar el tablet pero necesitas un telefono primeramente
                    	loader.setVisibility(View.GONE);
                    	((ImageButton) findViewById(R.id.main_connect)).setEnabled(true);
                    	Toast.makeText(getBaseContext(), "Tu dispositivo no es compatible. Debes obtener el código desde un dispositivo compatible.", 
                            Toast.LENGTH_SHORT).show();
                    break;
        		}
        	}
        };
        
        registerReceiver(bcr_sent, new IntentFilter(SENT));
 
        //---when the SMS has been delivered---
        /*registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context arg0, Intent arg1) 
            {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", 
                                Toast.LENGTH_SHORT).show();
                    break;
                    case Activity.RESULT_CANCELED:
                    	loader.setVisibility(View.GONE);
                    	Toast.makeText(getBaseContext(), "Se ha producido un error al recibir el código de verifiación. Asegúrese de que su dispositivo puede recibir mensajes de texto SMS.", 
                                Toast.LENGTH_SHORT).show();
                    break;                        
                }
            }
        }, new IntentFilter(DELIVERED));*/        
        
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);
        
        /*PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
        new Intent(DELIVERED), 0);*/
        
		SmsManager sms = SmsManager.getDefault();
	    sms.sendTextMessage(phoneNumber, null, message, sentPI/*, deliveredPI*/,null);
	}
	
	@Override
	protected void onDestroy() 
	{
		unregisterReceiver(bcr_sent);
		unregisterReceiver(bcr_received);
		super.onDestroy();
	}
	
	public void verifyCodeButtom(View view)
	{
		Log.i("tantest", "Verificando codigo");
		Log.i("tantest", "Codigo dado: "+phone_code.getText().toString());
		Log.i("tantest", "Codigo correcto: "+code);
		
		ClientSocket
		.getInstance()
		.send("confirmValidationCode", phone_code.getText().toString(), new ClientResponse(handler,1));
		
		view.setEnabled(false);
		loader.setVisibility(View.VISIBLE);
	}
	
	public void verifyCodeSMS(JSONObject response)
	{

		loader.setVisibility(View.GONE);

		boolean confirmated = false;
		
		try 
		{
			confirmated = response.getBoolean("response");
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(confirmated)
		{
			Log.i("tantest", "Codigo correcto!");
			
			unregisterReceiver(bcr_sent);
			unregisterReceiver(bcr_received);
			
			startActivity(new Intent(MainActivity.this, HomeActivity.class));
			
			Log.i("tantes","iniciando actividad");
			
		}

	}
	
	public void verifyCode(JSONObject response)
	{

		loader.setVisibility(View.GONE);

		boolean confirmated = false;
		
		try 
		{
			confirmated = response.getBoolean("response");
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		final Animation out = new AlphaAnimation(1.0f, 0.0f);
		out.setDuration(500);
		out.setStartOffset(1500);
		
		out.setAnimationListener(new AnimationListener() {

		    @Override
		    public void onAnimationEnd(Animation animation)
		    {
		    	
		    	textCode.setVisibility(View.GONE);
		    	
		    	if(correct == true)
		    	{
		    		//Hay que crear entrada en BD asegurando que en el onCreate
		    		//pasamos a HomeActivity
		    		startActivity(new Intent(MainActivity.this, HomeActivity.class));
		    	}

		    }

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});
		
		if(confirmated)
		{
			Log.i("tantest", "Codigo correcto!");
			
			//textCode.setGravity(Gravity.CENTER);
			textCode.setText(R.string.act_main_right_code);
			
			correct = true;
			
			textCode.startAnimation(out);
			
			//homeAct = new Intent(MainActivity.this, HomeActivity.class);
			Log.i("tantes","iniciando actividad");
			//overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			//startActivity(homeAct);
			
		}
		else
		{
			Log.i("tantest", "Codigo INcorrecto!");
			
			textCode.setText(R.string.act_main_wrong_code);

			textCode.startAnimation(out);
			
			ImageButton verify = (ImageButton) findViewById(R.id.main_verify);
    		
			verify.setEnabled(true);
		}
	}
	
    @Override
    public void onResume()
    {
    	super.onResume();
    	
    	Log.i("tantest", "RESUME");
    	
    	if(loader != null)
		{
    		loader.setVisibility(View.GONE);
		}
    	
    }
	
    public static String[] countries =
    	{ 
    		"Alemania","España", 
            "Finlandia","Francia","Reino Unido",
            "Honduras","Israel"
        };
    	
    	//Usar mejor Locale.getISOCountries()
    	public static String[] abbreviations = 
    	{ 
    		
    		"DE", "ES", "FI", "FR", "GB",
            "HN", "IL"
         };
	/*public static String[] countries =
	{ 
		"Andorra","(al-Imārāt) الامارات","(Afganistan) افغانستان", "ANTIGUA AND BARBUDA", 
		"ANGUILLA","ALBANIA","ARMENIA", "Netherlands Antilles", "ANGOLA", 
		"ARGENTINA","AMERICAN SAMOA", "AUSTRIA", "AUSTRALIA", "ARUBA", "ÅLAND ISLANDS",
		"AZERBAIJAN", "BOSNIA AND HERZEGOVINA", "BARBADOS", "BANGLADESH", "BELGIUM", 
		"BURKINA FASO","BULGARIA", "BAHRAIN", "BURUNDI", "BENIN", "SAINT BARTHÉLEMY", 
		"BERMUDA", "BRUNEI DARUSSALAM", "BOLIVIA", "BRAZIL",
        "BAHAMAS", "BHUTAN", "BOTSWANA", "BELARUS", "BELIZE", "CANADA", 
        "COCOS","CONGO, THE DEMOCRATIC REPUBLIC OF THE", "CENTRAL AFRICAN REPUBLIC", "CONGO",
        "SWITZERLAND", "CÔTE D'IVOIRE", "COOK ISLANDS", "CHILE", "CAMEROON", "CHINA",
        "COLOMBIA", "COSTA RICA", "CUBA", "CAPE VERDE", "CURAÇAO",
        "CHRISTMAS ISLAND", "CYPRUS", "CZECH REPUBLIC", "GERMANY", "DJIBOUTI", "DENMARK",
        "DOMINICA", "DOMINICAN REPUBLIC", "ALGERIA",
        "ECUADOR", "ESTONIA", "EGYPT", "WESTERN SAHARA", "ERITREA", "SPAIN", "ETHIOPIA",
        "FINLAND", "FIJI", "FALKLAND ISLANDS (MALVINAS)",
        "MICRONESIA, FEDERATED STATES OF", "FAROE ISLANDS", "FRANCE", "GABON", "UNITED KINGDOM",
        "GRENADA", "GEORGIA", "GUERNSEY", "GHANA", "GIBRALTAR", "GREENLAND",
        "GAMBIA", "GUINEA", "EQUATORIAL GUINEA", "GREECE", "SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS", 
        "GUATEMALA", "GUAM", "GUINEA-BISSAU", "GUYANA", "HONG KONG", "HONDURAS",
        "CROATIA", "HAITI", "HUNGARY", "IC", "ID", "IE", "IL", "IM", "IN", "IQ", "IR",
        "IS", "IT", "JE", "JM", "JO", "JP", "KE", "KG", "KH", "KI", "KM",
        "KN", "KP", "KR", "KW", "KY", "KZ", "LA", "LB", "LC", "LI", "LK",
        "LR", "LS", "LT", "LU", "LV", "LY", "MA", "MC", "MD", "ME", "MF",
        "MG", "MH", "MK", "ML", "MM", "MN", "MO", "MP", "MQ", "MR", "MS",
        "MT", "MU", "MV", "MW", "MX", "MY", "MZ", "NA", "NC", "NE", "NF",
        "NG", "NI", "NL", "NO", "NP", "NR", "NU", "NZ", "OM", "PA", "PE",
        "PF", "PG", "PH", "PK", "PL", "PN", "PR", "PS", "PT", "PW", "PY",
        "QA", "RO", "RS", "RU", "RW", "SA", "SB", "SC", "SD", "SE", "SG",
        "SH", "SI", "SK", "SL", "SM", "SN", "SO", "SR", "SS", "ST", "SV",
        "SY", "SZ", "TC", "TD", "TF", "TG", "TH", "TJ", "TK", "TL", "TM", 
        "TN", "TO", "TR", "TT", "TV", "TW", "TZ", "UA", "UG", "US", "UY",
        "UZ", "VA", "VC", "VE", "VG", "VI", "VN", "VU", "WF", "WS", "YE",
        "YT", "ZA","ZM","ZW"
    };
	
	//Usar mejor Locale.getISOCountries()
	public static String[] abbreviations = 
	{ 
		"AD","AE","AF", "AG", "AI","AL","AM", "AN", "AO", 
		"AR","AS", "AT", "AU", "AW", "AX", "AZ", "BA", "BB", "BD", "BE", 
		"BF","BG", "BH", "BI", "BJ", "BL", "BM", "BN", "BO", "BR",
        "BS", "BT", "BW", "BY", "BZ", "CA", "CC","CD", "CF", "CG",
        "CH", "CI", "CK", "CL", "CM", "CN", "CO", "CR", "CU", "CV", "CW",
        "CX", "CY", "CZ", "DE", "DJ", "DK", "DM", "DO2", "DZ",
        "EC", "EE", "EG", "EH", "ER", "ES", "ET", "FI", "FJ", "FK",
        "FM", "FO", "FR", "GA", "GB", "GD", "GE", "GG", "GH", "GI", "GL",
        "GM", "GN", "GQ", "GR", "GS", "GT", "GU", "GW", "GY", "HK", "HN",
        "HR", "HT", "HU", "IC", "ID", "IE", "IL", "IM", "IN", "IQ", "IR",
        "IS", "IT", "JE", "JM", "JO", "JP", "KE", "KG", "KH", "KI", "KM",
        "KN", "KP", "KR", "KW", "KY", "KZ", "LA", "LB", "LC", "LI", "LK",
        "LR", "LS", "LT", "LU", "LV", "LY", "MA", "MC", "MD", "ME", "MF",
        "MG", "MH", "MK", "ML", "MM", "MN", "MO", "MP", "MQ", "MR", "MS",
        "MT", "MU", "MV", "MW", "MX", "MY", "MZ", "NA", "NC", "NE", "NF",
        "NG", "NI", "NL", "NO", "NP", "NR", "NU", "NZ", "OM", "PA", "PE",
        "PF", "PG", "PH", "PK", "PL", "PN", "PR", "PS", "PT", "PW", "PY",
        "QA", "RO", "RS", "RU", "RW", "SA", "SB", "SC", "SD", "SE", "SG",
        "SH", "SI", "SK", "SL", "SM", "SN", "SO", "SR", "SS", "ST", "SV",
        "SY", "SZ", "TC", "TD", "TF", "TG", "TH", "TJ", "TK", "TL", "TM", 
        "TN", "TO", "TR", "TT", "TV", "TW", "TZ", "UA", "UG", "US", "UY",
        "UZ", "VA", "VC", "VE", "VG", "VI", "VN", "VU", "WF", "WS", "YE",
        "YT", "ZA","ZM","ZW"
     };*/
    	
}