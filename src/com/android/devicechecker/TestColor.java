package com.android.devicechecker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.util.FileOperate;

public class TestColor extends Activity {
	private static final String TAG = "TestColor";
		
	private Button mYes=null;
	private Button mNo=null;
	
	private TextView mText1 = null;

	private Toast mToast = null;
	
	private int mNum = 0;
	
	private AlertDialog progressAlert;
	boolean checkOk=false;	//是否是成功
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	      getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);      
			
	      TextView mTextView = new TextView(this);
//	      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

//			if(getRequestedOrientation()
//				== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//			}	
			
		setContentView(R.layout.test_color);
		initView();
		if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
		FileOperate.setIndexValue(FileOperate.TestItemColor, FileOperate.CHECK_FAILURE);
		FileOperate.writeToFile(this);
		}
	}
	
	private void initView(){		
		mText1 = (TextView)findViewById(R.id.test_color_text1);		
		mYes=(Button)findViewById(R.id.but_ok);
		mNo=(Button)findViewById(R.id.but_nook);
		
		mYes.setVisibility(View.GONE);
		mNo.setVisibility(View.GONE);
		
		toNextTest();
		
		mYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				finish();
				setValue(FileOperate.CHECK_SUCCESS);
				if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
					Intent mIntent = FileOperate.getCurIntent(TestColor.this,"Screen_Color");
					 if (mIntent!=null) {
						 startActivity(mIntent);
					}
				}		
			}
		});
		
		mNo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				    finish();
				    setValue(FileOperate.CHECK_FAILURE);
			}
		});

	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mNum<=7) {
				mYes.setVisibility(View.GONE);
				mNo.setVisibility(View.GONE);
				toNextTest();
			}else {
				mYes.setVisibility(View.VISIBLE);
				mNo.setVisibility(View.VISIBLE);
			}
			break;

		default:
			break;
		}
		
		return true;
	}
	
	private void setValue(int value){
		FileOperate.setIndexValue(FileOperate.TestItemColor, value);
		FileOperate.writeToFile(this);
	}
	
	
	private void brightness(float f) {    
	     WindowManager.LayoutParams lp = getWindow().getAttributes();    
	     lp.screenBrightness = f;    
	     getWindow().setAttributes(lp);    
	}   
	
	public void displayToast(String message) {
		if (null != mToast) {
			mToast.setText(message);
			mToast.setDuration(Toast.LENGTH_SHORT);
			mToast.show();
			
			return;
		}
		mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		mToast.show();
	}
	
	public void displayToast(int id) {
		displayToast(getString(id));
	}
	
	public void toNextTest(){
		
		if(mNum == 0){
			mText1.setBackgroundColor(Color.RED);
			displayToast(R.string.next_tips);
		}
		if(mNum == 1){
			mText1.setBackgroundColor(Color.GREEN);
			displayToast(R.string.next_tips);
		}
		if(mNum == 2){
			mText1.setBackgroundColor(Color.BLUE); 
			displayToast(R.string.next_tips);
		}				
		if(mNum == 3){
			mText1.setBackgroundColor(Color.BLACK); 
			displayToast(R.string.next_tips);
		}if(mNum == 4){
			mText1.setBackgroundColor(Color.WHITE); 
			displayToast(R.string.next_tips);
		}	
		if(mNum == 5){
			mText1.setBackgroundColor(Color.WHITE);
			displayToast(R.string.test_brightness_low);
			brightness(0.1f);
		}	
		if(mNum == 6){
			displayToast(R.string.test_brightness_middle);
			brightness(0.5f);
		}
		if (mNum == 7) {
			brightness(1.0f);
			displayToast(R.string.test_brightness_height);
			mYes.setVisibility(View.VISIBLE);
			mNo.setVisibility(View.VISIBLE);
			mNo.setBackgroundColor(Color.RED);
		}
		
		mNum ++;
	}
	
	 Handler mHandler = new Handler(){
		   public void handleMessage(Message msg){
			   switch (msg.what) {
			   }
			   
			   }
	 };

	
}
