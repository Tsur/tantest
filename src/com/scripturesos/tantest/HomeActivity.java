package com.scripturesos.tantest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class HomeActivity extends ActionBarActivity {

	private ProgressBar loader;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		/* INIT CONTENT VIEW */
		setContentView(R.layout.activity_home);
		
		loader = (ProgressBar) findViewById(R.id.home_progressbar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_home, menu);
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
		
		loader.setVisibility(View.VISIBLE);
		loader.setIndeterminate(true);
		
		switch(item.getItemId())
		{
			case R.id.menu_header_test:
				
				//Mostramos ajax cargando
				
				Log.i("tantes","creando actividad");

				
				//RelativeLayout rl = (RelativeLayout) findViewById(R.id.main_logoimg);
				//loader = new MYGIFView(this);
				//rl.addView(loader);
				
				Intent intent = new Intent(this, TestActivity.class);
				Log.i("tantes","iniciando actividad");
				startActivity(intent);
				

				break;
			case R.id.menu_header_social:
				Intent cintent = new Intent(this, ContactsActivity.class);
				Log.i("tantes","iniciando actividad");
				startActivity(cintent);
				break;
			case R.id.menu_settings:

				break;
			default:
		}
		
		return true;
	}
	
	/*private class MYGIFView extends View{

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
	}*/

}
