package smit.com.factorytest;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import smit.com.util.FileOperate;
import smit.com.util.HeadSet;
import smit.com.util.MultiPlayer;
import smit.com.util.ParseSeverData;
import smit.com.factorytest.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.media.MediaPlayer;
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
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MusicPlayActivity extends Activity {
	private static final String TAG = "MusicPlayActivity";
	
	TextView msdTextView,mAudioTips;
	HeadSet.OnListener m_HeadSetListerer;
	HeadSet mHeadSet;
	String path;
	smit.com.util.MultiPlayer MultiPlayer=null;
	private int mNum = 0;
/*	Button mButStop;
	Button mButPlay;*/
	
	private Button mYes=null;
	private Button mNo=null;
	
	SeekBar seekBar = null; 
	Timer timer;
	private static final int CHECK_SEEKBAR=0x1;
	
	private AlertDialog progressAlert;
	boolean checkOk=false;	//是否是成功
	private void setValue(int value){
		FileOperate.setIndexValue(FileOperate.TestItemAudio, value);
		FileOperate.writeToFile(this);
		
		//ParseSeverData.startUpTestItemThread("Audio");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		 final Window win = getWindow();
 	    win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		
		setContentView(R.layout.audio_player);
		msdTextView =(TextView) findViewById(R.id.headset_state_text);
		msdTextView.setText("");
		
		mAudioTips=(TextView)findViewById(R.id.audio_tips_text);
		
		mYes=(Button)findViewById(R.id.but_ok);
		mNo=(Button)findViewById(R.id.but_nook);
		mYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				if (mNum<3) {
					toNextTest();
				}else {
					setValue(1);
					finish();
					
					if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
						Intent mIntent = FileOperate.getCurIntent(MusicPlayActivity.this,"Audio");
						 if (mIntent!=null) {
							 startActivity(mIntent);
						}
					}
				}		
			}
		});
		
		mNo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setValue(2);
				FileOperate.setCurmode(false);
				finish();
			}
		});
		
		 seekBar=(SeekBar)findViewById(R.id.seekBar);
		 seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {  
	            @Override  
	            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromTouch) {            
	            }  
	            @Override  
	            public void onStartTrackingTouch(SeekBar seekBar) {  
	                  
	            }  
	            @Override  
	            public void onStopTrackingTouch(SeekBar seekBar) {  
	                  
	            }  
	  
	        }); 
		 
		 startRunSeekbar();
		 if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
		 FileOperate.setIndexValue(FileOperate.TestItemAudio, FileOperate.CHECK_FAILURE);
		 }
	}
	
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg) {  
            switch (msg.what) {      
            case CHECK_SEEKBAR:
            	setSeekBarpos(seekBar);
                break;
            }      
            super.handleMessage(msg);  
        }        
	};
		
	public void startRunSeekbar(){
		if (timer!=null) {
			timer.cancel();
			timer=null;
		}	   
		  timer=new Timer();
		  TimerTask task = new TimerTask(){
		        public void run() {
		            // TODO Auto-generated method stub
		            //由于主线程安全，页面的更新需放到主线程中
		            Message message = new Message();      
		            message.what = CHECK_SEEKBAR;      
		            handler.sendMessage(message);    
		        }
		    }; 
		   timer.schedule(task, 1000*1, 1000*1);//timer必须和任务在一起使用  必须设三个参数的 不然timer只来一次
	}
	
	public void setSeekBarpos(SeekBar seekBar){
		if (seekBar!=null) {
			if(MultiPlayer!=null){
				seekBar.setMax(MultiPlayer.getDuration());
				seekBar.setProgress(MultiPlayer.getCurrentPosition());
			}
		}
	}
	
	public void startPlay(){
		MultiPlayer.setdefaultPic(R.raw.test);
		MultiPlayer.setLoop(true);
		MultiPlayer.start();
	}
	
	public void musicStop(){
		MultiPlayer.stop();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		MultiPlayer = new MultiPlayer(getApplicationContext());
		
		MultiPlayer.setdefVolume();
		
		startPlay();
					
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(timer!=null){
			timer.cancel();
			timer=null;
		}
		
		if (MultiPlayer!=null) {
			MultiPlayer.stop();
			MultiPlayer=null;
		}	
		if (mHeadSet!=null) {
			mHeadSet.Stop();
			mHeadSet=null;
		}
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}
	
	public void toNextTest(){
		mNum ++;
		if(mNum == 0){
			mAudioTips.setText(R.string.audio_test_hinta);
			mYes.setText(R.string.audio_max_volume);
			MultiPlayer.setdefVolume();
		}
		if(mNum == 1){
			mAudioTips.setText(R.string.audio_test_hintb);
			mYes.setText(R.string.audio_min_volume);
			MultiPlayer.setMaxVolume();
		}
		if(mNum == 2){
			mAudioTips.setText(R.string.audio_test_hintc);
			mYes.setText(R.string.audio_headphne);
			MultiPlayer.setMinVolume();
		}				
		if(mNum == 3){
			mAudioTips.setText(R.string.audio_test_hintd);
			mYes.setText(R.string.check_ok);
			MultiPlayer.setdefVolume();
			
			//开始检测耳机
			mHeadSet=new HeadSet(getApplicationContext());
			mHeadSet.Start();
			m_HeadSetListerer = new HeadSet.OnListener(){
				 public void onHeadSetChanged(int nState)
				 {
					 if(nState==1)
					 {
						 msdTextView.setText(R.string.headset_test_in);
						 mAudioTips.setText(R.string.audio_test_hinte);			 
					 }
					 else
					 {
						 msdTextView.setText(R.string.headset_test_out);
						 mAudioTips.setText(R.string.audio_test_hintd);
					 }
				 }
			};
			mHeadSet.setListener(m_HeadSetListerer);
		}	

	}

}
