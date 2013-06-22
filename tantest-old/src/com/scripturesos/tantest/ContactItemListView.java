package com.scripturesos.tantest;

import java.io.Serializable;
import java.util.Date;


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
	protected Date date;
	
	public ContactItemListView(String id, String image, String name, String status, String points)
	{
	    this.id = id;
	    this.image = image;
	    this.name = name;
	    this.status = status;
	    this.points = points;
	}
	
	public ContactItemListView(String id, String image, String name, String status, String points, Date date)
	{
	    this.id = id;
	    this.image = image;
	    this.name = name;
	    this.status = status;
	    this.points = points;
	    this.date= date;
	}
     
	public String getImg() 
	{
		return image;
	}
	
	public void setImg(String image) 
	{
		this.image = image;
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
	
	public void setStatus(String status)
	{
		this.status = status;
	}
	
	public String getPoints()
	{
		return points;
	}
	
	public void setPoints(String points)
	{
		this.points = points;
	}
	
	public Date getLastDate()
	{
		return date;
	}
	
	public void setLastDate(Date date)
	{
		this.date = date;
	}

}