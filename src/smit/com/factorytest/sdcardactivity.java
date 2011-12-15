package smit.com.factorytest;

import java.io.File;

import smit.com.util.FileOperate;
import smit.com.util.ParseSeverData;
import smit.com.util.SDCard;
import smit.com.factorytest.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class sdcardactivity extends Activity {
	private static final String TAG = "sdcardactivity";
	TextView mSdTextView, mSdTips;
	TextView mSdSizeTextView;
	SDCard.OnListener m_sdListerer;
	SDCard m_SdCard;
	int curStatus = 1; // 1检测插入 2是检测拔出

	private Button mYes=null;
	private Button mNo = null;

	private AlertDialog progressAlert;
	boolean checkOk = false; // 是否是成功

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		final Window win = getWindow();
		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.sd_state);
		mSdTextView = (TextView) findViewById(R.id.sd_state_text);
		mSdTips = (TextView) findViewById(R.id.sdtips);

		mSdTips.setText(R.string.sd_test_hinta);
		
		mYes=(Button)findViewById(R.id.but_ok);
		
		mYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {			
				setValue(1);
				finish();
				
				if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
					Intent mIntent = FileOperate.getCurIntent(sdcardactivity.this,"Sd");
					if (mIntent != null) {
						startActivity(mIntent);
					}
				}
			}
		});

		mNo = (Button) findViewById(R.id.but_nook);

		mNo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/*
				 * checkOk=false; showDialog(FileOperate.DIALOG_UP_TEST_ITEM);
				 * ParseSeverData
				 * .startUpTestItemThread("Sd",mHandler,FileOperate
				 * .DIALOG_UP_TEST_ITEM,FileOperate.CHECK_FAILURE);
				 */
				setValue(2);
				FileOperate.setCurmode(false);
				finish();
			}
		});

		if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {
			FileOperate.setIndexValue(FileOperate.TestItemSd,
					FileOperate.CHECK_FAILURE);
			FileOperate.writeToFile(this);
		}

	}

	private void setValue(int value) {
		FileOperate.setIndexValue(FileOperate.TestItemSd, value);
		FileOperate.writeToFile(this);

		// ParseSeverData.startUpTestItemThread("Sd");
	}

	@Override
	protected void onStart() {
		super.onStart();
		m_SdCard = new SDCard(getApplicationContext());
		String status = Environment.getExternalStorageState();

		if (status.equals(Environment.MEDIA_MOUNTED)) {

			if (isExistLogoFile()) {
				mSdTips.setVisibility(View.GONE);
				mNo.setVisibility(View.GONE);
				mSdTextView.setText(R.string.test_sd_success);
				SetLogoStatus();
				
				mHandler.postDelayed(mHaveSDRunnable, 2000);
			}else{
				mSdTips.setVisibility(View.GONE);
				mNo.setVisibility(View.GONE);
				mSdTextView.setText(R.string.test_sd_success);

				mHandler.postDelayed(mHaveSDRunnable, 2000);
			}

		} else {
			m_SdCard.Start();
			m_sdListerer = new SDCard.OnListener() {
				public void onSDCardChanged(int nState) {
					if (nState == 1) {

						if (curStatus == 1) {

							if (isExistLogoFile()) {
								mSdTextView.setText(R.string.sd_test_hintc);

								SetLogoStatus();
								mHandler.postDelayed(mupdateLogoRunnable, 2000);
							} else {
								mSdTextView.setText(R.string.sd_test_in);
								curStatus = 2;
								mSdTips.setText(R.string.sd_test_hintb);

							}

						}

					} else {
						if (curStatus == 2) {
							mSdTextView.setText(R.string.sd_test_out);
							setValue(1);
							finish();

							if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {
								Intent mIntent = FileOperate.getCurIntent(
										sdcardactivity.this, "Sd");
								startActivity(mIntent);
							}
						} else {
							// 不合格
						}
					}
				}
			};
			m_SdCard.setListener(m_sdListerer);
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		m_SdCard.Stop();
		if (mHandler != null) {
			if (mHaveSDRunnable != null) {
				mHandler.removeCallbacks(mHaveSDRunnable);
			}
			if (mupdateLogoRunnable != null) {
				mHandler.removeCallbacks(mupdateLogoRunnable);
			}
		}
	}

	private Runnable mHaveSDRunnable = new Runnable() {
		public void run() {
			if(isExistLogoFile()){
				if (getLogoStatus()) {
					setValue(1);
					finish();
					if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {
						Intent mIntent = FileOperate.getCurIntent(sdcardactivity.this,
								"Sd");
						startActivity(mIntent);
					}
				} else {
					mHandler.postDelayed(mHaveSDRunnable, 2000);
				}
			}else{
				setValue(1);
				finish();
				if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {
					Intent mIntent = FileOperate.getCurIntent(sdcardactivity.this,
							"Sd");
					startActivity(mIntent);
				}
			}
			
			
		}

	};

	private Runnable mupdateLogoRunnable = new Runnable() {
		public void run() {
			if (getLogoStatus()) {
				curStatus = 2;
				mSdTextView.setText(R.string.sd_test_in);
				mSdTips.setText(R.string.sd_test_hintb);
			} else {
				mHandler.postDelayed(mupdateLogoRunnable, 2000);
			}

		}
	};

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			}

		}
	};


	public void SetLogoStatus() {
		SystemProperties.set("tcc.smit.mklogo", "2");
	}

	public static boolean isExistLogoFile() {
		File TestItemFile = new File("/mnt/sdcard/tflash/SMIT/Logo/lk_logo.bmp");
		if (TestItemFile.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean getLogoStatus() {
		Object localObject1 = "tcc.smit.mklogo";
		String str = SystemProperties.get("tcc.smit.mklogo", "0");

		if (str != null && str.length() > 0 && str.equals("0")) {
			return true;
		} else {
			return false;
		}
	}

}
