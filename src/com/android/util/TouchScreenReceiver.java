package com.android.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TouchScreenReceiver extends BroadcastReceiver
{
	private static final String TAG = "TsCalibrate";
    
	@Override
	public void onReceive(Context context, Intent intent)
	{
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {           
            Log.d(TAG, "boot start ..." );
            Intent i = new Intent(TouchScreenServer.class.getName());
            i.setClass(context, TouchScreenServer.class);
            context.startService(i);
        }
	}
}
