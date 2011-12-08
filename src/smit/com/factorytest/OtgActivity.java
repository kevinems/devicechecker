package smit.com.factorytest;

import smit.com.util.FileOperate;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class OtgActivity extends Activity{
	//private Button btStart,btOpen,btClose;
	 	private static final String TAG = "OtcActivityDemo";
	 	//private Button mYes=null;
	 	private Button mNo=null;
		int curStatus=1;   //1检测插入 2是检测拔出
		TextView mOtgTips,mOtgStatus;
		private static final int STORAGE_CNT = 2;
		private static final String VOL_NAME[] = {"scsi_sda","scsi_sdb"};
		
		private  BroadcastReceiver mReceiver;
			
		private AlertDialog progressAlert;
		boolean checkOk=false;	//是否是成功
	
	private void setValue(int value){
		FileOperate.setIndexValue(FileOperate.TestItemOtg, value);
		FileOperate.writeToFile(this);
		
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Window win = getWindow();
	    win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);     
        setContentView(R.layout.test_otc);
        
        //0 默认 1host 2slave
        Settings.System.putInt(getContentResolver(),"otg_setting", 1);
        
        mOtgTips=(TextView)findViewById(R.id.otgtips);
        mOtgStatus=(TextView)findViewById(R.id.otg_state_text);      
		mNo=(Button)findViewById(R.id.but_nook);
  
		
		mNo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setValue(2);
				finish();
				
			}
		});	
		
		if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
		FileOperate.setIndexValue(FileOperate.TestItemOtg, FileOperate.CHECK_FAILURE);
		FileOperate.writeToFile(this);
		}
		
	}

	 
	
	public void registerScSiSDAReceiver(){
		
		IntentFilter mIntentFilter;
		mIntentFilter = new IntentFilter();
		 mIntentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		 mIntentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		mIntentFilter.addDataScheme("file");
	     
		 mReceiver = new BroadcastReceiver() {
		        @Override
		        public void onReceive(Context context, Intent intent) {	

		        	String str = intent.getAction();
		        	String str1=intent.getData().getPath();

		        	//这里接口可能不一样，请注意
		        	Log.e(TAG,"++++++++++++++++++++++++++++"+"str1"+"++++++++++++++++++++++++");	
		        	if ((str1.equals("/sda1") || str1.equals("/sda2"))  //8900 is /sda1 /sda2 8803 is /mnt/sdcard/scsi_sda1
						|| (str1.equals("/mnt/sdcard/scsi_sda1") || str1.equals("/mnt/sdcard/scsi_sda2"))) {
					if (str.equals(Intent.ACTION_MEDIA_MOUNTED)) {
						if (curStatus == 1) {
							mOtgStatus.setText(R.string.test_otg_in);
							curStatus = 2;
							mOtgTips.setText(R.string.test_otg_hang_out);
						}

					} else if (str.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
						
						if (curStatus == 2) {			
							mOtgStatus.setText(R.string.test_otg_out);
							setValue(1);
							finish();

						if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
							 Intent mIntent = FileOperate.getCurIntent(OtgActivity.this,"Otg");
							 if (mIntent!=null) {
								 startActivity(mIntent);
							}	 
							 curStatus=3;
							}

					}
				}
	        	}
	        }
	    };
	
		registerReceiver(mReceiver, mIntentFilter);
	}	

	
	@Override
	protected void onStart() {
			
		super.onStart();
/*		 	String statusSda = Environment.getStorageState(Environment.getStorageInfo("scsi_sda").mDirectory.getPath());  
		 	String statusSdb = Environment.getStorageState(Environment.getStorageInfo("scsi_sdb").mDirectory.getPath());
		 	
		 	if (statusSda.equals(Environment.MEDIA_MOUNTED)||statusSdb.equals(Environment.MEDIA_MOUNTED)) {	 		
		 		mOtgTips.setVisibility(View.GONE);
	        	mNo.setVisibility(View.GONE);
	        	mOtgStatus.setText(R.string.test_otg_success);
		 		mHandler.postDelayed(mRunnable,2000);
	        }else {
	        	registerScSiSDAReceiver();
			}*/	
	}
	
	
	
    private Runnable mRunnable = new Runnable() {
		
		public void run() {		
			mOtgStatus.setText(R.string.test_otg_out);
			setValue(1);
			finish();

			if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {
				 Intent mIntent = FileOperate.getCurIntent(OtgActivity.this,"Otg");
				 if (mIntent!=null) {
					 startActivity(mIntent);
				}	 
			}
		}
    };
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (mReceiver!=null) {
			unregisterReceiver(mReceiver); 
		}
		
		if (mHandler!=null&&mRunnable!=null) {
			mHandler.removeCallbacks(mRunnable);
		}
		
	}
	    @Override
	    protected void onDestroy() {
	        super.onDestroy();            
	        Settings.System.putInt(getContentResolver(),"otg_setting", 0);
	    }

	    Handler mHandler = new Handler(){
			   public void handleMessage(Message msg){
				   switch (msg.what) {
				   }
				   
				   }
		 };

}

