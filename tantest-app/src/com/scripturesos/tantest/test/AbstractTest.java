//encoding="UTF-8"

package com.scripturesos.tantest.test;

import java.util.ArrayList;
import java.util.HashSet;
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
	
	@SuppressWarnings("unchecked")
	public String toHTML(TestGrade tg)
	{
		String html = "<div><p id=\"calification\">Calificación: "+tg.getCalification()+"</p>";
		
		html += "<p id=\"total_questions\">Total preguntas: "+tg.getTotalQuestions()+"</p>";
		html += "<p id=\"questions_right\">Preguntas acertadas: "+tg.numQuestionsOK()+"</p>";
		html += "<p id=\"questions_wrong\">Preguntas falladas: "+(tg.getTotalQuestions()-tg.numQuestionsOK())+"</p>";
		
		switch(tg.getDifficulty())
		{
			case 0:
				html += "<p id=\"difficulty\">Grado dificultad: Bajo</p>";
				break;
			case 1:
				html += "<p id=\"difficulty\">Grado dificultad: Medio</p>";
				break;
			case 2:
				html += "<p id=\"difficulty\">Grado dificultad: Alto</p>";
				break;
			case 3:
				html += "<p id=\"difficulty\">Grado dificultad: Avanzado</p>";
				break;
		}
			
		if(tg.getTime() == 0)
		{
			html += "<p id=\"time\">Tiempo: Sin Tiempo</p>";
		}
		else
		{
			html += "<p id=\"time\">Tiempo máximo: "+tg.getTime()+(tg.getTime() > 1 ? " minutos" : " minuto")+"</p>";
			
			if(tg.getRealTime() != null)
			{
				html += "<p id=\"time_used\">Tiempo empleado: "+tg.getRealTime()+"</p>";
			}
		}
		
		html += "<p id=\"points\">Puntos: "+tg.getPoints()+"</p>";
		
		html += "</div>";
		
		int pos = 0;
		String user_sol;
		Set<String> user_sol_set = new HashSet<String>();
		
		for(TestQuestion question: questions)
		{
			TestSolution ts = userSolutions.get(pos);
			pos++;
			user_sol = "";
			user_sol_set.clear();
			
			html += "<div class=\"questions\">";
			html += "<p class=\"title\">"+question.getTitle()+"</p>";
			html += "<p class=\"description\">"+question.getDescription()+"</p>";
			html += "<div class=\"answers\">";
			
			if(question instanceof TestQuestionRadio)
			{
				String solution = (String)question.getSolution().getSolutionADT();
				
				if(ts != null)
				{
					user_sol = (String) ts.getSolutionADT();
				}
				
				for(String answer: question.getAnswers())
				{
					if(answer.equals(solution))
					{
						if(answer.equals(user_sol))
						{
							html += "<p class=\"solution selected\">"+answer+"</p>";
						}
						else
						{
							html += "<p class=\"solution\">"+answer+"</p>";
						}
					}
					else
					{
						if(answer.equals(user_sol))
						{
							html += "<p class=\"selected\">"+answer+"</p>";
						}
						else
						{
							html += "<p>"+answer+"</p>";
						}
					}
				}
				
				html += "</div>";
			}
			else
			{
				Set<String> solution = (Set<String>)question.getSolution().getSolutionADT();
				
				if(ts != null)
				{
					user_sol_set = (Set<String>)ts.getSolutionADT();
				}
				
				for(String answer: question.getAnswers())
				{
					if(solution.contains(answer))
					{
						if(user_sol_set.contains(answer))
						{
							html += "<p class=\"solution selected\">"+answer+"</p>";
						}
						else
						{
							html += "<p class=\"solution\">"+answer+"</p>";
						}
					}
					else
					{
						if(user_sol_set.contains(answer))
						{
							html += "<p class=\"selected\">"+answer+"</p>";
						}
						else
						{
							html += "<p>"+answer+"</p>";
						}
					}
					
				}
				
				html += "</div>";
			}
			
			html += "</div>";
		}

		return html;
	}

}
