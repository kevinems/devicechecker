package com.android.devicechecker;


import com.android.devicechecker.R;
import com.android.util.FileOperate;
import com.android.util.Usb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MiniUsbActivity extends Activity {
	TextView mUsbTextView,mUsbTips;
	Usb.OnListener m_UsbListerer;
	Usb mUsb;
	int curStatus=1;   //1检测插入 2是检测拔出
	
	private Button mYes=null;
	private Button mNo=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub	
		super.onCreate(savedInstanceState);
		
		 final Window win = getWindow();
 	    win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		
		setContentView(R.layout.usb_state);
		mUsbTextView=(TextView)findViewById(R.id.usb_state_text);
		mUsbTips=(TextView)findViewById(R.id.usbtips);
		
		mUsbTips.setText(R.string.usb_test_hinta);
		
		mYes=(Button)findViewById(R.id.but_ok);
		mNo=(Button)findViewById(R.id.but_nook);
		mYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setValue(1);
				finish();
				if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
					Intent mIntent = FileOperate.getCurIntent(MiniUsbActivity.this,"Usb");
					if (mIntent != null) {
						startActivity(mIntent);
					}
				}
			}
		});
		
		mNo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setValue(2);
				FileOperate.setCurmode(false);
				finish();
			}
		});
	}
	
	private void setValue(int value){
		FileOperate.setIndexValue(FileOperate.TestItemUsb, value);
		FileOperate.writeToFile(this);
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub	
		super.onStart();
		mUsb=new Usb(getApplicationContext());
		mUsb.Start();
		m_UsbListerer = new Usb.OnListener(){
			 public void onUsbChanged(int nState)
			 {
				 if(nState==1)
				 {
					 if (curStatus==1) {
						 mUsbTextView.setText(R.string.usb_test_in);
						 
						 curStatus=2;
						 mUsbTips.setText(R.string.usb_test_hintb);
					}	 
				 }
				 else
				 {
					 if (curStatus==2) {
						 mUsbTextView.setText(R.string.usb_test_out);
						 //合格
						 setValue(1);
						 FileOperate.setCurTest(true);
						 finish();
						 
						 if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
								Intent mIntent = FileOperate.getCurIntent(getApplicationContext(),"Usb");
								startActivity(mIntent);
							}
					 }else {
						//不合格
					}
					 
				 }
			 }
		};
		mUsb.setListener(m_UsbListerer);
		
		
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mUsb.Stop();
		
	}
	
	

}
