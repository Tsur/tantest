package com.scripturesos.tantest;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.scripturesos.tantest.connection.HttpUtil;
import com.scripturesos.tantest.test.Test;
import com.scripturesos.tantest.test.TestConfiguration;
import com.scripturesos.tantest.test.TestException;
import com.scripturesos.tantest.test.TestFactory;
import com.scripturesos.tantest.test.TestGrade;
import com.scripturesos.tantest.test.TestQuestion;
import com.scripturesos.tantest.test.TestQuestionCheckBox;
import com.scripturesos.tantest.test.TestQuestionRadio;
import com.scripturesos.tantest.test.TestSolution;
import com.scripturesos.tantest.test.TestSolutionCheck;
import com.scripturesos.tantest.test.TestSolutionRadio;
import com.scripturesos.tantest.test.TestUtil;

public class TestActivity extends Application {
	
	private TestConfiguration tc;
	private Test test;
	private ProgressBar progress;
	private LinearLayout questionsContainer;
	private SeekBar seekbar;
	private TextView seekbarText;
	private SparseArray<ScrollView> state = new SparseArray<ScrollView>();
	
	private Typeface fontRadio;
	private Typeface fontText;
	
	private TextView timer;
	private DateFormat timer_formatter = new SimpleDateFormat("mm:ss", Locale.getDefault());
	private Animation finishingTimer;
	private boolean finishingTimerControl = false;
	private CountDownTimer timeCounter;
	private boolean testGraded = false;
	private double calification;
	private MenuItem item;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTitle(R.string.act_test_text1);
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_test);
		//overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		handler = new TestActivityHandler(TestActivity.this);
		progress = (ProgressBar) findViewById(R.id.act_test_loader);
		seekbar = (SeekBar) findViewById(R.id.act_test_sb);
		seekbarText = (TextView) findViewById(R.id.act_test_sb_text);
		questionsContainer = (LinearLayout) findViewById(R.id.act_test_llqc);
		
		fontRadio = Typeface.createFromAsset(getAssets(), "fuentes/cd.ttf");
		fontText = Typeface.createFromAsset(getAssets(), "fuentes/trat.ttf");
		
		(new Thread() {
		    
			public void run() 
			{
				Bundle extras = getIntent().getExtras();
				
				try 
				{	
					if(extras != null && extras.containsKey("configuration"))
					{
						tc = (TestConfiguration) HttpUtil.fromString(extras.getString("configuration"));
					}
					else
					{
						tc = new TestConfiguration();
					} 
				}
				catch(Exception e) 
				{
					tc = new TestConfiguration();
				}

				test = TestFactory.createTest(String.valueOf(tc.category));

				Message msg = new Message();
				
				if(test != null)
				{
					try 
					{
						test.initTest(tc.questions, tc.difficulty);
					}
					catch (Exception e) 
					{
						try 
						{
							//Dependiendo de la dificultad usamos uno u otro
							Log.i("tantest","Creando Test General, no URL: "+test.getSource());
							
							test.initTest(tc.questions, TestUtil.getSource(getResources().openRawResource(R.raw.bible)));
						} 
						catch (Exception e1) 
						{
							msg.what = 11;
							handler.sendMessage(msg);
						}
					}

					createTestLayout();
					
					msg.what = 0;
					msg.obj = tc.time;
					handler.sendMessage(msg);
					
				}
				else
				{
					msg.what = 10;
					handler.sendMessage(msg);
				}
		    }
		}).start();
		
	}
	
	/*private boolean isNetworkAvailable()
	{
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}*/

	/*protected void onPause()
	{
		
		super.onPause();
		
		//overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}*/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_scroll_test, menu);
		item = menu.findItem(R.id.test_menu_header_grade);
		return true;
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
        super.onActivityResult(requestCode, resultCode, data);
        
        switch(requestCode) 
        {
            case 0:      
            if (resultCode == RESULT_OK) 
            {  
            	Bundle b = data.getExtras();

            	if(b.containsKey("chat") && b.containsKey("test"))
            	{
            		//Mandamos a HomeActivity que mande mensaje e inicie chat
            		
    			    //HomeActivity.this.handler.post();
        		
    			    //shareTest((TestGrade)HttpUtil.fromString(b.getString("test")),(ContactItemListView)HttpUtil.fromString(b.getString("chat")));
            		Intent mIntent = new Intent();
    			    
    			    Bundle bundle = new Bundle();
    				
    				bundle.putString("chat", b.getString("chat"));
    				bundle.putString("test", b.getString("test"));
    				
    				mIntent.putExtras(bundle);
    			    
    			    setResult(RESULT_OK, mIntent);
    			    
            		//ifError(getString(R.string.act_test_text6));
    			    
    			    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    			        @Override
    			        public void onClick(DialogInterface dialog, int which) {

    			        	switch(which)
    			        	{
	    			            case DialogInterface.BUTTON_POSITIVE:
	    			                
	    			                break;
	
	    			            case DialogInterface.BUTTON_NEGATIVE:
	    			                finish();
	    			                break;
    			            }
    			        }
    			    };

    			    (new AlertDialog.Builder(this))
    			    .setMessage(R.string.act_test_text6)
    			    .setPositiveButton(R.string.act_test_text7, dialogClickListener)
    			    .setNegativeButton(R.string.act_test_text8, dialogClickListener)
    			    .setCancelable(false)
    			    .show();
            	}
            }
            break;
               
        }
        
    }
	
    @Override
    public void onConfigurationChanged(Configuration newConfig) 
    {
        super.onConfigurationChanged(newConfig);
    }

	public boolean onOptionsItemSelected(MenuItem item)
	{
		
		switch(item.getItemId())
		{
			case R.id.test_menu_header_grade:
				//item.setVisible(false);
				gradeTest();
				break;
			default:
		}
		
		return true;
	}
	
	public void createTestLayout()
	{

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
		
		
		int textColor = Color.rgb(23, 26, 30);
		
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
		//Log.i("tantes","First "+firstQuestion);
		gotoQuestion(firstQuestion);
	}
	
	public void lastQuestion(View view)
	{
	    // Do something in response to click button
		int lastQuestion = test.getLastQuestion();
		seekbar.setProgress(lastQuestion*100);
		//seekbarText.setText(String.valueOf(numQuestions+1));
		//Log.i("tantes","Last "+lastQuestion);
		gotoQuestion(lastQuestion);
	}
	
	private void gotoQuestion(int question)
	{	
		questionsContainer.removeAllViews();
		questionsContainer.addView(state.get(question));
	}
	
	private void startTest(Integer time)
	{
		progress.setVisibility(View.GONE);
		gotoQuestion(test.getFirstQuestion());
		((RelativeLayout) findViewById(R.id.act_test_nav)).setVisibility(View.VISIBLE);
		
		if(item != null)
		{
			item.setVisible(true);
		}
		
		if(time != null && time > 0)
		{
			View v = getSupportActionBar().getCustomView();
			
			timer = (TextView) v.findViewById(R.id.main_actionbar_textTitle);//findViewById(R.id.act_test_chronometer);
			timer.setTypeface(fontText);
			timer.setText((time >= 10 ? time : "0"+time)+":00");
			//timer.setVisibility(View.VISIBLE);
			finishingTimer = new AlphaAnimation(0.0f, 1.0f);
			finishingTimer.setDuration(400); //You can manage the time of the blink with this parameter
			//finishingTimer.setStartOffset(20);
			finishingTimer.setRepeatMode(Animation.REVERSE);
			finishingTimer.setRepeatCount(Animation.INFINITE);
			
			
			timeCounter = new CountDownTimer(time*60*1000,1000){

				@Override
				public void onFinish()
				{
					Log.i("tantest","Se acabó el tiempo");
					timer.setText(R.string.act_test_text4);
					timer.clearAnimation();
					gradeTest();
				}

				@Override
				public void onTick(long millisUntilFinished) 
				{
					if(millisUntilFinished <= 11000 && finishingTimerControl == false)
					{
						timer.startAnimation(finishingTimer);
						finishingTimerControl = true;
					}
					
					timer.setText(timer_formatter.format(new Date(millisUntilFinished)));
				}
				
				
			};
			
			timeCounter.start();
			
			
		}
	}
	
	public void gradeTest()
	{
		//Ocultamos para que el usuario no pueda modificar el test mientras se corrige
		((RelativeLayout) findViewById(R.id.act_test_nav)).setVisibility(View.GONE);
		questionsContainer.setVisibility(View.GONE);
		questionsContainer.removeAllViews();
		
		progress.setVisibility(View.VISIBLE);
		
		if(item != null)
		{
			item.setVisible(false);
		}
		
		(new Thread() {
		    
			public void run() 
			{
				String realTime = null;
				
				if(timeCounter != null)
				{
					//Parar tiempo
					timeCounter.cancel();
					timer.clearAnimation();
					
					try 
					{
						Date realTimemili = timer_formatter.parse(timer.getText().toString());
						realTime = timer_formatter.format(new Date((tc.time*60*1000) - realTimemili.getTime()));
				
					}
					catch (ParseException e) 
					{
						realTime = null;
					}
				}
				
				TestGrade tg = test.grade(10);
				
				tg.setPoints(tc.difficulty, tc.time, realTime);
				
				//HTML START

				try 
				{
					String html = test.toHTML(tg);

					JSONObject response = HttpUtil.post(HttpUtil.REGISTER_TEST, new String[]{html});
					
					tg.setUrl(response.getString("response"));
										
					Log.i("tantest","URL: "+tg.getUrl()); 
					
				}
				catch(Exception e) 
				{
					//Log.i("tantest","e: "+e);
				}

				//HTML END		
				
				TestQuestion tq;
				
				for(int q=0; q < state.size(); q++ )
				{
					tq = test.getQuestions().get(q);
					
					/*Creamos answer container*/
					if(tq instanceof TestQuestionRadio)
					{
						
						RadioGroup rg = (RadioGroup) ((LinearLayout)state.get(q).getChildAt(0)).getChildAt(1);
						String solution = (String) tq.getSolution().getSolutionADT();
						RadioButton rb;
						
						for(int k=0; k<4; k++)
						{
							rb = (RadioButton) rg.getChildAt(k);
							
							if(rb.getText().toString().equalsIgnoreCase(solution))
							{
								rb.setTextColor(Color.CYAN);
							}
							else
							{
								rb.setTextColor(Color.RED);
							}
							
							rb.setEnabled(false);
							
						}
						
						continue;
					}
					
					if(tq instanceof TestQuestionCheckBox)
					{
						
						LinearLayout container = (LinearLayout) ((LinearLayout)state.get(q).getChildAt(0)).getChildAt(1);
						@SuppressWarnings("unchecked")
						Set<String> solution = (Set<String>) tq.getSolution().getSolutionADT();
						CheckBox cb;
						
						for(int k=0; k<4; k++)
						{
							cb = (CheckBox) container.getChildAt(k);
							
							if(solution.contains(cb.getText().toString()))
							{
								cb.setTextColor(Color.CYAN);
							}
							else
							{
								cb.setTextColor(Color.RED);
							}
							
							cb.setEnabled(false);
							
						}
					}
				}
				
				
				if(tg.getPoints() > 0)
				{
					//Actualizar Base de datos con los puntos que ha acumulado
				}
				
				Message msg = new Message();
				msg.what = 1;
				msg.obj = tg;
				handler.sendMessage(msg);
		    }
		}).start();
	}
	
    @Override
    public void onResume()
    {
    	super.onResume();
    	
    	if(testGraded)
    	{
    		
    		View v = getSupportActionBar().getCustomView();
			TextView calificationTxt = (TextView) v.findViewById(R.id.main_actionbar_textTitle);
			calificationTxt.setTypeface(fontText);
			calificationTxt.setText(getString(R.string.act_test_text5)+" "+String.valueOf(calification));
    		
			progress.setVisibility(View.GONE);
    		((RelativeLayout) findViewById(R.id.act_test_nav)).setVisibility(View.VISIBLE);
    		questionsContainer.setVisibility(View.VISIBLE);
    		firstQuestion(null);
    	}
    	
    }
	
	public class TestActivityHandler extends Handler 
	{
        private TestActivity parent;

        public TestActivityHandler(TestActivity parent) 
        {
            this.parent = parent;
        }

        public void handleMessage(Message msg) 
        {
            parent.handleMessage(msg);
        }
    }
	
	public void handleMessage(Message msg) 
	{
        switch(msg.what) 
        {
        	case 0:
        		startTest((Integer) msg.obj);
        		break;
        	case 1:
        		
        		TestGrade tg = (TestGrade) msg.obj;
        		calification = tg.getCalification();

        		Intent intent = new Intent(this, TestGradeActivity.class);

				try 
				{
					Bundle bundle = new Bundle();
					bundle.putString("grade", HttpUtil.toString(tg));
					intent.putExtras(bundle);
				} 
				catch (IOException e) 
				{
					
				}
				testGraded = true;
				startActivityForResult(intent,0);
        		break;
        	case 10:
        		progress.setVisibility(View.GONE);
        		ifError(getString(R.string.act_test_text2));
        		finish();
        		break;
        	case 11:
        		progress.setVisibility(View.GONE);
        		ifError(getString(R.string.act_test_text3));
        		finish();
        		break;
            default:
            	break;
        }
    }
	
	public void ifError(String txt)
	{
		Toast.makeText(TestActivity.this, txt, Toast.LENGTH_SHORT).show();
	}
	
	public TestActivityHandler handler;

}
