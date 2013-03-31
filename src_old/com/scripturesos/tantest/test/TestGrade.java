package com.scripturesos.tantest.test;

import java.io.Serializable;
import java.text.DecimalFormat;

import android.util.Log;

public class TestGrade implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int base = 10;
	private double calification;
	private int questionsOK;
	private int totalQuestions;
	
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
		Log.i("tantes","final calification = "+ String.valueOf(this.calification));
		Log.i("tantes","final calification string= "+ twoDForm.format(this.calification));
		this.calification = Double.valueOf(twoDForm.format(this.calification).replace(',', '.'));
	}
	
	public void setQuestionsOK(int questionsOK)
	{
		this.questionsOK = questionsOK;
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
	
}
