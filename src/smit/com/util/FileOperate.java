package smit.com.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlSerializer;

import smit.com.factorytest.CameraActivity;
import smit.com.factorytest.CameraSubActivity;
import smit.com.factorytest.GpsActivity;
import smit.com.factorytest.HDMIActivity;
import smit.com.factorytest.MediaPlayerVideo;
import smit.com.factorytest.MediaRecoderactivity;
import smit.com.factorytest.MiniUsbActivity;
import smit.com.factorytest.MusicPlayActivity;
import smit.com.factorytest.OtgActivity;
import smit.com.factorytest.R;
import smit.com.factorytest.RtcActivity;
import smit.com.factorytest.SleepWakeActivity;
import smit.com.factorytest.StandardUsbActivity;
import smit.com.factorytest.TestColor;
import smit.com.factorytest.TestGSensor;
import smit.com.factorytest.TestKey;
import smit.com.factorytest.TestVibrator;
import smit.com.factorytest.WifiActivity;
import smit.com.factorytest.getLincenseActivity;
import smit.com.factorytest.sdcardactivity;
import smit.com.factorytest.R.string;

import android.R.bool;
import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageParser.NewPermissionInfo;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FileOperate {
	private static String FILE_DIR ="data/data/smit.com.factorytest/files/log.bin";	
	private static String FILE_DIR2 ="data/data/smit.com.factorytest/files/testvideo.3gp";
	private static String FILE_PATH ="log.bin";		//自己保存log
	private static String FILE_PATH_XML ="data/data/smit.com.factorytest/files/log.xml";//传给服务器log
	private static String TEST_ITEM_FILE ="data/data/smit.com.factorytest/files/testitemfile.xml";//测试项文件
	private static final String PRODUCT_PATH="/test_config.xml"; 
	public static final String sdCradPathTelechips=Environment.getExternalStorageDirectory()+"/tflash/";
	//public static final String sdCradPathNv=Environment.getExternalStorageDirectory()+"/extsd/";
	public static final String sdCradPathNv=Environment.getExternalStorageDirectory()+"";
	
	public static String Productid=null;
	public static String hostip=null;
	
	private static String Tag="FileOperate";
	private static byte[] buffer = new byte[100];
	public static int testCount=18;
	public static int curTestItem=0;
	public static boolean testmode=false;		//true 是测试模式
	
	public static final int TestItemWifi=0;
	public static final int TestItemColor=1;
	public static final int TestItemKey=2;
	public static final int TestItemGps=3;
	public static final int TestItemGSensor=4;
	public static final int TestItemVibrator=5;
	public static final int TestItemAudio=6;
	public static final int TestItemVideo=7;
	public static final int TestItemRecord=8;
	public static final int TestItemHDMI=9;
	public static final int TestItemCamera=10;
	public static final int TestItemCameraSub=11;
    public static final int TestItemOtg=12;
    public static final int TestItemUsb=13;
    public static final int TestItemStandardUsb=14;
    public static final int TestItemSd=15;
	public static final int TestItemClose=16;
	public static final int TestItemCpu=17;
	
	
	
	public static final int TestItemRTC=100;
    
	
    
    public static String []TestItemStr={"Wifi","Screen_Color","Key","Gps","GSensor",
    									"Vibrator","Audio","Video","Record","HDMI",
    									"Camera","CameraSub","Otg", "Usb", "StandardUsb", "Sd","Sleep-Awake"/*,"GetCpuID"*/};
    
    //当前需要测试的项目
    public static String []curtestItem={"Screen_Color","Camera","CameraSub","Key","GSensor","Vibrator",
		"Video","HDMI","Record",/*"Otg",*/"Usb", "StandardUsb", "Sd",
		"Sleep-Awake","Wifi"/*,"GetCpuID"*/};
	
	public static int CHECK_NULL=0;
	public static int CHECK_SUCCESS=1;
	public static int CHECK_FAILURE=2;
	public static String CHECK_NULL_STR="NoTest";
	public static String CHECK_SUCCESS_STR="Suc";
	public static String CHECK_FAILURE_STR="Fail";
	
	public static int TEST_MODE_ALL=0;
	public static int TEST_MODE_ITEM=1;
	public static int curStatus=TEST_MODE_ALL;
	
	//固定ip
	public static  String AP_NAME="\"TTPP-LLIINNKK\"";
	//public static  String AP_PASSWORD="12341234";
	public static  String AP_PASSWORD="43214321";
	
	//上传测试项
	public static final int DIALOG_UP_TEST_ITEM=0x100;
	
	public static Intent mIntent;
	
	public static int SuccessOrErrorId[]={R.string.check_none,R.string.check_ok,R.string.check_nook};
	
	public static LinkedList<String> TestInfoInfo=new LinkedList<String>();
	
	public static Handler mHandler;
	
	
	public static void setGobalHandle(Handler tmp){
		mHandler=tmp;
	}
	
	public static Handler getGobalHandle(){
		return mHandler;
	}
	
	
	public static void clearGobal(){
		if (TestInfoInfo!=null) {
			TestInfoInfo.clear();
		}		
	}


	public static int getIndexValue(int index)
	{
		return buffer[index];
	}
	
	public static void setIndexValue(int index,int value){
		buffer[index]=(byte)value;
	}
	
	
	public static int getCurIndex(String str){
		for (int i = 0; i < testCount; i++) {
			if (str.equals(TestItemStr[i])) {
				return i;
			}
		}
		
		return 0;
	}
	
	//是否测试全部成功
	public static boolean testAllSuccess(){
		int i=0,count=TestInfoInfo.size(),index;
		
		for (; i < count; i++) {
			index=getCurIndex(TestInfoInfo.get(i));
			if (getIndexValue(index)==1) {
				continue;
			}else {
				break;
			}
		}
		if (i>=count) {
			return true;
		}else {
			return false;
		}
	}
	
	
	
	//状态转化为串
	public static String testStatusToStr(int status){
		if (status==CHECK_SUCCESS) {
			return CHECK_SUCCESS_STR;
		}else if (status==CHECK_FAILURE) {
			return CHECK_FAILURE_STR;
		}else {
			return CHECK_NULL_STR;
		}
	}
	
	//得到总状态
	public static int getTestSttus(){
		int currstatus,count=TestInfoInfo.size();
		for (int i = 0; i < count; i++) {
			currstatus=getIndexValue(getCurIndex(TestInfoInfo.get(i)));
			if(currstatus==CHECK_NULL){
				return CHECK_FAILURE;
			}else if (currstatus==CHECK_FAILURE) {
				return currstatus;
			}else {
				continue;
			}
		}
		
		return CHECK_SUCCESS;
	}
	
	
	public static boolean getCurmode(){
		return testmode;
	}
	
	public static void setCurmode(boolean flag){
		testmode=flag;
	}
	
	public static int getCurTest(){
		return curTestItem;
	}
	
	//flag 真为加
	public static void setCurTest(boolean flag){
		if (flag) {
			if (curTestItem<(testCount-1)) {
				curTestItem++;
			}
		}else {
			
		}
	}
	
	public static void restartTest(Context mContext){
		buffer = new byte[100];
		writeToFile(mContext);
	}
	
	public static void SetTestItemXML()
	  {
		
		int len=curtestItem.length;
		
		for(int i=0; i < len; ++i){
			TestInfoInfo.add(curtestItem[i]);
		}
		
	  }
	
	public static void CreateFile(Context mContext){
		File destfile=new File(FILE_DIR);
		
		try {
			if (!destfile.isFile()) {
				destfile.createNewFile();	
			}
		} catch (Exception e) {
			Log.e(Tag,"Creat Log File error!");
		}
		
	}
	
	public static void writeToFile(Context mContext)
    {
       try {
           FileOutputStream fos = mContext.openFileOutput(FILE_PATH,Context.MODE_PRIVATE);
           fos.write(buffer);
           fos.close();
       } catch (FileNotFoundException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       } catch (IOException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
      
    }
	
    public static void readFromFile (Context mContext)
    {
    try {
           FileInputStream fis = mContext.openFileInput(FILE_PATH);
           
           fis.read( buffer );
           System.out.println( new String(buffer) );
           fis.close();
       } catch (FileNotFoundException e) {
           // TODO Auto-generated catch block
    	   Log.e(Tag,e.toString());
       } catch (IOException e) {
           // TODO Auto-generated catch block
    	   Log.e(Tag,e.toString());
       }
    }
    
  
    //读出传给服务器的xml
    public static String ReadXML(){
    	InputStream is=null;
    	 byte[] data=new byte[1000];
    	 String str=null;
    	
    	File TestItemFile = new File(FILE_PATH_XML);	
    	if (!TestItemFile.exists()) {
			return null;
		}	
    	try {	
    		is = new BufferedInputStream(new FileInputStream(TestItemFile));	
    		while(is.read(data)!=-1)
    	    is.close();

		} catch (Exception e) {
			// TODO: handle exception
		}
		
		str=new String(data);
		
    	return str;
    }
    
    
    //控制器取数据
    public static String runCmd(String cmd, boolean respond)
	{
		StringBuffer result = new StringBuffer();

		try
		{
			Process process = Runtime.getRuntime().exec("/system/bin/sh");
			DataOutputStream stdIn = new DataOutputStream(process.getOutputStream());
			DataInputStream stdOut = new DataInputStream(process.getInputStream());
			DataInputStream stdErr = new DataInputStream(process.getErrorStream());
	        
			if(cmd.endsWith("\n"))
				stdIn.writeBytes(cmd);
			else
				stdIn.writeBytes(cmd + "\n");
			stdIn.flush();
			
	    	try
	    	{
	    		if(respond)
	    		{
	    			while((stdOut.available()==0) && (stdErr.available()==0) );
	    		
	    			if(stdOut.available()>0)
	    			{
	    				while(stdOut.available() > 0)
	    				{
	    					result.append("" + (char)stdOut.read());
	    				}						
	    			}
				
	    			if(stdErr.available() > 0)
	    			{
	    				while(stdErr.available() > 0)
	    				{
	    					stdErr.read();
	    				}						
	    			}
	    			
	    		}
	    		
	    		return result.toString();
	    	}
	    	catch(IOException e)
	    	{
				e.printStackTrace();
				return("ERROR:" + e.getLocalizedMessage());
	    	}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return("ERROR:" + e.getLocalizedMessage());
		}
	}
    
    //两种模式
    static public int getCurMode(){
    	return curStatus;
    }
    static public void changeMode(){
    	if (curStatus==TEST_MODE_ALL) {
			curStatus=TEST_MODE_ITEM;
		}else if(curStatus==TEST_MODE_ITEM){
			curStatus=TEST_MODE_ALL;
		}
    }
    
    
    
  //写测试项xml文件   从服务器得到的
    public static void WriteTestItemXML(String str){
    	
    	File existFile = new File(TEST_ITEM_FILE);
    	if(existFile.exists() && existFile.length() > 0){
    		existFile.delete();
    	}
  	
    	File TestItemFile = new File(TEST_ITEM_FILE);
    	try{
    		TestItemFile.createNewFile();
    	}catch (IOException e) {
    		Log.e("IOException", "exception in createNewFile() method");
		}
    	FileOutputStream fileos = null;
    	try{
    		fileos = new FileOutputStream(TestItemFile);
    	}catch (FileNotFoundException  e) {
    		Log.e("FileNotFoundException", "can't create FileOutputStream");
		}

    	try {	
    		byte buf[] =str.getBytes();		
    		int numread = 0;

            numread = buf.length;
            if (numread <= 0) {
                   //break;
            } else {
                 fileos.write(buf, 0, numread);
             }
            fileos.close();
        } catch (Exception e) {
        	Log.e("Exception","error occurred while creating xml file");
        } 	
    }
    
    //测试写Item文件
    public static void TestWriteFile(String str){
    	String string=str;
    	//string="<global><item>Wifi</item><item>Screen_Color</item><item>GSensor</item><item>Audio</item></global>";  	
    	WriteTestItemXML(string);
    	ReadTestItemXML();
    }
    
    public static void ReadTestItemXML(){
    	String sNodeName;
    	
    	File TestItemFile = new File(TEST_ITEM_FILE);	
    	if (!TestItemFile.exists()) {
			return;
		}	
    	try {
    		InputStream is = new BufferedInputStream(new FileInputStream(TestItemFile));    	
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    		DocumentBuilder dbuilder = dbf.newDocumentBuilder();
    		Document doc = dbuilder.parse(is);
    		
     		NodeList n = doc.getElementsByTagName("item");
     		int itemcount=n.getLength();
     		if (itemcount<=0) {
    			return;
    		}
     		
     		TestInfoInfo.clear();
     			
     		for (int i = 0; i < itemcount; i++) {//所有item
     			Node item = n.item(i);			
     			sNodeName=item.getChildNodes().item(0).getNodeValue(); 	
     			TestInfoInfo.add(sNodeName);
     		}
     			
		} catch (Exception e) {
			Log.e(Tag, "====xml parse error===="+e.toString());
		}		
    }
    //存不存在测试文件
    public static boolean isExistTestItemFile(){
    	File TestItemFile = new File(TEST_ITEM_FILE);	
    	if (TestItemFile.exists()) {
			return true;
		}else {
			return false;
		}	
    }
    
    //得到测试项
    public static String getCurTestItem(int index){
    	if(TestInfoInfo!=null){
    		return TestInfoInfo.get(index);
    	}else {
			return null;
		}
    }
    
    //得到测试个数
    public static int getTestItemCount(){
    	if(TestInfoInfo!=null){
    		return TestInfoInfo.size();
    	}else {
			return 0;
		}
    }
    
    //得到下一项测试项
    static public String getNextTestStr(String curTestItemStr){
    	int i=0;
    	int count=getTestItemCount();
    	do {
			if (i>=(count-1)) {
				break;
			}
			if (curTestItemStr.equals(TestInfoInfo.get(i))) {
				break;
			}
			i++;
    		
		} while (true);
    	if (i>=(count-1)) {
			return null;
		}else {
			return TestInfoInfo.get(i+1);
		}
    }
    //到下一项测试   curTestItemStr为空测第一项
    public static Intent getCurIntent(Context mContext, String curTestItemStr){
    	String str; 
    	
    	if(curTestItemStr==null){
    		str=TestInfoInfo.get(0);
        	if (str!=null) {
    			return getItemIntent(mContext,str);
    		}else {
    			return null;
    		}
    	}else {
    		str=getNextTestStr(curTestItemStr);
        	if (str!=null) {
    			return getItemIntent(mContext,str);
    		}else {
    			return null;
    		}
		}
    	
    }
    	
    //得到测试项intent
    public static Intent getItemIntent(Context mContext, String curTestItemStr){
    	mIntent=null;
    	
    	if (curTestItemStr.equals("Wifi")) {
    		mIntent=new Intent(mContext.getApplicationContext(), WifiActivity.class);
		}else if(curTestItemStr.equals("Screen_Color")){
			mIntent=new Intent(mContext.getApplicationContext(), TestColor.class);
		}else if(curTestItemStr.equals("Key")){
			mIntent=new Intent(mContext.getApplicationContext(), TestKey.class);
		}else if(curTestItemStr.equals("Gps")){
			mIntent=new Intent(mContext.getApplicationContext(), GpsActivity.class);
		}else if(curTestItemStr.equals("GSensor")){
			mIntent=new Intent(mContext.getApplicationContext(), TestGSensor.class);
		}	
		else if(curTestItemStr.equals("Vibrator")){
			mIntent=new Intent(mContext.getApplicationContext(), TestVibrator.class);
		}
		else if(curTestItemStr.equals("Audio")){
			mIntent=new Intent(mContext.getApplicationContext(), MusicPlayActivity.class);
		}
		else if(curTestItemStr.equals("Video")){
			mIntent=new Intent(mContext.getApplicationContext(), MediaPlayerVideo.class);
		}
		else if(curTestItemStr.equals("Record")){
			mIntent=new Intent(mContext.getApplicationContext(), MediaRecoderactivity.class);
		}
		else if(curTestItemStr.equals("HDMI")){
			mIntent=new Intent(mContext.getApplicationContext(), HDMIActivity.class);
		}	
		else if(curTestItemStr.equals("Camera")){
			mIntent=new Intent(mContext.getApplicationContext(), CameraActivity.class);
		}
		else if(curTestItemStr.equals("CameraSub")){
			mIntent=new Intent(mContext.getApplicationContext(), CameraSubActivity.class);
		}
//		else if(curTestItemStr.equals("Otg")){
//			mIntent=new Intent(mContext.getApplicationContext(), OtgActivity.class);
//		}
		else if(curTestItemStr.equals("Usb")){
			mIntent=new Intent(mContext.getApplicationContext(), MiniUsbActivity.class);
		}
		else if(curTestItemStr.equals("StandardUsb")){
			mIntent=new Intent(mContext.getApplicationContext(), StandardUsbActivity.class);
		}
		else if(curTestItemStr.equals("Sd")){
			mIntent=new Intent(mContext.getApplicationContext(), sdcardactivity.class);
		}
		else if(curTestItemStr.equals("Sleep-Awake")){
			mIntent=new Intent(mContext.getApplicationContext(), SleepWakeActivity.class);
		}
		else if(curTestItemStr.equals("GetCpuID")){
			mIntent=new Intent(mContext.getApplicationContext(), getLincenseActivity.class);
		}else {
			
		}
    	

    	
    	return mIntent;
	}
   
    
    
    //测试项是否存在
    public static boolean existTestItem(String str){
    	int i=0;
    	int count=getTestItemCount();
    	do {
			if (i>=(count)) {
				break;
			}
			if (str.equals(TestInfoInfo.get(i))) {
				break;
			}
			i++;
    		
		} while (true);
    	if (i>=(count)) {
			return false;
		}else {
			return true;
		}
    }
    
    //超时错误
    public static void UpErrorTips(Context mContext){
    		ConnectivityManager conMan = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
    		NetworkInfo.State wifi = (conMan
    				.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState();
    		String string = wifi.toString();
    		if (!string.equals("CONNECTED")){
    			//Toast.makeText(mContext.getApplicationContext(), mContext.getResources().getString(R.string.wifi_disconnect), Toast.LENGTH_SHORT).show();
    			MyToast(mContext,null,R.string.wifi_disconnect);
    		}else {
    			MyToast(mContext,null,R.string.service_app_error);
    			//Toast.makeText(mContext.getApplicationContext(), mContext.getResources().getString(R.string.service_app_error), Toast.LENGTH_SHORT).show();
			}
    		
    	
    }
    
    public static void MyToast(Context mContext,String str,int mId){
    	LayoutInflater inflater = LayoutInflater.from(mContext);
    	View layout = inflater.inflate(R.layout.toast_tips, null);   
   
    	TextView text = (TextView) layout.findViewById(R.id.text);
    	if (mId!=-1) {
    		text.setText(mContext.getResources().getString(mId));
		}else {
			text.setText(str);
		}
    	

    	Toast toast = new Toast(mContext.getApplicationContext());
    	
    	toast.setGravity(Gravity.CENTER_VERTICAL, 0, mContext.getResources().getDimensionPixelSize(R.dimen.toast_y_offset));
    	toast.setDuration(Toast.LENGTH_SHORT);
    	toast.setView(layout);
    	toast.show();
    }
       
}
