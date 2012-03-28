package com.android.util;


import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class TouchScreenServer extends Service
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
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		SharedPreferences settings = getSharedPreferences(SETTING_CALIBRATE, android.content.Context.MODE_PRIVATE);
		if(settings.getBoolean(VALID, false))
		{
			int cal[] = new int[7];
			TouchScreen ts = new TouchScreen();

			cal[0] = settings.getInt(CAL0, 0);
			cal[1] = settings.getInt(CAL1, 0);
			cal[2] = settings.getInt(CAL2, 0);
			cal[3] = settings.getInt(CAL3, 0);
			cal[4] = settings.getInt(CAL4, 0);
			cal[5] = settings.getInt(CAL5, 0);
			cal[6] = settings.getInt(CAL6, 0);
			
			try {
				ts.writeCalToHardware(cal);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, String.format("read calibrate: [%d, %d, %d, %d, %d, %d, %d]", cal[0],cal[1],cal[2],cal[3],cal[4],cal[5],cal[6]));
		}
		else
		{
			Log.d(TAG, String.format("read calibrate: No valid value."));	
		}
		stopSelf();
	}
}
