package com.scripturesos.tantest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;

public class TestGradeActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_test_grade);
		
		Bundle extras = getIntent().getExtras();
		
		if (extras != null)
		{
		    String questionsOK = extras.getString("questionsOK");
		    String totalQuestions = extras.getString("totalQuestions");
		    String calification = extras.getString("calification");
		    String base = extras.getString("base");
		    
		    showResults(questionsOK,totalQuestions,calification,base);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_test_grade, menu);
		return true;
	}
	
	private void showResults(String questionsOK, String totalQuestions, String calification, String base)
	{
		TextView textQuestionsOK = (TextView) findViewById(R.id.testgrade_textquestionsok);
		
		if(Integer.parseInt(questionsOK) == 0)
		{
			textQuestionsOK.setText("No has acertado ninguna pregunta");
			
		}
		else
		{
			textQuestionsOK.setText("Has acertado "+questionsOK+" pregunta"+((Integer.parseInt(questionsOK) > 1) ? "s":""));
		
		}
		
		
		TextView textGrade = (TextView) findViewById(R.id.testgrade_textgrade);
		textGrade.setText("Calificacion: "+calification);
		
		TextView textTotalQuestions = (TextView) findViewById(R.id.testgrade_texttotalquestions);
		textTotalQuestions.setText("(Total preguntas: "+totalQuestions+")");
		
		TextView textBase = (TextView) findViewById(R.id.testgrade_textbase);
		textBase.setText("(sobre: "+base+")");
		
		ImageView img;
		
		if(Double.parseDouble(calification) >= Integer.parseInt(base)/2)
		{
			img = (ImageView) findViewById(R.id.testgrade_imageOK);
		}
		else
		{
			img = (ImageView) findViewById(R.id.testgrade_imageKO);
		}
		
		img.setVisibility(View.VISIBLE);
	}

}
