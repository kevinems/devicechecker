package com.android.devicechecker;
import com.android.devicechecker.R;
import com.android.util.FileOperate;
import com.android.util.HeadSet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class headsetactivity extends Activity {

	TextView mheadsetTextView,mHeadTips;
	HeadSet.OnListener m_HeadSetListerer;
	HeadSet mHeadSet;
	
	//private Button mYes=null;
	private Button mNo=null;
	
	int curStatus=1;   //1检测插入 2是检测拔出
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub	
		super.onCreate(savedInstanceState);
		
		 final Window win = getWindow();
 	    win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		
		setContentView(R.layout.headset_state);
		mheadsetTextView=(TextView)findViewById(R.id.headset_state_text);
		
		mHeadTips=(TextView)findViewById(R.id.headtips);
		//mYes=(Button)findViewById(R.id.but_ok);
		mNo=(Button)findViewById(R.id.but_nook);
		
		mNo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setValue(2);
				FileOperate.setCurmode(false);
				finish();
			}
		});
	}
	
	private void setValue(int value){
		//FileOperate.setIndexValue(FileOperate.TestItemHeadset, value);
		//FileOperate.writeToFile(this);
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub	
		super.onStart();
		mHeadSet=new HeadSet(getApplicationContext());
		mHeadSet.Start();
		m_HeadSetListerer = new HeadSet.OnListener(){
			 public void onHeadSetChanged(int nState)
			 {
				 if(nState==1)
				 {		 
					 if (curStatus==1) {
						 mheadsetTextView.setText(R.string.headset_test_in);						 
						 curStatus=2;
						 mHeadTips.setText(R.string.headset_test_hintb);
					}
				 }
				 else
				 {
					 
					 if (curStatus==2) {
						 mHeadTips.setText(R.string.usb_test_out);
						 //合格
						 setValue(1);
						 FileOperate.setCurTest(true);
						 finish();
						 
						 Intent mIntent = new Intent(headsetactivity.this, MusicPlayActivity.class);
							startActivity(mIntent);
					 }else {
						//不合格
					}
				 }
			 }
		};
		mHeadSet.setListener(m_HeadSetListerer);
		
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mHeadSet.Stop();
		
	}
}
