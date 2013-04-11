package com.scripturesos.tantest.connection;


public interface MessageCallback {
	//public void on(String event, JSONObject... data);
	public void onMessage(IOMessage msg);
	public void onConnect();
	public void onDisconnect();
	public void onConnectFailure();
	public void onMessageFailure(IOMessage msg);
}
