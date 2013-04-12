package com.scripturesos.tantest;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import com.scripturesos.tantest.connection.HttpUtil;
import com.scripturesos.tantest.test.TestGrade;

public class TestGradeActivity extends Application {

	private TestGrade tg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_test_grade);
		
		Bundle extras = getIntent().getExtras();
		
		if(extras != null)
		{
		    try 
		    {
				tg = (TestGrade) HttpUtil.fromString(extras.getString("grade"));
			} 
		    catch(Exception e) 
		    {
			
			}
			
		    showResults();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_test_grade, menu);
		return true;
	}
	
	private void showResults()
	{
		TextView textQuestionsOK = (TextView) findViewById(R.id.testgrade_textquestionsok);
		
		if(tg.numQuestionsOK() == 0)
		{
			textQuestionsOK.setText("No has acertado ninguna pregunta");
			
		}
		else
		{
			textQuestionsOK.setText("Has acertado "+tg.numQuestionsOK()+" pregunta"+((tg.numQuestionsOK() > 1) ? "s":""));
		
		}
		
		
		TextView textGrade = (TextView) findViewById(R.id.testgrade_textgrade);
		textGrade.setText("Calificacion: "+tg.getCalification());
		
		TextView textTotalQuestions = (TextView) findViewById(R.id.testgrade_texttotalquestions);
		textTotalQuestions.setText("(Total preguntas: "+tg.getTotalQuestions()+")");
		
		TextView textBase = (TextView) findViewById(R.id.testgrade_textbase);
		textBase.setText("(sobre: "+tg.getBase()+")");
		
		ImageView img;
		
		if(tg.getCalification() >= tg.getBase()/2)
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
