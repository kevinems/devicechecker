package com.android.devicechecker;

import com.android.devicechecker.R;
import com.android.util.FileOperate;
import com.android.util.GSensor;
import com.android.util.ParseSeverData;
import com.android.util.GSensor.OnShakeListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class StandardUsbActivity extends Activity{
	private static final String TAG = "StandardUsb";
	private Button mYes=null;
	private Button mNo=null;
	
	boolean checkOk=false;	//�Ƿ��ǳɹ�
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		 final Window win = getWindow();
 	    win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.test_standard_usb);		
		
		mYes=(Button)findViewById(R.id.but_ok);
		mNo=(Button)findViewById(R.id.but_nook);
		mYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {			
				setValue(1);
				finish();
				
				if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
					Intent mIntent = FileOperate.getCurIntent(StandardUsbActivity.this,"StandardUsb");
					startActivity(mIntent);
				}
			}
		});
		
		mNo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setValue(2);
				finish();
			}
		});
		
		if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
			FileOperate.setIndexValue(FileOperate.TestItemStandardUsb, FileOperate.CHECK_FAILURE);
			FileOperate.writeToFile(this);
		}
		
	}
	
	private void setValue(int value){
		FileOperate.setIndexValue(FileOperate.TestItemStandardUsb, value);
		FileOperate.writeToFile(this);
		
	}
	

}
