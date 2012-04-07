package com.android.util;

import android.content.Context;
import android.widget.Toast;

public class enhanceToast{
	Toast mToast = null;
	Context mContext = null;

	public enhanceToast(Context context) {
		mContext = context;
	}
	
	public void displayToast(String message) {
		if (null != mToast) {
			mToast.setText(message);
			mToast.setDuration(Toast.LENGTH_LONG);
			mToast.show();

			return;
		}
		mToast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
		mToast.show();
	}
	
	public void displayToast(String message, int duration) {
		if (null != mToast) {
			mToast.setText(message);
			if (duration == Toast.LENGTH_SHORT) {
				mToast.setDuration(Toast.LENGTH_SHORT);
			} else {
				mToast.setDuration(Toast.LENGTH_LONG);
			}
			
			mToast.show();

			return;
		}
		if (duration == Toast.LENGTH_SHORT) {
			mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
		} else {
			mToast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);	
		}
		
		mToast.show();
	}
	
	public void cancel() {
		if (null != mToast) {
			mToast.cancel();
		}
	}
}
