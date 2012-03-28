package com.android.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
public class SDCard{
	 private String TAG="TCard Model";
	 private IntentFilter mIntentFilter;
	 private BroadcastReceiver mBroadcastreceiver;
	 private OnListener mListener;  
	 private Context mContext;	
	 public interface OnListener{    	
	        public void onSDCardChanged(int nState);//1 Ϊ���ӣ�0Ϊ�Ͽ�
	 }  
	 

	    public SDCard(Context context)
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
				    if (str.equals(Intent.ACTION_MEDIA_UNMOUNTED)){
				
				      Log.d(TAG, "T Card  Unmounted");
				      if(mListener!=null)
				      {
				    	  mListener.onSDCardChanged(0);
				      }
				    }
				     else if (str.equals(Intent.ACTION_MEDIA_MOUNTED)){
				        Log.d(TAG, "T Card Mounted");
				        if(mListener!=null)
					      {
				        	mListener.onSDCardChanged(1);
					      }
				      }
				      return;
				  	  }
			};
		
		    mIntentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		    mIntentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		   //mIntentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);	   
		    //mIntentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		    mIntentFilter.addDataScheme("file");
		    mContext.registerReceiver(mBroadcastreceiver,mIntentFilter);
	 }
	 public void Stop()
	 {
		 if (mBroadcastreceiver!=null) {
			 mContext.unregisterReceiver(mBroadcastreceiver);
		}
		 
	 }
	 //д����
	 public void SDCardWriteTest(String FileName,String data) throws Exception
	 {
		    String SdStateString =android.os.Environment.getExternalStorageState();
			// ӵ�пɶ���дȨ��
			 if(SdStateString.equals(android.os.Environment.MEDIA_MOUNTED))
			 {
				try{
			
				 //����
				 File SDFile = android.os.Environment.getExternalStorageDirectory();
				 File myFile=new File(SDFile.getAbsolutePath()+ File.separator+FileName);
				 if(myFile.exists())
				 {
					 myFile.delete();
				 }
				
				 myFile.createNewFile();
				 
				 
				 //д���
				 String szOutText=data;
				 FileOutputStream outputStream=new FileOutputStream(myFile);
				 outputStream.write(szOutText.getBytes());
				 outputStream.close();
			}catch(Exception e)
			{
				throw new IOException("Write date Excepton");  
			}
			 }
			 else
			 {
				 throw new Exception("Not Read permission"); 
			 }
			 
	 }
	 //������
	 public String  SDCardReadTest(String FileName) throws Exception
	 {
		 String data=null;
		 String SdStateString =android.os.Environment.getExternalStorageState();
		//��
			if(SdStateString.equals(android.os.Environment.MEDIA_MOUNTED_READ_ONLY))
			{
				
				//����
				 File SDFile = android.os.Environment.getExternalStorageDirectory();
				 File myFile=new File(SDFile.getAbsolutePath()+ File.separator+FileName);
				
					 if(myFile.exists())
					 {
						 try{
						 FileInputStream inputStream = new FileInputStream(myFile);
						 byte[] buffer = new byte[1024];
						 inputStream.read(buffer);
						 inputStream.close();
						 data=new String(buffer);
					 } catch (Exception e) {
						 throw new IOException("Read date Excepton");  
					 }
						 
						 
					 }
			}
			else
			{
			 throw new Exception("Not Read permission"); 
			}
			return data;
			 
	 }
	 //Io��д����
	 public  boolean SDCardIOTest(String data) throws Exception
	 {
		 try {
		    SDCardWriteTest("mytest.txt",data);
		    String readdate=SDCardReadTest("mytest.txt");
		    if(readdate!=null)
		    {
		    	if(data.equals(readdate))
		    	{
		    		return true;
		    	}
		    }
		    
		 
		 }
		 catch(Exception e)
		 {
			 
		 }
		 return false;
		 
	 }
	 //��ȡ���Ĵ�С
	 public long SDCardSizeTest() 
	 {
		 // ȡ��SDCard��ǰ��״̬
		 String sDcString = android.os.Environment.getExternalStorageState();
		 if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) 
		 {
		// ȡ��sdcard�ļ�·��	 
		File pathFile = android.os.Environment.getExternalStorageDirectory();
		android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());
		// ��ȡSDCard��BLOCK����
		long nTotalBlocks = statfs.getBlockCount();
		// ��ȡSDCard��ÿ��block��SIZE
		long nBlocSize = statfs.getBlockSize();
		// ��ȡ�ɹ�����ʹ�õ�Block������
		long nAvailaBlock = statfs.getAvailableBlocks();
		// ��ȡʣ�µ�����Block������(����Ԥ����һ������޷�ʹ�õĿ�)
		long nFreeBlock = statfs.getFreeBlocks();
		// ����SDCard ��������СMB
		long nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;
		// ���� SDCard ʣ���СMB
		long nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024;
		return nSDFreeSize;
	    }// end of if
		return 0;		
	 }

	 
}
