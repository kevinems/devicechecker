package com.android.devicechecker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.devicechecker.interfaces.ItestActTemplate;
import com.android.util.FileOperate;

public class TestVibratorActivity extends ItestActTemplate {
	private static final String TAG = "TestGSensor";

	private Button mYes = null;
	private Button mNo = null;
	private Vibrator mVibrator;
	private AudioManager mAudioManager;

	private AlertDialog progressAlert;
	boolean checkOk = false; //

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("TestGSensor~!~!~!~!~!~!~!~!");
		final Window win = getWindow();
		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.test_vibrator);

		mYes = (Button) findViewById(R.id.but_ok);
		mNo = (Button) findViewById(R.id.but_nook);
		setYesBtnOnClickListener(mYes, FileOperate.TestItemVibrator);
		setNoBtnOnClickListener(mNo, FileOperate.TestItemVibrator);

		mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
				AudioManager.VIBRATE_SETTING_ON);

		mVibrator.vibrate(60000);
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

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			}

		}
	};

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

}
