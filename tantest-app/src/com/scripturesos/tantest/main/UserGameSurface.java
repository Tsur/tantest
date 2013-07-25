package com.scripturesos.tantest.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class UserGameSurface extends SurfaceView implements SurfaceHolder.Callback 
{

	UserGameThread thread;
	
	public UserGameSurface(Context context) 
	{
		super(context);
		 
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new UserGameThread(holder);
        
		setFocusable(true);
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
		//private static final int SPEED = 2;
		private boolean run = false;

		private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		public UserGameThread(SurfaceHolder surfaceHolder) 
		{
			 sh = surfaceHolder;
			 paint.setColor(Color.rgb(46, 203, 237));
			 paint.setStyle(Paint.Style.STROKE);
			 paint.setStrokeWidth(1);
		}
		  
		public void doStart() 
		{
			synchronized(sh) 
			{
				offset = canvasWidth;
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
		
		public void setSurfaceSize(int width, int height) 
		{
			synchronized (sh) 
			{
				canvasWidth = width;
				canvasHeight = height;
				doStart();
			}
		}
		
		private void doDraw(Canvas canvas) 
		{
			
			try 
			{
				Path p = new Path();
		        
				p.moveTo(0,  (float) (100+(10 *(Math.sin((float)(1+offset)*1/35))))); 
				
		        for (int i=1; i<canvasWidth; i++) 
				{
					p.lineTo(i, (float) (100+(10 *(Math.sin((float)(i+offset)*1/35))))); 
				}

				canvas.restore();
				canvas.drawColor(Color.BLACK);
				//canvas.drawColor(Color.BLACK);
				canvas.drawPath(p, paint);
				
				offset -= 2;
				
				/*if(offset <= 0)
				{
					offset += 2;
				}
				else
				{
					
				}*/
				
				UserGameThread.sleep(50);
			} 
			catch (InterruptedException e) 
			{
				
			}
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
