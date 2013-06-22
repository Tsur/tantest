package com.scripturesos.tantest.connection;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class IOMessage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Special cases
	/*public static final int ERROR = 0;
	public static final int DISCONNECT = 1;
	public static final int HEARTBEAT = 2;
	public static final int EVENT = 3;*/
	
	//Message cases
	/*public static final int CHAT_MESSAGE = 10;
	public static final int CHAT_CONFIRMATION = 11;
	public static final int CHAT_ROOT = 12;*/
	
	
	private int type;
	private transient JSONObject messageData;
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
			case MessageCallback.CHAT_MESSAGE:
				packet ="{\"method\":\"sendMsg\",\"arguments\":[";
				break;
			case MessageCallback.CHAT_CONFIRMATION:
				packet ="{\"method\":\"confirmMsg\",\"arguments\":[";
				break;
			case MessageCallback.CHAT_HAS_GONE:
				packet ="{\"method\":\"hasGone\",\"arguments\":[";
				break;
			case MessageCallback.CHAT_ONLINE:
				packet ="{\"method\":\"online\",\"arguments\":[";
				break;
			case MessageCallback.CHAT_HAS_CHANGED:
				packet ="{\"method\":\"hasChanged\",\"arguments\":[";
				break;
			case MessageCallback.CHAT_IS_WRITING:
				packet ="{\"method\":\"writing\",\"arguments\":[";
				break;
			case MessageCallback.CHAT_IN_TEST:
				packet ="{\"method\":\"inTest\",\"arguments\":[";
				break;
			case MessageCallback.CHAT_CREATE:
				packet ="{\"method\":\"initChat\",\"arguments\":[";
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
