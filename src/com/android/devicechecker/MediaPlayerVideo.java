/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.devicechecker;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;

import com.android.util.FileOperate;
import com.android.util.ParseSeverData;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;


public class MediaPlayerVideo extends Activity implements
        OnBufferingUpdateListener, OnCompletionListener,
        OnPreparedListener, OnVideoSizeChangedListener, SurfaceHolder.Callback {

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
		FileOperate.setIndexValue(FileOperate.TestItemVideo, value);
		FileOperate.writeToFile(this);
		
		//ParseSeverData.startUpTestItemThread("Video");
	}
    
    /**
     * 
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        final Window win = getWindow();
	    win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);       
        setContentView(R.layout.mediaplayervideo);
        mPreview = (SurfaceView) findViewById(R.id.surface);
        holder = mPreview.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
       
        
        
        mYes=(Button)findViewById(R.id.but_ok);
		mNo=(Button)findViewById(R.id.but_nook);
        mYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {	
				setValue(1);
				finish();
				
				if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
					Intent mIntent = FileOperate.getCurIntent(MediaPlayerVideo.this,"Video");
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
		
		mSeekBar=(SeekBar)findViewById(R.id.seekBar);
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
			FileOperate.setIndexValue(FileOperate.TestItemVideo, FileOperate.CHECK_FAILURE);
			FileOperate.writeToFile(this);
		}
    }
    
    
    private void playVideoEx(String urlpath) {
        doCleanUp();
        try{
        
        path=urlpath;
     
        /*if (path == "") {
            Toast
                    .makeText(
                            MediaPlayerVideo.this,
                            "Please edit MediaPlayerDemo_Video Activity,"
                                    + " and set the path variable to your media file URL.",
                            Toast.LENGTH_LONG).show();
        }
*/
        
            // Create a new media player and set the listeners
        mMediaPlayer = new MediaPlayer();
        
        AssetFileDescriptor aFD = this.getAssets().openFd("testvideo.mp4");
        FileDescriptor fileDescriptor = aFD.getFileDescriptor();
        mMediaPlayer.setDataSource(fileDescriptor, aFD.getStartOffset(), aFD.getLength());
        aFD.close(); 

            
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        
        //mMediaPlayer.setDataSource(path);
        mMediaPlayer.setDisplay(holder);
        
        mMediaPlayer.prepare();
        
        mMediaPlayer.setLooping(true);

            
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnCompletionListener(this);      
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
        //onBackPressed();
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
        //playVideo(extras.getInt(MEDIA));
        File SDFile = android.os.Environment.getExternalStorageDirectory();
		path = SDFile.getAbsolutePath()+ File.separator+"test.mp4";      
        //playVideoEx(path);
		//playVideoEx("data/data/smit.com.factorytest/files/testvideo.3gp");
		playVideoEx("testvideo.mp4");
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
