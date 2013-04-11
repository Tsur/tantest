package com.scripturesos.tantest.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;

import android.util.Log;

public class IOSocket {
	
	private Socket socket;
	private final String client_id;
	
	private PrintWriter out;
	private BufferedReader in;
	
	private int heartTimeout = 5000;//5 s
	private int connectTimeout = 10000;// 10s
	private int reconnectTime = 150 * 1000; // 2m y medio
	private int intervalResponse = 800;//close to 1s
	private Timer timer;
	
	private boolean reconnect;
	private final MessageCallback callback;

	private boolean connecting;
	private boolean connected;
 
	public IOSocket(String client_id, MessageCallback callback)
	{
	    this(client_id, callback, false);
	}
	
	public IOSocket(String client_id, MessageCallback callback, boolean reconnect)
	{
	    this.client_id = client_id;
		this.callback = callback;
		this.reconnect = reconnect;
	}
	
	/*private static class InstanceHolder 
	{
	    private static final IOSocket instance = new IOSocket();
	}

	public static IOSocket create(String client_id, MessageCallback callback) 
	{
	    return InstanceHolder.instance;
	}*/
	
	public void connect() 
	{
		synchronized(this) 
		{
			connecting = true;
		}
		
		//Se ejecutará dentro de 10 segundos para ver si se ha conectado
		clearTimer();
		setTimer(new ConnectTimeout(), connectTimeout);
		
        (new Thread(new Handshake())).start();
        
        //Log.i("tantest","handshake");
	}
	
	public void emit(String event, JSONObject message/*, AckCallback callback*/) throws IOException {
		/*try {
			JSONObject data = new JSONObject();
			data.put("name", event);
			data.put("args", message);
			IOMessage packet = new IOMessage(IOMessage.EVENT, addAcknowledge(callback, message), "", data.toString());
			packet.setAck(true);
			webSocket.sendMessage(packet);
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
	}
	
	public void send(int type, String... arg)
	{
		IOMessage message = new IOMessage();

		message.setType(type);
		
		message.setPacket(arg);
		
		synchronized(this)
		{
			if(connected)
			{
				if(message.isSet())
				{
					Log.i("tantest"," mensaje: "+message.getPacket());
					out.write(message.getPacket());
					out.flush();
				}
				else
				{
					callback.onMessageFailure(message); 
				}
				
			}
			else
			{
				callback.onMessageFailure(message); 
			}
		}
			
	}
	
	
	public synchronized void close() 
	{
		
		if(connected) 
		{
			try 
			{
				socket.close();
				out.close();
				in.close();
			} 
			catch (IOException e) 
			{

			}

		}	
	}

	synchronized void onConnect()
	{

		if (!connected) 
		{
			connected = true;
			connecting = false;
			
			(new Thread(new Listener())).start();
			
			callback.onConnect();
		}
		
	}
	
	synchronized void onDisconnect() 
	{
		
		connected = false;
		connecting = false;
		
		try 
		{
			if(socket !=null)
			{
				socket.close();
				socket = null;
			}
		}
		catch (IOException e)
		{
			
		}

		if(reconnect)
        {
        	clearTimer();
        	setTimer(new ReConnect(), reconnectTime);
        }
		
		callback.onDisconnect();
	}
    
    private synchronized void onConnectFailure() 
    {
        connecting = false;
        connected = false;
        
        if(reconnect)
        {
        	clearTimer();
        	setTimer(new ReConnect(), reconnectTime);
        }
        
        callback.onConnectFailure();
    }
	
	public synchronized boolean isConnected() 
	{
		return connected;
	}
	
	public synchronized boolean isConnecting() 
	{
		return connecting;
	}


    private class Handshake implements Runnable 
    {
        @Override
        public void run() 
        {
            try 
            {
            	socket = new Socket(IP,PORT);
            	//Start handshake process
            	out = new PrintWriter(socket.getOutputStream(), true);
            	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            	
            	out.write("{\"method\":\"handshake\",\"arguments\":\""+client_id+"\"}");
                out.flush();
                
    			while(connecting == true)
    			{

    				if(in.ready()) 
                    {
    					String response = in.readLine();
    					
    					if(response.equals("handshake:ok"))
    					{
    						onConnect();
    					}
    					else
    					{
    						onConnectFailure();
    					}   
                        
                    } 
                    
                    Thread.sleep(intervalResponse);
                    
    			}
        		
            } 
            catch (Exception e) 
            {
            	onConnectFailure();
            }
            
        }
    }
	
	private class ConnectTimeout extends TimerTask 
	{
		@Override
		public void run() 
		{
			synchronized(IOSocket.this) 
			{			
				//Estamos conectados, genial!
				if(connected || !connecting) 
				{
					return;
				}
				
				//Estamos aun en estado "Conectando"
				connecting = false;
				
				//Cerramos el socket si se hubiera abierto
				if(socket != null) 
				{
					try 
					{
						socket.close();
						socket = null;
					} 
					catch (IOException e) 
					{
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}
				
			}
			
			//Lanzamos callback
			onConnectFailure();
		}
	}
	
	private class ReConnect extends TimerTask 
	{
		@Override
		public void run() 
		{
			synchronized(IOSocket.this) 
			{
				//Estamos conectados, genial!
				if(connected || connecting) 
				{
					return;
				}
				
				if(socket != null) 
				{
					try 
					{
						socket.close();
						socket = null;
					} 
					catch (IOException e) 
					{
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}	
			}
			
			connect();
		}
	}
	
    private class Listener implements Runnable 
    {
        @Override
        public void run() 
        {
            
        	clearTimer();
			out.write("{\"method\":\"heartbeat\"}");
			out.flush();
			setTimer(new CloseTask(), heartTimeout);
        	
			while(connected == true)
			{
        		try 
        		{
        			if(in.ready())
        			{
        				IOMessage message = IOMessage.parseMsg(in.readLine());
        			
        				switch (message.getType()) 
            			{			
            				case IOMessage.HEARTBEAT:
            					clearTimer();
            					out.write("{\"method\":\"heartbeat\"}");
            					out.flush();
            					setTimer(new CloseTask(), heartTimeout);
            					break;
            			
            				case IOMessage.CHAT_MESSAGE:
            				case IOMessage.CHAT_CONFIRMATION:
            					callback.onMessage(message);
            					break;
            			
            				case IOMessage.EVENT:
            			
            					break;

            				case IOMessage.ERROR:
            				case IOMessage.DISCONNECT:
            					//TODO
            					break;
            			}
        			}
        			
        			Thread.sleep(intervalResponse);
        		}
        		catch (Exception e) 
                {
                       
                }
        	}   
        }
    }
	
	private void setTimer(TimerTask tm, int time) 
	{		
		timer = new Timer();
		timer.schedule(tm, time);
	}
	
	private void clearTimer() 
	{
		if (timer != null) 
		{
			timer.cancel();
			timer = null;
		}
	}
	
	private class CloseTask extends TimerTask 
	{
		@Override
		public void run() 
		{
			synchronized(IOSocket.this) 
			{
				try 
				{
					if(socket != null)
					{
						socket.close();
						socket = null;
					}
						
				} 
				catch (IOException e)
				{
				
				}
			
				onDisconnect();
			}
				
		}
	}
	
	private final String IP = "54.246.107.200";
	//private final String IP = "192.168.1.132";
    private final int PORT = 3000;
}
