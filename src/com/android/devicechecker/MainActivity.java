package com.android.devicechecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.xml.sax.InputSource;

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
import android.graphics.Color;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.util.AccessPointState;
import com.android.util.FileOperate;
import com.android.util.ParseSeverData;
import com.android.util.TestItemProperty;
import com.android.util.UpTestItem;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	private LinearLayout mfirstLayout, msecLayout, mthirdLayout;

	private TextView mVersion = null;
	private Intent mIntent = null;
	private Button mStartTest = null;
	private Button mModeChange = null;
	// private Button mLinkWanAgain=null; //
	// private Button mReloadTest=null; //重取测试项
	// private Button mActiveMachine=null; //激活

	EditText etPassword;
	private AlertDialog.Builder mBuilderpass;
	private AlertDialog mAlertpass;
	private AlertDialog progressAlert;
	public final static int DIALOG_ADMINSETTINGS = 1; // 弹出输入密码
	public final static int DIALOG_ERROR = 2; // 输入密码失败
	public final static int DIALOG_CONNECT_WIFI = 3; // 连接wifi
	public final static int DIALOG_WIFI_ERROR = 4; // WIFI连接失败
	public final static int DIALOG_GET_ID = 5; // 取id
	public final static int DIALOG_ID_NOINVALID = 6; // 不合法
	public final static int DIALOG_GET_TEST_ITEM = 7; // 取测试项
	public final static int DIALOG_GET_TEST_ERROR = 8; // 取测试项失败
	public final static int DIALOG_UP_TEST_STATUS = 9; // 上传测试状态
	public final static int DIALOG_UP_TEST_STATUS_ERROR = 10; // 上传测试状态失败
	public final static int DIALOG_UP_LOG = 11; // 上传log
	public final static int DIALOG_UP_LOG_ERROR = 12; // 上传log失败
	public final static int DIALOG_START_ACTIVE = 13; // 开始激活
	public final static int DIALOG_START_ACTIVE_ERROR = 14; // 激活失败
	public final static int DIALOG_ACTIVE_CONFRIM = 15; // 激活确认
	public final static int DIALOG_ACTIVE_CONFRIM_ERROR = 16; // 激活确认
	public final static int DIALOG_QUIT = 17; // 确认退出

	public static final String PSK = "PSK";
	public static final String WEP = "WEP";
	public static final String EAP = "EAP";
	public static final String OPEN = "Open";

	Timer mTimer; // 检测
	TimerTask mTask;

	private WifiManager mWifiManager;
	private WifiInfo mWifiInfo;
	private int connetcount = 0; // 连接计数
	private int UpOrDownDataTime = 10; // 上传或下载数据时间
	private int searchcount = 0; // 搜索TTPP-LLIIKK计数
	private UpTestItem mThread;
	private InputSource isSource = null;
	private String retStr = null;
	private int nRet;

	// 提示信息文字 按钮
	TextView mTipsText;
	Button mTipsButton;

	WifiReceiver receiverWifi;
	private IntentFilter mIntentFilter;
	private int TipsTime = 8000;

	Handler mhHandler = new Handler() {
		public void handleMessage(Message msg) {
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

		final Window win = getWindow();
		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);

		// setWifi();

		// mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		// mWifiInfo = mWifiManager.getConnectionInfo();

//		FileOperate.CreateFile(this);
		FileOperate.readFromFile(this);

		FileOperate.clearGobal();

		// FileOperate.ReadTestItemXML();
		FileOperate.setGobalHandle(mhHandler);

		FileOperate.SetTestItemXML(this);	

		refreashTestItemLayout();
		
		if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {
			setTitle(R.string.to_factory_mode);
		} else {
			setTitle(R.string.to_one_mode);
		}

	}

	private void initButton() {
		// TODO Auto-generated method stub		
		DisplayVersion();
		
		mModeChange = (Button) findViewById(R.id.mode_change);
		mModeChange.setVisibility(View.INVISIBLE);
		
		mStartTest = (Button) findViewById(R.id.test_start);
		
		mStartTest.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (FileOperate.getTestItemCount() > 0) {
					reStartTest();
				} else {
					FileOperate.MyToast(getApplicationContext(), null,
							R.string.no_test);
				}

			}
		});
	}

	private void initLayout() {
		mfirstLayout = (LinearLayout) findViewById(R.id.first_layout);
		msecLayout = (LinearLayout) findViewById(R.id.second_layout);
		mthirdLayout = (LinearLayout) findViewById(R.id.third_layout);
				
		mfirstLayout.removeAllViews();
		msecLayout.removeAllViews();
		mthirdLayout.removeAllViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		// return super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int mId = item.getItemId();

		switch (mId) {
		case R.id.main_menu_about:
			Intent mIntent = new Intent(getApplicationContext(),
					aboutActivity.class);
			startActivity(mIntent);
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void DisplayVersion() {
		mVersion = (TextView) findViewById(R.id.test_version);
		mVersion.setText(getString(R.string.cur_version)
				+ getAppVersionName(getApplicationContext()));

	}

	public void ChagngeModeTest() {
		if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {

			FileOperate.setCurmode(false);
			FileOperate.curTestItem = 0;

		} else {
			setContentView(R.layout.main);

			initButton();

			FileOperate.setCurmode(true);
			FileOperate.curTestItem = 0;
		}

		refreashTestItemLayout();
		onStart();

		if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {
			// mModeShow.setText(R.string.to_factory_mode);
			setTitle(R.string.to_factory_mode);
		} else {
			// mModeShow.setText(R.string.to_one_mode);
			setTitle(R.string.to_one_mode);
		}

	}

	public void reStartTest() {
		if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {
			FileOperate.CreateFile(this);
			FileOperate.readFromFile(this);

			FileOperate.setCurmode(true);
			FileOperate.curTestItem = 0;
			FileOperate.restartTest(this);

			mIntent = FileOperate.getCurIntent(MainActivity.this, -1);
			startActivity(mIntent);
		} else {
			FileOperate.CreateFile(this);
			FileOperate.readFromFile(this);

			FileOperate.setCurmode(true);
			FileOperate.curTestItem = 0;
			FileOperate.restartTest(this);
		}

		onStart();

		if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {
			// mModeShow.setText(R.string.to_factory_mode);
			setTitle(R.string.to_factory_mode);
		} else {
			// mModeShow.setText(R.string.to_one_mode);
			setTitle(R.string.to_one_mode);
		}
	}

	// @Override
	// public void onBackPressed() {
	// // TODO Auto-generated method stub
	// //super.onBackPressed();
	// showDialog(DIALOG_QUIT);
	// }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		FileOperate.setCurmode(false);
		ParseSeverData.CloseAllThread();
		closeProgressAlert();
		closeRequestData();

	}

	// @Override
	// protected void onResume() {
	// // TODO Auto-generated method stub
	// super.onResume();
	// registerIntentReceivers();
	// mhHandler.postDelayed(mRunnableTips, TipsTime);
	// }

	// @Override
	// protected void onPause() {
	// // TODO Auto-generated method stub
	// super.onPause();
	// unregisterIntentReceivers();
	// if (mhHandler != null && mRunnableTips != null) {
	// mhHandler.removeCallbacks(mRunnableTips);
	// }
	// }

	// @Override
	// protected void onStop() {
	// // TODO Auto-generated method stub
	// super.onStop();
	// if (mhHandler != null && mRunnable != null) {
	// mhHandler.removeCallbacks(mRunnable);
	// }
	// }

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		for (int i = 0; i < FileOperate.TestInfoInfo.size(); i++) {

			switch (FileOperate.getIndexValue(FileOperate.TestInfoInfo.get(i).getIndex())) {
			case 0: {
				FileOperate.TestInfoInfo.get(i).getButton().setBackgroundColor(Color.WHITE);
				break;
			}
			case 1: {
				FileOperate.TestInfoInfo.get(i).getButton().setBackgroundColor(Color.GREEN);
				break;
			}
			case 2: {
				FileOperate.TestInfoInfo.get(i).getButton().setBackgroundColor(Color.RED);
				break;
			}
			default:
				break;
			}
		}
	}

	//
	public void changeStatus() {
		String str = null;
		if (etPassword != null) {
			str = etPassword.getText().toString();
			if (str.equals("aaa")) {
				FileOperate.changeMode();
				// mAlertpass.dismiss();
				dismissDialog(DIALOG_ADMINSETTINGS);
				ChagngeModeTest();

			} else {
				showDialog(DIALOG_ERROR);
			}
			etPassword.setText("");
		}
	}

	public Dialog onCreateDialog(int id) {
		Dialog dialog = null;

		if (id == DIALOG_ADMINSETTINGS) {
			LayoutInflater inflater = LayoutInflater.from(this);
			View password = inflater.inflate(R.layout.loginchangepass, null);
			password.setMinimumHeight(480);
			password.setMinimumWidth(800);
			mBuilderpass = new AlertDialog.Builder(this);
			mBuilderpass.setTitle("").setView(password);
			etPassword = (EditText) password.findViewById(R.id.inputpassword);
			Button btnlogin = (Button) password.findViewById(R.id.login);
			Button btncancel = (Button) password.findViewById(R.id.cancel);

			btnlogin.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					changeStatus();
				}
			});

			btncancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// mAlertpass.dismiss();
					dismissDialog(DIALOG_ADMINSETTINGS);

				}
			});

			mAlertpass = mBuilderpass.create();
			dialog = mAlertpass;
		} else if (id == DIALOG_ERROR) {
			/*
			 * final AlertDialog.Builder builder = new
			 * AlertDialog.Builder(this);
			 * builder.setMessage(R.string.password_error);
			 * builder.setPositiveButton("OK", null); final AlertDialog alert =
			 * builder.create(); alert.show();
			 */

			LayoutInflater inflater = LayoutInflater.from(this);
			View tips = inflater.inflate(R.layout.tipsdiagle, null);
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
			mBuilder.setView(tips);
			mTipsText = (TextView) tips.findViewById(R.id.tipsinfo);
			mTipsButton = (Button) tips.findViewById(R.id.button_id);
			mTipsText.setText(R.string.password_error);
			mTipsButton.setText(R.string.button_ok);
			progressAlert = mBuilder.create();
			progressAlert.show();
			mTipsButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					closeProgressAlert();
				}
			});
		}

		else if (id == DIALOG_CONNECT_WIFI) {
			closeProgressAlert();

			LayoutInflater inflater = LayoutInflater.from(this);
			View progressView = inflater
					.inflate(R.layout.progress_layout, null);
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
			mBuilder.setView(progressView);
			TextView message = (TextView) progressView
					.findViewById(R.id.progress_message);
			message.setText(R.string.connectisrunning);
			message.setTextSize(30);
			message.setTextColor(getResources().getColor(R.color.yellow));
			progressAlert = mBuilder.create();
			progressAlert.show();
			progressAlert.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK
							&& event.getAction() == KeyEvent.ACTION_DOWN) {
						closeRequestData();
					}
					return false;
				}
			});

			if (!checkWifiIscon()) {
				searchcount = 0;
				startconnectWifi();
			}

			Log.e(TAG,
					"=====================start wifi connect============================");
			// startconnectWifi();
			mTimer = new Timer();
			TimerTask task = new TimerTask() {
				public void run() {
					// TODO Auto-generated method stub
					// 由于主线程安全，页面的更新需放到主线程中
					Message message = new Message();
					message.what = ParseSeverData.CONNECT_WIFI;
					mhHandler.sendMessage(message);
				}
			};
			mTimer.schedule(task, 1000 * 2, 1000 * 2);// timer必须和任务在一起使用
														// 必须设三个参数的 不然timer只来一次
			connetcount = 0;
		} else if (id == DIALOG_WIFI_ERROR) {
			LayoutInflater inflater = LayoutInflater.from(this);
			View tips = inflater.inflate(R.layout.tipsdiagle, null);
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
			mBuilder.setView(tips);
			mTipsText = (TextView) tips.findViewById(R.id.tipsinfo);
			mTipsButton = (Button) tips.findViewById(R.id.button_id);
			mTipsText.setText(R.string.wifi_error);
			mTipsButton.setText(R.string.button_ok);
			progressAlert = mBuilder.create();
			progressAlert.show();
			mTipsButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					closeProgressAlert();
				}
			});
		} else if (id == DIALOG_QUIT) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.exit_tips);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							finish();
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub

						}
					});
			final AlertDialog alert = builder.create();
			dialog = alert;
		}

		return dialog;
	}

	// 关闭进度对话框
	public void closeProgressAlert() {

		if (progressAlert != null) {
			progressAlert.dismiss();
			progressAlert = null;
		}
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
			mTimer = null;
		}
		if (mTask != null) {
			mTask.cancel();
			mTask = null;
		}
	}

	public void closeRequestData() {
		/*
		 * if (mThread!=null) { mThread.stopThread(); mThread=null; }
		 */
	}

	// 更新显示测试项布局
	public void refreashTestItemLayout() {
		initLayout();
		updateTestItem();
		initButton();
	}
	
	private void updateTestItem() {
		for (int i = 0; i < FileOperate.TestInfoInfo.size(); i++) {
			final TestItemProperty mItemProperty = FileOperate.TestInfoInfo.get(i);
			
			//set onclick button event
			mItemProperty.getButton().setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent mIntent = new Intent(getApplicationContext(), mItemProperty.getCls());
					startActivity(mIntent);
				}
			});
			
			switch (i / 6) {
			case 0: {
				LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
						(int) getResources().getDimension(
								R.dimen.main_menu_button_width2),
						(int) getResources().getDimension(
								R.dimen.test_item_bottom_button_heith2));
				btnParams.setMargins(10, 10, 10, 10);
				mfirstLayout.addView(mItemProperty.getButton(), btnParams);
				
				break;
			}
			case 1: {
				LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
						(int) getResources().getDimension(
								R.dimen.main_menu_button_width2),
						(int) getResources().getDimension(
								R.dimen.test_item_bottom_button_heith2));
				btnParams.setMargins(10, 10, 10, 10);
				msecLayout.addView(mItemProperty.getButton(), btnParams);
				break;
			}
			case 2: {
				mthirdLayout.addView(mItemProperty.getButton());
				break;
			}
			default:
				break;
			}
		}
	}

	// wifi
	public void setWifi() {
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);
	}

	public void startconnectWifi() {
		/*
		 * WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
		 * WifiConfiguration wc = new WifiConfiguration();
		 * 
		 * wc.SSID = "\"TTPP-LLIINNKK\""; wc.preSharedKey = "\"43214321\"";
		 * 
		 * 
		 * wc.hiddenSSID = true; wc.status = WifiConfiguration.Status.ENABLED;
		 * wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		 * wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		 * wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		 * wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		 * wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		 * wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		 * 
		 * int res = wifi.addNetwork(wc); Log.d("WifiPreference",
		 * "add Network returned " + res ); boolean b =wifi.enableNetwork(res,
		 * true); Log.d("WifiPreference", "enableNetwork returned " + b );
		 * 
		 * 
		 * String string=checkNetworkInfo(); if
		 * (string.equals("DISCONNECTED")||string.equals("SCANNING")) {
		 * wifi.reconnect(); }
		 */
		WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
		List<AccessPointState> wifiList = ScanResultsAvailable();
		int count = wifiList.size();
		int index = 0;
		for (; index < wifiList.size(); index++) {
			AccessPointState scanResult = wifiList.get(index);
			if (scanResult.ssid.equals(FileOperate.AP_NAME)) {
				if (scanResult.security != null
						&& !scanResult.security.contains(OPEN)) {
					scanResult.setPassword(FileOperate.AP_PASSWORD);
				}
				WifiConfiguration config = new WifiConfiguration();
				scanResult.updateWifiConfiguration(config);
				final int networkId = wifi.addNetwork(config);
				if (networkId == -1) {
					return;
				}
				Log.d("WifiPreference", "add Network returned " + networkId);
				boolean b = wifi.enableNetwork(networkId, true);
				Log.d("WifiPreference", "enableNetwork returned " + b);
				break;
			}
		}

		// 搜索10次
		searchcount++;
		if (index == count && searchcount <= 10) {
			mhHandler.postDelayed(mRunnable, 2000);
		}
	}

	Runnable mRunnable = new Runnable() {
		public void run() {
			startconnectWifi();
		}
	};

	Runnable mRunnableTips = new Runnable() {
		public void run() {
			// Toast.makeText(getApplicationContext(),
			// getResources().getString(R.string.connecttips),
			// Toast.LENGTH_SHORT).show();

			// FileOperate.MyToast(getApplicationContext(), null,
			// R.string.connecttips);
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

	private String checkNetworkInfo() {
		ConnectivityManager conMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo.State wifi = (conMan
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState();
		String string = wifi.toString();
		return string;
	}

	public boolean NetworkInfoExist() {
		String appname = null, macaddr = null, ipaddrStr = null, apsingalStr = null, linktimeStr = null;
		int ipaddrInt;
		Integer ipaddrIntaa;
		boolean nRet = false;

		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
		if (mWifiInfo != null) {
			appname = mWifiInfo.getSSID();
			macaddr = mWifiInfo.getMacAddress(); // 机器mac地址
			ipaddrInt = mWifiInfo.getIpAddress();
			ipaddrStr = intToIp(ipaddrInt);
			if (appname != null && macaddr != null
					&& (!ipaddrStr.equals("0.0.0.0"))) {
				nRet = true;
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
			receiverWifi = null;
		}

	}

	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {
			Log.i(TAG, "Message Receive" + intent.getAction());
			setWifiStatus();
		}
	}

	// 设置wifi状态
	private void setWifiStatus() {
		int[] SINGAL_LEVEL = { R.drawable.stat_sys_wifi_signal_0,
				R.drawable.stat_sys_wifi_signal_1,
				R.drawable.stat_sys_wifi_signal_2,
				R.drawable.stat_sys_wifi_signal_3,
				R.drawable.stat_sys_wifi_signal_4 };

		if (checkWifiIscon()) {
			/*
			 * mWifiManager = (WifiManager)
			 * getSystemService(Context.WIFI_SERVICE); mWifiInfo =
			 * mWifiManager.getConnectionInfo(); if (mWifiInfo != null) { int
			 * level = WifiManager.calculateSignalLevel(mWifiInfo.getRssi(), 5);
			 * if (level < SINGAL_LEVEL.length) { mWifiStatus.setText("");
			 * mWifiStatus.setBackgroundResource(SINGAL_LEVEL[level]); }
			 * 
			 * }
			 */
			mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			mWifiInfo = mWifiManager.getConnectionInfo();
			if (mWifiInfo != null) {
				// mWifiStatus.setText(getResources().getText(R.string.apname)+mWifiInfo.getSSID());
			} else {
				// mWifiStatus.setText(R.string.apname);
			}

		} else {

			// mWifiStatus.setText(R.string.disconnect);
		}
	}

	private boolean checkWifiIscon() {
		String str = checkNetworkInfo();
		if (str.equals("CONNECTED")) {
			return true;
		} else {
			return false;
		}
	}

	private String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
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