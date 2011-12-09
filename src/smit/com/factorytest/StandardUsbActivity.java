package smit.com.factorytest;

import smit.com.util.FileOperate;
import smit.com.util.GSensor;
import smit.com.util.ParseSeverData;
import smit.com.util.GSensor.OnShakeListener;
import smit.com.factorytest.R;
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
	
	boolean checkOk=false;	//是否是成功
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
