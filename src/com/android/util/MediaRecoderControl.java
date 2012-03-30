package com.android.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;

import com.android.devicechecker.R;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class MediaRecoderControl {
	private String TAG = "MediaRecoder Model";
	MediaRecorder mMediaRecorder;
	File file = null;
	private Context mContext;
	boolean mbInit = false;

	public MediaRecoderControl(Context context) {
		mContext = context;
	}

	public void init() throws Exception {
		try {
			mMediaRecorder = new MediaRecorder();
			mMediaRecorder.reset();
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder
					.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			FileOutputStream stream = mContext.openFileOutput(FileOperate.FILE_RECORD_AUDIO,
					Context.MODE_WORLD_WRITEABLE);
			FileDescriptor fileDescriptor = stream.getFD();

			mMediaRecorder.setOutputFile(fileDescriptor);
			mMediaRecorder.prepare();
			mbInit = true;

		} catch (Exception ex) {
			Log.d(TAG, "init", ex);
			throw new Exception("init fail");
		}
	}

	public void start() {
		if (mbInit)
			startRecording();
	}

	public void stop() {
		if (mbInit) {
			stopRecording();
			mbInit = false;
		}
	}

	public void startRecording() {
		mMediaRecorder.start();
	}

	/**
	 * This method stops recording
	 */
	private void stopRecording() {
		mMediaRecorder.stop();
		mMediaRecorder.release();

	}

	public void Release() {

	}
}
