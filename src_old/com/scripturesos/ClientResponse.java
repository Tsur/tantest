package com.scripturesos.tantest.connection;

import android.os.Handler;


public class ClientResponse
{
	
	public Handler handler;
	public int what;
	
	public ClientResponse(Handler h, int w)
	{
		handler = h;
		what = w;
	}

}
