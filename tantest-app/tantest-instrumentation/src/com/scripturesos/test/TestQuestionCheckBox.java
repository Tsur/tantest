package com.scripturesos.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestQuestionCheckBox implements TestQuestion{
	
	private String title;
	private String desc;
	private List<String> answers;
	private TestSolution solution;
	
	public TestQuestionCheckBox(){}
	
	public void initQuestion(JSONObject jsonQuestion) throws JSONException, TestException
	{
		title = jsonQuestion.getString("question");
		desc = jsonQuestion.getString("desc");
		
		answers = new ArrayList<String>();
		JSONArray jsonAnswers = jsonQuestion.getJSONArray("ans");
		
		for(int j=0; j<jsonAnswers.length();j++)
		{
			answers.add(jsonAnswers.getString(j));
		}
		
		Collections.shuffle(answers);
		
		solution = new TestSolutionCheck();
		
		solution.setSolutionADT(jsonQuestion.getJSONArray("sol"));
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getDescription()
	{
		return desc;
	}
	
	public List<String> getAnswers()
	{
		return answers;
	}

	public TestSolution getSolution()
	{
		return solution;
	}


}
