package com.scripturesos.tantest.connection;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;
import android.util.Log;

import com.scripturesos.tantest.MainActivity;
import com.scripturesos.tantest.MainActivity.MainActivityHandler;

public class MainClientSocketController implements SocketResponse
{

	private MainActivityHandler handler;
	private String response;
	private String method;
	
	public MainClientSocketController(MainActivityHandler handler, String method)
	{
		this.handler = handler;
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
				
				Message msg = new Message();
				msg.obj = code;
				msg.what = 0;
				
				handler.sendMessage(msg);
				
			} 
			catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
