package com.scripturesos.tantest.connection;

import org.json.JSONException;
import org.json.JSONObject;

//import android.util.Log;

public class User 
{

    public static String get(String key)
    {
        try
        {
        	return user.getString(key);
        }
        catch(Exception e)
        {
        	return null;
        }
    }
    
    public static void set(String key, String value) throws JSONException
    {
        user.put(key, value);
    }
	
	public static void init(JSONObject userinfo) 
	{
		user = userinfo;
	}

    
    public static final String key = "";
    private static JSONObject user;

}
