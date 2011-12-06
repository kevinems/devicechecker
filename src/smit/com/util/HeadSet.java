package smit.com.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
public class HeadSet{
	 private String TAG="HeadSet Model";
	 private IntentFilter mIntentFilter;
	 private BroadcastReceiver mBroadcastreceiver;
	 private OnListener mListener;  
	 private Context mContext;	
	 public interface OnListener{    	
	        public void onHeadSetChanged(int nState);//1 为连接，0为断开
	 }  
	 

	    public HeadSet(Context context)
	    {  
	        mContext = context;  
	        
	    }
	    public void setListener(OnListener listener)
	    {
	    	mListener=listener;
	    }
	    
	    
		
	 public void Start()
	 {
		    mIntentFilter = new IntentFilter();
		    mBroadcastreceiver = new BroadcastReceiver(){		
				@Override
				public void onReceive(Context paramContext, Intent paramIntent){
				    String str = paramIntent.getAction();
				    
				    if (str.equals("android.intent.action.HEADSET_PLUG"))
				    {
				
				    	Bundle localBundle = paramIntent.getExtras();
				    	Log.d(TAG, "ACTION_HEADSET_PLUG");
					    int i = localBundle.getInt("state");
					      
					     
				      if(mListener!=null)
				      {
				    	  mListener.onHeadSetChanged(i);
				      }
				     }
				
				      return;
				  	  }
			};
		    mIntentFilter.addAction("android.intent.action.HEADSET_PLUG");
		    mContext.registerReceiver(mBroadcastreceiver, mIntentFilter);
	 }
	 public void Stop()
	 {
		 mContext.unregisterReceiver(mBroadcastreceiver);
	 }
	 
}
