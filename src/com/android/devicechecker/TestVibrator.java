package com.android.devicechecker;

import com.android.devicechecker.R;
import com.android.util.FileOperate;
import com.android.util.GSensor;
import com.android.util.ParseSeverData;
import com.android.util.GSensor.OnShakeListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class TestVibrator extends Activity{
	private static final String TAG = "TestGSensor";
	
	private Button mYes=null;
	private Button mNo=null;
	private Vibrator mVibrator;
	private AudioManager mAudioManager;
	private int noldsattus;
	
	private AlertDialog progressAlert;
	boolean checkOk=false;	//是否是成功
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("TestGSensor~!~!~!~!~!~!~!~!");
		 final Window win = getWindow();
 	    win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.test_vibrator);		
			
		mYes=(Button)findViewById(R.id.but_ok);
		mNo=(Button)findViewById(R.id.but_nook);
		mYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setValue(1);
				finish();
				
				if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
					Intent mIntent = FileOperate.getCurIntent(TestVibrator.this,"Vibrator");
					 if (mIntent!=null) {
						 startActivity(mIntent);
					}
				}
			}
		});
		
		mNo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setValue(2);
				finish();
			}
		});
		
		mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		mAudioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
                true ? AudioManager.VIBRATE_SETTING_ON
                        : AudioManager.VIBRATE_SETTING_OFF);
        
        mVibrator.vibrate(60000);
        if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
        	FileOperate.setIndexValue(FileOperate.TestItemVibrator, FileOperate.CHECK_FAILURE);
        	FileOperate.writeToFile(this);
        }
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mVibrator.cancel();
	}

	@Override
	protected void onStop() {
		super.onStop();
		
	}
	
	
	private void setValue(int value){
		FileOperate.setIndexValue(FileOperate.TestItemVibrator, value);
		FileOperate.writeToFile(this);
	}
	
	Handler mHandler = new Handler(){
		   public void handleMessage(Message msg){
			   switch (msg.what) {
			   }
			   
			   }
	 };

	public Dialog onCreateDialog(int id){
 	Dialog dialog=null;
 	if (id==FileOperate.DIALOG_UP_TEST_ITEM) {
 		LayoutInflater inflater = LayoutInflater.from(this);
			View progressView = inflater.inflate(R.layout.progress_layout, null);
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
			mBuilder.setView(progressView);
			TextView message=(TextView)progressView.findViewById(R.id.progress_message);
			message.setText(R.string.up_test_item);
			message.setTextSize(30);
			message.setTextColor(getResources().getColor(R.color.yellow));
			progressAlert = mBuilder.create();
			progressAlert.show();
			dialog=progressAlert;
			progressAlert.setOnKeyListener(new OnKeyListener(){
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN) {
						
					}
					return false;
				}
			});
		}
	
 	return dialog;
	}

}
