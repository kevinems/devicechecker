package com.android.devicechecker;

import java.io.File;
import java.io.FileDescriptor;

import com.android.util.FileOperate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class HDMIActivity extends Activity implements
OnBufferingUpdateListener, OnCompletionListener,
OnPreparedListener, OnVideoSizeChangedListener, SurfaceHolder.Callback{
	//private Button btStart,btOpen,btClose;
	 private static final String TAG = "MediaPlayerDemo";
	    private int mVideoWidth;
	    private int mVideoHeight;
	    private MediaPlayer mMediaPlayer;
	    private SurfaceView mPreview;
	    private SurfaceHolder holder;
	    private String path;
	    private Bundle extras;
	    private static final String MEDIA = "media";
	    private static final int LOCAL_AUDIO = 1;
	    private static final int STREAM_AUDIO = 2;
	    private static final int RESOURCES_AUDIO = 3;
	    private static final int LOCAL_VIDEO = 4;
	    private static final int STREAM_VIDEO = 5;
	    private boolean mIsVideoSizeKnown = false;
	    private boolean mIsVideoReadyToBePlayed = false;
	
	private Button mYes=null;
	private Button mNo=null;
	private SeekBar mSeekBar;
	AudioManager mAudioManager;
	int maxVolume;
	
	private AlertDialog progressAlert;
	boolean checkOk=false;	//是否是成功
	
	private void setValue(int value){
		FileOperate.setIndexValue(FileOperate.TestItemHDMI, value);
		FileOperate.writeToFile(this);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Window win = getWindow();
	    win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);     
        setContentView(R.layout.hdmi);
        mPreview = (SurfaceView) findViewById(R.id.surface);
        holder = mPreview.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        
        Settings.System.putInt(getContentResolver(), "output_select", 1);//打开hdmi
        
        mYes=(Button)findViewById(R.id.but_ok);
		mNo=(Button)findViewById(R.id.but_nook);
        mYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setValue(1);
				finish();
				
				if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
					Intent mIntent = FileOperate.getCurIntent(HDMIActivity.this,"HDMI");
					 if (mIntent!=null) {
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
		
		mSeekBar=(SeekBar)findViewById(R.id.hdmiseekBar);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {  
            @Override  
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromTouch) {
            	int volume=(progress*maxVolume)/100;
            	mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volume, 0);
            }  
            @Override  
            public void onStartTrackingTouch(SeekBar seekBar) {  
                  
            }  
            @Override  
            public void onStopTrackingTouch(SeekBar seekBar) {  
                  
            }  
  
        }); 
		
		mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    	maxVolume=mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int curVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    	mSeekBar.setProgress(curVolume*100/(maxVolume+1));
		
		if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
			FileOperate.setIndexValue(FileOperate.TestItemHDMI, FileOperate.CHECK_FAILURE);
			FileOperate.writeToFile(this);
		}
    }

	
	 private void playVideoEx(String urlpath) {
	        doCleanUp();
	        try{
	        
	        path=urlpath;
	        //path = "http://data.vod.itc.cn/v/20110303/198278-260422-9a3e3957-7ca0-48ac-ae3e-f7d957a804cc.mp4";
	        if (path == "") {
	            // Tell the user to provide a media file URL.
	            Toast
	                    .makeText(
	                            HDMIActivity.this,
	                            "Please edit MediaPlayerDemo_Video Activity,"
	                                    + " and set the path variable to your media file URL.",
	                            Toast.LENGTH_LONG).show();
	        }
        
	            // Create a new media player and set the listeners
	            mMediaPlayer = new MediaPlayer();
	            AssetFileDescriptor aFD = this.getAssets().openFd("testvideo.mp4");
	            FileDescriptor fileDescriptor = aFD.getFileDescriptor();
	            mMediaPlayer.setDataSource(fileDescriptor, aFD.getStartOffset(), aFD.getLength());
	            aFD.close(); 
	               
	            mMediaPlayer.setDisplay(holder);
	            mMediaPlayer.prepare();
	            mMediaPlayer.setLooping(true);
	            mMediaPlayer.setOnBufferingUpdateListener(this);
	            mMediaPlayer.setOnCompletionListener(this);
	            mMediaPlayer.setOnPreparedListener(this);
	            mMediaPlayer.setOnVideoSizeChangedListener(this);
	            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

	        } catch (Exception e) {
	            Log.e(TAG, "error: " + e.getMessage(), e);
	        }
	    }

	    public void onBufferingUpdate(MediaPlayer arg0, int percent) {
	        Log.d(TAG, "onBufferingUpdate percent:" + percent);

	    }

	    public void onCompletion(MediaPlayer arg0) {
	        Log.d(TAG, "onCompletion called");
	        onBackPressed();
	    }

	    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
	        Log.v(TAG, "onVideoSizeChanged called");
	        if (width == 0 || height == 0) {
	            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
	            return;
	        }
	        mIsVideoSizeKnown = true;
	        mVideoWidth = width;
	        mVideoHeight = height;
	        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
	            startVideoPlayback();
	        }
	    }


	    public void onPrepared(MediaPlayer mediaplayer) {
	        Log.d(TAG, "onPrepared called");
	        mIsVideoReadyToBePlayed = true;
	        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
	            startVideoPlayback();
	        }
	    }
	    

	    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
	        Log.d(TAG, "surfaceChanged called");

	    }

	    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
	        Log.d(TAG, "surfaceDestroyed called");
	    }


	    public void surfaceCreated(SurfaceHolder holder) {
	        Log.d(TAG, "surfaceCreated called");
	  
	        File SDFile = android.os.Environment.getExternalStorageDirectory();
			path = SDFile.getAbsolutePath()+ File.separator+"test.mp4";      
	        playVideoEx(path);
	    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
        doCleanUp();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	 String string=null;
	 int mcurProgress;
	 switch (keyCode) {
	
	case KeyEvent.KEYCODE_VOLUME_DOWN:{
		mcurProgress=mSeekBar.getProgress();
		mcurProgress-=6;
		if (mcurProgress>=0) {
			mSeekBar.setProgress(mcurProgress);
		}else {
			mSeekBar.setProgress(0);
		}
		
		break;
		}
	case KeyEvent.KEYCODE_VOLUME_UP:{
		mcurProgress=mSeekBar.getProgress();
		mcurProgress+=6;
		if (mcurProgress<=99) {
			mSeekBar.setProgress(mcurProgress);
		}else {
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
	    protected void onDestroy() {
	        super.onDestroy();
	        releaseMediaPlayer();
	        doCleanUp();
	        Settings.System.putInt(getContentResolver(), "output_select", 0);//关闭hdmi	
	    }
	    
		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			//onBackPressed();
			return super.onTouchEvent(event);
		}

	    private void releaseMediaPlayer() {
	        if (mMediaPlayer != null) {
	        	mMediaPlayer.stop();
	            mMediaPlayer.release();
	            mMediaPlayer = null;
	        }
	    }

	    private void doCleanUp() {
	        mVideoWidth = 0;
	        mVideoHeight = 0;
	        mIsVideoReadyToBePlayed = false;
	        mIsVideoSizeKnown = false;
	    }

	    private void startVideoPlayback() {
	        Log.v(TAG, "startVideoPlayback");
	        holder.setFixedSize(mVideoWidth, mVideoHeight);
	        mMediaPlayer.start();
	    }

}
