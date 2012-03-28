
package com.android.util;


import java.io.FileDescriptor;
import java.io.IOException;


import android.R.integer;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;


/**
 * Provides a unified interface for dealing with midi files and
 * other media files.
 */
public class MultiPlayer {
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private Handler mHandler;
    private Context mContext;	
    private boolean mIsInitialized = false;

    public MultiPlayer(Context context) {
    	mContext = context;  
        //mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
    }
    
    public void setdefaultPic(int id){
    	mMediaPlayer=MediaPlayer.create(mContext, id);
    }
    

    public void setDataSourceAsync(String path) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(preparedlistener);
            mMediaPlayer.prepareAsync();
        } catch (IOException ex) {
            // TODO: notify the user why the file couldn't be opened
            mIsInitialized = false;
            return;
        } catch (IllegalArgumentException ex) {
            // TODO: notify the user why the file couldn't be opened
            mIsInitialized = false;
            return;
        }
        mMediaPlayer.setOnCompletionListener(listener);
        mMediaPlayer.setOnErrorListener(errorListener);
        
        mIsInitialized = true;
    }
    
    public void setDataSource(String path) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setOnPreparedListener(null);
            if (path.startsWith("content://")) {
                mMediaPlayer.setDataSource(mContext, Uri.parse(path));
            } else {
                mMediaPlayer.setDataSource(path);
            }
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepare();
        } catch (IOException ex) {
            // TODO: notify the user why the file couldn't be opened
            mIsInitialized = false;
            return;
        } catch (IllegalArgumentException ex) {
            // TODO: notify the user why the file couldn't be opened
            mIsInitialized = false;
            return;
        }
        mMediaPlayer.setOnCompletionListener(listener);
        mMediaPlayer.setOnErrorListener(errorListener);
        
        mIsInitialized = true;
    }
    
    public void setDataSource(FileDescriptor fd,long offset,long length) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setDataSource(fd, offset, length);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepare();
        } catch (IOException ex) {
            // TODO: notify the user why the file couldn't be opened
            mIsInitialized = false;
            return;
        } catch (IllegalArgumentException ex) {
            // TODO: notify the user why the file couldn't be opened
            mIsInitialized = false;
            return;
        }
        mMediaPlayer.setOnCompletionListener(listener);
        mMediaPlayer.setOnErrorListener(errorListener);
        
        mIsInitialized = true;
    }
    
    public boolean isInitialized() {
        return mIsInitialized;
    }

    public void start() {
        mMediaPlayer.start();
    }
    
    public void setLoop(boolean flag){
    	mMediaPlayer.setLooping(flag);
    }

    public void stop() {
        mMediaPlayer.reset();
        mIsInitialized = false;
    }
    
    
    public int getDuration(){
    	return mMediaPlayer.getDuration();
    }
    
    public int getCurrentPosition(){
    	return mMediaPlayer.getCurrentPosition();
    }

    /**
     * You CANNOT use this player anymore after calling release()
     */
    public void release() {
        stop();
        mMediaPlayer.release();
    }
    
    public void pause() {
        mMediaPlayer.pause();
    }
    
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            // Acquire a temporary wakelock, since when we return from
            // this callback the MediaPlayer will release its wakelock
            // and allow the device to go to sleep.
            // This temporary wakelock is released when the RELEASE_WAKELOCK
            // message is processed, but just in case, put a timeout on it.
           // mWakeLock.acquire(30000);
          //  mHandler.sendEmptyMessage(TRACK_ENDED);
           // mHandler.sendEmptyMessage(RELEASE_WAKELOCK);
        }
    };

    MediaPlayer.OnPreparedListener preparedlistener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            //notifyChange(ASYNC_OPEN_COMPLETE);
        }
    };

    MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int what, int extra) {
            switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                mIsInitialized = false;
                mMediaPlayer.release();
                // Creating a new MediaPlayer and settings its wakemode does not
                // require the media service, so it's OK to do this now, while the
                // service is still being restarted
                mMediaPlayer = new MediaPlayer(); 
               // mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
                //mHandler.sendMessageDelayed(mHandler.obtainMessage(SERVER_DIED), 2000);
                return true;
            default:
                Log.d("MultiPlayer", "Error: " + what + "," + extra);
                break;
            }
            return false;
       }
    };

    public long duration() {
        return mMediaPlayer.getDuration();
    }

    public long position() {
        return mMediaPlayer.getCurrentPosition();
    }

    public long seek(long whereto) {
        mMediaPlayer.seekTo((int) whereto);
        return whereto;
    }

    public void setMaxVolume(){
    	AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    	int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    	mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,maxVolume, 0);
    }
    
    public void setMinVolume(){
    	AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    	//int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    	mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    }
    
    public void setdefVolume() {
    	
    	AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    	int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    	mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0);

        //mMediaPlayer.setVolume(vol, vol);
    }
}