package com.scripturesos.tantest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Test {
	
	private int num_questions;
	private int level;
	private List<TestQuestion> questions;
	private int cursor = -1;
	
	public Test()
	{
		this.num_questions = 5;
		this.level = 0;
	}
	
	public Test(int num_questions)
	{
		this.num_questions = num_questions;
		this.level = 0;
	}
	
	public Test(int num_questions, int level)
	{
		this.num_questions = num_questions;
		this.level = level;
	}
	
	public List<TestQuestion> getQuestions()
	{
		return questions;
	}
	
	public int getCursor()
	{
		if(cursor == num_questions-1)
		{
			cursor = 0;
		}
		else
		{
			cursor++;
		}
		
		return cursor;
	}
	/**
	 * Este método obtiene un recurso JSON por file
	 * y lo devuelve
	 * 
	 * @return
	 */
	public JSONArray getRawFileTest(InputStream file)
	{	
		try
		{
			Log.i("TEST","empezamos");
			
			//Vamos a leer el fichero linea por linea
			BufferedReader reader = new BufferedReader(new InputStreamReader(file, "UTF-8"), 8);
			
			//Obtenemos todo el fichero JSON en un string
			StringBuilder string_builder = new StringBuilder();
			String line = null;
			String result;
			
			while ((line = reader.readLine()) != null)
			{
				string_builder.append(line + "\n");
			}
			
			result = string_builder.toString();
			
			Log.i("TEST","Hola");

			//Creamos objeto json a partir del string
			JSONObject json = new JSONObject(result);
			
			Log.i("TEST","Nos vamos");
			
			return json.getJSONArray(String.valueOf(level));
			
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
		
	}
	
	/**
	 * Este método obtiene un recurso JSON por url
	 * y lo devuelve
	 * 
	 * @return
	 */
	public JSONArray getRawUrleTest(String url)
	{
		try
		{
			// Create a new HTTP Client
		    DefaultHttpClient httpClient = new DefaultHttpClient();
		    // Setup the get request
		    HttpGet httpGetRequest = new HttpGet(url);

		    // Execute the request in the client
		    HttpResponse httpResponse = httpClient.execute(httpGetRequest);
		    
		    HttpEntity entity = httpResponse.getEntity();

		    InputStream file = entity.getContent();
		    
		    return getRawFileTest(file);
			
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	private Set<Integer> random(int len)
	{
		Set<Integer> randoms 	= new HashSet<Integer>();
		Random randomGenerator  = new Random();
		int res;
		
		//Obtenemos num_questions numeros aleatorios que no se repitan entre 0 y test.length(), no incluido test.length()
		while(randoms.size() < num_questions)
		{
			res = randomGenerator.nextInt(len);
			randoms.add(res);
		}
		
		return randoms;
	}
	
	public void makeTest(JSONArray test) throws JSONException
	{
		if(num_questions > test.length())
		{
			//Error
			num_questions = test.length();
		}
		
		//Colección donde guardar el test: linked list util para muchas escrituras y eliminaciones
		questions = new LinkedList<TestQuestion>();

		Set<Integer> randoms = random(test.length()-1);

		for(Integer i : randoms)
		{
			//Obtenemos pregunta aleatoria
			JSONObject question = test.getJSONObject(i);
			
			//Log.i("tantes","leyendo JSON: "+question.getString("question"));
			
			TestQuestion tq = new TestQuestion(
					
					question.getString("question"),
					question.getString("desc"),
					question.getString("clue"),
					question.getString("sol"),
					question.getInt("type"),
					question.getInt("cat"),
					question.getJSONArray("ans")
					
					);
			
			questions.add(tq);
		}
		
		test = null;
		
	}
	
	
}
