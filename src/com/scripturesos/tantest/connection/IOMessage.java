package com.scripturesos.tantest.connection;

import org.json.JSONException;
import org.json.JSONObject;

public class IOMessage {
	
	//Special cases
	public static final int ERROR = 0;
	public static final int DISCONNECT = 1;
	public static final int HEARTBEAT = 2;
	public static final int EVENT = 3;
	
	//Message cases
	public static final int CHAT_MESSAGE = 10;
	public static final int CHAT_CONFIRMATION = 11;
	public static final int CHAT_ROOT = 12;
	
	
	private int type;
	private JSONObject messageData;
	private String packet;
	
	public IOMessage(){}
	
	public static IOMessage parseMsg(String message)
	{
		IOMessage msg = new IOMessage();
		
		try 
		{
			JSONObject jresponse = new JSONObject(message);
			
			
			msg.setType(jresponse.getInt("type"));
			msg.setMessageData(jresponse.getJSONObject("content"));
		} 
		catch (JSONException e) 
		{
			
		}
		
		return msg;
	}

	public void setType(int type) 
	{
		this.type = type;
	}


	public int getType() 
	{
		return type;
	}


	public void setMessageData(JSONObject messageData) 
	{
		this.messageData = messageData;
	}

	public void setPacket(String[] arg)
	{
		
		switch(this.getType())
		{
			case CHAT_MESSAGE:
				packet ="{\"method\":\"sendMsg\",\"arguments\":[";
				break;
			case CHAT_CONFIRMATION:
				packet ="{\"method\":\"confirmMsg\",\"arguments\":[";
				break;
		}
		
		if(packet == null)
		{
			return;
		}
		
		if(arg.length > 0)
		{
			for(int i=0; i < arg.length; i++)
			{
				packet += "\""+arg[i]+"\",";
			}
			
			packet = packet.substring(0,packet.length()-1);
		}
		
		packet +="]}";
	}
	
	public String getPacket()
	{
		if(packet != null)
		{
			return packet;
		}
		
		return null;
	}
	
	public boolean isSet()
	{
		return (packet != null);
	}
	
	public JSONObject getMessageData() 
	{
		return messageData;
	}
	

}
