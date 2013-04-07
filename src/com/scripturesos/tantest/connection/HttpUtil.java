package com.scripturesos.tantest.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public final class HttpUtil {

   
    
    public static String getURL(int method, String[] params) throws UnsupportedEncodingException
    {
    	String url = BASE_URL;

		switch(method)
		{
			case CREATE_USER:
				url += "createUser/"+URLEncoder.encode(params[0],"UTF-8");
				break;
			case EMAIL_CODE:
				url += "createEmailCode/"+URLEncoder.encode(params[0],"UTF-8");
				break;
			case CONFIRM_CODE:
				url += "confirmEmailCode/"+URLEncoder.encode(params[0],"UTF-8")+"/code/"+URLEncoder.encode(params[1],"UTF-8");
				break;
			case REGISTER_EMAIL:
				url += "registerEmail/"+URLEncoder.encode(params[0],"UTF-8")+"/email/"+URLEncoder.encode(params[1],"UTF-8");
				break;
			case GET_CONTACTS:
				url += "getContacts/";
				break;
			case GET_UNREAD_MSG:
				url += "getUnreadMsgs/"+URLEncoder.encode(params[0],"UTF-8");
				break;
			case GET_UNCHEKED_MSG:
				url += "getUnconfirmedMsgs/"+URLEncoder.encode(params[0],"UTF-8");
				break;
			case UPLOAD_FILE:
				url += "uploadFile/";
				break;
			case CREATE_FRIEND:
				url += "createFriend/"+URLEncoder.encode(params[0],"UTF-8");
				break;
			default:break;
		}
		Log.i("tantest","URL: " + url);
		return url;
    }
    
    
    public static JSONObject get(String url) throws JSONException, ClientProtocolException, IOException
	{
    	
    	// Create a new HTTP Client
	    DefaultHttpClient httpClient = new DefaultHttpClient();
	    
	    // Setup the get request
	    HttpGet httpGetRequest = new HttpGet(url);

	    // Execute the request in the client
	    HttpResponse httpResponse = httpClient.execute(httpGetRequest);
	    
	    HttpEntity entity = httpResponse.getEntity();

	    /*if(httpResponse.getStatusLine().getStatusCode() >= 200 || 
	    		httpResponse.getStatusLine().getStatusCode() <= 202)
	    {
	    	return;
	    }*/
	    
	    if(httpResponse.getStatusLine().getStatusCode() != 200)
	    {
	    	throw new ClientProtocolException();
	    }
	    
	    InputStream is = entity.getContent();
	    
	  //Vamos a leer el fichero linea por linea
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"),8);

		//Obtenemos todo el fichero JSON en un string
		StringBuilder string_builder = new StringBuilder();
		String line = null;
		String result;
		
		while ((line = reader.readLine()) != null)
		{
			string_builder.append(line);
			//Log.i("tantest","JSON linea: " + line);
		}
		
		result = string_builder.toString();
		Log.i("tantest","HTTP content: " + result);

		//Creamos objeto json a partir del string
		return new JSONObject(result);
		
	}
    
    private static final String BASE_URL 		= "http://www.scripturesos.com:3001/";
    public static final int CREATE_USER 		= 0;
    public static final int EMAIL_CODE 			= 1;
    public static final int CONFIRM_CODE 		= 2;
    public static final int REGISTER_EMAIL 		= 3;
    public static final int GET_CONTACTS 		= 4;
    public static final int GET_UNREAD_MSG 		= 5;
    public static final int GET_UNCHEKED_MSG 	= 6;
    public static final int CREATE_FRIEND 		= 7;
    public static final int UPLOAD_FILE 		= 8;
}
