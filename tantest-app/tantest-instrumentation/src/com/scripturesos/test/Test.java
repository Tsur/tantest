package com.scripturesos.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.SparseArray;

public interface Test
{

	public void initTest(int totalQuestions, int difficulty) throws TestException, JSONException, MalformedURLException, IOException;
	public void initTest(int totalQuestions, JSONArray raw) throws TestException, JSONException;
	public List<TestQuestion> getQuestions();
	public SparseArray<TestSolution> getUserSolutions();
	public TestSolution getUserSolution();
	public void setUserSolution(TestSolution ts);
	public void setCurrentQuestion(int numQuestion);
	public String getSource();
	
	public TestGrade grade(int base);
	
	public int getNumQuestions();
	public int getNextQuestion();
	public int getPrevQuestion();
	public int getFirstQuestion();
	public int getLastQuestion();
	
	public String toHTML(TestGrade tg);

}
