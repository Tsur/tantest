package com.scripturesos.test;

import org.json.JSONException;

public interface TestSolution {

	/*Devuelve siempre entre 0(totalmente erronea) y 1(totalmente correcta)*/
	public double grade(TestSolution ts);
	public Object getSolutionADT();
	public void setSolutionADT(Object adt) throws JSONException, TestException;
}
