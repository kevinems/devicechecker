package smit.com.factorytest;

import android.content.Context;
import android.widget.MediaController;
import android.widget.VideoView;

public class Videplayer {
	private VideoView mVideoView;
	private MediaController mMediaController;
	 private Context  mContext;
	public Videplayer(Context context)
	{
		  
		 mContext = context;  
		   
	}
	 protected void Start(String path)
	 {
	   //mVideoView  = new VideoView();
	   mMediaController = new MediaController(mContext);
       mVideoView.setMediaController(mMediaController);
       mVideoView.setVideoPath(path);
       mVideoView.start();
	 }
	 protected void Stop()
	 {
	   mVideoView.stopPlayback();
	 }
}
