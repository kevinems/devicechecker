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
	private String TAG="MediaRecoder Model";
	MediaRecorder mMediaRecorder;
	File file=null;
	private Context mContext;	
	boolean mbInit=false;
	public MediaRecoderControl(Context context)
	{
		mContext=context;
	}
	public void init() throws Exception {
        try {
        	mMediaRecorder = new MediaRecorder();
        	mMediaRecorder.reset();
        	mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        	mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        	mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        	//mMediaRecorder.setAudioChannels(AudioFormat.CHANNEL_CONFIGURATION_STEREO);
        	
        	mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        	mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        	//mMediaRecorder.setAudioEncodingBitRate(44100);
//        	mMediaRecorder.setAudioSamplingRate(44100);
        	
        	
        	/*if (file == null) {
        	  String SdStateString =android.os.Environment.getExternalStorageState();
              
              if(SdStateString.equals(android.os.Environment.MEDIA_MOUNTED))
      		{
      			
            	  File rootDir = Environment.getExternalStorageDirectory();
                  file=new File(rootDir.getAbsolutePath()+ File.separator+"test.3gp");
    				 if(file.exists())
    				 {
    					 file.delete();
    				 }
    				
    				 file.createNewFile();
    				 
      		}else
      		{
      			Toast.makeText(mContext, R.string.test_audio_sd_error, Toast.LENGTH_LONG).show();
      		}
               
        }
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            mMediaRecorder.prepare();
            mbInit=true;*/
        	
       FileOutputStream out; 	
       File destfile2=new File("data/data/smit.com.factorytest/files/test.3gp"); 	
       if (destfile2.isFile()) {
    	   destfile2.delete();
       }
       destfile2.createNewFile();
       
       out = new FileOutputStream(destfile2);
       FileDescriptor fileDescriptor=out.getFD();
       mMediaRecorder.setOutputFile(fileDescriptor);
       mMediaRecorder.prepare();
       mbInit=true;  
   
        } catch (Exception ex) {
                Log.d(TAG, "init", ex);
                throw new Exception("init fail");
        }
}

public void start() {
	if(mbInit)
        startRecording();
}

public void stop() {
	    if(mbInit)
	    {
          stopRecording();
          mbInit=false;
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
public void Release()
{
	
}
}
