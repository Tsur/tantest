package com.scripturesos.test;

public class TestSolutionRadio implements TestSolution{

	private String radioAnswer = null;

	public TestSolutionRadio(){}

	public double grade(TestSolution ts)
	{
		if(ts == null)
		{
			return 0;
		}
		
		if(radioAnswer.equalsIgnoreCase((String)ts.getSolutionADT()))
		{
			return 1;
		}
		
		return 0;
	}

	public Object getSolutionADT()
	{
		// TODO Auto-generated method stub
		return radioAnswer;
	}
	
	public void setSolutionADT(Object answer)
	{
		// TODO Auto-generated method stub
		radioAnswer = (String) answer;
	}

}
