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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
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
	private String abbr;
	private String phone;
	private String code;
	private EditText phone_input;
	
	@SuppressWarnings("deprecation")
	@TargetApi(16)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		handler = new MainActivityHandler(this);
		
		/* INIT CONTENT VIEW */
		setContentView(R.layout.activity_main);
		
		loader = (ProgressBar) findViewById(R.id.main_progressbar);
		loader.setIndeterminate(true);
		
		country = (Button) findViewById(R.id.main_country);
		
		phone_input = (EditText) findViewById(R.id.main_phone);
		
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
	    }
	    else
	    {
		   countriesContainer.setVisibility(View.VISIBLE);
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
		
		Thread handlePhone = new Thread() {
		    
			public void run() 
			{
				try 
				{
					
					PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
					PhoneNumber phoneData = phoneUtil.parse(phone, abbr);
		        
					phone = /*"+41661188615";*/phoneUtil.format(phoneData, PhoneNumberFormat.E164);
					Log.i("tantest", "Telefono final es: "+phone);
		        
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

		//Mandamos al servidor
		
		/*ClientSocket
		.getInstance()
		.init(phone)
		.send("createUser", phone, new MainClientSocketController(this,"createUser"));
		*/
		
	}
	
	public void test(View view)
	{
		loader.setVisibility(View.VISIBLE);
		
		Intent intent = new Intent(this, TestActivity.class);
		Log.i("tantes","iniciando actividad");
		startActivity(intent);
	}
	
	public void validateCode(String code)
	{
		Log.i("tantest","Main validateCode: "+code);
		country.setVisibility(View.GONE);
		
		Button connect = (Button) findViewById(R.id.main_connect);
		connect.setVisibility(View.GONE);
		Button verify = (Button) findViewById(R.id.main_code);
		verify.setVisibility(View.VISIBLE);
		
		TextView textCode = (TextView) findViewById(R.id.main_text_code);
		textCode.setVisibility(View.VISIBLE);
		
		phone_input.setText("");
		phone_input.setHint("Codigo verificacion");
		
		this.code = code;
		
		loader.setVisibility(View.GONE);
	}
	
	public void verifyCode(View view)
	{
		Log.i("tantest", "Verificando codigo");
		String code_given = phone_input.getText().toString();
		
		Log.i("tantest", "Codigo dado: "+code_given);
		Log.i("tantest", "Codigo correcto: "+code);
		
		if(code.equals(code_given))
		{
			Log.i("tantest", "Codigo correcto!");
		}
		else
		{
			Log.i("tantest", "Codigo INcorrecto!");
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
	
	public static String[] countries =
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
     };
}
