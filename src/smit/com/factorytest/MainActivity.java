package smit.com.factorytest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.xml.sax.InputSource;

import smit.com.util.AccessPointState;
import smit.com.util.FileOperate;
import smit.com.util.ParseSeverData;
import smit.com.util.UpTestItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity{
	private static final String TAG = "MainActivity";

	
	private LinearLayout mfirstLayout,msecLayout,mthirdLayout;

	private Button mTestWifi= null;
	private Button mTestLcd = null;
	private Button mTestKey = null;
	private Button mTestGps = null;
	private Button mTestGSeneor = null;
	
	private Button mTestVibrator=null;
	private Button mTestAudio=null;
	private Button mTestVideo= null;
	private Button mTestRecoder= null;
	private Button mTestHDMI= null;
	
	private Button mTestCamera=null;
	private Button mTestCameraSub=null;
	private Button mTestOtg=null;
	private Button mTestUsb=null;
	private Button mTestStandardUsb=null;
	private Button mTestSD = null;
	private Button mTestclose=null;
	
	private Button mGetCpuid=null;
	private Button mTestBattery=null;
	
	private TextView mVersion = null;
	
	//private Button mTestRtc = null;
	//
	//
	private Intent mIntent = null;	
	private Button mStartTest=null;
	private Button mModeChange=null;
	//private Button mLinkWanAgain=null;  		//
    //private Button mReloadTest=null; 	//重取测试项
    //private Button mActiveMachine=null; //激活
	public LinkedList<Button> existitem=new LinkedList<Button>();
	public LinkedList<Button> noexistitem=new LinkedList<Button>();
	
	EditText etPassword;
    private AlertDialog.Builder mBuilderpass;
    private AlertDialog mAlertpass;
    private AlertDialog progressAlert;
	public final static int DIALOG_ADMINSETTINGS=1;			//弹出输入密码
	public final static int DIALOG_ERROR=2;					//输入密码失败
	public final static int DIALOG_CONNECT_WIFI=3;			//连接wifi
	public final static int DIALOG_WIFI_ERROR=4;			//WIFI连接失败
	public final static int DIALOG_GET_ID=5;				//取id
	public final static int DIALOG_ID_NOINVALID=6;  		//不合法
	public final static int DIALOG_GET_TEST_ITEM=7; 		//取测试项
	public final static int DIALOG_GET_TEST_ERROR=8; 		//取测试项失败
	public final static int DIALOG_UP_TEST_STATUS=9;		//上传测试状态
	public final static int DIALOG_UP_TEST_STATUS_ERROR=10; //上传测试状态失败
	public final static int DIALOG_UP_LOG=11;				//上传log
	public final static int DIALOG_UP_LOG_ERROR=12;  		//上传log失败
	public final static int DIALOG_START_ACTIVE=13;			//开始激活
	public final static int DIALOG_START_ACTIVE_ERROR=14;  	//激活失败
	public final static int DIALOG_ACTIVE_CONFRIM=15;       // 激活确认
	public final static int DIALOG_ACTIVE_CONFRIM_ERROR=16;       // 激活确认
	public final static int DIALOG_QUIT=17;					//确认退出
	
	public static final String PSK = "PSK";
	public static final String WEP = "WEP";
	public static final String EAP = "EAP";
	public static final String OPEN = "Open";
	
	Timer mTimer;      //检测
	TimerTask mTask;
	
	private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private int connetcount=0;   		//连接计数
    private int UpOrDownDataTime=10;	//上传或下载数据时间
    private int searchcount=0;   		//搜索TTPP-LLIIKK计数
    private UpTestItem mThread;
 	private InputSource isSource =null;
 	private String retStr=null;
 	private int nRet;
    
    //提示信息文字 按钮
    TextView mTipsText;
    Button mTipsButton;
    
    WifiReceiver receiverWifi;
	private IntentFilter mIntentFilter;
	private int TipsTime=8000;
	
   Handler mhHandler = new Handler(){
	   public void handleMessage(Message msg){
		   switch (msg.what) {
	   }
		   		   
	   super.handleMessage(msg);
	   }
   };
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.i(TAG, "====================onCreate=================");
        Log.i(TAG, "====================onCreate=================");
        Log.i(TAG, "====================onCreate=================");
        Log.i(TAG, "====================onCreate=================");
        Log.i(TAG, "====================onCreate=================");
        Log.i(TAG, "====================onCreate=================");
        Log.i(TAG, "====================onCreate=================");
        Log.i(TAG, "====================onCreate=================");
        Log.i(TAG, "====================onCreate=================");
        Log.i(TAG, "====================onCreate=================");
        
        final Window win = getWindow();
	    win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);       
        setContentView(R.layout.main);
        
        setWifi();
        
    	mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
        
        
        FileOperate.CreateFile(this);
        FileOperate.readFromFile(this);     
        
        FileOperate.clearGobal();

        FileOperate.ReadTestItemXML();
        FileOperate.setGobalHandle(mhHandler);
        
        FileOperate.SetTestItemXML();
             
        initButton();    
                
        refreashTestItemLayout();
         
        
        if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL) {
    		setTitle(R.string.to_factory_mode);
		}else {
			setTitle(R.string.to_one_mode);
		}
        
        
    }


	private void DisplayVersion() {
		mVersion.setText(getString(R.string.cur_version) + getAppVersionName(getApplicationContext()));
		
	}
    
   
    private void initButton(){
    	//mModeShow=(TextView)findViewById(R.id.mode_show);
    	
    	//mWifiStatus=(TextView)findViewById(R.id.wifisingalimage);
    	mVersion = (TextView)findViewById(R.id.test_version);
        DisplayVersion();

    	
    	mTestWifi = (Button)findViewById(R.id.test_wifi);
    	mTestLcd = (Button)findViewById(R.id.test_lcd);
    	mTestKey = (Button)findViewById(R.id.test_key);
    	mTestGps = (Button)findViewById(R.id.test_gps);
    	mTestGSeneor = (Button)findViewById(R.id.test_GSensor);
    	mTestVibrator=(Button)findViewById(R.id.test_vibrator);
    	mTestAudio = (Button)findViewById(R.id.test_audio);
    	mTestVideo = (Button)findViewById(R.id.test_video);
    	mTestRecoder = (Button)findViewById(R.id.test_record);
    	mTestHDMI = (Button)findViewById(R.id.test_HDMI); 
    	mTestCamera=(Button)findViewById(R.id.test_carema); 	
    	mTestCameraSub=(Button)findViewById(R.id.test_caremaSub); 
    	mTestOtg=(Button)findViewById(R.id.test_otg);
    	mTestUsb=(Button)findViewById(R.id.test_usb);
    	mTestStandardUsb=(Button)findViewById(R.id.test_standard_usb);
    	mTestSD=(Button)findViewById(R.id.test_sd);
    	mTestclose = (Button)findViewById(R.id.test_close);	
    	mTestBattery = (Button)findViewById(R.id.test_battery);	
    	//mTestRtc = (Button)findViewById(R.id.test_Rtc);
    	mGetCpuid=(Button)findViewById(R.id.test_cpuid);
    	
    	
    	mStartTest=(Button)findViewById(R.id.test_start);  	
    	mModeChange=(Button)findViewById(R.id.mode_change);
    	mModeChange.setVisibility(View.INVISIBLE);
    	//mLinkWanAgain=(Button)findViewById(R.id.test_con_wifi);
    	//mReloadTest=(Button)findViewById(R.id.reloadtestitem);
    	//mActiveMachine=(Button)findViewById(R.id.active_machine);
    	
    	
    	mfirstLayout=(LinearLayout)findViewById(R.id.first_layout);
    	msecLayout=(LinearLayout)findViewById(R.id.second_layout);
    	mthirdLayout=(LinearLayout)findViewById(R.id.third_layout);
    	
    
    	
    	mTestWifi.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, WifiActivity.class);
				
				startActivity(mIntent);
			}
		});
    	
    	mTestLcd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, TestColor.class);
				startActivity(mIntent);
			}
		});
    	
    	mTestKey.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, TestKey.class);
				startActivity(mIntent);
			}
		});
    	
    	mTestGps.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, GpsActivity.class);
				startActivity(mIntent);
			}
		});
    	
    	mTestGSeneor.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, TestGSensor.class);
				startActivity(mIntent);
			}
		});
    	
    	mTestVibrator.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, TestVibrator.class);
				startActivity(mIntent);
			}
		});
    	
    	mTestAudio.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, MusicPlayActivity.class);
				startActivity(mIntent);
			}
		});
    	mTestVideo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, MediaPlayerVideo.class);
				startActivity(mIntent);
			}
		});
    	mTestRecoder.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, MediaRecoderactivity.class);
				startActivity(mIntent);
			}
		});
    	
    	mTestHDMI.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, HDMIActivity.class);
				startActivity(mIntent);
			}
		});
    	
    	mTestCamera.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, CameraActivity.class);
				startActivity(mIntent);
			}
		});
    	
    	mTestCameraSub.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, CameraSubActivity.class);
				startActivity(mIntent);
			}
		});
    	
    	mTestOtg.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, OtgActivity.class);
				startActivity(mIntent);
			}
		});
    	
    	mTestUsb.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, MiniUsbActivity.class);
				startActivity(mIntent);
			}
		});
    	
    	mTestStandardUsb.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, StandardUsbActivity.class);
				startActivity(mIntent);
			}
		});
    	
    	mTestSD.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, sdcardactivity.class);
				startActivity(mIntent);
			}
		});
    	
    	mTestclose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, SleepWakeActivity.class);
				startActivity(mIntent);
			}
		});
    	mGetCpuid.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mIntent = new Intent(MainActivity.this, getLincenseActivity.class);
				startActivity(mIntent);
			}
		});
    	    
    	mTestBattery.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mIntent = new Intent(MainActivity.this, TestBatteryActivity.class);
				startActivity(mIntent);
			}
		});
    	
    	mStartTest.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (FileOperate.getTestItemCount()>0) {
					reStartTest();
				}else {
					//Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_test), Toast.LENGTH_SHORT).show();
					FileOperate.MyToast(getApplicationContext(), null, R.string.no_test);
				}
				
			}
		});
    	
    	mModeChange.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
