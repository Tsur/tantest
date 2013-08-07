package com.scripturesos.tantest.main;

import java.io.Serializable;
import java.util.Date;


public class UserItemListView implements Serializable
{
  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String email;
	protected String tid;
	protected String alias;
	protected String status;
	protected String desc;
	protected String whereabout;
	protected char gender;
	protected String deno = "Adenominacional";
	protected String phone;
	protected String qr;
	protected int level = 0;
	protected int points;
	
	protected Date date;
	
	public UserItemListView(String email)
	{
	    this.email = email;
	}
	
	public void setTid(String tid)
	{
		this.tid = tid;
	}
	
	public void setAlias(String alias)
	{
		this.alias = alias;
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}
	
	public void setDesc(String desc)
	{
		this.desc = desc;
	}
	
	public void setWhere(String where)
	{
		this.whereabout = where;
	}
	
	public void setGender(char gender)
	{
		this.gender = gender;
	}
	
	public void setDeno(String deno)
	{
		this.deno = deno;
	}
	
	public void setPhone(String phone)
	{
		this.phone = phone;
	}
	
	public void setQR(String qr)
	{
		this.qr = qr;
	}
	
	public void setLevel(int level)
	{
		this.level = level;
	}
	
	public void setPoints(int points)
	{
		this.points = points;
	}

	public void setLastDate(Date date)
	{
		this.date = date;
	}
	
	public Date getLastDate()
	{
		return date;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public String getDeno()
	{
		return deno;
	}
	
	public int getLevel()
	{
		return level;
	}

}