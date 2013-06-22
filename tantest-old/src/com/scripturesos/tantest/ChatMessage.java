package com.scripturesos.tantest;

import java.io.Serializable;


public class ChatMessage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean left;
	public boolean root;
	public boolean confirmed = false;
	public String message;
	public String id;

	
	public ChatMessage(boolean left, String message, boolean root, String id) 
	{
		this.left = left;
		this.message = message;
		this.root = root;
		this.id = id;
	}
}