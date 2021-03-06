package com.scripturesos.tantest.test;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;

public class TestBible extends AbstractTest implements Test{

	
	public TestBible()
	{
		URL = "http://www.scripturesos.com:3001/test/general/";
	}
	
	public void initTest(int totalQuestions, int difficulty) throws TestException, JSONException, MalformedURLException, IOException
	{
		URL += String.valueOf(difficulty)+".json";
		
		initTest(totalQuestions, TestUtil.getRemoteSource(URL));
	}
	
	public TestGrade grade(int base)
	{
		// TODO Auto-generated method stub
		int pos = 0;
		int questionsOK = 0;
		double calification_total = 0;
		
		for(TestQuestion question: questions)
		{
			TestSolution ts = userSolutions.get(pos);

			double calification = question.getSolution().grade(ts);
			
			calification_total += calification;
			
			if(calification >= 1)
			{
				questionsOK++;
			}
			
			pos++;
			
		}
		
		TestGrade grade = new TestGrade();
		
		grade.setBase(base);
		grade.setCalification(calification_total, totalQuestions);
		grade.setQuestionsOK(questionsOK);
		grade.setTotalQuestions(questions.size());
		
		return grade;
	}
}
