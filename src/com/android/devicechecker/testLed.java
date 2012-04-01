package com.android.devicechecker;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.android.devicechecker.interfaces.ItestActTemplate;
import com.android.util.FileOperate;

public class testLed extends ItestActTemplate {

	private static final int ID_LED = 1234556782;
	private static final String TAG_STRING = "test led";
	NotificationManager nm;
	private Button mYes = null;
	private Button mNo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test_led);

		mYes = (Button) findViewById(R.id.but_ok);
		mNo = (Button) findViewById(R.id.but_nook);
		setYesBtnOnClickListener(mYes, FileOperate.TestItemLed,
				FileOperate.TEST_LED_STRING);
		setNoBtnOnClickListener(mNo, FileOperate.TestItemLed);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Log.i(TAG_STRING, "onResume");
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();		
		
		Log.i(TAG_STRING, "onStop");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		Thread mThread = new activeLedThread();
		
		Log.i(TAG_STRING, "onPause");
		mThread.start();
	}
	
	class activeLedThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			try {
				Log.i(TAG_STRING, "Thread run");
				sleep(500);
				Log.i(TAG_STRING, "after sleep");
				activateNotification();
				Log.i(TAG_STRING, "after activateNotification");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Thread.currentThread().interrupt();
		}
	}
	
	
	private void activateNotification() {
		nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

		Notification notification = new Notification();
		notification.ledARGB = Color.CYAN;
		notification.ledOnMS = 100;
		notification.ledOffMS = 100;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		nm.notify(ID_LED, notification);
	}
}
