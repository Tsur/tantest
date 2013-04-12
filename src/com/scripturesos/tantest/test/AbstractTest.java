package com.scripturesos.tantest.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.SparseArray;

public class AbstractTest
{

	protected List<TestQuestion> questions = new ArrayList<TestQuestion>();
	protected SparseArray<TestSolution> userSolutions = new SparseArray<TestSolution>();
	protected int totalQuestions;
	protected int questionCursor = 0;
	protected String URL;
	
	public List<TestQuestion> getQuestions()
	{
		// TODO Auto-generated method stub
		return questions;
	}
	
	public void initTest(int totalQuestions, JSONArray raw) throws TestException, JSONException
	{
		//Obtener total numero de preguntas con dificultad dada
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


	public SparseArray<TestSolution> getUserSolutions()
	{
		// TODO Auto-generated method stub
		return userSolutions;
	}
	
	public TestSolution getUserSolution()
	{
		// TODO Auto-generated method stub
		return userSolutions.get(questionCursor);
	}


	public void setUserSolution(TestSolution ts)
	{
		// TODO Auto-generated method stub
		userSolutions.put(questionCursor, ts);
	}

	public int getNumQuestions()
	{
		// TODO Auto-generated method stub
		return totalQuestions;
	}

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

	public int getFirstQuestion()
	{
		// TODO Auto-generated method stub
		questionCursor = 0;
		return 0;
	}

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

	public String getSource() 
	{
		// TODO Auto-generated method stub
		return URL;
	}

}
