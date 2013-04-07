package com.scripturesos.tantest;

import java.io.Serializable;


public class ContactItemListView implements Serializable
{
  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected long id;
	protected String urlImg;
	protected String name;
	protected String status;
	protected String points;
     
	public ContactItemListView(long id, String name, String status, String points)
	{
	    this.id = id;
	    this.name = name;
	    this.status = status;
	    this.points = points;
	}
	
	public ContactItemListView(long id, String name, String status, String points, String urlImg)
	{
	    this.id = id;
	    this.name = name;
	    this.status = status;
	    this.points = points;
	    this.urlImg = urlImg;
	}
     
	public long getId()
	{
		return id;
	}
     
	public String getImg() 
	{
		return urlImg;
	}

     
	public String getName()
	{
		return name;
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