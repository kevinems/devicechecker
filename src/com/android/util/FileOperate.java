package com.android.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.devicechecker.GpsActivity;
import com.android.devicechecker.HDMIActivity;
import com.android.devicechecker.testNfc;
import com.android.devicechecker.testRecord;
import com.android.devicechecker.MiniUsbActivity;
import com.android.devicechecker.R;
import com.android.devicechecker.TestBatteryActivity;
import com.android.devicechecker.TestGSensor;
import com.android.devicechecker.TestKey;
import com.android.devicechecker.testLcd;
import com.android.devicechecker.TestVibratorActivity;
import com.android.devicechecker.TsTestActivity;
import com.android.devicechecker.WifiActivity;
import com.android.devicechecker.getLincenseActivity;
import com.android.devicechecker.sdcardactivity;
import com.android.devicechecker.testCameraActivity;
import com.android.devicechecker.testLed;

public class FileOperate {
	private static String FILE_DIR ="data/data/com.android.devicechecker/files/log.bin";	//log file
	public static String FILE_RECORD_AUDIO ="record_audio.3gp";		//record file created in record test
	private static String FILE_PATH ="log.bin";		//log file
	private static String FILE_PATH_XML ="data/data/com.android.devicechecker/files/log.xml";//
	private static String TEST_ITEM_FILE ="data/data/com.android.devicechecker/files/testitemfile.xml";//
	private static final String PRODUCT_PATH="/test_config.xml"; 
	public static final String sdCradPathTelechips=Environment.getExternalStorageDirectory()+"/tflash/";
	//public static final String sdCradPathNv=Environment.getExternalStorageDirectory()+"/extsd/";
	public static final String sdCradPathNv=Environment.getExternalStorageDirectory()+"";
	
	public static String Productid=null;
	public static String hostip=null;
	
	private static String Tag="FileOperate";
	private static byte[] buffer = new byte[100];
	public static int testCount=23;
	public static int curTestItem=0;
	public static boolean testmode=false;		//true
	
	public static final int TestItemWifi=0;
	public static final int TestItemLcd=1;
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
	public static final int TestItemBattery=18;
	public static final int TestItemCheckSum=19;
	public static final int TestItemTouchScreen=20;
	public static final int TestItemLed=21;
	public static final int TestItemNfc=22;
	
	public static final int TestItemRTC=100;
	
	public static final String TEST_LED_STRING = "Led";
	public static final String TEST_LCD_STRING = "Lcd";
	public static final String TEST_NFC_STRING = "NFC";
    
    //to control which item will be display on main activity.
	//the string sequence decide the sequence of the item display on the main activity.
    public static String []curtestItem={TEST_LCD_STRING, TEST_LED_STRING, "TouchScreen", "Camera","GSensor","Vibrator",
		/*"Video",*/"HDMI","Record",/*"Otg",*//*"Usb", "Sd",*/
		"Battery"/*,"Wifi"*/,"Key", TEST_NFC_STRING/*,"GetCpuID"*/};
	
	public static int CHECK_NULL=0;
	public static int CHECK_SUCCESS=1;
	public static int CHECK_FAILURE=2;
	public static String CHECK_NULL_STR="NoTest";
	public static String CHECK_SUCCESS_STR="Suc";
	public static String CHECK_FAILURE_STR="Fail";
	
	public static int TEST_MODE_ALL=0;
	public static int TEST_MODE_ITEM=1;
	public static int curStatus=TEST_MODE_ALL;
	
	//鍥哄畾ip
	public static  String AP_NAME="\"TTPP-LLIINNKK\"";
	//public static  String AP_PASSWORD="12341234";
	public static  String AP_PASSWORD="43214321";
	
	//
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
	
