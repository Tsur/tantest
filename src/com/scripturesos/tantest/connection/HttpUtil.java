package com.scripturesos.tantest.connection;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;


import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
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
			/*case GET_CONTACTS:
				url += "getContacts/";
				break;*/
			case GET_UNREAD_MSG:
				url += "getUnreadMsgs/"+URLEncoder.encode(params[0],"UTF-8");
				break;
			case GET_UNCHEKED_MSG:
				url += "getUnconfirmedMsgs/"+URLEncoder.encode(params[0],"UTF-8");
				break;
			/*case UPLOAD_FILE:
				url += "uploadFile/";
				break;*/
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
    
    public static JSONObject post(int method, String[] data) throws JSONException, ClientProtocolException, IOException
	{
    	
    	// Create a new HTTP Client
    	String url="";
   	    HttpPost httppost = null;
   	    
   	    switch(method)
   	    {
   	    	case GET_CONTACTS:
   	    		
   			url = "getContacts";
   			httppost = new HttpPost(BASE_URL+url);
   			
   			List<NameValuePair> params = new ArrayList<NameValuePair>();
   			params.add(new BasicNameValuePair("contacts", data[0]));
   			
   			// Request parameters and other properties.
   	    	httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
   	    	
   			break;
   			
			
   			default:break;
   	    }
   	    

    	DefaultHttpClient httpclient = new DefaultHttpClient();
    	//Execute and get the response.
    	HttpResponse response = httpclient.execute(httppost);
    	
    	if(response.getStatusLine().getStatusCode() != 200)
	    {
	    	throw new ClientProtocolException();
	    }
    	
    	HttpEntity entity = response.getEntity();

    	if(entity != null) 
    	{
    	    InputStream is = entity.getContent();
    	    
    	    try 
    	    {
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
    	    finally 
    	    {
    	        is.close();
    	    }
    	}
    	
		return null;
		
	}

    public static String uploadFile(File file) throws JSONException, ClientProtocolException, IOException
	{
    	
    	// Create a new HTTP Client
   	    HttpPost httppost = new HttpPost(BASE_URL+"uploadFile");

		MultipartEntity mentity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		FileBody fileBody = new FileBody(file);
		mentity.addPart("file", fileBody);
		
		httppost.setEntity(mentity);


    	DefaultHttpClient httpclient = new DefaultHttpClient();
    	//Execute and get the response.
    	HttpResponse response = httpclient.execute(httppost);
    	
    	if(response.getStatusLine().getStatusCode() != 200)
	    {
	    	throw new ClientProtocolException();
	    }
    	
    	HttpEntity entity = response.getEntity();

    	if(entity != null) 
    	{
    	    InputStream is = entity.getContent();
    	    
    	    try 
    	    {
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
    			JSONObject json = new JSONObject(result);
    			return json.getString("response");
    	    } 
    	    finally 
    	    {
    	        is.close();
    	    }
    	}
    	
		return null;
		
	}

    /** Read the object from Base64 string. */
    public static Object fromString( String s ) throws IOException , ClassNotFoundException{
        byte [] data = Base64Coder.decode( s );
        ObjectInputStream ois = new ObjectInputStream( 
                                        new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    /** Write the object to a Base64 string. */
    public static String toString(Serializable o) throws IOException 
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject(o);
        oos.close();
        
        return new String(Base64Coder.encode( baos.toByteArray()));
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
