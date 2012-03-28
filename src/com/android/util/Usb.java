package com.android.util;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
public class Usb{
	 private String TAG="USB Model";
	 private IntentFilter mIntentFilter;
	 private BroadcastReceiver mBroadcastreceiver;
	 private OnListener mListener;  
	 private Context mContext;	
	 public interface OnListener{    	
	        public void onUsbChanged(int nState);//1 Ϊ���ӣ�0Ϊ�Ͽ�
	 }  
	 

	    public Usb(Context context)
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
				    if (str.equals("android.intent.action.UMS_DISCONNECTED")){
				
				      Log.d(TAG, "USB disconnected");
				      if(mListener!=null)
				      {
				    	  mListener.onUsbChanged(0);
				      }
				    }
				     else if (str.equals("android.intent.action.UMS_CONNECTED")){
				        Log.d(TAG, "USB connected");
				        if(mListener!=null)
					      {
				        	mListener.onUsbChanged(1);
					      }
				      }
				      return;
				  	  }
			};
		    mIntentFilter.addAction("android.intent.action.UMS_DISCONNECTED");
		    mIntentFilter.addAction("android.intent.action.UMS_CONNECTED");
		    mContext.registerReceiver(mBroadcastreceiver, mIntentFilter);
	 }
	 public void Stop()
	 {
		 mContext.unregisterReceiver(mBroadcastreceiver);
	 }
	 
}
