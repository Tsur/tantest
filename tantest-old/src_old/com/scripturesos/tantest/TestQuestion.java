package com.scripturesos.tantest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class TestQuestion{
	
	public static final int RADIO = 1;
	public static final int CHECKBOX = 2;
	
	private String question;
	private String clue;
	private String description;
	private String solution;
	private int type,cat;
	private List<String> answers;
	
	
	public TestQuestion(String question,String desc, String clue, String sol, int type, int cat, JSONArray ans) throws JSONException
	{
		this.question = question;
		this.description = desc;
		this.clue = clue;
		this.solution = sol;
		this.type = type;
		this.cat = cat;
		
		answers = new ArrayList<String>();
		
		for(int i=0;i<ans.length();i++)
		{
			answers.add(ans.getString(i));
		}
		
		Collections.shuffle(answers);
	}
	
	public String getQuestion()
	{
		return question;
	}
	
	public int getType()
	{
		return type;
	}
	
	public List<String> getAnswers()
	{
		return answers;
	}

}
