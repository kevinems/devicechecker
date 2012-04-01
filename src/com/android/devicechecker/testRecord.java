package com.android.devicechecker;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

import com.android.devicechecker.R;
import com.android.util.FileOperate;
import com.android.util.MediaRecoderControl;
import com.android.util.MultiPlayer;
import com.android.util.ParseSeverData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class testRecord extends Activity {
	private static final String TAG = "MediaRecoderactivity";
	MediaRecoderControl mMediaRecoder = null;
	Button mButStartRecord;
	// Button mButStopRecord;
	Button mButPlayRecord;
	// Button mButStopPlayRecord;
	TextView mHintText;
	MultiPlayer MultiPlayer = null;
	private Timer mTimer = null, timer = null;
	private TimerTask mTimerTask;
	int mSecond = 0;
	String str1;
	private static final int UPDATE_TIME = 1;
	private static final int CHECK_SEEKBAR = 2;
	private Button mYes = null;
	private Button mNo = null;
	private SeekBar seekBar = null;
	private SeekBar mSeekBar = null;
	AudioManager mAudioManager;
	int maxVolume;

	private AlertDialog progressAlert;
	boolean checkOk = false; // 鏄惁鏄垚鍔�

	private void setValue(int value) {
		FileOperate.setIndexValue(FileOperate.TestItemRecord, value);
		FileOperate.writeToFile(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		final Window win = getWindow();
		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.media_recoder);
		mButStartRecord = (Button) findViewById(R.id.record_but_start_record);
		mButPlayRecord = (Button) findViewById(R.id.record_but_play);
		mHintText = (TextView) findViewById(R.id.hint_text);
		mHintText.setText("");

		mYes = (Button) findViewById(R.id.but_ok);
		mNo = (Button) findViewById(R.id.but_nook);
		mYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setValue(1);
				finish();

				if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {
					Intent mIntent = FileOperate.getCurIntent(
							testRecord.this, "Record");
					if (mIntent != null) {
						startActivity(mIntent);
					}
				}
			}
		});

		mNo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setValue(2);
				finish();
			}
		});

		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromTouch) {
			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onStopTrackingTouch(SeekBar seekBar) {

			}

		});

		mSeekBar = (SeekBar) findViewById(R.id.seekBarvol);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromTouch) {
				int volume = (progress * maxVolume) / 100;
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						volume, 0);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onStopTrackingTouch(SeekBar seekBar) {

			}

		});

		mAudioManager = (AudioManager) getApplicationContext()
				.getSystemService(Context.AUDIO_SERVICE);
		maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int curVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		mSeekBar.setProgress(curVolume * 100 / (maxVolume + 1));
	}

	private Handler mHandler = new Handler() {
		float mCurrentVolume = 1.0f;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_TIME:
				mHintText.setText(str1);
				/*
				 * if (mSecond==10) { stopRecord(); }
				 */
				break;
			case CHECK_SEEKBAR:
				setSeekBarpos(seekBar);
				break;
			default:
				break;
			}
		}
	};

	public void startRunSeekbar() {
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
				message.what = CHECK_SEEKBAR;
				mHandler.sendMessage(message);
			}
		};
		timer.schedule(task, 1000 * 1, 1000 * 1);// timer蹇呴』鍜屼换鍔″湪涓�捣浣跨敤
													// 蹇呴』璁句笁涓弬鏁扮殑
													// 涓嶇劧timer鍙潵涓�

		if (seekBar != null) {
			seekBar.setVisibility(View.VISIBLE);
		}
	}

	public void setSeekBarpos(SeekBar seekBar) {
		if (seekBar != null) {
			if (MultiPlayer != null) {
				seekBar.setMax(MultiPlayer.getDuration());
				seekBar.setProgress(MultiPlayer.getCurrentPosition());
			}

		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mMediaRecoder = new MediaRecoderControl(getApplicationContext());
		MultiPlayer = new MultiPlayer(getApplicationContext());

		mButStartRecord.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					MultiPlayer.stop();
					if (seekBar != null) {
						seekBar.setVisibility(View.GONE);
					}

					mMediaRecoder.init();
					mMediaRecoder.start();
					mHintText.setText(R.string.record_test_recording);

					mButStartRecord.setEnabled(false);
					mButPlayRecord.setEnabled(true);
					mSecond = 0;
					mTimer = new Timer(true);
					mTimerTask = new TimerTask() {
						public void run() {
							mSecond++;
							str1 = getResources().getString(
									R.string.record_test_recording);
							str1 = String.format("%s %ds", str1, mSecond);

							mHandler.sendEmptyMessage(UPDATE_TIME);

						}
					};
					mTimer.schedule(mTimerTask, 1000, 1000);// 鍦�绉掑悗姣�绉掓墽琛屼竴娆″畾鏃跺櫒涓殑鏂规硶锛屾瘮濡傛湰鏂囦负璋冪敤log.v鎵撳嵃杈撳嚭銆�

				} catch (Exception e) {
					// TODO: handle exception
					mHintText.setText(R.string.record_test_init_err);
				}

			}
		});

		/*
		 * mButStopRecord.setOnClickListener(new View.OnClickListener() { public
		 * void onClick(View v) { stopRecord(); } });
		 */
		mButPlayRecord.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					stopRecord();

					File mFile = getFileStreamPath(FileOperate.FILE_RECORD_AUDIO);
					long length = mFile.length();

					FileInputStream fis = openFileInput(FileOperate.FILE_RECORD_AUDIO);

					if (fis == null) {
						mHintText.setText(R.string.test_record_file_error);
					} else {
						FileDescriptor fileDescriptor = fis.getFD();
						MultiPlayer.setDataSource(fileDescriptor, 0, length);
						MultiPlayer.start();
						MultiPlayer.setLoop(true);
						// MultiPlayer.setMaxVolume();
						mHintText.setText(R.string.record_test_Playing);

						mButStartRecord.setEnabled(true);
						mButPlayRecord.setEnabled(false);

						startRunSeekbar();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		});
		/*
		 * mButStopPlayRecord.setOnClickListener(new View.OnClickListener() {
		 * public void onClick(View v) { MultiPlayer.stop();
		 * mHintText.setText(R.string.record_test_stop_Playing);
		 * 
		 * mButStartRecord.setEnabled(true); mButStopRecord.setEnabled(true);
		 * mButPlayRecord.setEnabled(true); mButStopPlayRecord.setEnabled(true);
		 * 
		 * if (timer!=null) { timer.cancel(); timer=null; } if (seekBar!=null) {
		 * seekBar.setVisibility(View.GONE); } } });
		 */

	}

	public void stopRecord() {
		mMediaRecoder.stop();
		// mHintText.setText(R.string.record_test_recording_stop);

		mButStartRecord.setEnabled(true);
		// mButStopRecord.setEnabled(true);
		mButPlayRecord.setEnabled(true);
		// mButStopPlayRecord.setEnabled(true);

		while (!mTimerTask.cancel())
			mTimer.cancel();

		if (seekBar != null) {
			seekBar.setVisibility(View.GONE);
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		String string = null;
		int mcurProgress;
		switch (keyCode) {

		case KeyEvent.KEYCODE_VOLUME_DOWN: {
			mcurProgress = mSeekBar.getProgress();
			mcurProgress -= 6;
			if (mcurProgress >= 0) {
				mSeekBar.setProgress(mcurProgress);
			} else {
				mSeekBar.setProgress(0);
			}

			break;
		}
		case KeyEvent.KEYCODE_VOLUME_UP: {
			mcurProgress = mSeekBar.getProgress();
			mcurProgress += 6;
			if (mcurProgress <= 99) {
				mSeekBar.setProgress(mcurProgress);
			} else {
				mSeekBar.setProgress(99);
			}
			break;
		}

		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		// MultiPlayer.setdefVolume();

		mMediaRecoder.stop();

		MultiPlayer.stop();
	}

}
