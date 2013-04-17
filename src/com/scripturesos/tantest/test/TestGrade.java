package com.scripturesos.tantest.test;

import java.io.Serializable;
import java.text.DecimalFormat;

import com.scripturesos.tantest.R;

public class TestGrade implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int base = 10;
	private double calification;
	private int questionsOK;
	private int totalQuestions;
	private int points;
	private int total;
	private int difficulty;
	private int time;
	private String realTime;
	private String url="";
	
	public TestGrade(){}
	
	public void setBase(int base)
	{
		this.base = base;
	}
	
	public void setCalification(double calification, int totalQuestions)
	{
		this.totalQuestions = totalQuestions;
		
		DecimalFormat twoDForm = new DecimalFormat("#.#");
		
		this.calification = (calification/totalQuestions) * base;
		//Log.i("tantes","final calification = "+ String.valueOf(this.calification));
		//Log.i("tantes","final calification string= "+ twoDForm.format(this.calification));
		this.calification = Double.valueOf(twoDForm.format(this.calification).replace(',', '.'));
	}
	
	public void setQuestionsOK(int questionsOK)
	{
		this.questionsOK = questionsOK;
	}
	
	public void setTotalQuestions(int questions)
	{
		this.total = questions;
	}
	
	public double getCalification()
	{
		return calification;
	}
	
	public int numQuestionsOK()
	{
		return questionsOK;
	}
	
	public int getBase()
	{
		return base;
	}
	
	public void setPoints(int difficulty, int time, String realTime)
	{
		switch(difficulty)
		{
			case 0:
				this.difficulty = R.string.act_test_grade_text14;
				break;
			case 1:
				this.difficulty = R.string.act_test_grade_text15;
				break;
			case 2:
				this.difficulty = R.string.act_test_grade_text16;
				break;
			case 3:
				this.difficulty = R.string.act_test_grade_text17;
				break;
			default:break;
		}
		
		//this.difficulty = difficulty;
		this.time = time;
		
		this.realTime = realTime;
		
		//Si aprueba ->Suma puntos con respecto a la dificultad
		if(calification >= 5)
		{
			//0 -> 7p, 1-> 27p, 2->70p, 3-> 170p
			switch(difficulty)
			{
				case 0:
					points = 7;
					break;
				case 1:
					points = 27;
					break;
				case 2:
					points = 70;
					break;
				case 3:
					points = 170;
					break;
				default:
					points = 0;
					break;
			}
			
			//+plus (numero preguntas_acertadas)
			points += questionsOK;
			
			//+plus (tiempo)
			if(time > 0)
			{
				points += (61-(time % 61));
			}
			
		}
		//Si no aprueba, 0 puntos
		else
		{
			points = 0;
		}
	}
	
	public int getPoints()
	{
		return points;
	}
	
	public int getDifficulty()
	{
		return difficulty;
	}
	
	public int getTime()
	{
		return time;
	}
	
	public String getRealTime()
	{
		return realTime;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public int getTotalQuestions()
	{
		return total;
	}
	
	public String toString()
	{
		return String.valueOf(base)+
				"@"+
				String.valueOf(questionsOK)+
				"@"+
				String.valueOf(calification)+
				"@"+
				String.valueOf(totalQuestions);
	}
	
	public void setUrl(String url)
	{
		this.url = url;
	}
	
}
