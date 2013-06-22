package com.scripturesos.tantest.connection;

public interface SocketResponse extends Runnable
{

	void setResponse(String response);
	String getResponse();
}
