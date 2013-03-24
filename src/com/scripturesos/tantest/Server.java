package com.scripturesos.tantest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;

public final class Server extends Socket 
{

    private static Socket clientSocket;
    private static String command = "{}";
    private static PrintWriter out = null;
    private static BufferedReader in = null;
    
    static 
    {
        try 
        {
            clientSocket = new Server(Server.IP, Server.PORT);
            
            out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    private Server(final String address, final int port) throws IOException 
    {
        super(address, port);
    }

    /*public static final Socket getInstance() 
    {
        return clientSocket;
    }*/
 
    public static final void setCommand(String method, Collection<String> arguments)
    {
    	command = "{\"method\":\""+method+"\",\"arguments\":";
    	
    	String args = "[";
    	
    	Iterator<String> it = arguments.iterator();
    	
    	while(it.hasNext())
    	{
    		args += "\""+it.next()+"\",";
    	}
    	
    	if(arguments.size() > 0)
    	{
    		args = args.substring(0,args.length()-1);
    	}

    	args += "]}";
    	
    	//Log.i("tantes",command);
    	
    }
    
    public static final void setCommand(String method, String argument)
    {
    	command = "{\"method\":\""+method+"\",\"arguments\":[\""+argument+"\"]}";
    }
    
    public static final String send()
    {
    	
        
        try 
        {
			//out = new PrintWriter(clientSocket.getOutputStream(), true);
			//in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
			out.println(command);
			
			//String response = in.readLine();
			
			//out.close();
			//in.close();
			
			//return response;
			return in.readLine();
        }
        catch (IOException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return "";

    }
    
    public static final void stop()
    {
		
		try 
		{
			out.close();
			in.close();
			clientSocket.close();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    private static final String IP = "scripturesos.com";
    private static final int PORT = 3000;

}
