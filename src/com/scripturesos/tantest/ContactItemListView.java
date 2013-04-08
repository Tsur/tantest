package com.scripturesos.tantest;

import java.io.Serializable;


public class ContactItemListView implements Serializable
{
  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int id;
	protected String client;
	protected String urlImg;
	protected String name;
	protected String status;
	protected String points;
    
	public ContactItemListView(int id, String name, String status, String points)
	{
	    this.id = id;
	    this.name = name;
	    this.status = status;
	    this.points = points;
	}
	
	public ContactItemListView(int id, String name, String status, String points, String urlImg, String client)
	{
	    this.id = id;
	    this.name = name;
	    this.status = status;
	    this.points = points;
	    this.urlImg = urlImg;
	    this.client = client;
	}
     
	public String getImg() 
	{
		return urlImg;
	}

     
	public String getName()
	{
		return name;
	}
	
	public int getPosition()
	{
		return id;
	}
     
	public String getStatus()
	{
		return status;
	}
	
	public String getPoints()
	{
		return points;
	}

}