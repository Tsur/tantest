package com.scripturesos.tantest.connection;


public interface MessageCallback {
	//public void on(String event, JSONObject... data);
	public void onMessage(IOMessage msg);
	public void onConnect();
	public void onDisconnect();
	public void onConnectFailure();
	public void onMessageFailure(IOMessage msg);
	
	public static final int ERROR = 0;
	public static final int DISCONNECT = 1;
	public static final int HEARTBEAT = 2;
	public static final int EVENT = 3;
	
	//Message cases
	public static final int CHAT_MESSAGE = 10;
	public static final int CHAT_CONFIRMATION = 11;
	public static final int CHAT_ROOT = 12;
}
