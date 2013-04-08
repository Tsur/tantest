package com.scripturesos.tantest;

import java.io.Serializable;


public class ContactItemListView implements Serializable
{
  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String id;
	protected String image;
	protected String name;
	protected String status;
	protected String points;
    
	public ContactItemListView(String id, String image, String name, String status, String points)
	{
	    this.id = id;
	    this.image = image;
	    this.name = name;
	    this.status = status;
	    this.points = points;
	}
     
	public String getImg() 
	{
		return image;
	}

     
	public String getName()
	{
		return name;
	}
	
	public String getID()
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