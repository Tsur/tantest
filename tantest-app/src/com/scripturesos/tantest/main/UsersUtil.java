package com.scripturesos.tantest.main;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;

import com.scripturesos.tantest.connection.HttpUtil;

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
    
    public static void saveUsers(String[] users)
    {
    	try 
    	{
			JSONObject response = HttpUtil.post(HttpUtil.GET_USERS, users);
			
			if(response.getInt("error") == 0)
			{
				JSONArray ausers = response.getJSONArray("users");
				
				for(int i=0; i<ausers.length();i++)
				{
					saveUser(ausers.getJSONObject(i));
				}
			}
		}
    	catch(Exception e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void initUser(JSONObject user)
    {
    	try 
    	{
			//UKEY = user.getString("key");
			UID = user.getString("_id");
			UTOKEN = user.getString("token");
			UEMAIL = user.getString("email");
			saveUser(user);
		} 
    	catch (Exception e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    public static void saveUser(JSONObject user)
    {
    	try 
		{
			UserItemListView item = new  UserItemListView(user.getString("email"));
			
			if(user.has("tid"))
			{
				item.setTid(user.getString("tid"));
			}
			
			if(user.has("alias"))
			{
				item.setAlias(user.getString("alias"));
			}
			
			if(user.has("status"))
			{
				item.setStatus(user.getString("status"));
			}
			
			if(user.has("desc"))
			{
				item.setDesc(user.getString("desc"));
			}
			
			if(user.has("whereabout"))
			{
				item.setWhere(user.getString("whereabout"));
			}
			
			if(user.has("gender"))
			{
				item.setGender(user.getString("gender").charAt(0));
			}
			
			if(user.has("deno"))
			{
				item.setDeno(user.getString("deno"));
			}
			
			if(user.has("phone"))
			{
				item.setPhone(user.getString("phone"));
			}
			
			if(user.has("qr"))
			{
				item.setQR(user.getString("qr"));
			}
			
			if(user.has("level"))
			{
				item.setLevel(user.getInt("level"));
			}
			
			if(user.has("points"))
			{
				item.setPoints(user.getInt("points"));
			}
			
			if(user.has("img"))
			{
				downloadImg(user.getString("email"), user.getString("img"));
			}
			else
			{
				UsersUtil.imagesCache.put(user.getString("email"), HomeActivity.default_dr);
			}
			
			contactsCache.put(user.getString("email"), item);
		}
    	catch(Exception e)
    	{

    	}
	      
    }
    
    static public String UID, UTOKEN, UEMAIL;
    static final public String UKEY = "";
    static public Map<String, UserItemListView> contactsCache = new HashMap<String, UserItemListView>();
    static public Map<String, Drawable> imagesCache = new HashMap<String, Drawable>();
}
