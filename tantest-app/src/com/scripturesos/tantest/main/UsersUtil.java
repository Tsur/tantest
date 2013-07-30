package com.scripturesos.tantest.main;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.graphics.drawable.Drawable;

public class UsersUtil 
{

    public static void downloadImg(String id, String imgUrl)
    {
    	try 
		{
			InputStream ins = (InputStream) new URL(imgUrl).getContent();
			UsersUtil.imagesCache.put(id, Drawable.createFromStream(ins, null));	
		}
    	catch(Exception e)
    	{
    		UsersUtil.imagesCache.put(id, HomeActivity.default_dr);
    	}
	      
    }
    
    static public Map<String, Drawable> imagesCache = new HashMap<String, Drawable>();
}
