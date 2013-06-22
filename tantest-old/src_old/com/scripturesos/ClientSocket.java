package com.scripturesos.tantest.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;
import android.util.Log;

public class ClientSocket extends Thread
{
	
	private Socket clientSocket;
	public String clientID;
	public String countryCode;
	//private List<String> commands;
	//private String command;
	private PrintWriter out;
    private BufferedReader in;
    //private SocketResponse responseController;
	private Map<String,ClientResponse> responseHandlers = new HashMap<String,ClientResponse>();
	private Map<String,Boolean> errorHandlers = new HashMap<String,Boolean>();
	
	boolean connected = false;
	
	//Bill Pugh Singleton thread safe solution
	private ClientSocket(){}
	
	private static class InstanceHolder 
	{
	    private static final ClientSocket instance = new ClientSocket();
	}

	public static ClientSocket getInstance() 
	{
	    return InstanceHolder.instance;
	}
	
	public void close()
	{
		if(clientSocket != null && clientSocket.isConnected())
		{
			try 
			{
				clientSocket.close();
			} 
			catch (IOException e) 
			{
				Log.i("tantest","Error cerrando conexion");
			}
		}
	}
	
	private synchronized void init() 
    {

		if(clientSocket != null && clientSocket.isConnected())
		{
			return;
		}
        
    	try 
        {
    		//Log.i("tantest", "Conectando");
            //commands = new ArrayList<String>();
            //responseHandlers = new HashMap<String,ClientResponse>();
            Log.i("tantest", "Conectando a: "+IP+":"+PORT);
            clientSocket = new Socket(IP, PORT); 
            //Log.i("tantest", "Conectado");
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			Log.i("tantest", "Buffers arrancados");
			
			out.write("{\"client\":\""+getClient()+"\",\"method\":\"identify\",\"id\":\"void\",\"arguments\":[]}");
			
			out.flush();

		} 
        catch (Exception e) 
        {
        	/*Log.i("tantest", "Error conexion");
        	
        	ClientResponse response = responseHandlers.get("onConnectionError");
			
			//Compose the message including the JSON object
			Message msg = new Message();
			msg.what = response.what;
			
			response.handler.sendMessage(msg);*/
        }
    	
    }
    
	public synchronized Socket getSocket()
    {
    	return clientSocket;
    }
	
	public synchronized Boolean getErrorHandlers(String id)
	{
		return errorHandlers.get(id);
	}
	
	public synchronized void setErrorHandlers(String id, boolean b)
	{
		errorHandlers.put(id,b);
	}
	
	public String getClient()
    {
    	return clientID;
    }
    
    public Map<String,ClientResponse> getHandlers()
    {
    	return responseHandlers;
    }
    
    public String getCountry()
    {
    	return countryCode;
    }
    
    /*public void send(String method, String argument, SocketResponse controller)
    {
    	assert clientSocket != null;
    	
    	String command = "{\"client\":\""+getClient()+"\",\"method\":\""+method+"\",\"arguments\":[\""+argument+"\"]}";

    	commands.add(command);
    	
		responseController = controller;
    }*/
    
    public void sendMsg(String to, String msg)
    {

    	if(clientSocket !=null && clientSocket.isConnected())
    	{
	    	String message_id = UUID.randomUUID().toString();
	
			out.write("{\"client\":\""+getClient()+
	    			"\",\"method\":\"sendMsg\",\"id\":\""+message_id+
	    			"\",\"arguments\":[\""+to+"\",\""+msg+"\"]}");
			
			out.flush();
			
			Log.i("tantest", "Mensaje enviado to: "+to+", msg: "+msg);
    	}
    	else
    	{
    		//Guardamos en BD para posterior envio
    	}
    	
    }
    
    public void sendConfirmation(String message_id, String to)
    {

    	if(clientSocket !=null && clientSocket.isConnected())
    	{
	
			out.write("{\"client\":\""+getClient()+
	    			"\",\"method\":\"confirmMsg\",\"id\":\"void\",\"arguments\":[\""+message_id+"\",\""+to+"\"]}");
			
			out.flush();
			
			Log.i("tantest", "Mensaje enviado");
    	}
    	else
    	{
    		//Guardamos en BD para posterior envio
    	}
			
		/*(new Thread() {
		    
			public void run() 
			{
				try 
				{
					Thread.sleep(time);
					
					if(getErrorHandlers(id) == false)
					{
						ClientResponse response = responseHandlers.get("onTimeoutError");
						
						//Compose the message including the JSON object
						Message msg = new Message();
						msg.what = response.what;
						
						response.handler.sendMessage(msg);
					}
				} 
				catch (InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
			
		}).start();*/

    }
 
