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
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import com.scripturesos.tantest.main.UsersActivity.UsersActivityHandler;

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
	
	public void setHandler(UsersActivityHandler uah)
	{
		thread.usersActHandler = uah;
	}
	
	public UserGameThread getThread() 
	{
	    return thread;
	}
	
	public void surfaceCreated(SurfaceHolder holder) 
	{
	    if(!thread.getRunning())
	    {
	    	thread.setRunning(true);
	    	thread.start();
	    }
		
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
	

	public boolean onTouch(View v, MotionEvent event) 
	{
		//Log.d("tantest", "Touch");
		
		thread.detectTouch(event);
	    
	    return super.onTouchEvent(event);
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
		private Rect player;
		private Rect fishrect;
		private Random rand = new Random();
		public UsersActivityHandler usersActHandler;
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
			 boat = BitmapFactory.decodeResource(getResources(), R.drawable.users_boatc);
			 boat_bind = BitmapFactory.decodeResource(getResources(), R.drawable.users_boat_bind2);
			 boat_info = BitmapFactory.decodeResource(getResources(), R.drawable.users_game_bubble);
			 boat_stone1 = BitmapFactory.decodeResource(getResources(), R.drawable.users_game_stonel);
			 boat_stone2 = BitmapFactory.decodeResource(getResources(), R.drawable.users_game_stonec);
			 boat_stone3 = BitmapFactory.decodeResource(getResources(), R.drawable.users_game_stoner);
			 boat_fish = BitmapFactory.decodeResource(getResources(), R.drawable.users_game_fishr2);
			 rand.setSeed(10000);
		}
		  
		public void doStart() 
		{
			synchronized(sh) 
			{
				offset = canvasWidth;
				boatX = (canvasWidth/2)-36;
				boat_bindY = (canvasHeight/2)-24;
				offsetY = 0;
				
				/*Random r = new Random();
				for(int i=0;i<=2;i++)
				{
					FishGame afish = new FishGame();
					afish.setY(r.nextInt(canvasHeight-FishGame.height - 115 + 1) + 115);
					//afish.setVx((int) (Math.random() * 5));
					afish.setMaxW(canvasWidth);
					fish.add(afish);
				}*/

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
						/*(new Thread() {
						    
							public void run() 
							{
								
								
								try
								{
									sleep(rand.nextInt(300)+200);
								} 
								catch (InterruptedException e) 
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						    }
						}).start();*/
						
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
						if(!contactFound)
						{
							doDraw(c);
						}
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
		
		public boolean getRunning() 
		{ 
			return run;
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
				doStart();
			}
		}
		
		public void restart()
		{
			doStart();
			contactFound = false;
		}
		
		private void doDraw(Canvas canvas) 
		{
			
			try 
			{
				//int saveCount = canvas.getSaveCount();
			    
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
		        
		        //canvas.restore();
				canvas.drawColor(Color.BLACK);
				canvas.drawBitmap(boat, boatX, boatY, null);
				canvas.drawBitmap(boat_bind, boatX, boat_bindY, null);
				
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
				
				canvas.drawBitmap(boat_info, boatX+72, boatY-25, null);
				canvas.drawBitmap(boat_stone1, 0, canvasHeight-50, null);
				canvas.drawBitmap(boat_stone2, (canvasWidth/2)-35, canvasHeight-30, null);
				canvas.drawBitmap(boat_stone3, canvasWidth-100, canvasHeight-50, null);
				
				//canvas.save();
				
				player = new Rect(boatX+40, boat_bindY+15,boatX+44,boat_bindY+33);
				
				for(int f=0;f<fish.size();f++)
				{
					FishGame afish = fish.get(f);
					
					if(afish.ismarked())
					{
						fish.remove(f);

						continue;
					}
					else
					{
						afish.update();
					}
					
					fishrect = new Rect(afish.x, afish.y,afish.x+FishGame.width,afish.y+FishGame.height);

					if(Rect.intersects(player,fishrect))
					{
						contactFound = true;
						fish.clear();
						//canvas.restoreToCount(saveCount);
						//canvas.drawColor(Color.BLACK);
						
						Bitmap bg = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.RGB_565);
						Canvas tempCanvas = new Canvas(bg);
						
						makeBG(tempCanvas);
						
						Message msg = new Message();
						msg.what = 0;
						msg.obj = new BitmapDrawable(bg);
						usersActHandler.sendMessage(msg);
						//invalidate();
						return;
					}
					
					canvas.drawBitmap(boat_fish, afish.x, afish.y, null);
				}

				UserGameThread.sleep(20);
				
				if(fish.size()<3 && rand.nextInt(1000)<50)
				{
					Log.i("tantest", "Length: "+ fish.size());
					FishGame afish = new FishGame();
					afish.setY(rand.nextInt(canvasHeight-FishGame.height - 115 + 1) + 115);
					afish.setVx(rand.nextInt(5)+1);
					afish.setMaxW(canvasWidth);
					fish.add(afish);
				}
			} 
			catch (InterruptedException e) 
			{
				
			}
		}
		
		private void makeBG(Canvas a)
		{
			Path p = new Path();
	        
			p.moveTo(0,  (float) (110+offsetY+(10 *(Math.sin((float)(1+offset)*1/35))))); 
			
	        for (int i=1; i<canvasWidth; i++) 
			{
				p.lineTo(i, (float) (110+offsetY+(10 *(Math.sin((float)(i+offset)*1/35))))); 
			}
			
			Path p2 = new Path();
	        
			p2.moveTo(boatX+36, boatY+50);
			
			p2.lineTo(boatX+36, boat_bindY); 
	        
	        //canvas.restore();
			a.drawColor(Color.BLACK);
			a.drawBitmap(boat, boatX, boatY, null);
			a.drawBitmap(boat_bind, boatX, boat_bindY, null);
			
			//canvas.drawColor(Color.BLACK);
			paint.setColor(Color.rgb(46, 203, 237));
			paint.setStrokeWidth(3);
			a.drawPath(p, paint);
			paint.setColor(Color.rgb(177, 177, 177));
			paint.setStrokeWidth(1);
	        a.drawPath(p2, paint);
			
			a.drawBitmap(boat_info, boatX+72, boatY-25, null);
			a.drawBitmap(boat_stone1, 0, canvasHeight-50, null);
			a.drawBitmap(boat_stone2, (canvasWidth/2)-35, canvasHeight-30, null);
			a.drawBitmap(boat_stone3, canvasWidth-100, canvasHeight-50, null);
			
		}
	}
	
	class FishGame 
	{
		static final int width = 30;
		static final int height = 20;
		int x = -width;
		int y = 0;
		int speed = 2;
		int starting = 0;
		private Random r = new Random();
		int maxWidth;
		int linearY = 0;
		int linearDir = 0;
		
		public FishGame(){}
		
		public void setX(int x)
		{
			this.x = x;
		}
		
		public void setY(int y)
		{
			this.y = y;
		}
		
		public void setVx(int x)
		{
			this.speed = x;
		}
		
		public void setMaxW(int w)
		{
			this.maxWidth = w;
		}
		
		public void update()
		{
			x += speed;

			if(y<=120)
			{
				y += r.nextInt(3);
			}
			else if(linearY == 0)
			{
				linearY = r.nextInt(12);
				linearDir = r.nextInt(2);
			}
			else
			{
				y = (linearDir == 0) ? y+1:y-1;
				//y += 1;
				linearY--;
			}
		}
		
		public boolean ismarked()
		{
			if(x >= maxWidth) return true;
			return false;
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
