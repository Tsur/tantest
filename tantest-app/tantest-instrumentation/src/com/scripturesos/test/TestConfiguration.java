package com.scripturesos.test;

import java.io.Serializable;


public class TestConfiguration implements Serializable
{
  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int questions = 5;
	public int time = 0;
	public int category = 0;
	public int difficulty = 0;
    
	public TestConfiguration(){}
	
	public TestConfiguration(int q, int t, int d, int c)
	{
		questions = q;
		time = t;
		category = c;
		difficulty = d;
	}

}