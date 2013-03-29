package com.scripturesos.tantest.connection;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.scripturesos.tantest.MainActivity;

public class MainClientSocketController implements SocketResponse
{

	private MainActivity activity;
	private String response;
	private String method;
	
	public MainClientSocketController(MainActivity context, String method)
	{
		this.activity = context;
		this.method = method;
	}



	@Override
	public void setResponse(String response) {
		// TODO Auto-generated method stub
		this.response = response;
	}


	@Override
	public String getResponse() {
		// TODO Auto-generated method stub
		return response;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(method.equals("createUser"))
		{
			
			try 
			{
				Log.i("tantest", response);
				JSONObject jresponse = new JSONObject(response);
				
				String code = jresponse.getString("validationCode");
				Log.i("tantest", "El codigo de validacion es: "+code);
				
				activity.validateCode(code);
				
			} 
			catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
