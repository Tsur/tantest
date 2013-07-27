package com.scripturesos.tantest.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class UserGameSurface extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener  
{

	UserGameThread thread;
	
	public UserGameSurface(Context context) 
	{
		super(context);
		 
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new UserGameThread(holder);
        
		setFocusable(true);
		setOnTouchListener((OnTouchListener) this);
		setFocusableInTouchMode(true);
	}
	
	public UserGameThread getThread() 
	{
	    return thread;
	}
	
	public void surfaceCreated(SurfaceHolder holder) 
	{
	    thread.setRunning(true);
	    thread.start();
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) 
	{
		thread.setSurfaceSize(width, height);
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		 boolean retry = true;
		 
		 thread.setRunning(false);
		 
		 while (retry) 
		 {
			 try 
			 {
		        //Wait for thread to finish
				thread.join();
		        retry = false;
		     } 
			 catch(InterruptedException e)
			 {
				 
		     }
		}

		Log.i("tantest","Thread destroyed");
	}
	
	class UserGameThread extends Thread 
	{
		private SurfaceHolder sh;
		private int canvasWidth;
		private int canvasHeight;
		private int offset;
		private double offsetY;
		private Bitmap boat;
		private Bitmap boat_bind;
		private Bitmap boat_info;
		private Bitmap boat_stone1;
		private Bitmap boat_stone2;
		private Bitmap boat_stone3;
		private Bitmap boat_fish;
		private List<FishGame> fish = new ArrayList<FishGame>();
		private boolean contactFound = false;
		private int boatX;
		private int boatY;
		private int boat_bindY;
		//Matrix matrix = new Matrix();
		
		//private static final int SPEED = 2;
		private boolean run = false;

		private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		public UserGameThread(SurfaceHolder surfaceHolder) 
		{
			 sh = surfaceHolder;
			 paint.setColor(Color.rgb(46, 203, 237));
			 paint.setStyle(Paint.Style.STROKE);
			 paint.setStrokeWidth(1);
			 boat = BitmapFactory.decodeResource(getResources(), R.drawable.users_boat);
			 boat_bind = BitmapFactory.decodeResource(getResources(), R.drawable.users_boat_bind2);
			 boat_info = BitmapFactory.decodeResource(getResources(), R.drawable.users_game_bubble);
			 boat_stone1 = BitmapFactory.decodeResource(getResources(), R.drawable.users_game_stonel);
			 boat_stone2 = BitmapFactory.decodeResource(getResources(), R.drawable.users_game_stonec);
			 boat_stone3 = BitmapFactory.decodeResource(getResources(), R.drawable.users_game_stoner);
			 boat_fish = BitmapFactory.decodeResource(getResources(), R.drawable.users_game_fishr);
		}
		  
		public void doStart() 
		{
			synchronized(sh) 
			{
				offset = canvasWidth;
				boatX = (canvasWidth/2)-36;
				boat_bindY = (canvasHeight/2)-24;
				offsetY = 0;
				Canvas c = null;
				
				try 
				{
					c = sh.lockCanvas(null);
					
					if(c != null)
					{
						/*Draw Initial Wave sea*/
						/*Path p = new Path();
				         
				        for (int i=0; i<canvasWidth; i++) 
				        {
				        	p.lineTo(i, (float) (50 * Math.sin(i))); 
				        }

						c.restore();
						//canvas.drawColor(Color.BLACK);
						c.drawPath(p, paint);*/
					}
					
				} 
				finally 
				{
					if(c != null) 
					{
						sh.unlockCanvasAndPost(c);
					}
				}
			}
		}
		
		public void run() 
		{
			while(run) 
		    {
				Canvas c = null;
				
				try 
				{
					c = sh.lockCanvas(null);
					
					synchronized(sh) 
					{
						doDraw(c);
					}
				} 
				finally 
				{
					if(c != null) 
					{
						sh.unlockCanvasAndPost(c);
					}
				}
		    }
		}
		    
		public void setRunning(boolean b) 
		{ 
			run = b;
		}
		
		public void moveBoat(int w) 
		{ 
			boatX += w;
		}
		
		public void detectTouch(MotionEvent event)
		{
			//Log.d("tantest", "TX: " + event.getX()); 
			//Log.d("tantest", "TX: " + event.getY());
			int y = (int) event.getY();
			float c = canvasWidth/2;
			if(y < boatY+100 || y >= canvasHeight-40 || event.getX() < c-36 || event.getX() > c+36)
			{
				return;
			}
			
			boat_bindY = y-36;
		}
       
		public void setSurfaceSize(int width, int height) 
		{
			synchronized (sh) 
			{
				canvasWidth = width;
				canvasHeight = height;
				fish.add(new FishGame(120, canvasHeight-20,canvasWidth));
				fish.add(new FishGame(120, canvasHeight-20,canvasWidth,10));
				fish.add(new FishGame(120, canvasHeight-20,canvasWidth,3));
				doStart();
			}
		}
		
		private void doDraw(Canvas canvas) 
		{
			
			try 
			{
				if(contactFound)
				{
					return;
				}
				
				Path p = new Path();
		        
				p.moveTo(0,  (float) (110+offsetY+(10 *(Math.sin((float)(1+offset)*1/35))))); 
				
		        for (int i=1; i<canvasWidth; i++) 
				{
					p.lineTo(i, (float) (110+offsetY+(10 *(Math.sin((float)(i+offset)*1/35))))); 
				}
				
				boatY = ((int) (110+(10 *(Math.sin((float)((canvasWidth/2)+offset)*1/35)))))-69;
				//boatY = ((int) (110+offsetY+(10 *(Math.sin((float)((canvasWidth/2)+offset)*1/35)))))-70;
				
				Path p2 = new Path();
		        
				p2.moveTo(boatX+36, boatY+50);
				
				p2.lineTo(boatX+36, boat_bindY); 
		        
		        canvas.restore();
				canvas.drawColor(Color.BLACK);
				canvas.drawBitmap(boat, boatX, boatY, paint);
				canvas.drawBitmap(boat_bind, boatX, boat_bindY, paint);
				
				//canvas.drawColor(Color.BLACK);
				paint.setColor(Color.rgb(46, 203, 237));
				paint.setStrokeWidth(3);
				canvas.drawPath(p, paint);
				paint.setColor(Color.rgb(177, 177, 177));
				paint.setStrokeWidth(1);
		        canvas.drawPath(p2, paint);

				offset -= 2;
				
				offsetY = Math.random();
				
				if(Math.random() <= 0.5)
				{
					offsetY = -offsetY;
				}
				
				canvas.drawBitmap(boat_info, boatX+72, boatY-25, paint);
				canvas.drawBitmap(boat_stone1, 0, canvasHeight-50, paint);
				canvas.drawBitmap(boat_stone2, (canvasWidth/2)-35, canvasHeight-30, paint);
				canvas.drawBitmap(boat_stone3, canvasWidth-100, canvasHeight-50, paint);
				
				for(FishGame afish:fish)
				{
					if(!afish.update( boatX, boat_bindY))
					{
						contactFound = true;
						return;
					}
					
					canvas.drawBitmap(boat_fish, afish.x, afish.y, paint);
				}

				
				
				UserGameThread.sleep(20);
			} 
			catch (InterruptedException e) 
			{
				
			}
		}
	}

	public boolean onTouch(View v, MotionEvent event) 
	{
		Log.d("tantest", "Touch");
		
		thread.detectTouch(event);
	    
	    return super.onTouchEvent(event);
	}
	
	class FishGame 
	{
		static final int width = 30;
		static final int height = 20;
		int x = -width;
		int y = 0;
		int speed = 3;
		int starting = 0;
		
		int max;
		int min;
		int maxWidth;
		
		public FishGame(int mi,int ma, int mw)
		{
			//bottomLimit = canvasHeight-20;
			//topLimit = 120;
			Random rand = new Random();

			// nextInt is normally exclusive of the top value,
			// so add 1 to make it inclusive
			y = rand.nextInt(ma - mi + 1) + mi;
			
			max = ma;
			min = mi;
			maxWidth = mw;
			
			//y = (int) (Math.random()*(bottomLimit-topLimit)) + topLimit;
			
		}
		
		public FishGame(int mi,int ma, int mw, int s)
		{
			
			Random rand = new Random();
			y = rand.nextInt(ma - mi + 1) + mi;
			
			max = ma;
			min = mi;
			maxWidth = mw;
			speed = s;
		}
		
		public boolean update(int netX, int netY)
		{
			
			/*if(((x-netX)^2+(y-netY)^2)<=((15+32)^2))
			{
				return false;
			}*/
			
			x += speed;
			
			if(x >= maxWidth)
			{
				x=-width;
				Random rand = new Random();
				y = rand.nextInt(max - min + 1) + min;
				//starting = ;
			}
			
			return true;
		}
	}
}
//int halfX = (int) maxX / 2;
//int halfY = (int) maxY / 2;

/**/

/*
int getNormalizedSine(int x, int halfY, int maxX) 
{
    double piDouble = 2 * Math.PI;
    double factor = piDouble / maxX;
    return (int) (Math.sin(x * factor) * halfY + halfY);
}
*/
