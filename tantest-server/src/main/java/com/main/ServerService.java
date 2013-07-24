package com.main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;

public class ServerService 
{

	Map<String,UUID> emailsMap = new HashMap<String, UUID>();
	Map<UUID,SocketIOClient> usersMap = new HashMap<UUID, SocketIOClient>();
	
	@OnConnect
    public void onConnectHandler(SocketIOClient client) 
	{
		usersMap.put(client.getSessionId(), client);
    }
	
	 @OnDisconnect
     public void onDisconnectHandler(SocketIOClient client) 
	 {
		 usersMap.remove(client.getSessionId());
     }
	 
	// SocketIOClient, AckRequest and Data could be ommited
    @OnEvent("RegisterEvent")
    public void onRegisterEvent(SocketIOClient client, JSONObject data, AckRequest ackRequest) 
    {
    	try 
    	{
			emailsMap.put(data.getString("email"), client.getSessionId());
		} 
    	catch (JSONException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
 // SocketIOClient, AckRequest and Data could be ommited
    @OnEvent("MessageEvent")
    public void onMessageEvent(SocketIOClient client, JSONObject data, AckRequest ackRequest) 
    {
    	try 
    	{
    		SocketIOClient receiver = usersMap.get(emailsMap.get(data.get("to")));
    		
    		if(receiver == null)
    		{
    			
    		}
    		else
    		{
    			receiver.sendEvent("MessageEvent", data);
    		}
    		
		} 
    	catch (JSONException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
