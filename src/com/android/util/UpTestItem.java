package com.android.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

//提交一个个测试项交互线程   把取的状态传给主线程
public  class UpTestItem extends Thread {

	public 		URL 			mUrl;
	private 	Handler 		mMainHandle;
	private     int 			msg;
	List<NameValuePair> 		mParams=null;
	public 		String 	        getString =null;
	private     Timer 			mTimer;
	private     String          postString=null;
	private     final int       TimeOut=20*1000;
	private     String          Tag="UpTestItem";
	private     boolean         timeoutflag=false;

	
	public void sendData() {  
		String tag ="HTTPSend:sendData";  
		int statsID = 101;  
		StringBuffer sb = new StringBuffer();  

		
		 try {
	        	 HttpClient client = new DefaultHttpClient(); 
	        	 String urlString=mUrl.toString();
	             HttpPost post = new HttpPost(mUrl.toString());
	             Log.e("UpTestItem", "111111111111111111111111111");
	             if (mParams!=null) {
	            	 post.setEntity(new UrlEncodedFormEntity(mParams,HTTP.UTF_8));
				}   
	             HttpResponse response = client.execute(post);
	             
	             int responseCode=response.getStatusLine().getStatusCode();
	             if (responseCode==HttpURLConnection.HTTP_OK) {
	            	 Log.e("UpTestItem", "333333333333333333333333");
	            	 //当正确响应时处理数据
			           String readLine;
			           BufferedReader responseReader;          
			           HttpEntity httpEntity = response.getEntity();
			           //将H中返回实体转化为输入流
			           
			           //处理响应流，必须与服务器响应流输出的编码一致
			           responseReader = new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"));
			           while ((readLine = responseReader.readLine()) != null) {
			               sb.append(readLine).append("\n");
			            }
			            responseReader.close();
				}
	             Log.e("UpTestItem", "222222222222222222222222222");
	             
	             if (timeoutflag) {
	            	Log.e("UpTestItem", "===========Timeout===========");
					return;
				}
	             
	            getString=null;
	 	        getString=sb.toString();           
	 			if (mTimer!=null) {      //取到数据马上返回
	 				mTimer.cancel();
	 				mTimer=null;
	 			}
	 			Message m =new Message();
	 			m.what=msg; 				
	 			m.arg1=getUpItemStatus();
	 			if (postString!=null) {//传参数进来就传回
	 				m.obj=postString;
	 			}else {
	 				m.obj=getString;  //否则传服务器传回数据
	 			}
	 			
	 			Log.e(Tag, "===To Message to Ui Thread===");
	 			mMainHandle.sendMessage(m);
			} catch (Exception e) {	
				Log.e("UpTestItem",e.toString());
				Message m =new Message();
				m.what=msg; 				
				m.arg1=getUpItemStatus();
				m.obj=postString;
				mMainHandle.sendMessage(m);
			}
	}
	
	public UpTestItem(URL url, Handler Handle, int message, String str, List<NameValuePair> param)
	{		
		mMainHandle=Handle;
		mUrl=url;
		msg=message;
		postString=str;
		mParams=param;
		stopThread();
		mTimer= new Timer();
		timeoutflag=false;
		TimerTask timeTask=new TimerTask() {		
				@Override
				public void run() {
					timeoutflag=true;
					Log.e(Tag, "===Get Data Timeout===");
					Message m =new Message();
					m.what=msg; 				
					m.arg1=getUpItemStatus();
					m.obj=postString;
					mMainHandle.sendMessage(m);
				}
			};
		mTimer.schedule(timeTask,TimeOut);//通知主线程数据已取完 		
	
	}
	
	public int getUpItemStatus(){
		return 1;
	}
	
	
	@Override
	public void run() {
		sendData();
		stopThread();
		super.run();
	}
	
	public void stopThread(){
		stop();
		if (mTimer!=null) {
			mTimer.cancel();
			mTimer=null;
		}
	}   	
	//得到数据
	public  String getDate()
	{
		return getString;
	}
}