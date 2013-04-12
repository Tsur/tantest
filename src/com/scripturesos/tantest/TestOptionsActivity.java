package com.scripturesos.tantest;

import java.io.IOException;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.scripturesos.tantest.connection.HttpUtil;
import com.scripturesos.tantest.test.TestConfiguration;

public class TestOptionsActivity extends Application {
	
	private final int MAX_NUM_QUESTIONS = 40;
	private final int MAX_TIME = 60;
	
	private SeekBar sb1;
	private TextView sb1_text;
	
	private SeekBar sb2;
	private TextView sb2_text;
	
	private TextView difficulty_text;
	private int[] difficulty_levels = new int[]{R.string.act_test_opt_text5,R.string.act_test_opt_text6,R.string.act_test_opt_text7,R.string.act_test_opt_text8};
	private int difficulty_level = 0;
	
	private TextView category_text;
	private int[] category_levels = new int[]{R.string.act_test_opt_text9,R.string.act_test_opt_text10,R.string.act_test_opt_text11,R.string.act_test_opt_text12,R.string.act_test_opt_text13};
	private int category_level = 0;
	
	private LongPressRecursiveButtton b1;
	private LongPressRecursiveButtton b2;
	private LongPressRecursiveButtton b3;
	private LongPressRecursiveButtton b4;
	private boolean longPressed;
	private Handler mHandler = new Handler();
	private final Runnable b1Runnable = new Runnable() {
        
		public void run() 
        {
            if(longPressed) 
            {
            	minorNumQuestions(null);
                mHandler.postDelayed(this, 100);
            }
        }
    };
    private final Runnable b2Runnable = new Runnable() {
        
		public void run() 
        {
            if(longPressed) 
            {
            	plusNumQuestions(null);
                mHandler.postDelayed(this, 100);
            }
        }
    };
    private final Runnable b3Runnable = new Runnable() {
        
 		public void run() 
         {
             if(longPressed) 
             {
             	 minorTime(null);
                 mHandler.postDelayed(this, 100);
             }
         }
     };
     private final Runnable b4Runnable = new Runnable() {
         
 		public void run() 
         {
             if(longPressed) 
             {
             	 plusTime(null);
                 mHandler.postDelayed(this, 100);
             }
         }
     };
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTitle(R.string.act_test_opt_text14);
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_test_options);
		
		Typeface labelFontTitle = Typeface.createFromAsset(getAssets(), "fuentes/cdbold.ttf");
		
		sb1 = (SeekBar) findViewById(R.id.act_test_opt1_b2);
		sb1_text = (TextView) findViewById(R.id.act_test_opt1_value);
		sb1.setMax(MAX_NUM_QUESTIONS-5);
		sb1.incrementProgressBy(1);
		sb1.setProgress(0);
		sb1_text.setTypeface(labelFontTitle);
		sb1_text.setText(String.valueOf(sb1.getProgress()+5));
		sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		        
		    	sb1_text.setText(String.valueOf(progress+5));
		    }

		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) {}

		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) {}
		});
		
		sb2 = (SeekBar) findViewById(R.id.act_test_opt2_b2);
		sb2_text = (TextView) findViewById(R.id.act_test_opt2_value);
		sb2.setMax(MAX_TIME);
		sb2.incrementProgressBy(1);
		sb2.setProgress(0);
		sb2_text.setTypeface(labelFontTitle);
		sb2_text.setText(R.string.act_test_opt_text17);
		sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		        
		    	if(progress == 0)
		    	{
		    		sb2_text.setText(R.string.act_test_opt_text17);
		    	}
		    	else
		    	{
		    		sb2_text.setText(String.valueOf(progress));
		    	}
		    	
		    }

		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) {}

		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) { }
		});
		
		difficulty_text = (TextView) findViewById(R.id.act_test_opt3_value);
		difficulty_text.setText(difficulty_levels[difficulty_level]);
		difficulty_text.setTypeface(labelFontTitle);
		
		category_text = (TextView) findViewById(R.id.act_test_opt4_value);
		category_text.setText(category_levels[category_level]);
		category_text.setTypeface(labelFontTitle);
		
		((TextView) findViewById(R.id.act_test_opt1_text)).setTypeface(labelFontTitle);
		((TextView) findViewById(R.id.act_test_opt2_text)).setTypeface(labelFontTitle);
		((TextView) findViewById(R.id.act_test_opt3_text)).setTypeface(labelFontTitle);
		((TextView) findViewById(R.id.act_test_opt4_text)).setTypeface(labelFontTitle);

		b1 = (LongPressRecursiveButtton) findViewById(R.id.act_test_opt1_b1);
		b1.setOnLongClickListener(new OnLongClickListener() 
		{ 
	        
			@Override
	        public boolean onLongClick(View v) 
			{
				longPressed = true;
                mHandler.post(b1Runnable);
                return true;
	        }
		});
		b1.setSampleLongpress(this);
		
		b2 = (LongPressRecursiveButtton) findViewById(R.id.act_test_opt1_b3);
		b2.setOnLongClickListener(new OnLongClickListener() 
		{ 
	        
			@Override
	        public boolean onLongClick(View v) 
			{
				longPressed = true;
                mHandler.post(b2Runnable);
                return true;
	        }
		});
		b2.setSampleLongpress(this);
		
		b3 = (LongPressRecursiveButtton) findViewById(R.id.act_test_opt2_b1);
		b3.setOnLongClickListener(new OnLongClickListener() 
		{ 
	        
			@Override
	        public boolean onLongClick(View v) 
			{
				longPressed = true;
                mHandler.post(b3Runnable);
                return true;
	        }
		});
		b3.setSampleLongpress(this);
		
		b4 = (LongPressRecursiveButtton) findViewById(R.id.act_test_opt2_b3);
		b4.setOnLongClickListener(new OnLongClickListener() 
		{ 
	        
			@Override
	        public boolean onLongClick(View v) 
			{
				longPressed = true;
                mHandler.post(b4Runnable);
                return true;
	        }
		});
		b4.setSampleLongpress(this);
	
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_test_options, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		
		
		switch(item.getItemId())
		{
			case R.id.act_test_opt_i2:
				
				
				
				Intent mIntent = new Intent();
			    
			    Bundle bundle = new Bundle();
				
				try 
				{
					TestConfiguration tc = null;
					
					if(sb2.getProgress() != 0)
					{
						tc = new TestConfiguration(Integer.parseInt(sb1_text.getText().toString()), Integer.parseInt(sb2_text.getText().toString()), difficulty_level, category_level);
					}
					else
					{
						tc = new TestConfiguration(Integer.parseInt(sb1_text.getText().toString()), 0, difficulty_level, category_level);
					}
					
					String conf = HttpUtil.toString(tc);
					bundle.putString("configuration", conf);
					
					//Guardamos en BD para luego pasarlo directamente a Test dependiendo de las preferencias del usuario
				} 
				catch (IOException e) 
				{

				}
				
				mIntent.putExtras(bundle);
			    
			    setResult(RESULT_OK, mIntent);
			    
			    finish();

				break;
			default:
		}
		
		return true;
	}
	
	public void cancelLongPress() 
	{
        longPressed = false;
    }
	
	public void minorNumQuestions(View view)
	{

		int progress = sb1.getProgress();
		
		if(progress == 0)
		{
			progress = MAX_NUM_QUESTIONS -5;
		}
		else
		{
			progress--;
		}
		
		sb1.setProgress(progress);
		sb1_text.setText(String.valueOf(progress+5));

	}

	
	public void plusNumQuestions(View view)
	{

		int progress = sb1.getProgress();
		
		if(progress == MAX_NUM_QUESTIONS-5)
		{
			progress = 0;
		}
		else
		{
			progress++;
		}
		
		sb1.setProgress(progress);
		sb1_text.setText(String.valueOf(progress+5));

	}
	
	public void minorTime(View view)
	{

		int progress = sb2.getProgress();
		
		if(progress == 0)
		{
			progress = MAX_TIME;
		}
		else if(progress == 1)
		{
			sb2.setProgress(0);
			sb2_text.setText(R.string.act_test_opt_text17);
			return;
		}
		else
		{
			progress--;
		}
		
		sb2.setProgress(progress);
		sb2_text.setText(String.valueOf(progress));
	}
	
	public void plusTime(View view)
	{

		int progress = sb2.getProgress();
		
		if(progress >= MAX_TIME)
		{
			sb2.setProgress(0);
			sb2_text.setText(R.string.act_test_opt_text17);
			return;
		}

		progress++;
		
		sb2.setProgress(progress);
		sb2_text.setText(String.valueOf(progress));
	}
	
	public void changeDifficultyUp(View view)
	{
		((ImageButton) findViewById(R.id.act_test_opt3_b1)).setEnabled(true);
		
		if(difficulty_level == difficulty_levels.length-1)
		{
			view.setEnabled(false);
			return;
		}
		else
		{
			difficulty_level++;
		}

		
		difficulty_text.setText(difficulty_levels[difficulty_level]);
	}
	
	public void changeDifficultyDown(View view)
	{
		((ImageButton) findViewById(R.id.act_test_opt3_b3)).setEnabled(true);
		
		if(difficulty_level == 0)
		{   
			view.setEnabled(false);
			return;
		}
		else
		{
			difficulty_level--;
		}

		
		difficulty_text.setText(difficulty_levels[difficulty_level]);
	}
	
	public void changeCategoryUp(View view)
	{

		if(category_level == category_levels.length-1)
		{
			category_level = 0;
		}
		else
		{
			category_level++;
		}

		
		category_text.setText(category_levels[category_level]);
	}
	
	public void changeCategoryDown(View view)
	{

		if(category_level == 0)
		{
			category_level = category_levels.length-1;
		}
		else
		{
			category_level--;
		}

		
		category_text.setText(category_levels[category_level]);
	}
	
}
