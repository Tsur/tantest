package com.scripturesos.tantest.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TestBible implements Test{

	private List<TestQuestion> questions = new ArrayList<TestQuestion>();
	private Map<Integer, TestSolution> userSolutions = new HashMap<Integer,TestSolution>();
	private int totalQuestions;
	private int questionCursor = 0;
	
	public TestBible(){}
	
	@Override
	public void initTest(int totalQuestions, JSONArray raw) throws TestException, JSONException
	{
		// TODO Auto-generated method stub
		if(totalQuestions > raw.length() || totalQuestions < 1)
		{
			//Error
			throw new TestException();
		}
		
		Set<Integer> randoms = TestUtil.random(totalQuestions, raw.length());

		for(Integer i : randoms)
		{
			//Obtenemos pregunta aleatoria
			JSONObject jsonQuestion = raw.getJSONObject(i);
			TestQuestion question;
			
			switch(jsonQuestion.getInt("type"))
			{
				case TestQuestion.RADIO: 
					question = new TestQuestionRadio();
					break;
				case TestQuestion.CHECKBOX:
					question = new TestQuestionCheckBox();
					break;
				default:throw new TestException();
			}
			
			question.initQuestion(jsonQuestion);
			
			questions.add(question);	
		}
		
		this.totalQuestions = totalQuestions;
	}

	@Override
	public List<TestQuestion> getQuestions()
	{
		// TODO Auto-generated method stub
		return questions;
	}

	@Override
	public Map<Integer, TestSolution> getUserSolutions()
	{
		// TODO Auto-generated method stub
		return userSolutions;
	}
	
	public TestSolution getUserSolution()
	{
		// TODO Auto-generated method stub
		return userSolutions.get(questionCursor);
	}

	@Override
	public void setUserSolution(TestSolution ts)
	{
		// TODO Auto-generated method stub
		userSolutions.put(questionCursor, ts);
	}

	@Override
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
		
		return grade;
	}

	@Override
	public int getNumQuestions()
	{
		// TODO Auto-generated method stub
		return totalQuestions;
	}

	@Override
	public int getNextQuestion()
	{
		// TODO Auto-generated method stub
		int current = questionCursor;
		
		if(current == getLastQuestion())
		{
			current = getFirstQuestion();
		}
		else
		{
			current++;
		}
		
		questionCursor = current;
		
		return questionCursor;
	}

	@Override
	public int getPrevQuestion()
	{
		// TODO Auto-generated method stub
		int current = questionCursor;
		
		if(current == getFirstQuestion())
		{
			current = getLastQuestion();
		}
		else
		{
			current--;
		}
		
		questionCursor = current;
		
		return questionCursor;
	}

	@Override
	public int getFirstQuestion()
	{
		// TODO Auto-generated method stub
		questionCursor = 0;
		return 0;
	}

	@Override
	public int getLastQuestion()
	{
		// TODO Auto-generated method stub
		questionCursor = getNumQuestions()-1;
		return getNumQuestions()-1;
	}
	
	public void setCurrentQuestion(int numQuestion)
	{
		questionCursor = numQuestion;
	}

}
