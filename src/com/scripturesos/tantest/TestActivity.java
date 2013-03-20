package com.scripturesos.tantest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.scripturesos.tantest.test.Test;
import com.scripturesos.tantest.test.TestBible;
import com.scripturesos.tantest.test.TestException;
import com.scripturesos.tantest.test.TestFactory;
import com.scripturesos.tantest.test.TestGrade;
import com.scripturesos.tantest.test.TestQuestion;
import com.scripturesos.tantest.test.TestQuestionCheckBox;
import com.scripturesos.tantest.test.TestQuestionRadio;
import com.scripturesos.tantest.test.TestRemoteSourceException;
import com.scripturesos.tantest.test.TestSolution;
import com.scripturesos.tantest.test.TestSolutionCheck;
import com.scripturesos.tantest.test.TestSolutionRadio;
import com.scripturesos.tantest.test.TestUtil;

public class TestActivity extends SherlockActivity {
	
	private Test test;
	
	private LinearLayout questionsContainer;
	private SeekBar seekbar;
	private TextView seekbarText;
	private Map<Integer,ScrollView> state = new HashMap<Integer,ScrollView>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		/* ACTION BAR CONFIGURATION */
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		
		LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.main_actionbar_title, null);

		//if you need to customize anything else about the text, do it here.
		//I'm using a custom TextView with a custom font in my layout xml so all I need to do is set title
		Typeface fontTitle = Typeface.createFromAsset(getAssets(), "fuentes/kg.ttf");
		TextView title = (TextView)v.findViewById(R.id.main_actionbar_textTitle);
		title.setText(getTitle());
		title.setTypeface(fontTitle);

		//assign the view to the actionbar
		getSupportActionBar().setCustomView(v);
		
		//overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		
		//Creamos Test
		try
		{
			test = TestFactory.createTest(TestBible.class);
			Log.i("tantes","Iniciando");
			
			if(isNetworkAvailable())
			{
				//JSONArray test_data = TestUtil.getRemoteSource("https://dl.dropbox.com/s/uiwybzpsxcy04je/bible.json?token_hash=AAEHX2RaffWtjJbXQMdSAefawUyquGBYgwI3tW2BFe5tAg&dl=1");
				//test.initTest(5, test_data);
				test.initTest(5, TestUtil.getSource(getResources().openRawResource(R.raw.bible)));
				
			}
			else
			{
				test.initTest(5, TestUtil.getSource(getResources().openRawResource(R.raw.bible)));
			}
			
			Log.i("tantes","Creando Test");
			
			//Si no activamos activity_test, no podemos usar nada de lo que tiene
			setContentView(R.layout.activity_scroll_test);
			
			createTestLayout();
			Log.i("tantes","LayoutCreando");
			
			gotoQuestion(test.getFirstQuestion());
			Log.i("tantes","Test creado");
			
			Log.i("tantes","Todo fue correcto!");

		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (NotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TestRemoteSourceException e)
		{
			try
			{
				test.initTest(5, TestUtil.getSource(getResources().openRawResource(R.raw.bible)));
				Log.i("tantes","Creando Test");
				
				//Si no activamos activity_test, no podemos usar nada de lo que tiene
				setContentView(R.layout.activity_scroll_test);
				
				createTestLayout();
				Log.i("tantes","LayoutCreando");
				
				gotoQuestion(test.getFirstQuestion());
				Log.i("tantes","Test creado");
				
				Log.i("tantes","Todo fue correcto!");
			}
			catch (NotFoundException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (TestException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (JSONException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		}
		catch (TestException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private boolean isNetworkAvailable()
	{
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	protected void onPause()
	{
		
		super.onPause();
		
		//overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_scroll_test, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		
		
		switch(item.getItemId())
		{
			case R.id.test_menu_header_grade:
				
				Log.i("tantes","Calificando test");
				//Calificar test
				TestGrade tg = test.grade(10);
				Log.i("tantes","Calificado!");
				//Desactivar radio y checkbuttons
				
				
				//Mostrar pantalla resultados
				
				//Bundle bundle = new Bundle();
				//bundle.putSerializable("result", tg.toString());
				
				Intent intent = new Intent(this, TestGradeActivity.class);
				intent.putExtra("questionsOK", String.valueOf(tg.numQuestionsOK()));
				intent.putExtra("totalQuestions", String.valueOf(test.getNumQuestions()));
				intent.putExtra("calification", String.valueOf(tg.getCalification()));
				intent.putExtra("base", String.valueOf(tg.getBase()));
				//Log.i("tantes","iniciando actividad");
				startActivity(intent);

				break;
			default:
		}
		
		return true;
	}
	
	public void createTestLayout()
	{

		seekbar = (SeekBar) findViewById(R.id.testSeekBar);
		seekbarText = (TextView) findViewById(R.id.testSeekBarText);
		seekbar.setMax((test.getNumQuestions()-1)*100);
		seekbar.incrementProgressBy(10);
		seekbar.setProgress(0);
		
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		        
		    	
		    	seekbarText.setText(String.valueOf((progress/100)+1));
		        //seekBarValue.setText(String.valueOf(progress));
		    }

		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) {
		    	seekbarText.setVisibility(View.VISIBLE);
		    }

		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) {

		    	seekbarText.setVisibility(View.GONE);
		    	test.setCurrentQuestion(seekBar.getProgress()/100);
		    	gotoQuestion(seekBar.getProgress()/100);
		    }
		});
		Log.i("tantes","seekBar Configurado");
		
		questionsContainer = (LinearLayout) findViewById(R.id.testQuestionsContainer);
		Log.i("tantes","Obtenida referencia a contenedor principal");
		
		int textColor = Color.rgb(23, 26, 30);
		Typeface fontRadio = Typeface.createFromAsset(getAssets(), "fuentes/cd.ttf");
		Typeface fontText = Typeface.createFromAsset(getAssets(), "fuentes/trat.ttf");
		Log.i("tantes","Variables tipografia");
		
		int numQuestionLabel = 1;
		OnCheckedChangeListener occl = new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton checkbox,boolean checked)
			{
				
				TestSolution ts = (TestSolutionCheck) test.getUserSolution();
				
				if(ts == null)
				{
					ts = new TestSolutionCheck();
				}
				
				if(checked)
				{
					((Set<String>)ts.getSolutionADT()).add(checkbox.getText().toString());
				}
				else
				{
					((Set<String>)ts.getSolutionADT()).remove(checkbox.getText().toString());
				}
				
				test.setUserSolution(ts);
			}

	    };
		
		for(TestQuestion tq : test.getQuestions())
		{
			
			/*Creamos vertical ScrollView*/
			ScrollView sv = new ScrollView(this);
			ScrollView.LayoutParams svParams = new ScrollView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
			sv.setLayoutParams(svParams);
			
			/*Creamos question container*/
			LinearLayout questionContainer = new LinearLayout(this);
			LinearLayout.LayoutParams qcParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
			questionContainer.setOrientation(LinearLayout.VERTICAL);
			questionContainer.setLayoutParams(qcParams);
			
			//Añadimos question container a scroll vertical
			sv.addView(questionContainer);
			
			/*Creamos Texto pregunta*/
			TextView text = new TextView(this);
			text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
			text.setGravity(Gravity.CENTER_HORIZONTAL);
			text.setPadding(0, 0, 0, 30);
			text.setTextColor(textColor);
			text.setTypeface(fontText);
			text.setText(numQuestionLabel+"# "+tq.getTitle());
			
			//Añadimos texto pregunta a question container
			questionContainer.addView(text);
			
			/*Creamos answer container*/
			if(tq instanceof TestQuestionRadio)
			{
				RadioGroup rg = new RadioGroup(this);
				rg.setPadding(80, 0, 0, 0);
				
				RadioButton rb1 = new RadioButton(this);
				rb1.setText(tq.getAnswers().get(0));
				rb1.setTypeface(fontRadio);
				rb1.setPadding(53, 0, 0, 0);
				
				RadioButton rb2 = new RadioButton(this);
				rb2.setText(tq.getAnswers().get(1));
				rb2.setTypeface(fontRadio);
				rb2.setPadding(53, 0, 0, 0);
				
				RadioButton rb3 = new RadioButton(this);
				rb3.setText(tq.getAnswers().get(2));
				rb3.setTypeface(fontRadio);
				rb3.setPadding(53, 0, 0, 0);
				
				RadioButton rb4 = new RadioButton(this);
				rb4.setText(tq.getAnswers().get(3));
				rb4.setTypeface(fontRadio);
				rb4.setPadding(53, 0, 0, 0);
				
				//Listener to radio group
				rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
				{
				    public void onCheckedChanged(RadioGroup rGroup, int checkedId)
				    {
				        // This will get the radiobutton that has changed in its check state
				        RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(checkedId);

				        // If the radiobutton that has changed in check state is now checked...
				        if(checkedRadioButton.isChecked())
				        {
				            TestSolution ts = new TestSolutionRadio();
				        	
				            try 
				            {
								ts.setSolutionADT(checkedRadioButton.getText().toString());
							}
				            catch (JSONException e) 
				            {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
				            catch (TestException e) 
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				            
				        	test.setUserSolution(ts);
				        }
				    }
				});
				
				//Add radio buttons al radio group
				rg.addView(rb1);
				rg.addView(rb2);
				rg.addView(rb3);
				rg.addView(rb4);
				
				//Añadimos answercontainer a question container
				questionContainer.addView(rg);
			}
			else if(tq instanceof TestQuestionCheckBox)
			{
				LinearLayout cg = new LinearLayout(this);
				cg.setOrientation(LinearLayout.VERTICAL);
				cg.setPadding(80, 0, 0, 0);
				
				CheckBox cb1 = new CheckBox(this);
				cb1.setText(tq.getAnswers().get(0));
				cb1.setTypeface(fontRadio);
				cb1.setPadding(53, 0, 0, 0);
				cb1.setOnCheckedChangeListener(occl);
				
				CheckBox cb2 = new CheckBox(this);
				cb2.setText(tq.getAnswers().get(1));
				cb2.setTypeface(fontRadio);
				cb2.setPadding(53, 0, 0, 0);
				cb2.setOnCheckedChangeListener(occl);
				
				CheckBox cb3 = new CheckBox(this);
				cb3.setText(tq.getAnswers().get(2));
				cb3.setTypeface(fontRadio);
				cb3.setPadding(53, 0, 0, 0);
				cb3.setOnCheckedChangeListener(occl);
				
				CheckBox cb4 = new CheckBox(this);
				cb4.setText(tq.getAnswers().get(3));
				cb4.setTypeface(fontRadio);
				cb4.setPadding(53, 0, 0, 0);
				cb4.setOnCheckedChangeListener(occl);
				
				//Añadimos answercontainer a question container
				cg.addView(cb1);
				cg.addView(cb2);
				cg.addView(cb3);
				cg.addView(cb4);
				
				questionContainer.addView(cg);
			}

			//Añadimos scroll vertical a questions container
			//questionsContainer.addView(sv);
			state.put(numQuestionLabel-1, sv);
			
			numQuestionLabel++;
		}
		
	}
	
	public void nextQuestion(View view)
	{
	    // Do something in response to click button
		int nextQuestion = test.getNextQuestion();
		seekbar.setProgress(nextQuestion*100);
		//seekbarText.setText(String.valueOf(cursor+1));
		Log.i("tantes","Next "+nextQuestion);
		gotoQuestion(nextQuestion);
	}
	
	public void prevQuestion(View view)
	{
	    // Do something in response to click button
		int prevQuestion = test.getPrevQuestion();
		seekbar.setProgress(prevQuestion*100);
		//seekbarText.setText(String.valueOf(cursor+1));
		Log.i("tantes","Prev "+prevQuestion);
		gotoQuestion(prevQuestion);
	}
	
	public void firstQuestion(View view)
	{
	    // Do something in response to click button
		int firstQuestion = test.getFirstQuestion();
		seekbar.setProgress(firstQuestion);
		//seekbarText.setText("1");
		Log.i("tantes","First "+firstQuestion);
		gotoQuestion(firstQuestion);
	}
	
	public void lastQuestion(View view)
	{
	    // Do something in response to click button
		int lastQuestion = test.getLastQuestion();
		seekbar.setProgress(lastQuestion*100);
		//seekbarText.setText(String.valueOf(numQuestions+1));
		Log.i("tantes","Last "+lastQuestion);
		gotoQuestion(lastQuestion);
	}
	
	private void gotoQuestion(int question)
	{
		ScrollView questionView = state.get(question);
		
		questionsContainer.removeAllViews();
		questionsContainer.addView(questionView);
	}

}
