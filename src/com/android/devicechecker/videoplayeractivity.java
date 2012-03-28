package com.android.devicechecker;

import java.io.File;

import com.android.devicechecker.R;
import com.android.util.FileOperate;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class videoplayeractivity extends Activity {

	 /** Called when the activity is first created. */
	public static final String TAG = "VideoPlayer";
	private int mPositionWhenPaused;
	private Uri uri;
	private VideoView mVideoView;
	private MediaController mMediaController;
	
	private void setValue(int value){
		FileOperate.setIndexValue(FileOperate.TestItemVideo, value);
		FileOperate.writeToFile(this);
	}
	
	
	private Button mYes=null;
	private Button mNo=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Window win = getWindow();
	    win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.video_player);
        
        mVideoView  = (VideoView) findViewById(R.id.videoview);
        uri = this.getIntent().getData();
        mMediaController = new MediaController(this);
        mVideoView.setMediaController(mMediaController);   
        
        mYes=(Button)findViewById(R.id.but_ok);
		mNo=(Button)findViewById(R.id.but_nook);
        mYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setValue(1);
				FileOperate.setCurTest(true);
				finish();
				
				Intent mIntent = new Intent(videoplayeractivity.this, MediaRecoderactivity.class);
				startActivity(mIntent);
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
    @Override
    protected void onStart() {
    	
    	
    	String SdStateString =android.os.Environment.getExternalStorageState();
    	String path=null;
    	//��
		if(SdStateString.equals(android.os.Environment.MEDIA_MOUNTED))
		{		
			//����
			 File SDFile = android.os.Environment.getExternalStorageDirectory();
			 path = SDFile.getAbsolutePath()+ File.separator+"test.mp4";
			 File myFile=new File(path);
			 if(!myFile.exists())
	         {
	        	 Toast.makeText(getApplicationContext(), R.string.test_video_file_error, Toast.LENGTH_LONG).show();
	         }
			 else
			 {
				 mVideoView.setVideoPath(path);
			 }
		}else
		{
			Toast.makeText(getApplicationContext(), R.string.test_audio_sd_error, Toast.LENGTH_LONG).show();
		}
    	mVideoView.start();
    	super.onStart();
    	
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	
   
    }
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	mPositionWhenPaused = mVideoView.getCurrentPosition();
    	mVideoView.stopPlayback();
    	Log.d(TAG, "OnStop: mPositionWhenPaused = " + mPositionWhenPaused);
    	Log.d(TAG, "OnStop: getDuration = " + mVideoView.getDuration());
    	super.onPause();
    }
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	if(mPositionWhenPaused >= 0) {
    	mVideoView.seekTo(mPositionWhenPaused);
    	mPositionWhenPaused = -1;
    	}
    	super.onResume();
    }
}
