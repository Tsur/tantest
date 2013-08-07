package com.scripturesos.test;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public interface TestQuestion {

	public static final int RADIO = 1;
	public static final int CHECKBOX = 2;

	public void initQuestion(JSONObject jsonQuestion) throws JSONException, TestException;
	public String getTitle();
	public String getDescription();
	public List<String> getAnswers();
	public TestSolution getSolution();

}
