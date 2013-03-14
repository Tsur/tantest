package com.scripturesos.tantest.test;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

public interface Test
{

	public void initTest(int totalQuestions, JSONArray raw) throws TestException, JSONException;
	public List<TestQuestion> getQuestions();
	public Map<Integer,TestSolution> getUserSolutions();
	public TestSolution getUserSolution();
	public void setUserSolution(TestSolution ts);
	public void setCurrentQuestion(int numQuestion);
	
	public TestGrade grade(int base);
	
	public int getNumQuestions();
	public int getNextQuestion();
	public int getPrevQuestion();
	public int getFirstQuestion();
	public int getLastQuestion();

}
