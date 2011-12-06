package smit.com.factorytest;

import smit.com.factorytest.R;
import smit.com.util.FileOperate;
import smit.com.util.ParseSeverData;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TestColor extends Activity {
	private static final String TAG = "TestColor";
		
	private Button mBrightnessLow = null;
	private Button mBrightnessMiddle= null;
	private Button mBrightnessHeight = null;
	private Button mYes=null;
	private Button mNo=null;
	
	private TextView mText1 = null;
	private TextView mText2 = null;
	private TextView mText3 = null;
	private Intent mIntent = null;
	
	private int mNum = 0;
	
	private AlertDialog progressAlert;
	boolean checkOk=false;	//是否是成功
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	      getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);      
			
	      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

			if(getRequestedOrientation()
				== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}	
			
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
		
		mYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (mNum<=7) {
					toNextTest();
				}else {
					finish();
					setValue(FileOperate.CHECK_SUCCESS);
					if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
						Intent mIntent = FileOperate.getCurIntent(TestColor.this,"Screen_Color");
						 if (mIntent!=null) {
							 startActivity(mIntent);
						}
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
	
	private void setValue(int value){
		FileOperate.setIndexValue(FileOperate.TestItemColor, value);
		FileOperate.writeToFile(this);
	}
	
	
	private void brightness(float f) {    
	     WindowManager.LayoutParams lp = getWindow().getAttributes();    
	     lp.screenBrightness = f;    
	     getWindow().setAttributes(lp);    
	}   
	
	public void toNextTest(){
		mNum ++;
		if(mNum == 0){
			mText1.setBackgroundColor(Color.RED);
		}
		if(mNum == 1){
			mText1.setBackgroundColor(Color.GREEN);
		}
		if(mNum == 2){
			mText1.setBackgroundColor(Color.BLUE); 
		}				
		if(mNum == 3){
			mText1.setBackgroundColor(Color.BLACK); 
		}if(mNum == 4){
			mText1.setBackgroundColor(Color.WHITE); 
		}	
		if(mNum == 5){
			mText1.setBackgroundResource(R.drawable.pic1);
			
			mYes.setText(R.string.test_brightness_low);
		}	
		
		
		if(mNum == 6){//亮度低
			mText1.setBackgroundColor(Color.WHITE);
			mYes.setText(R.string.test_brightness_middle);
			brightness(0.1f);
		}	
		if(mNum == 7){//亮度中
			mYes.setText(R.string.test_brightness_height);
			brightness(0.5f);
		}
		if (mNum == 8) {
			brightness(1.0f);
			mYes.setText(R.string.check_ok);
		}
	}
	
	 Handler mHandler = new Handler(){
		   public void handleMessage(Message msg){
			   switch (msg.what) {
			   }
			   
			   }
	 };

	
}