    private void processResponse(String response)
    {

		//responseController.setResponse(response);
		
		//new Thread(responseController).start();

    	final String res = response;
    	Log.i("tantest", "Respuesta del servidor: "+res);
    	
    	(new Thread() {
		    
			public void run() 
			{
				try 
				{
					//Log.i("tantest", "respuesta es: "+res);
					
					JSONObject jres = new JSONObject(res);
					String id = jres.getString("id");
					
					//Error timeout
					/*if(getErrorHandlers(id) != null)
					{
						setErrorHandlers(id, true);
					}*/
					
					//Error transmicion datos, db, ...etc
					/*if(jres.getBoolean("error"))
					{
						ClientResponse response = responseHandlers.get("onServerError");
						
						//Compose the message including the JSON object
						Message msg = new Message();
						msg.what = response.what;
						
						response.handler.sendMessage(msg);
					
					}
					else
					{*/
						//Get the handler information
						ClientResponse response = responseHandlers.get(id);
					
						//Compose the message including the JSON object
						Message msg = new Message();
						msg.obj = jres;
						msg.what = response.what;
						
						response.handler.sendMessage(msg);
					
						//responseHandlers.remove(id);
							
					//}
					
					
					//Send Message to handler
					//Log.i("tantest", "Enviamos mensaje al manejador");
					
				} 
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
			
		}).start();
		
    }
    
    @Override
    public void run()
    {
    	
    	try 
    	{

    		//Log.i("tantest", "ESperando respuesta");
    		
	 	    while(true)
	 	    {
	 	    	
	 	    	if(clientSocket != null && clientSocket.isConnected())
	 	    	{
	 	    		if(in.ready())
		 	    	{
	 				   //System.out.println("obtengo respuesta");
		 	    		//Log.i("tantest", "Tenemos respuesta!");
	 				   processResponse(in.readLine());
		 	    	}
	 	    		
	 	    		sleep(INTERVAL);
	 	    	}
	 	    	else
	 	    	{
	 	    		init();
	 	    		
	 	    		if(clientSocket != null && clientSocket.isConnected())
		 	    	{
	 	    			sleep(INTERVAL);
		 	    	}
	 	    		else
	 	    		{
	 	    			sleep(RECONNECTION_INTERVAL);
	 	    		}
	 	    		
	 	    	}
	 	    	/*if(commands.size() > 0)
	 	    	{
 				  for(String command: commands)
 				  {
 					  out.write(command);
 					  out.flush();
 				  }
 				   
 				  commands.clear();
	 	    	}*/
 			   
	 	    	
	 	    }
		} 
    	catch (InterruptedException e) 
		{		
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	catch (UnknownHostException e1) 
    	{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
    	catch (IOException e1) 
    	{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
 	   
    }
	
	private final String IP = "54.246.107.200";
	//private final String IP = "192.168.1.131";
    private final int PORT = 3000;
    private final int INTERVAL = 1000;
    private final int RECONNECTION_INTERVAL = 60*1000;//1 minuto
    //private final int CON_INTERVAL = 100;
}

/*
BroadcastReceiver wifiStatusReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        logger.d("checking wifi state...");
        SupplicantState supState;
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        supState = wifiInfo.getSupplicantState();
        logger.d("supplicant state: " + supState);

        SherlockDialogFragment dialog = (SherlockDialogFragment) fragmentManager
                .findFragmentByTag(WifiAlertDialogFragment.DIALOG_WIFI);

        if (supState.equals(SupplicantState.COMPLETED)) {
            logger.d("wifi enabled and connected");
            if (dialog != null)
                dialog.dismiss();
        } else {
            WifiAlertDialogFragment.wifiCheck(HomeActivity.this);
            if (supState.equals(SupplicantState.SCANNING)) {
                logger.d("wifi scanning");
            } else if (supState.equals(SupplicantState.DISCONNECTED)) {
                logger.d("wifi disonnected");
            } else {
                Toast.makeText(HomeActivity.this, "Wifi Enabling",
                        Toast.LENGTH_LONG).show();
                logger.d("wifi connecting");
            }
        }
    }
};

//Registering my receiver (in onResume or in OnCreate):

IntentFilter filter = new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
this.registerReceiver(wifiStatusReceiver, filter);
*/