	//
	public static String testStatusToStr(int status){
		if (status==CHECK_SUCCESS) {
			return CHECK_SUCCESS_STR;
		}else if (status==CHECK_FAILURE) {
			return CHECK_FAILURE_STR;
		}else {
			return CHECK_NULL_STR;
		}
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
	
	//flag 鐪熶负鍔�
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
//           System.out.println( new String(buffer) );
           fis.close();
       } catch (FileNotFoundException e) {
           // TODO Auto-generated catch block
    	   Log.e(Tag,e.toString());
       } catch (IOException e) {
           // TODO Auto-generated catch block
    	   Log.e(Tag,e.toString());
       }
    }
    
  
    //璇诲嚭浼犵粰鏈嶅姟鍣ㄧ殑xml
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
    
    
    //鎺у埗鍣ㄥ彇鏁版嵁
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
    
    //涓ょ妯″紡
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
    
    
    
  //鍐欐祴璇曢」xml鏂囦欢   浠庢湇鍔″櫒寰楀埌鐨�
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
    
    //娴嬭瘯鍐橧tem鏂囦欢
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
     			
     		for (int i = 0; i < itemcount; i++) {//鎵�湁item
     			Node item = n.item(i);			
     			sNodeName=item.getChildNodes().item(0).getNodeValue(); 	
     			TestInfoInfo.add(sNodeName);
     		}
     			
		} catch (Exception e) {
			Log.e(Tag, "====xml parse error===="+e.toString());
		}		
    }
    //瀛樹笉瀛樺湪娴嬭瘯鏂囦欢
    public static boolean isExistTestItemFile(){
    	File TestItemFile = new File(TEST_ITEM_FILE);	
    	if (TestItemFile.exists()) {
			return true;
		}else {
			return false;
		}	
    }
    
    //寰楀埌娴嬭瘯椤�
    public static String getCurTestItem(int index){
    	if(TestInfoInfo!=null){
    		return TestInfoInfo.get(index);
    	}else {
			return null;
		}
    }
    
    //
    public static int getTestItemCount(){
    	if(TestInfoInfo!=null){
    		return TestInfoInfo.size();
    	}else {
			return 0;
		}
    }
    
    //寰楀埌涓嬩竴椤规祴璇曢」
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
    //
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
    	
    //寰楀埌娴嬭瘯椤筰ntent
    public static Intent getItemIntent(Context mContext, String curTestItemStr){
    	mIntent=null;
    	
    	if (curTestItemStr.equals("Wifi")) {
    		mIntent=new Intent(mContext.getApplicationContext(), WifiActivity.class);
		}else if(curTestItemStr.equals(FileOperate.TEST_LCD_STRING)){
			mIntent=new Intent(mContext.getApplicationContext(), testLcd.class);
		}else if(curTestItemStr.equals("Led")){
			mIntent=new Intent(mContext.getApplicationContext(), testLed.class);
		}else if(curTestItemStr.equals(FileOperate.TEST_NFC_STRING)){
			mIntent=new Intent(mContext.getApplicationContext(), testNfc.class);
		}else if(curTestItemStr.equals("Key")){
			mIntent=new Intent(mContext.getApplicationContext(), TestKey.class);
		}else if(curTestItemStr.equals("Gps")){
			mIntent=new Intent(mContext.getApplicationContext(), GpsActivity.class);
		}else if(curTestItemStr.equals("GSensor")){
			mIntent=new Intent(mContext.getApplicationContext(), TestGSensor.class);
		}	
		else if(curTestItemStr.equals("Vibrator")){
			mIntent=new Intent(mContext.getApplicationContext(), TestVibratorActivity.class);
		}
		else if(curTestItemStr.equals("Record")){
			mIntent=new Intent(mContext.getApplicationContext(), testRecord.class);
		}
		else if(curTestItemStr.equals("HDMI")){
			mIntent=new Intent(mContext.getApplicationContext(), HDMIActivity.class);
		}	
		else if(curTestItemStr.equals("Camera")){
			mIntent=new Intent(mContext.getApplicationContext(), testCameraActivity.class);
		}
		else if(curTestItemStr.equals("Usb")){
			mIntent=new Intent(mContext.getApplicationContext(), MiniUsbActivity.class);
		}
		else if(curTestItemStr.equals("Sd")){
			mIntent=new Intent(mContext.getApplicationContext(), sdcardactivity.class);
		}
		else if(curTestItemStr.equals("Battery")){
			mIntent=new Intent(mContext.getApplicationContext(), TestBatteryActivity.class);
		}
		else if(curTestItemStr.equals("GetCpuID")){
			mIntent=new Intent(mContext.getApplicationContext(), getLincenseActivity.class);
		}
		else if(curTestItemStr.equals("TouchScreen")){
			mIntent=new Intent(mContext.getApplicationContext(), TsTestActivity.class);
		}else {
			
		}
    	
    	return mIntent;
	}
   
    
    
    //娴嬭瘯椤规槸鍚﹀瓨鍦�
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
