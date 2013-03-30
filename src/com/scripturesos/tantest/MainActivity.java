package com.scripturesos.tantest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
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

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.scripturesos.tantest.connection.MainClientSocketController;

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
        switch(msg.what) 
        {
        	case 0: validateCode((String) msg.obj);
            break;
        }
    }
	
	private ProgressBar loader;
	private ListView countriesContainer;
	private Button country;
	private ImageButton connect;
	private String abbr;
	private String phone;
	private String code;
	private EditText phone_input;
	private EditText phone_code;
	private TextView textCode;
	private boolean correct = false;
	
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
	
	public void connect(View view)
	{
		
		Log.i("tantest", "Pulsado connect");
		loader.setVisibility(View.VISIBLE);
		loader.setIndeterminate(true);

		phone = phone_input.getText().toString();
		
		Log.i("tantest", "telefono es: "+ phone);
		Log.i("tantest", "pais es: "+ abbr);
		
		if(phone.equals("") || abbr == null)
		{
			//Display error
			return;
		}
		
		ImageButton connect = (ImageButton) findViewById(R.id.main_connect);
		
		connect.setEnabled(false);
		
		Thread handlePhone = new Thread() {
		    
			public void run() 
			{
				try 
				{
					
					PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
					PhoneNumber phoneData = phoneUtil.parse(phone, abbr);
		        
					phone = /*"+41661188615";*/phoneUtil.format(phoneData, PhoneNumberFormat.E164);
					Log.i("tantest", "Telefono final es: "+phone);
		        
					/*ClientSocket
					.getInstance()
					.init(phone)
					.send("createUser", phone, new MainClientSocketController(handler,"createUser"));
					*/
					MainClientSocketController responseController = new MainClientSocketController(handler,"createUser");
					responseController.setResponse("{\"validationCode\":\"3434\"}");
	    		
					new Thread(responseController).start();
	    		
				} 
				catch(NumberParseException e) 
				{
					Log.i("tantest", "Error telefono "+phone);
				}
		    }
		};
		
		handlePhone.start();
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
	
	public void validateCode(String code)
	{
		Log.i("tantest","Main validateCode: "+code);
		country.setVisibility(View.GONE);
		phone_input.setVisibility(View.GONE);
		View line = (View) findViewById(R.id.main_vertical_line);
		line.setVisibility(View.GONE);
		phone_code.setVisibility(View.VISIBLE);
		ImageButton test = (ImageButton) findViewById(R.id.main_test);
		test.setVisibility(View.GONE);
		
		connect.setVisibility(View.GONE);
		ImageButton verify = (ImageButton) findViewById(R.id.main_verify);
		verify.setVisibility(View.VISIBLE);

		if(phone.equals("+34652905791"))
		{
			textCode.setText(textCode.getText()+" Pero como te conozco, solo tienes que pulsar en verificar :)");
			phone_code.setText(code);
		}
		
		Animation out = new TranslateAnimation(0, 0, -50, 0);
		out.setFillAfter(true);
		out.setDuration(2000);
		
		textCode.startAnimation(out);
		textCode.setVisibility(View.VISIBLE);
		
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
		
		this.code = code;
		
		loader.setVisibility(View.GONE);
	}
	
	public void verifyCode(View view)
	{
		Log.i("tantest", "Verificando codigo");
		String code_given = phone_code.getText().toString();
		
		Log.i("tantest", "Codigo dado: "+code_given);
		Log.i("tantest", "Codigo correcto: "+code);
		
		//final Animation in = new AlphaAnimation(0.0f, 1.0f);
		//in.setDuration(1000);
		
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
		
		if(code.equals(code_given))
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
    
    // Alternative variant for API 5 and higher
    @Override
    public void onBackPressed() 
    {
      moveTaskToBack(true);
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
