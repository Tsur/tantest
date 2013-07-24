package com.scripturesos.tantest.connection;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

/*
import com.scripturesos.tantest.main.ContactItemListView;
import com.scripturesos.tantest.main.ContactListAdapter;
import com.scripturesos.tantest.main.HomeActivity;
import android.graphics.drawable.Drawable;
*/

import android.util.Log;

public final class HttpUtil {

    
    public static String getURL(int method, String[] params) throws UnsupportedEncodingException
    {
    	String url = BASE_URL;

		switch(method)
		{
			case ACCESS:
				url += "access?email="+URLEncoder.encode(params[0],"UTF-8")+"&password="+URLEncoder.encode(params[1],"UTF-8");
				break;
			case VALIDATE_CODE:
				url += "validate?email="+URLEncoder.encode(params[0],"UTF-8")+"&code="+URLEncoder.encode(params[1],"UTF-8");
				break;
			case FORGOT:
				url += "forgot?email="+URLEncoder.encode(params[0],"UTF-8");
				break;
			case RESTORE:
				url += "restore?email="+URLEncoder.encode(params[0],"UTF-8")+"&code="+URLEncoder.encode(params[1],"UTF-8")+"&password="+URLEncoder.encode(params[2],"UTF-8");
				break;
			case RANDOM:
				url += "random?";
				for(int k=0;k<params.length;k+=2)
				{
					url += params[k]+"="+URLEncoder.encode(params[k+1],"UTF-8")+"&";
				}
				break;
			case ID:
				url += "id?"+params[0]+"="+URLEncoder.encode(params[1],"UTF-8");
				break;
			/*case GET_CONTACTS:
				url += "getContacts/";
				break;*/
			/*case GET_UNREAD_MSG:
				url += "getUnreadMsgs/"+URLEncoder.encode(params[0],"UTF-8");
				break;
			case GET_UNCHEKED_MSG:
				url += "getUnconfirmedMsgs/"+URLEncoder.encode(params[0],"UTF-8");
				break;*/
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
   	    List<NameValuePair> params;
   	    
   	    switch(method)
   	    {
   	    	case INFO:
   	    		
	   			url = "info";
	   			httppost = new HttpPost(BASE_URL+url);
	   			
	   			params = new ArrayList<NameValuePair>();
	   			params.add(new BasicNameValuePair("key", User.key));
	   			params.add(new BasicNameValuePair("email", User.get("email")));
	   			params.add(new BasicNameValuePair("id", User.get("_id")));
	   			params.add(new BasicNameValuePair("token", User.get("token")));
	   			
	   			for(int k=0;k<data.length;k+=2)
				{
	   				params.add(new BasicNameValuePair(data[k], data[k+1]));
				}
	   			
	   			// Request parameters and other properties.
	   	    	httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
   	    	
   			break;
   			
   	    	case REGISTER_TEST:
   	    		
   	   			url = "registerTest";
   	   			httppost = new HttpPost(BASE_URL+url);
   	   			
   	   			params = new ArrayList<NameValuePair>();
   	   			params.add(new BasicNameValuePair("html", data[0]));
   	   			
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
   	    /*HttpPost httppost = new HttpPost(BASE_URL+"upload");

		MultipartEntity mentity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		FileBody fileBody = new FileBody(file);
		mentity.addPart("multimedia", fileBody);
		
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
    	
		return null;*/

        HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024; 
    
        try { 
              
               // open a URL connection to the Servlet
             FileInputStream fileInputStream = new FileInputStream(file);
             URL url = new URL(BASE_URL+"upload");
              
             // Open a HTTP  connection to  the URL
             conn = (HttpURLConnection) url.openConnection(); 
             conn.setDoInput(true); // Allow Inputs
             conn.setDoOutput(true); // Allow Outputs
             conn.setUseCaches(false); // Don't use a Cached Copy
             conn.setRequestMethod("POST");
             conn.setRequestProperty("Connection", "Keep-Alive");
             conn.setRequestProperty("ENCTYPE", "multipart/form-data");
             conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
             conn.setRequestProperty("key", User.key); 
             conn.setRequestProperty("id", User.get("_id")); 
             
             dos = new DataOutputStream(conn.getOutputStream());
    
             dos.writeBytes(twoHyphens + boundary + lineEnd); 
             dos.writeBytes("Content-Disposition: form-data;"+ lineEnd);
              
             dos.writeBytes(lineEnd);
    
             // create a buffer of  maximum size
             bytesAvailable = fileInputStream.available(); 
    
             bufferSize = Math.min(bytesAvailable, maxBufferSize);
             buffer = new byte[bufferSize];
    
             // read file and write it into form...
             bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
                
             while (bytesRead > 0) {
                  
               dos.write(buffer, 0, bufferSize);
               bytesAvailable = fileInputStream.available();
               bufferSize = Math.min(bytesAvailable, maxBufferSize);
               bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
                
              }
    
             // send multipart form data necesssary after file data...
             dos.writeBytes(lineEnd);
             dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
    
             // Responses from the server (code and message)
             int serverResponseCode = conn.getResponseCode();
             String serverResponseMessage = conn.getResponseMessage();
               
             Log.i("uploadFile", "HTTP Response is : "
                     + serverResponseMessage + ": " + serverResponseCode);

             //close the streams //
             fileInputStream.close();
             dos.flush();
             dos.close();
             
             JSONObject response;
             
             if(serverResponseCode == 200){
                  
                  response = new JSONObject(serverResponseMessage); 
                  
                  return response.getString("url");
             } 
             
             return "";
               
        } 
        catch (MalformedURLException ex) 
        {
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            return "";
        } 
        catch (Exception e)
        {
            Log.e("Upload file to server Exception", "Exception : "
                                             + e.getMessage(), e);  
            return "";
        }
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
	
	public static boolean isValidEmail(String email) 
	{
		Matcher m = emailPattern.matcher(email); 
		return m.matches();
	}

    static Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	
    public static AtomicInteger uniqid 			= new AtomicInteger();
    private static final String BASE_URL 		= "http://www.scripturesos.com:3001/";
    public static final int ACCESS		 		= 0;
    public static final int VALIDATE_CODE 		= 1;
    public static final int FORGOT		 		= 2;
    public static final int RESTORE		 		= 3;
    public static final int RANDOM		 		= 4;
    public static final int ID			 		= 5;
    public static final int GET_UNCHEKED_MSG 	= 6;
    public static final int INFO		 		= 7;
   // public static final int UPLOAD_FILE 		= 8;
    public static final int REGISTER_TEST 		= 8;
    public static final int GET_UNREAD_MSG 		= 9;
    public static final int GET_CONTACTS 		= 10;
}
