package com.scripturesos.tantest;

import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity {

	private View loader;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		/* ACTION BAR CONFIGURATION */
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);

		LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = (View) inflator.inflate(R.layout.main_actionbar_title, null);
		
		Typeface fontTitle = Typeface.createFromAsset(getAssets(), "fuentes/kg.ttf");
		TextView title = (TextView)v.findViewById(R.id.main_actionbar_textTitle);
		title.setText(getTitle());
		title.setTypeface(fontTitle);

		//assign the view to the actionbar
		getSupportActionBar().setCustomView(v);
		
		/* INIT CONTENT VIEW */
		setContentView(R.layout.activity_main);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	

    @Override
    public void onResume()
    {
    	if(loader != null)
		{
    		loader.setVisibility(View.GONE);
		}
    	
    	super.onResume();
    }
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		
		
		switch(item.getItemId())
		{
			case R.id.menu_header_test:
				
				//Mostramos ajax cargando
				
				Log.i("tantes","creando actividad");
				
				RelativeLayout rl = (RelativeLayout) findViewById(R.id.main_logoimg);
				loader = new MYGIFView(this);
				rl.addView(loader);
				
				Intent intent = new Intent(this, TestActivity.class);
				Log.i("tantes","iniciando actividad");
				startActivity(intent);
				

				break;
			case R.id.menu_header_social:
				
				break;
			case R.id.menu_settings:

				break;
			default:
		}
		
		return true;
	}
	
	private class MYGIFView extends View{

		Movie movie;
		InputStream is=null;
		long moviestart;
		
		public MYGIFView(Context context)
		{
			super(context);

			is=context.getResources().openRawResource(R.drawable.loader);
			movie=Movie.decodeStream(is);
		}

		@Override
		protected void onDraw(Canvas canvas)
		{

			canvas.drawColor(Color.TRANSPARENT);
			super.onDraw(canvas);
			long now = android.os.SystemClock.uptimeMillis();

			if(moviestart == 0)
			{ 
				// first time
				moviestart = now;
			}
			
			int relTime = (int)((now - moviestart) % movie.duration()) ;
		
			movie.setTime(relTime);
			movie.draw(canvas,this.getWidth()/2-11,this.getHeight()/2+27);
			this.invalidate();
		}
	}

}
