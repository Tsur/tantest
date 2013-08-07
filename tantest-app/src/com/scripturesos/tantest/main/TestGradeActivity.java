package com.scripturesos.tantest.main;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.scripturesos.tantest.connection.HttpUtil;
import com.scripturesos.tantest.connection.MessageCallback;
import com.scripturesos.tantest.test.TestGrade;

public class TestGradeActivity extends Application {

	private TestGrade tg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
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
		    
		    (new Thread(){
			    
				public void run() 
				{
					HomeActivity.server.emit("CHAT_HAS_CHANGED", "2", String.valueOf(tg.getPoints()));
					
			    }
				
			}).start();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_test_grade, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		
		
		switch(item.getItemId())
		{
			case R.id.act_test_grade_menu_chat:
				//Busca contacto y entonces abre chat
				//Intent tintent = new Intent(this, ContactsActivity.class);
				//startActivityForResult(tintent,0);
				break;

			case R.id.act_test_grade_menu_share:
				
				(new Thread(){
				    
					public void run() 
					{
						try 
						{
							JSONObject response = HttpUtil.post(HttpUtil.REGISTER_TEST, new String[]{tg.getHTML()});
						
							tg.setUrl(response.getString("response"));
							
							//tg.setUrl(response.getString("response"));
							Intent i=new Intent(android.content.Intent.ACTION_SEND);
							i.setType("text/plain");
							i.putExtra(Intent.EXTRA_SUBJECT, R.string.act_test_grade_text21);
							i.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.act_test_grade_text22),tg.getCalification(),tg.getUrl()));
							startActivity(i);
						} 
						catch (Exception e) 
						{
							Log.i("tantest","error generando test");
						}
					}
					
				}).start();
				
				
				break;
				
			default:break;
		}
		
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

            	if(b.containsKey("chat"))
            	{
            		Intent mIntent = new Intent();
    			    
    			    Bundle bundle = new Bundle();
    				
    				bundle.putString("chat", b.getString("chat"));
    				
    				if(tg.getRealTime() != null)
    				{
    					bundle.putString("test", String.format(getString(R.string.act_test_grade_text24),tg.getCalification(),tg.getTotalQuestions(), tg.numQuestionsOK(), (tg.getTotalQuestions()-tg.numQuestionsOK()),getString(tg.getDifficulty()),tg.getTime(),tg.getRealTime()));
    				}
    				else
    				{
    					bundle.putString("test", String.format(getString(R.string.act_test_grade_text26),tg.getCalification(),tg.getTotalQuestions(), tg.numQuestionsOK(), (tg.getTotalQuestions()-tg.numQuestionsOK()),getString(tg.getDifficulty()),tg.getTime()));
    				}
    				
    				mIntent.putExtras(bundle);
    			    
    			    setResult(RESULT_OK, mIntent);
    			    
    			    finish();
            		
            	}

            }
            break;
               
        }
        
    }
	
	private void showResults()
	{
		TextView title = (TextView) findViewById(R.id.act_test_grade_text1);
		title.setTypeface(this.fontTitle);
		
		if(tg.getCalification() <= (tg.getBase() * 0.1))
		{
			title.setText(R.string.act_test_grade_text1);
		}
		else if(tg.getCalification() > (tg.getBase() * 0.1) && tg.getCalification() < tg.getBase()/2)
		{
			title.setText(R.string.act_test_grade_text2);
		}
		else if(tg.getCalification() >= tg.getBase()/2 && tg.getCalification() < (tg.getBase() * 0.9))
		{
			title.setText(R.string.act_test_grade_text3);
		}
		else
		{
			title.setText(R.string.act_test_grade_text4);
		}
		
		//Calification
		((TextView) findViewById(R.id.act_test_grade_text2)).setText(getString(R.string.act_test_grade_text5)+" "+String.valueOf(tg.getCalification()));
		((TextView) findViewById(R.id.act_test_grade_text2)).setTypeface(this.fontTitle);
		
		//Total questions
		((TextView) findViewById(R.id.act_test_grade_text3)).setText(getString(R.string.act_test_grade_text6)+" "+String.valueOf(tg.getTotalQuestions()));
		
		//Total questions right
		((TextView) findViewById(R.id.act_test_grade_text4)).setText(getString(R.string.act_test_grade_text7)+" "+String.valueOf(tg.numQuestionsOK()));
		
		//Total questions wrong
		((TextView) findViewById(R.id.act_test_grade_text5)).setText(getString(R.string.act_test_grade_text8)+" "+String.valueOf(tg.getTotalQuestions()-tg.numQuestionsOK()));
		
		//Difficulty level
		
		/*String difficulty = "";
		
		switch(tg.getDifficulty())
		{
			case 0:
				difficulty = getString(R.string.act_test_grade_text14);
				break;
			case 1:
				difficulty = getString(R.string.act_test_grade_text15);
				break;
			case 2:
				difficulty = getString(R.string.act_test_grade_text16);
				break;
			case 3:
				difficulty = getString(R.string.act_test_grade_text17);
				break;
			default:break;
		}*/
		
		((TextView) findViewById(R.id.act_test_grade_text6)).setText(getString(R.string.act_test_grade_text9)+" "+getString(tg.getDifficulty()));
		
		//Time
		if(tg.getTime() > 1)
		{
			((TextView) findViewById(R.id.act_test_grade_text7)).setText(getString(R.string.act_test_grade_text10)+" "+String.valueOf(tg.getTime())+" "+getString(R.string.act_test_grade_text18));
			
		}
		else if(tg.getTime() == 1)
		{
			((TextView) findViewById(R.id.act_test_grade_text7)).setText(getString(R.string.act_test_grade_text10)+" 1 "+getString(R.string.act_test_grade_text23));
			
		}
		else
		{
			((TextView) findViewById(R.id.act_test_grade_text7)).setText(getString(R.string.act_test_grade_text10)+" "+getString(R.string.act_test_opt_text17));
		}
		
		if(tg.getRealTime() != null)
		{
			 TextView realTime = (TextView) findViewById(R.id.act_test_grade_text11);
			 realTime.setText(getString(R.string.act_test_grade_text25)+" "+tg.getRealTime());
			 realTime.setVisibility(View.VISIBLE);
		}
		
		//Calification
		((TextView) findViewById(R.id.act_test_grade_text8)).setText(getString(R.string.act_test_grade_text11)+" "+String.valueOf(tg.getCalification()));
		
		//Base
		((TextView) findViewById(R.id.act_test_grade_text9)).setText(getString(R.string.act_test_grade_text12)+" "+String.valueOf(tg.getBase()));
		
		//Points
		((TextView) findViewById(R.id.act_test_grade_text10)).setText(getString(R.string.act_test_grade_text13)+" "+String.valueOf(tg.getPoints()));
		
	}

}
