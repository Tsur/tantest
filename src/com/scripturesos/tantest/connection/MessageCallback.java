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
	public static final int CHAT_HAS_GONE = 13;
	public static final int CHAT_HAS_CHANGED = 14;
	public static final int CHAT_IS_WRITING = 15;
	public static final int CHAT_IN_TEST = 16;
	public static final int CHAT_ONLINE = 17;
	public static final int CHAT_CREATE = 18;
}