//				if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL) {
//					showDialog(DIALOG_ADMINSETTINGS);
//				}else {
//					FileOperate.changeMode();
//					ChagngeModeTest();
//				}
				FileOperate.changeMode();
				ChagngeModeTest();
			}
		});
    	
    	/*mLinkWanAgain.setOnClickListener(new View.OnClickListener(){
    		public void onClick(View v){
    			showDialog(DIALOG_CONNECT_WIFI);
    		}	
    	});*/
    	
    	/*mReloadTest.setOnClickListener(new View.OnClickListener(){
    		public void onClick(View v){
    			
    			showDialog(DIALOG_GET_TEST_ITEM);
    		}	
    	});
    	
    	mActiveMachine.setOnClickListener(new View.OnClickListener(){
    		public void onClick(View v){
    			if (FileOperate.getTestItemCount()<=0) {
    				//Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_test), Toast.LENGTH_SHORT).show();
    				FileOperate.MyToast(getApplicationContext(), null, R.string.no_test);
    			}else if (FileOperate.getTestSttus()==FileOperate.CHECK_FAILURE) {
					FileOperate.MyToast(getApplicationContext(),null,R.string.can_not_active);
					//Toast.makeText(getApplicationContext(), getResources().getString(R.string.can_not_active), Toast.LENGTH_SHORT).show();
				}else {
					showDialog(DIALOG_START_ACTIVE);
				}
    					
    		}	
    	});*/
     	
    }
    
    public void ChagngeModeTest(){
    	if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL) {
           
        	FileOperate.setCurmode(false);
        	FileOperate.curTestItem=0;
        	
		}else {
			setContentView(R.layout.main);
			
            initButton();   
               
        	FileOperate.setCurmode(true);
        	FileOperate.curTestItem=0;
		}
    	
    	refreashTestItemLayout();
    	onStart();
    	
    	if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL) {
    		//mModeShow.setText(R.string.to_factory_mode);
    		setTitle(R.string.to_factory_mode);
		}else {
			//mModeShow.setText(R.string.to_one_mode);
			setTitle(R.string.to_one_mode);
		}
    	
    }
    
    public void reStartTest(){
    	if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL) {
    		//setContentView(R.layout.main);
            //initButton();   
            FileOperate.CreateFile(this);
            FileOperate.readFromFile(this);
               
        	FileOperate.setCurmode(true);
        	FileOperate.curTestItem=0;
        	FileOperate.restartTest(this);
        	
        	
        	
        	//mIntent = new Intent(MainActivity.this, WifiActivity.class);
        	mIntent=FileOperate.getCurIntent(MainActivity.this,null);
    		startActivity(mIntent);
		}else {
			//setContentView(R.layout.main);
            //initButton();   
            FileOperate.CreateFile(this);
            FileOperate.readFromFile(this);
               
        	FileOperate.setCurmode(true);
        	FileOperate.curTestItem=0;
        	FileOperate.restartTest(this);
		}
    	 	
    	onStart();
    	
    	if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL) {
    		//mModeShow.setText(R.string.to_factory_mode);
    		setTitle(R.string.to_factory_mode);
		}else {
			//mModeShow.setText(R.string.to_one_mode);
			setTitle(R.string.to_one_mode);
		}
    }
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	//super.onBackPressed();
    	showDialog(DIALOG_QUIT);
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	FileOperate.setCurmode(false);
    	ParseSeverData.CloseAllThread();
    	closeProgressAlert();
    	closeRequestData();   	
    	
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	registerIntentReceivers();
    	 mhHandler.postDelayed(mRunnableTips, TipsTime);
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	unregisterIntentReceivers();
    	if (mhHandler!=null&&mRunnableTips!=null) {
			mhHandler.removeCallbacks(mRunnableTips);
		}
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	if (mhHandler!=null&&mRunnable!=null) {
			mhHandler.removeCallbacks(mRunnable);
		}
    }
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	
    	View v=null;
    	
    	for (int i = 0; i < FileOperate.testCount; i++) {
    		if (i==FileOperate.TestItemWifi) {
				v=mTestWifi;
			}else if (i==FileOperate.TestItemColor) {
				v=mTestLcd;
			}else if (i==FileOperate.TestItemKey) {
				v=mTestKey;
			}else if (i==FileOperate.TestItemGps){
				v=mTestGps;
			}else if (i==FileOperate.TestItemGSensor) {
				v=mTestGSeneor;
			}else if (i==FileOperate.TestItemVibrator) {
				v=mTestVibrator;
			}else if(i==FileOperate.TestItemAudio){
				v=mTestAudio;
			}else if (i==FileOperate.TestItemVideo) {
				v=mTestVideo;
			}else if (i==FileOperate.TestItemRecord) {
				v=mTestRecoder;
			}else if (i==FileOperate.TestItemHDMI) {
				v=mTestHDMI;
			}else if(i==FileOperate.TestItemCamera){
				v=mTestCamera;
			}else if(i==FileOperate.TestItemCameraSub){
				v=mTestCameraSub;
			}else if(i==FileOperate.TestItemOtg){
				v=mTestOtg;
			}else if(i==FileOperate.TestItemUsb){
				v=mTestUsb;
			}else if(i==FileOperate.TestItemStandardUsb){
				v=mTestStandardUsb;
			}else if(i==FileOperate.TestItemSd){
				v=mTestSD;
			}else if (i==FileOperate.TestItemClose) {
				v=mTestclose;
			}else if (i==FileOperate.TestItemCpu){
				v=mGetCpuid;
			}else if (i==FileOperate.TestItemBattery){
				v=mTestBattery;
			}
    		
    		switch (FileOperate.getIndexValue(i)) {
    		
			case 0:{
//				if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL) {
//					v.setEnabled(false);
//				}else {
//					v.setEnabled(true);
//				}
								
				break;
				}
			case 1:{
				v.setBackgroundColor(getResources().getColor(R.color.green));
				/*if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL)
				{
					v.setEnabled(false);
				}else {
					v.setEnabled(true);
				}*/
				v.setEnabled(true);
				break;
				
				}
			case 2:{
				v.setBackgroundColor(getResources().getColor(R.color.red));
				v.setEnabled(true);
				break;
				}
			default:
				break;
			}			
		}
    	
    	//openLoginDlg(DIALOG_UP_TEST_STATUS);
    	
    	/*//开始激活
    	if (FileOperate.testAllSuccess()) {
    		
		}  	*/
    }
    
    //
    public void changeStatus(){
    	String str=null;
		if (etPassword!=null) {	
			str=etPassword.getText().toString();
			if (str.equals("aaa")) {
				FileOperate.changeMode();
				//mAlertpass.dismiss();
				dismissDialog(DIALOG_ADMINSETTINGS);
				ChagngeModeTest();
		    	
				
			}else {
				showDialog(DIALOG_ERROR);
			}
			etPassword.setText("");
		}
    }
    
    
    public Dialog onCreateDialog(int id){
    	Dialog dialog=null;
    	
    	if (id==DIALOG_ADMINSETTINGS) {
    		LayoutInflater inflater = LayoutInflater.from(this);
    		View password = inflater.inflate(R.layout.loginchangepass, null);
    		password.setMinimumHeight(480);
    		password.setMinimumWidth(800);
    		mBuilderpass = new AlertDialog.Builder(this);
    		mBuilderpass.setTitle("").setView(password);		
    		etPassword=(EditText)password.findViewById(R.id.inputpassword);
    		Button btnlogin = (Button)password.findViewById(R.id.login);
    		Button btncancel=(Button)password.findViewById(R.id.cancel);
    		
    	    btnlogin.setOnClickListener(new OnClickListener(){		
    				@Override
    				public void onClick(View v) {
    					changeStatus();
    				}
    	        });
    	    
    	    btncancel.setOnClickListener(new OnClickListener(){		
    			@Override
    			public void onClick(View v) {
    				//mAlertpass.dismiss();
    				dismissDialog(DIALOG_ADMINSETTINGS);
    				
    			}
            });

    	    mAlertpass = mBuilderpass.create();
    		dialog=mAlertpass;
		}else if(id==DIALOG_ERROR){
	/*		  final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 		   builder.setMessage(R.string.password_error);
	 		   builder.setPositiveButton("OK", null);
	 		   final AlertDialog alert = builder.create();
	 		   alert.show();*/
	 		   
				LayoutInflater inflater = LayoutInflater.from(this);
	    		View tips = inflater.inflate(R.layout.tipsdiagle, null);
	    		AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
	    		mBuilder.setView(tips);
	    		mTipsText=(TextView)tips.findViewById(R.id.tipsinfo);
	    		mTipsButton=(Button)tips.findViewById(R.id.button_id);
	    		mTipsText.setText(R.string.password_error);
	    		mTipsButton.setText(R.string.button_ok);
	    		progressAlert = mBuilder.create();
				progressAlert.show();
				mTipsButton.setOnClickListener(new OnClickListener(){
					@Override
    				public void onClick(View v) {
    					closeProgressAlert();
    				}
				});
		}
		
		
		else if (id==DIALOG_CONNECT_WIFI) {
			closeProgressAlert();
			
			LayoutInflater inflater = LayoutInflater.from(this);
			View progressView = inflater.inflate(R.layout.progress_layout, null);
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
			mBuilder.setView(progressView);
			TextView message=(TextView)progressView.findViewById(R.id.progress_message);
			message.setText(R.string.connectisrunning);
			message.setTextSize(30);
			message.setTextColor(getResources().getColor(R.color.yellow));
			progressAlert = mBuilder.create();
			progressAlert.show();
			progressAlert.setOnKeyListener(new OnKeyListener(){
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN) {
						closeRequestData();
					}
					return false;
				}
			});
			
			if(!checkWifiIscon())
			{
				searchcount=0;
				startconnectWifi();
			}
			
			Log.e(TAG, "=====================start wifi connect============================");
			  //startconnectWifi();
			  mTimer=new Timer();
			  TimerTask task = new TimerTask(){
			        public void run() {
			            // TODO Auto-generated method stub
			            //由于主线程安全，页面的更新需放到主线程中
			            Message message = new Message();      
			            message.what = ParseSeverData.CONNECT_WIFI;      
			            mhHandler.sendMessage(message);    
			        }
			    }; 
			    mTimer.schedule(task, 1000*2, 1000*2);//timer必须和任务在一起使用  必须设三个参数的 不然timer只来一次
			    connetcount=0;
		}else if (id==DIALOG_WIFI_ERROR) {
			LayoutInflater inflater = LayoutInflater.from(this);
    		View tips = inflater.inflate(R.layout.tipsdiagle, null);
    		AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
    		mBuilder.setView(tips);
    		mTipsText=(TextView)tips.findViewById(R.id.tipsinfo);
    		mTipsButton=(Button)tips.findViewById(R.id.button_id);
    		mTipsText.setText(R.string.wifi_error);
    		mTipsButton.setText(R.string.button_ok);
    		progressAlert = mBuilder.create();
			progressAlert.show();
			mTipsButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					closeProgressAlert();
				}
			});
		}else if (id== DIALOG_QUIT){
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("你确定退出吗?");
			builder.setTitle("退出");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {		
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
			});
			final AlertDialog alert = builder.create();
			dialog = alert;
		}
    	
    	return dialog;
    }
    
    //关闭进度对话框
    public void closeProgressAlert(){
    	
    	if (progressAlert!=null) {
			progressAlert.dismiss();
			progressAlert=null;
		}if(mTimer!=null){
    		mTimer.cancel();
    		mTimer=null;
    		mTimer=null;
    	}if (mTask!=null) {
			mTask.cancel();
			mTask=null;
		}	
    }
    
    public void closeRequestData(){
/*    	if (mThread!=null) {
			mThread.stopThread();
			mThread=null;
		}*/
    }
    
    
    //更新显示测试项布局
    public void refreashTestItemLayout(){
    		
    		existitem.clear();
    		noexistitem.clear();
    		
    		mfirstLayout.removeAllViews();
    		msecLayout.removeAllViews();
    		mthirdLayout.removeAllViews();
    	
			if (FileOperate.existTestItem("Wifi")) {
				//existitem.add(mTestWifi);
			}else {
				noexistitem.add(mTestWifi);
			}
			
			if (FileOperate.existTestItem("Screen_Color")) {
				//existitem.add(mTestLcd);
			}else {
				noexistitem.add(mTestLcd);
			}
			
			if (FileOperate.existTestItem("Key")) {
				//existitem.add(mTestKey);
			}else {
				noexistitem.add(mTestKey);
			}
			
			if (FileOperate.existTestItem("Gps")) {
				//existitem.add(mTestGps);
			}else {
				noexistitem.add(mTestGps);
			}
			
			if (FileOperate.existTestItem("GSensor")) {
				//existitem.add(mTestGSeneor);
			}else {
				noexistitem.add(mTestGSeneor);
			}
			
			if (FileOperate.existTestItem("Vibrator")) {
				//existitem.add(mTestVibrator);
			}else {
				noexistitem.add(mTestVibrator);
			}
			
			if (FileOperate.existTestItem("Audio")) {
				//existitem.add(mTestAudio);
			}else {
				noexistitem.add(mTestAudio);
			}
			
			if (FileOperate.existTestItem("Video")) {
				//existitem.add(mTestVideo);
			}else {
				noexistitem.add(mTestVideo);
			}
			
			if (FileOperate.existTestItem("Record")) {
				//existitem.add(mTestRecoder);
			}else {
				noexistitem.add(mTestRecoder);
			}
			
			if (FileOperate.existTestItem("HDMI")) {
				//existitem.add(mTestHDMI);
			}else {
				noexistitem.add(mTestHDMI);
			}
			
			if (FileOperate.existTestItem("Camera")) {
				//existitem.add(mTestCamera);
			}else {
				noexistitem.add(mTestCamera);
			}
			
			if (FileOperate.existTestItem("CameraSub")) {
				//existitem.add(mTestCamera);
			}else {
				noexistitem.add(mTestCameraSub);
			}
			
			if (FileOperate.existTestItem("Otg")) {
				//existitem.add(mTestOtg);
			}else {
				noexistitem.add(mTestOtg);
			}
			
			if (FileOperate.existTestItem("Usb")) {
				//existitem.add(mTestUsb);
			}else {
				noexistitem.add(mTestUsb);
			}
			
			if (FileOperate.existTestItem("StandardUsb")) {
				//existitem.add(mTestUsb);
			}else {
				noexistitem.add(mTestStandardUsb);
			}
			
			if (FileOperate.existTestItem("Sd")) {
				//existitem.add(mTestSD);
			}else {
				noexistitem.add(mTestSD);
			}
			
			if (FileOperate.existTestItem("Sleep-Awake")) {
				//existitem.add(mTestclose);
			}else {
				noexistitem.add(mTestclose);
			}
			
			if (FileOperate.existTestItem("GetCpuID")) {
				//existitem.add(mTestclose);
			}else {
				noexistitem.add(mGetCpuid);
			}
			
			if (FileOperate.existTestItem("Battery")) {
				//existitem.add(mTestclose);
			}else {
				noexistitem.add(mTestBattery);
			}
    		
    		for (int i = 0; i < FileOperate.getTestItemCount(); i++) {
    			String curString=FileOperate.getCurTestItem(i);
    			
    			if (curString.equals("Wifi")) {
    				existitem.add(mTestWifi);
    			}else if (curString.equals("Screen_Color")) {
    				existitem.add(mTestLcd);
    			}else if (curString.equals("Key")) {
    				existitem.add(mTestKey);
    			}else if (curString.equals("Gps")) {
    				existitem.add(mTestGps);
    			}
    			else if (curString.equals("GSensor")) {
    				existitem.add(mTestGSeneor);
    			}else if (curString.equals("Vibrator")) {
    				existitem.add(mTestVibrator);
    			}else if (curString.equals("Audio")) {
    				existitem.add(mTestAudio);
    			}else if (curString.equals("Video")) {
    				existitem.add(mTestVideo);
    			}else if (curString.equals("Record")) {
    				existitem.add(mTestRecoder);
    			}else if (curString.equals("HDMI")) {
    				existitem.add(mTestHDMI);
    			}else if (curString.equals("Camera")) {
    				existitem.add(mTestCamera);
    			}else if (curString.equals("CameraSub")) {
    				existitem.add(mTestCameraSub);
    			}else if (curString.equals("Otg")) {
    				existitem.add(mTestOtg);
    			}else if (curString.equals("Usb")) {
    				existitem.add(mTestUsb);
    			}else if (curString.equals("StandardUsb")) {
    				existitem.add(mTestStandardUsb);
    			}else if (curString.equals("Sd")) {
    				existitem.add(mTestSD);
    			}else if (curString.equals("Sleep-Awake")) {
    				existitem.add(mTestclose);
    			}else if (curString.equals("GetCpuID")){
    				existitem.add(mGetCpuid);
    			}else if (curString.equals("Battery")){
    				existitem.add(mTestBattery);
    			}
			}
    		
			
			for (int i = 0; i < existitem.size(); i++) {
				switch (i/5) {
				case 0:
				{
					existitem.get(i).setVisibility(View.VISIBLE);
					mfirstLayout.addView(existitem.get(i));
					break;
				}
				case 1:
				{
					existitem.get(i).setVisibility(View.VISIBLE);
					msecLayout.addView(existitem.get(i));
					break;
				}
				case 2:
				{
					existitem.get(i).setVisibility(View.VISIBLE);
					mthirdLayout.addView(existitem.get(i));
					break;
				}
				default:
					break;
				}
			}
			
			for (int i = 0; i < noexistitem.size(); i++) {
				noexistitem.get(i).setVisibility(View.GONE);
				//mthirdLayout.addView(noexistitem.get(i));
			}
			mTestWifi.invalidate();
    }
    
    
    
    //wifi接口
    public void setWifi(){
		WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);	
	}
    
    
    
    
    public void startconnectWifi(){
		/*WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiConfiguration wc = new WifiConfiguration();

		wc.SSID = "\"TTPP-LLIINNKK\"";
		wc.preSharedKey  = "\"43214321\"";
		
		
		wc.hiddenSSID = true;
		wc.status = WifiConfiguration.Status.ENABLED;        
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		
		int res = wifi.addNetwork(wc);
		Log.d("WifiPreference", "add Network returned " + res );
		boolean b =wifi.enableNetwork(res, true);
		Log.d("WifiPreference", "enableNetwork returned " + b );
		
		
		String string=checkNetworkInfo();
		if (string.equals("DISCONNECTED")||string.equals("SCANNING")) {
			wifi.reconnect();
		}	*/
    	WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
    	List<AccessPointState> wifiList=ScanResultsAvailable();
    	int count=wifiList.size();
    	int index=0;
    	for ( ;index < wifiList.size(); index++) {
    		AccessPointState scanResult = wifiList.get(index);
    		if (scanResult.ssid.equals(FileOperate.AP_NAME)) {
    			if (scanResult.security != null && !scanResult.security.contains(OPEN))
    			{
    				scanResult.setPassword(FileOperate.AP_PASSWORD);
    			}
    			WifiConfiguration config = new WifiConfiguration();
    			scanResult.updateWifiConfiguration(config);
    			final int networkId = wifi.addNetwork(config);
    			if (networkId == -1) {
    				return;
    			}
    			Log.d("WifiPreference", "add Network returned " + networkId );
    			boolean b =wifi.enableNetwork(networkId, true);
    			Log.d("WifiPreference", "enableNetwork returned " + b );
    			break;
			}
		}
    	
    	//搜索10次
    	searchcount++;
    	if(index==count&&searchcount<=10){
    		mhHandler.postDelayed(mRunnable, 2000);
    	}
	}
    
    Runnable mRunnable = new Runnable() {	
		@Override
		public void run() {
			startconnectWifi();
		}
	};
	
	  Runnable mRunnableTips = new Runnable() {	
			@Override
			public void run() {
				//Toast.makeText(getApplicationContext(), getResources().getString(R.string.connecttips), Toast.LENGTH_SHORT).show();
				
				//FileOperate.MyToast(getApplicationContext(), null, R.string.connecttips);
				mhHandler.postDelayed(mRunnableTips, TipsTime);
			}
		};
    
    private List<AccessPointState> ScanResultsAvailable() {
		
			WifiManager mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);		
			List<AccessPointState> newScanList = new ArrayList<AccessPointState>();

			List<ScanResult> list = mWifiManager.getScanResults();
			if (list != null) {
				for (int i = list.size() - 1; i >= 0; i--) {
					final ScanResult scanResult = list.get(i);

					if (scanResult == null) {
						continue;
					}

					if (AccessPointState.isAdhoc(scanResult)
							|| TextUtils.isEmpty(scanResult.SSID)) {
						continue;
					}

					AccessPointState ap = new AccessPointState(this);
					// Give it the latest state
					ap.updateFromScanResult(scanResult);
					newScanList.add(ap);
				}
			}
			
			return newScanList;
		}
    
    private String checkNetworkInfo(){
        ConnectivityManager conMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo.State wifi = (conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState();
        String string=wifi.toString();
        return string;
    }
    
    public boolean NetworkInfoExist(){
 	   String appname=null,macaddr=null,ipaddrStr=null,apsingalStr=null,linktimeStr=null;
 	   int ipaddrInt;
 	   Integer ipaddrIntaa;
 	   boolean nRet=false;
 	   
 	   mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
 	   mWifiInfo = mWifiManager.getConnectionInfo();
 	   if (mWifiInfo!=null) {
 		appname=mWifiInfo.getSSID();
 		macaddr=mWifiInfo.getMacAddress(); //机器mac地址
 		ipaddrInt=mWifiInfo.getIpAddress();
 		ipaddrStr=intToIp(ipaddrInt);
 		if (appname!=null && macaddr!=null && (!ipaddrStr.equals("0.0.0.0"))) {
 			nRet=true;
 		}
 	   }   
 	   return nRet;
    }
    
    private void registerIntentReceivers() {
		
    	mIntentFilter = new IntentFilter();
    	mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
    	mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
    	mIntentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
    	mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
    	mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    	mIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		
		receiverWifi = new WifiReceiver();
		registerReceiver(receiverWifi, mIntentFilter);
		
		mWifiManager.startScanActive();
		
	}
    
	private void unregisterIntentReceivers() {
		if (receiverWifi != null) {
			unregisterReceiver(receiverWifi);
			receiverWifi=null;
		}

	}

	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {
			Log.i(TAG, "Message Receive"+intent.getAction());
			setWifiStatus();
		}
	}
	
    
    //设置wifi状态
    private void setWifiStatus(){
    	int[] SINGAL_LEVEL = { R.drawable.stat_sys_wifi_signal_0,
				R.drawable.stat_sys_wifi_signal_1,
				R.drawable.stat_sys_wifi_signal_2,
				R.drawable.stat_sys_wifi_signal_3,
				R.drawable.stat_sys_wifi_signal_4 };
    	
    	
    	if (checkWifiIscon()) {
    		/*mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    		mWifiInfo = mWifiManager.getConnectionInfo();
    		if (mWifiInfo != null) {
    			int level = WifiManager.calculateSignalLevel(mWifiInfo.getRssi(), 5);
    			if (level < SINGAL_LEVEL.length) {
    				mWifiStatus.setText("");
    				mWifiStatus.setBackgroundResource(SINGAL_LEVEL[level]);
    			}

    		}*/
    		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    		mWifiInfo = mWifiManager.getConnectionInfo();
    		if (mWifiInfo != null) {		
    			//mWifiStatus.setText(getResources().getText(R.string.apname)+mWifiInfo.getSSID());
    		}else {
    			//mWifiStatus.setText(R.string.apname);
			}
    		
		}else {
			
			//mWifiStatus.setText(R.string.disconnect);
		}
    }
    
    private boolean checkWifiIscon(){
 	   String str=checkNetworkInfo();
 	   if (str.equals("CONNECTED")) {
 			return true;
 		}else {
 			return false;
 		}
    }
    
    private String intToIp(int i){
	   return ( i & 0xFF)+ "." + ((i >> 8 ) & 0xFF)  + "." + ((i >> 16 ) & 0xFF) +"."+((i >> 24 ) & 0xFF ) ;
    }
    
    /**  
     * 返回当前程序版本名  
     */  
    public static String getAppVersionName(Context context) {   
        String versionName = "";   
        try {   
            // ---get the package info---   
            PackageManager pm = context.getPackageManager();   
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);   
            versionName = pi.versionName;   
            if (versionName == null || versionName.length() <= 0) {   
                return "";   
            }   
        } catch (Exception e) {   
            Log.e("VersionInfo", "Exception", e);   
        }   
        return versionName;   
    }
    
       
}