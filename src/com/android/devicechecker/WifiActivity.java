package com.android.devicechecker;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.xml.sax.InputSource;

import com.android.util.AccessPointState;
import com.android.util.FileOperate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class WifiActivity extends Activity implements OnClickListener {
	private static final String TAG = "WifiActivity";
	// private Button btLink,btNoLink;
	private Button mNo = null;
	private Button mLink = null;
	private Button mTestWan = null;
	private Button mYes = null;
	private TextView wifiTextView, wifiLinkInfo;
	Timer timer;
	private final int CHECK_CONNECT = 0x200;
	private final int CHECK_DISCONNECT = 0x201;
	private final int CHECK_TEST_NET = 0x202;
	private int connetcount, netWan = 0; // 杩炴帴璁℃暟,鍙栫綉缁滄暟鎹�
	private int searchcount = 0; // 鎼滅储TTPP-LLIIKK璁℃暟
	private String noticeString;

	private int curStatus;

	private static final int STATE_DISABLED = 0;
	private static final int STATE_ENABLED = 1;
	private static final int STATE_DISENABLEING = 2;
	private static final int STATE_ENABLEING = 3;

	private AlertDialog mAlert, progressAlert;

	private WifiManager mWifiManager;
	private WifiInfo mWifiInfo;
	getXmlThread thread;
	String webString[] = { "http://www.baidu.com", "http://www.qq.com",
			"http://www.sina.com.cn", "www.163.com" };
	private List<AccessPointState> mApScanList = new ArrayList<AccessPointState>();
	public static final String PSK = "PSK";
	public static final String WEP = "WEP";
	public static final String EAP = "EAP";
	public static final String OPEN = "Open";

	private static final int SECURITY_AUTO = 0;
	private static final int SECURITY_NONE = 1;
	private static final int SECURITY_WEP = 2;
	private static final int SECURITY_PSK = 3;
	private static final int SECURITY_EAP = 4;
	ListView mListView;

	WifiReceiver receiverWifi;
	private IntentFilter mIntentFilter;

	boolean checkOk = false; //

	private Handler mpopnetmoviehand = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHECK_CONNECT: {
				if (checkWifiIscon() && NetworkInfoExist()) {
					if (timer != null) {
						timer.cancel();
						timer = null;
					}
					if (progressAlert != null) {
						progressAlert.dismiss();
						progressAlert = null;
					}
					FileOperate.MyToast(getApplicationContext(), null,
							R.string.wificonnect);
					AddListData();

				} else {
					connetcount++;
					if (connetcount > 20) {
						if (timer != null) {
							timer.cancel();
							timer = null;
						}
						if (progressAlert != null) {
							progressAlert.dismiss();
							progressAlert = null;
						}
						FileOperate.MyToast(getApplicationContext(), null,
								R.string.wifidisconnect);
					}
				}
				break;
			}
			case CHECK_DISCONNECT: {
				if (checkWifiIscon()) {
					connetcount++;
					if (connetcount > 20) {
						if (timer != null) {
							timer.cancel();
							timer = null;
						}
						if (progressAlert != null) {
							progressAlert.dismiss();
							progressAlert = null;
						}
						wifiTextView.setText(R.string.wificonnect);

						setValue(2);
						finish();
					}

				} else {
					if (timer != null) {
						timer.cancel();
						timer = null;
					}
					if (progressAlert != null) {
						progressAlert.dismiss();
						progressAlert = null;
					}
					wifiTextView.setText(R.string.wifidisconnect);

					/*
					 * checkOk=true;
					 * showDialog(FileOperate.DIALOG_UP_TEST_ITEM);
					 * ParseSeverData
					 * .startUpTestItemThread("Wifi",mpopnetmoviehand
					 * ,FileOperate
					 * .DIALOG_UP_TEST_ITEM,FileOperate.CHECK_SUCCESS);
					 */
					setValue(1);
					// FileOperate.setCurTest(true);
					finish();

					if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {
						Intent mIntent = FileOperate.getCurIntent(
								WifiActivity.this, "Wifi");
						if (mIntent != null) {
							startActivity(mIntent);
						}
					}
				}
				break;
			}

			case CHECK_TEST_NET: {
				InputSource isSource = null;
				isSource = getXmlThread.getInputSource();

				if (isSource != null && isSource.toString().length() > 0) {
					if (progressAlert != null) {
						progressAlert.dismiss();
						progressAlert = null;
					}

					wifiTextView.setText(R.string.browserconnect);

					/*
					 * checkOk=true;
					 * showDialog(FileOperate.DIALOG_UP_TEST_ITEM);
					 * ParseSeverData
					 * .startUpTestItemThread("Wifi",mpopnetmoviehand
					 * ,FileOperate
					 * .DIALOG_UP_TEST_ITEM,FileOperate.CHECK_SUCCESS);
					 */
					setValue(1);
					// FileOperate.setCurTest(true);
					finish();

					if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {
						Intent mIntent = FileOperate.getCurIntent(
								WifiActivity.this, "Wifi");
						if (mIntent != null) {
							startActivity(mIntent);
						}
					}

				} else {

					netWan++;
					if (netWan >= webString.length) {
						if (progressAlert != null) {
							progressAlert.dismiss();
							progressAlert = null;
						}
						wifiTextView.setText(R.string.browserdisconnect);

						/*
						 * checkOk=false;
						 * showDialog(FileOperate.DIALOG_UP_TEST_ITEM);
						 * ParseSeverData
						 * .startUpTestItemThread("Wifi",mpopnetmoviehand
						 * ,FileOperate
						 * .DIALOG_UP_TEST_ITEM,FileOperate.CHECK_FAILURE);
						 */
						setValue(2);
						// FileOperate.setCurmode(false);
						finish();
					} else {
						try {
							URL url = new URL(webString[netWan]);
							thread = new getXmlThread(url, mpopnetmoviehand,
									CHECK_TEST_NET);
							thread.start();
						} catch (Exception e) {
							Log.e("========WifiActivity=======", e.toString());
						}
					}

				}
				break;
			}
			}
			super.handleMessage(msg);
		}
	};

	private void setValue(int value) {
		FileOperate.setIndexValue(FileOperate.TestItemWifi, value);
		FileOperate.writeToFile(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Window win = getWindow();
		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.wifi);

		wifiTextView = (TextView) findViewById(R.id.wifi_state_text);
		wifiLinkInfo = (TextView) findViewById(R.id.wifitext);

		mListView = (ListView) findViewById(R.id.wifilist);

		mYes = (Button) findViewById(R.id.but_ok);
		mNo = (Button) findViewById(R.id.but_nook);
		mLink = (Button) findViewById(R.id.connectwifi);
		mTestWan = (Button) findViewById(R.id.test_net);
		mYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				setValue(1);
				finish();

				if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {
					Intent mIntent = FileOperate.getCurIntent(
							WifiActivity.this, "Wifi");
					if (mIntent != null) {
						startActivity(mIntent);
					}
				}
			}
		});

		mNo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/*
				 * checkOk=false; showDialog(FileOperate.DIALOG_UP_TEST_ITEM);
				 * ParseSeverData
				 * .startUpTestItemThread("Screen_Color",mpopnetmoviehand
				 * ,FileOperate.DIALOG_UP_TEST_ITEM,FileOperate.CHECK_FAILURE);
				 */
				setValue(2);
				// FileOperate.setCurmode(false);
				finish();
			}
		});

		mTestWan.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				netWan = 0;
				startConnectNet();

			}
		});

		mLink.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startwificonnect();
			}
		});

		setWifi();

		AddListData();
		mpopnetmoviehand.postDelayed(mRunnableCheck, 5000);
		if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {
			FileOperate.setIndexValue(FileOperate.TestItemWifi,
					FileOperate.CHECK_FAILURE);
			FileOperate.writeToFile(this);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterIntentReceivers();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (mpopnetmoviehand != null && mRunnableCheck != null) {
			mpopnetmoviehand.removeCallbacks(mRunnableCheck);
		}

		if (mpopnetmoviehand != null && mRunnable != null) {
			mpopnetmoviehand.removeCallbacks(mRunnable);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (progressAlert != null) {
			progressAlert.dismiss();
			progressAlert = null;
		}
		if (thread != null) {
			thread.stopThread();
			// thread.stop();
			thread.interrupt();
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		if (progressAlert != null) {
			progressAlert.dismiss();
			progressAlert = null;

		}
		if (thread != null) {
			thread.stopThread();
			thread.stop();
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerIntentReceivers();
	}

	private void startWifi() {
		final Intent mIntent = new Intent();
		final ComponentName component = new ComponentName(
				"com.android.settings",
				"com.android.settings.wifi.WifiSettings");
		if (component != null) {
			mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mIntent.setComponent(component);
			startActivity(mIntent);
		}

	}

	private void startBrowser() {
		Uri mUri = Uri.parse("http://www.qq.com");
		Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
		startActivity(mIntent);
	}

	private static int getWifiState(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		int wifiState = wifiManager.getWifiState();
		if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
			return STATE_DISABLED;
		} else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {

			return STATE_ENABLED;
		} else {

			return STATE_ENABLEING;
		}
	}

	private void toggleWifi(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		int wifiState = getWifiState(context);
		if (wifiState == STATE_ENABLED) {
			wifiManager.setWifiEnabled(false);

		} else if (wifiState == STATE_DISABLED) {
			wifiManager.setWifiEnabled(true);
		}

	}

	public void setWifi() {
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);

	}

	public void startconnectWifi() {

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

		// 鎼滅储10娆�
		searchcount++;
		if (index == count && searchcount <= 10) {
			mpopnetmoviehand.postDelayed(mRunnable, 2000);
		}

	}

	Runnable mRunnable = new Runnable() {
		public void run() {
			startconnectWifi();
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

	public void disconnectWifi() {
		WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);

		wifi.disconnect();

		List<WifiConfiguration> wifiList;
		wifiList = wifi.getConfiguredNetworks();
		WifiConfiguration mwifiConfiguration;

		int i = 0;
		int count = 0;
		while (true) {
			wifiList = wifi.getConfiguredNetworks();
			count = wifiList.size();
			for (i = 0; i < count; i++) {
				mwifiConfiguration = wifiList.get(i);
				if (mwifiConfiguration.SSID.equals("\"TTPP-LLIINNKK\"")) {
					wifi.removeNetwork(mwifiConfiguration.networkId);
					break;
				}
			}

			if (i == count) {
				break;
			}

		}

	}

	// 寮�杩炴帴wifi
	private void startwificonnect() {
		if (!checkWifiIscon()) {
			searchcount = 0;
			startconnectWifi();
		}

		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				// TODO Auto-generated method stub
				// 鐢变簬涓荤嚎绋嬪畨鍏紝椤甸潰鐨勬洿鏂伴渶鏀惧埌涓荤嚎绋嬩腑
				Message message = new Message();
				message.what = CHECK_CONNECT;
				mpopnetmoviehand.sendMessage(message);
			}
		};
		timer.schedule(task, 1000 * 2, 1000 * 2);// timer蹇呴』鍜屼换鍔″湪涓�捣浣跨敤
													// 蹇呴』璁句笁涓弬鏁扮殑
													// 涓嶇劧timer鍙潵涓�
		connetcount = 0;

		LayoutInflater inflater = LayoutInflater.from(this);
		View progressView = inflater.inflate(R.layout.progress_layout, null);
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
		mBuilder.setView(progressView);
		TextView message = (TextView) progressView
				.findViewById(R.id.progress_message);
		message.setText(R.string.connectisrunning);
		message.setTextSize(30);
		progressAlert = mBuilder.create();
		progressAlert.show();

		progressAlert.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					if (timer != null) {
						timer.cancel();
						timer = null;
					}
					if (progressAlert != null) {
						progressAlert.dismiss();
						progressAlert = null;
					}
				}
				return true;
			}
		});
	}

	// 寮�鏂紑wifi
	private void startdisconnectWifi() {
		disconnectWifi();

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				// TODO Auto-generated method stub
				// 鐢变簬涓荤嚎绋嬪畨鍏紝椤甸潰鐨勬洿鏂伴渶鏀惧埌涓荤嚎绋嬩腑
				Message message = new Message();
				message.what = CHECK_DISCONNECT;
				mpopnetmoviehand.sendMessage(message);
			}
		};
		timer.schedule(task, 1000 * 2, 1000 * 2);// timer蹇呴』鍜屼换鍔″湪涓�捣浣跨敤
													// 蹇呴』璁句笁涓弬鏁扮殑
													// 涓嶇劧timer鍙潵涓�
		connetcount = 0;

		LayoutInflater inflater = LayoutInflater.from(this);
		View progressView = inflater.inflate(R.layout.progress_layout, null);
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
		mBuilder.setView(progressView);
		TextView message = (TextView) progressView
				.findViewById(R.id.progress_message);
		message.setText(R.string.disconnectisrunning);
		message.setTextSize(30);
		progressAlert = mBuilder.create();
		progressAlert.show();
	}

	public void startConnectNet() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View progressView = inflater.inflate(R.layout.progress_layout, null);
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
		mBuilder.setView(progressView);
		TextView message = (TextView) progressView
				.findViewById(R.id.progress_message);
		message.setText(R.string.test_neting);
		message.setTextSize(30);
		progressAlert = mBuilder.create();
		progressAlert.show();

		try {
			URL url;
			if (netWan < webString.length) {
				url = new URL(webString[netWan]);
			} else {
				url = new URL(webString[0]);
			}

			thread = new getXmlThread(url, mpopnetmoviehand, CHECK_TEST_NET);
			thread.start();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void onClick(View v) {
		String str;
	}

	private boolean checkWifiIscon() {
		String str = checkNetworkInfo();
		if (str.equals("CONNECTED")) {
			return true;
		} else {
			return false;
		}
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
			macaddr = mWifiInfo.getMacAddress(); // 鏈哄櫒mac鍦板潃
			ipaddrInt = mWifiInfo.getIpAddress();
			ipaddrStr = intToIp(ipaddrInt);
			if (appname != null && macaddr != null
					&& (!ipaddrStr.equals("0.0.0.0"))) {
				nRet = true;
			}
		}

		return nRet;
	}

	private String checkNetworkInfo() {
		ConnectivityManager conMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo.State wifi = (conMan
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState();
		String string = wifi.toString();
		return string;
	}

	public static String BigIntToString(BigInteger ipInBigInt) {
		byte[] bytes = ipInBigInt.toByteArray();
		byte[] unsignedBytes = bytes;

		// 鍘婚櫎绗﹀彿浣�
		try {
			String ip = InetAddress.getByAddress(unsignedBytes).toString();
			return ip.substring(ip.indexOf('/') + 1).trim();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	private String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
	}

	// 寰楀埌杩炴帴缃戠粶淇℃伅
	private String getNetworkInfo() {

		String appname = null, macaddr = null, ipaddrStr = null, apsingalStr = null, linktimeStr = null;
		int ipaddrInt;
		Integer ipaddrIntaa;

		int[] SINGAL_LEVEL = { R.drawable.stat_sys_wifi_signal_0,
				R.drawable.stat_sys_wifi_signal_1,
				R.drawable.stat_sys_wifi_signal_2,
				R.drawable.stat_sys_wifi_signal_3,
				R.drawable.stat_sys_wifi_signal_4 };

		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
		if (mWifiInfo != null) {
			appname = getResources().getString(R.string.apname)
					+ mWifiInfo.getSSID();
			macaddr = "\n" + getResources().getString(R.string.mac_addr)
					+ mWifiInfo.getMacAddress(); // 鏈哄櫒mac鍦板潃
			ipaddrInt = mWifiInfo.getIpAddress();
			ipaddrStr = "\n" + getResources().getString(R.string.ip_addr)
					+ intToIp(ipaddrInt);
			// apsingalStr="\n"+getResources().getString(R.string.singal_speed)+mWifiInfo.getRssi();

			linktimeStr = "\n" + getResources().getString(R.string.linktime)
					+ connetcount * 2
					+ getResources().getString(R.string.time_second);

			noticeString = appname + macaddr + ipaddrStr + linktimeStr;

			TextView tView = (TextView) findViewById(R.id.wifisingaltext);
			tView.setText(R.string.singal_speed);
			int level = WifiManager
					.calculateSignalLevel(mWifiInfo.getRssi(), 5);
			ImageView ImageView = (ImageView) findViewById(R.id.wifisingalimage);
			if (level < SINGAL_LEVEL.length) {
				ImageView.setBackgroundResource(SINGAL_LEVEL[level]);
			}

		}
		return (noticeString);
	}

	private void handleScanResultsAvailable() {
		synchronized (this) {

			WifiManager mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
			// In the end, we'll moved the ones no longer seen into the
			// mApOtherList
			List<AccessPointState> oldScanList = mApScanList;
			List<AccessPointState> newScanList = new ArrayList<AccessPointState>(
					oldScanList.size());

			List<ScanResult> list = mWifiManager.getScanResults();
			if (list != null) {
				for (int i = list.size() - 1; i >= 0; i--) {
					final ScanResult scanResult = list.get(i);

					if (scanResult == null) {
						continue;
					}

					/*
					 * Ignore adhoc, enterprise-secured, or hidden networks.
					 * Hidden networks show up with empty SSID.
					 */
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

			mApScanList = newScanList;
		}

	}

	private void AddListData() {
		String haveSecurity = null;
		int[] SINGAL_LEVEL = { R.drawable.stat_sys_wifi_signal_0,
				R.drawable.stat_sys_wifi_signal_1,
				R.drawable.stat_sys_wifi_signal_2,
				R.drawable.stat_sys_wifi_signal_3,
				R.drawable.stat_sys_wifi_signal_4 };

		handleScanResultsAvailable();
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < mApScanList.size(); i++) {
			AccessPointState ap = mApScanList.get(i);
			if (ap != null) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemScript", ap.getHumanReadableSsid());
				mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				mWifiInfo = mWifiManager.getConnectionInfo();
				if (mWifiInfo == null || mWifiInfo.getBSSID() == null) {
					map.put("ItemLink", "");
				} else {
					if (mWifiInfo.getSSID().equals(ap.getHumanReadableSsid())) {
						map.put("ItemLink", "已连接");
					} else {
						map.put("ItemLink", "");
					}
				}

				haveSecurity = ap.security;
				if (haveSecurity != null && !haveSecurity.contains(OPEN)) {
					map.put("encrypt", R.drawable.ic_lock_lock);
				} else {
					map.put("encrypt", null);
				}
				int level = WifiManager.calculateSignalLevel(ap.signal, 5);
				if (level < SINGAL_LEVEL.length) {
					map.put("signal", SINGAL_LEVEL[level]);
				}
				listItem.add(map);
			}
		}

		//
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,// 鏁版嵁婧�
				R.layout.wifilistitem,//
				//
				new String[] { "ItemScript", "ItemLink", "encrypt", "signal" },
				//
				new int[] { R.id.title, R.id.linkstatus, R.id.encrypt,
						R.id.signal });

		//
		mListView.setAdapter(listItemAdapter);

	}

	private void registerIntentReceivers() {
		IntentFilter mFilter;

		mFilter = new IntentFilter();
		mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);

		receiverWifi = new WifiReceiver();
		registerReceiver(receiverWifi, mFilter);

	}

	private Runnable mRunnableCheck = new Runnable() {
		public void run() {
			AddListData();
			mpopnetmoviehand.postDelayed(mRunnableCheck, 5000);
		}
	};

	private void unregisterIntentReceivers() {
		if (receiverWifi != null) {
			unregisterReceiver(receiverWifi);
			receiverWifi = null;
		}

	}

	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {
			AddListData();

		}
	}

	public Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		if (id == FileOperate.DIALOG_UP_TEST_ITEM) {
			LayoutInflater inflater = LayoutInflater.from(this);
			View progressView = inflater
					.inflate(R.layout.progress_layout, null);
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
			mBuilder.setView(progressView);
			TextView message = (TextView) progressView
					.findViewById(R.id.progress_message);
			message.setText(R.string.up_test_item);
			message.setTextSize(30);
			message.setTextColor(getResources().getColor(R.color.yellow));
			progressAlert = mBuilder.create();
			progressAlert.show();
			dialog = progressAlert;
			progressAlert.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK
							&& event.getAction() == KeyEvent.ACTION_DOWN) {

					}
					return false;
				}
			});
		}

		return dialog;
	}

	//
	public static class getXmlThread extends Thread {

		public URL mUrl;
		private Handler mHandle;
		private int msg;
		static InputSource is = null;
		Timer timer2;

		public getXmlThread(URL url, Handler Handle, int message) {

			mHandle = Handle;
			mUrl = url;
			msg = message;
			stopThread();
			timer2 = new Timer();
			TimerTask timeTask = new TimerTask() {

				@Override
				public void run() {
					Message m = new Message();
					m.what = msg;
					mHandle.sendMessage(m);
				}
			};
			timer2.schedule(timeTask, 5000); //

		}

		@Override
		public void run() {
			try {
				is = null;
				is = new InputSource(mUrl.openStream());
			} catch (IOException e) {
				e.printStackTrace();

			}
			super.run();
		}

		public void stopThread() {
			if (timer2 != null) {
				timer2.cancel();
				timer2 = null;
			}
		}

		//
		public static InputSource getInputSource() {
			return is;
		}
	}

}
