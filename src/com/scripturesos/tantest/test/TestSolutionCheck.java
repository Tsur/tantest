package com.scripturesos.tantest.test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class TestSolutionCheck implements TestSolution{

	private Set<String> checkAnswer = new HashSet<String>();

	public TestSolutionCheck(){}

	@Override
	public double grade(TestSolution userSolution)
	{
		// TODO Auto-generated method stub
		if(userSolution == null)
		{
			if(checkAnswer.size() == 0)
			{
				return 1;
			}
			
			return 0;
		}
		
		Iterator<String> itr = ((Set<String>) userSolution.getSolutionADT()).iterator();
		
		double points = 0;
		double plus = 1D/checkAnswer.size();
		
		Log.i("tantes","plus = "+String.valueOf(plus));
		Log.i("tantes","size = "+checkAnswer.size());
		
		while(itr.hasNext())
		{
			  String v = itr.next();
			  Log.i("tantes","dada = "+v);
			  if(checkAnswer.contains(v))
		      {
				  points += plus;
				  
		      }
			  else
			  {
				  points = 0;
				  break;
			  }
			  
			  Log.i("tantes","points = "+String.valueOf(points));
		}
		
		Log.i("tantes","points = "+String.valueOf(points));;
		return points;
	}

	@Override
	public Object getSolutionADT()
	{
		// TODO Auto-generated method stub
		return checkAnswer;
	}

	@Override
	public void setSolutionADT(Object adt) throws JSONException, TestException
	{
		// TODO Auto-generated method stub
		if(adt instanceof JSONArray)
		{
			Set<String> solution = new HashSet<String>();
			
			JSONArray jsonSolution = (JSONArray)adt;
			
			for(int j=0; j<jsonSolution.length();j++)
			{
				solution.add(jsonSolution.getString(j));
			}
			
			checkAnswer = solution;
		}
		else if(adt instanceof Set)
		{
			checkAnswer = (Set<String>) adt;
		}
		else
		{
			throw new TestException();
		}
		
		
	}
}
