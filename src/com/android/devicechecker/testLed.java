package com.android.devicechecker;

import com.android.devicechecker.interfaces.IsetTestResult;
import com.android.devicechecker.interfaces.ItestActTemplate;
import com.android.util.FileOperate;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class testLed extends ItestActTemplate {

	private static final int ID_LED = 1234556782;
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
		setYesBtnOnClickListener(mYes, FileOperate.TestItemLed, FileOperate.TEST_LED_STRING);
		setNoBtnOnClickListener(mNo, FileOperate.TestItemLed);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

		Notification notification = new Notification();
		notification.ledARGB = Color.RED;
		notification.ledOnMS = 100;
		notification.ledOffMS = 100;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		nm.notify(ID_LED, notification);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		nm.cancel(ID_LED);
	}
}
