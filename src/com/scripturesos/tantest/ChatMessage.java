package com.scripturesos.tantest;

import java.io.Serializable;


public class ChatMessage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean left;
	public String message;

	public ChatMessage(boolean left, String message) 
	{

		this.left = left;
		this.message = message;
	}
}