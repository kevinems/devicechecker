package smit.com.factorytest;

import java.io.IOException;
import java.util.ArrayList;

import smit.com.util.TouchScreen;
import smit.com.factorytest.R;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class TunchScreenActivity extends Activity
{
	private static final String TAG = "TsCalibrate";
	private static final String SETTING_CALIBRATE = "SETTING_CALIBRATE";
	private static final String CAL0 = "CAL0";
	private static final String CAL1 = "CAL1";
	private static final String CAL2 = "CAL2";
	private static final String CAL3 = "CAL3";
	private static final String CAL4 = "CAL4";
	private static final String CAL5 = "CAL5";
	private static final String CAL6 = "CAL6";
	private static final String VALID = "VALID";
	private static final int ZOOM = 3;
	private static final int MIN_X = 100;
	private static final int MIN_Y = 100;
	
	private TsCalibrateView tc_view;
	private TouchScreen ts;
	private int[] default_cal;
	private int[] perform_cal;
	private int[] original_cal = {1, 0, 0, 0, 1, 0, 1};
	private boolean need_recovery = true;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	
		/* no title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		/* full screen */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		/* screen orientation landscape */
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	
		/* make the screen full bright for this activity */
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = 1.0f;
		getWindow().setAttributes(lp);

		/* set custom view */
		tc_view = new TsCalibrateView(this);
		setContentView(tc_view);
		
		/* touch screen */
		ts = new TouchScreen();
		
		/* read default cal */
		try {
			default_cal = ts.readCalFromHardware();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* reset tc view */
		tc_view.reset();
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == BIND_AUTO_CREATE)
		{
			if(resultCode == RESULT_OK)
			{
				Toast.makeText(getApplicationContext(), R.string.success_tips, Toast.LENGTH_SHORT).show();
				writeCalToConfigFile(perform_cal);
				Log.d(TAG, String.format("write calibrate: [%d, %d, %d, %d, %d, %d, %d]", perform_cal[0],perform_cal[1],perform_cal[2],perform_cal[3],perform_cal[4],perform_cal[5],perform_cal[6]));
				need_recovery = false;
				finish();
			}
			if(resultCode == RESULT_CANCELED)
			{
				tc_view.reset();
				need_recovery = true;
			}
		}
	}
	
	public void writeCalToConfigFile(int[] cal)
	{
        SharedPreferences settings = getSharedPreferences(SETTING_CALIBRATE, android.content.Context.MODE_PRIVATE);

        settings.edit()
        .putBoolean(VALID, false)
        .commit();
        
		settings.edit()
		.putInt(CAL0, cal[0])
		.putInt(CAL1, cal[1])
		.putInt(CAL2, cal[2])
		.putInt(CAL3, cal[3])
		.putInt(CAL4, cal[4])
		.putInt(CAL5, cal[5])
		.putInt(CAL6, cal[6])
		.commit();
		
        settings.edit()
        .putBoolean(VALID, true)
        .commit();
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		tc_view.reset();
	}
	
    @Override
    protected void onStop()
    {
        if(need_recovery)
		{
			try {
				ts.setZoomToHardware(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	try {
				ts.writeCalToHardware(default_cal);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		
		super.onStop();
	}
    
	public class TsCalibrateView extends View
	{
		private static final int FIRST_POSTION_LEFT_TOP = 0;
		private static final int FIRST_POSTION_RIGHT_TOP = 1;
		private static final int FIRST_POSTION_RIGHT_BOTTOM = 2;
		private static final int FIRST_POSTION_LEFT_BOTTOM = 3;
		private static final int FIRST_POSTION_CENTER = 4;
		private static final int SECOND_POSTION_LEFT_TOP = 5;
		private static final int SECOND_POSTION_RIGHT_TOP = 6;
		private static final int SECOND_POSTION_RIGHT_BOTTOM = 7;
		private static final int SECOND_POSTION_LEFT_BOTTOM = 8;
		private static final int SECOND_POSTION_CENTER = 9;
		private int postion = FIRST_POSTION_LEFT_TOP;
		private final ArrayList<Point> fb_value = new ArrayList<Point>();
		private final ArrayList<Point> value1 = new ArrayList<Point>();
		private final ArrayList<Point> value2 = new ArrayList<Point>();
		
		private class calibration extends Object
		{
			public int cal[];
			
			public calibration()
			{
				cal = new int[7];
				cal[1] = cal[2] = cal[3] = cal[5] = 0;
				cal[0] = cal[4] = cal[6] = 1;
			}
			
			public boolean perform_calibration(ArrayList<Point> fb, ArrayList<Point> value)
			{
				int x[] = new int[5];
				int xfb[] = new int[5];
				int y[] = new int[5];
				int yfb[] = new int[5];
				float n, x1, y1, x2, y2, xy, z, zx, zy;
				float det, a, b, c, e, f, i;
				float scaling = (float)65536;
				
				if((fb.size() != 5) && (value.size() != 5))
					return false;

				for(int j=0; j<5; j++)
				{
					x[j] = value.get(j).x;
					y[j] = value.get(j).y;
					
					xfb[j] = fb.get(j).x;
					yfb[j] = fb.get(j).y;
				}

				/* get sums for matrix */
				n = x1 = y1 = x2 = y2 = xy = 0;
				for(int j=0; j<5; j++)
				{
					n += (float)(1.0);
					x1 += (float)(x[j]);
					y1 += (float)(y[j]);
					x2 += (float)(x[j]*x[j]);
					y2 += (float)(y[j]*y[j]);
					xy += (float)(x[j]*y[j]);
				}

				/* get determinant of matrix -- check if determinant is too small */
				det = n*(x2*y2 - xy*xy) + x1*(xy*y1 - x1*y2) + y1*(x1*xy - y1*x2);
				if(det < 0.1 && det > -0.1)
				{
					/* determinant is too small */
					return false;
				}

				/* get elements of inverse matrix */
				a = (x2*y2 - xy*xy)/det;
				b = (xy*y1 - x1*y2)/det;
				c = (x1*xy - y1*x2)/det;
				e = (n*y2 - y1*y1)/det;
				f = (x1*y1 - n*xy)/det;
				i = (n*x2 - x1*x1)/det;

				/* get sums for x calibration */
				z = zx = zy = 0;
				for(int j=0;j<5;j++)
				{
					z += (float)xfb[j];
					zx += (float)(xfb[j]*x[j]);
					zy += (float)(xfb[j]*y[j]);
				}

				/* now multiply out to get the calibration for framebuffer x coord */
				cal[2] = (int)((a*z + b*zx + c*zy)*(scaling));
				cal[0] = (int)((b*z + e*zx + f*zy)*(scaling));
				cal[1] = (int)((c*z + f*zx + i*zy)*(scaling));

				/* get sums for y calibration */
				z = zx = zy = 0;
				for(int j=0; j<5; j++)
				{
					z += (float)(yfb[j]);
					zx += (float)(yfb[j]*x[j]);
					zy += (float)(yfb[j]*y[j]);
				}

				/* now multiply out to get the calibration for framebuffer y coord */
				cal[5] = (int)((a*z + b*zx + c*zy)*(scaling));
				cal[3] = (int)((b*z + e*zx + f*zy)*(scaling));
				cal[4] = (int)((c*z + f*zx + i*zy)*(scaling));

				/* if we got here, we're OK, so assign scaling to cal[6] and return */
				cal[6] = (int)scaling;
				return true;			
			}
		}
		
		public TsCalibrateView(Context c)
		{
			super(c);
			postion = FIRST_POSTION_LEFT_TOP;
			fb_value.clear();
			value1.clear();
			value2.clear();
			invalidate();
		}

		protected void reset()
		{
			postion = FIRST_POSTION_LEFT_TOP;
			fb_value.clear();
			value1.clear();
			value2.clear();
			invalidate();
			try {
				ts.writeCalToHardware(original_cal);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				ts.setZoomToHardware(ZOOM);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		protected void onDraw(Canvas canvas)
		{
			int x = 50, y = 50;
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.YELLOW);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(2);

			switch(postion)
			{
			case FIRST_POSTION_LEFT_TOP:
			case SECOND_POSTION_LEFT_TOP:
				x = 50;
				y = 50;
				break;
			case FIRST_POSTION_RIGHT_TOP:
			case SECOND_POSTION_RIGHT_TOP:
				x = getWidth() - 50;
				y = 50;
				break;
			case FIRST_POSTION_RIGHT_BOTTOM:
			case SECOND_POSTION_RIGHT_BOTTOM:
				x = getWidth() - 50;
				y = getHeight() - 50;
				break;
			case FIRST_POSTION_LEFT_BOTTOM:
			case SECOND_POSTION_LEFT_BOTTOM:
				x = 50;
				y = getHeight() - 50;
				break;
			case FIRST_POSTION_CENTER:
			case SECOND_POSTION_CENTER:
				x = getWidth() / 2;
				y = getHeight() / 2;
				break;
			default:
				x = 50;
				y = 50;
				postion = FIRST_POSTION_LEFT_TOP;
				break;
			}
			
			canvas.drawCircle(x, y, 10, paint);
			canvas.drawLine(x-15, y, x+15, y, paint);
			canvas.drawLine(x, y-15, x, y+15, paint);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event)
		{
			if(event.getAction() == MotionEvent.ACTION_UP)
			{		
				switch(postion)
				{
				case FIRST_POSTION_LEFT_TOP:
					fb_value.add(new Point((int)50, (int)50));
					value1.add( new Point( ((int)event.getX())<<ZOOM, ((int)event.getY())<<ZOOM ) );
					postion = FIRST_POSTION_RIGHT_TOP;
					invalidate();
					break;
				case FIRST_POSTION_RIGHT_TOP:
					fb_value.add(new Point((int)(getWidth() - 50), (int)50));
					value1.add( new Point( ((int)event.getX())<<ZOOM, ((int)event.getY())<<ZOOM ) );
					postion = FIRST_POSTION_RIGHT_BOTTOM;
					invalidate();
					break;
				case FIRST_POSTION_RIGHT_BOTTOM:
					fb_value.add(new Point((int)(getWidth() - 50), (int)(getHeight() - 50)));
					value1.add( new Point( ((int)event.getX())<<ZOOM, ((int)event.getY())<<ZOOM ) );
					postion = FIRST_POSTION_LEFT_BOTTOM;
					invalidate();
					break;
				case FIRST_POSTION_LEFT_BOTTOM:
					fb_value.add(new Point((int)50, (int)(getHeight() - 50)));
					value1.add( new Point( ((int)event.getX())<<ZOOM, ((int)event.getY())<<ZOOM ) );
					postion = FIRST_POSTION_CENTER;
					invalidate();
					break;
				case FIRST_POSTION_CENTER:
					fb_value.add(new Point((int)(getWidth()/2), (int)(getHeight()/2)));
					value1.add( new Point( ((int)event.getX())<<ZOOM, ((int)event.getY())<<ZOOM ) );
					postion = SECOND_POSTION_LEFT_TOP;
					invalidate();
					break;
				case SECOND_POSTION_LEFT_TOP:
					value2.add( new Point( ((int)event.getX())<<ZOOM, ((int)event.getY())<<ZOOM ) );
					postion = SECOND_POSTION_RIGHT_TOP;
					invalidate();
					break;
				case SECOND_POSTION_RIGHT_TOP:
					value2.add( new Point( ((int)event.getX())<<ZOOM, ((int)event.getY())<<ZOOM ) );
					postion = SECOND_POSTION_RIGHT_BOTTOM;
					invalidate();
					break;
				case SECOND_POSTION_RIGHT_BOTTOM:
					value2.add( new Point( ((int)event.getX())<<ZOOM, ((int)event.getY())<<ZOOM ) );
					postion = SECOND_POSTION_LEFT_BOTTOM;
					invalidate();
					break;
				case SECOND_POSTION_LEFT_BOTTOM:
					value2.add( new Point( ((int)event.getX())<<ZOOM, ((int)event.getY())<<ZOOM ) );
					postion = SECOND_POSTION_CENTER;
					invalidate();
					break;
				case SECOND_POSTION_CENTER:
					value2.add( new Point( ((int)event.getX())<<ZOOM, ((int)event.getY())<<ZOOM ) );
					
					if((value1.size() != 5) && (value2.size() != 5) && (fb_value.size() != 5))
					{
						this.reset();
						Toast.makeText(getApplicationContext(), R.string.try_again_tips, Toast.LENGTH_SHORT).show();	
						return true;
					}
					
					for(int i=0; i<5; i++)
					{
						if( (Math.abs(value1.get(i).x - value2.get(i).x) > MIN_X)
							|| (Math.abs(value1.get(i).y - value2.get(i).y) > MIN_Y))
						{
							this.reset();
							Toast.makeText(getApplicationContext(), R.string.try_again_tips, Toast.LENGTH_SHORT).show();	
							return true;
						}
					}
					
					calibration cal = new calibration();
					if(cal.perform_calibration(fb_value, value1) !=true)
					{
						this.reset();
						Toast.makeText(getApplicationContext(), R.string.try_again_tips, Toast.LENGTH_SHORT).show();	
						return true;
					}
					
					perform_cal = cal.cal;
					try {
						ts.setZoomToHardware(0);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						ts.writeCalToHardware(perform_cal);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					need_recovery = false;
					startActivityForResult(new Intent(TunchScreenActivity.this, TsTestActivity.class), BIND_AUTO_CREATE);
					break;
				default:
					this.reset();
					break;
				}
			}
			
			return true;
		}
	}
}
