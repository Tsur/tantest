package com.scripturesos.tantest.connection;

import android.app.Activity;

public class MainClientSocketController implements SocketResponse
{

	private Activity context;
	private String response;
	
	public MainClientSocketController(Activity context)
	{
		this.context = context;
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
		
	}

}
