package smit.com.factorytest;

import java.io.UnsupportedEncodingException;

import smit.com.util.FileOperate;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheckSumActivity extends Activity {
    /** Called when the activity is first created. */
	private Button mYes=null;
	private Button mNo=null;
	
	private TextView text_tag;
	TextView tv;
	byte[] result; 
	public Button calc;
	public static byte[] byte_data;
	
	private static int count_flag_file = 0;
	private static int count_flag_sys = 0;
	int file_count = 0;

	static Handler handler_refesh;
	static int ret_type = 0;
	private boolean thread_flag = true;;
	static String str_filename = " ";
	static int flag = 0;
	static int file_type = 0;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checksum);
        
        mYes=(Button)findViewById(R.id.but_ok);
		mNo=(Button)findViewById(R.id.but_nook);
		mYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {			
				setValue(1);
				finish();
				
				if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
					Intent mIntent = FileOperate.getCurIntent(CheckSumActivity.this,"CheckSum");
					if (mIntent != null) {
						startActivity(mIntent);
					}
				}
			}
		});
		
		mNo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setValue(2);
				finish();
			}
		});
		
		if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
			FileOperate.setIndexValue(FileOperate.TestItemCheckSum, FileOperate.CHECK_FAILURE);
			FileOperate.writeToFile(this);
		}
        
        tv = (TextView)findViewById(R.id.textView1);
        text_tag = (TextView)findViewById(R.id.text_tag);
        
        calc = (Button)findViewById(R.id.button1);
        View.OnClickListener ChecksumFunc = new View.OnClickListener() {		 
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(thread_flag)
			{
				ShowResult("正在计算CheckSum的值....");
				tv.setTextColor(Color.WHITE);
				count_flag_file = 0;
				count_flag_sys = 0;
				str_filename = " ";
				new check_cacl().start();
			}
		}
       };
       calc.setOnClickListener(ChecksumFunc);
       handler_refesh = new Handler()
       {
       	  public void handleMessage(Message msg)
			  {        
       		  try {
				refreshContent(msg.what);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				  super.handleMessage(msg);
			  }
       };
    } 
	public void refreshContent(int type) throws UnsupportedEncodingException
	{ 
		if(ret_type == 0)
		{
			String str = new String(byte_data,"gb2312");
			ShowResult(str);
			if(file_type == 1)
			{
				text_tag.setText("计算完成，系统文件总数：" +Integer.toString(count_flag_sys) + "个，" + "预装资料文件总数："
						+Integer.toString(count_flag_file) + "个");
			}
			else
			{
				text_tag.setText("计算完成，系统文件总数：" +Integer.toString(count_flag_sys) + "个");
			}
			
		}
		else if(ret_type == 3)
		{
			ShowResult(str_filename + "\n" );
			
			if(file_type == 0)
			{
				text_tag.setText("正在计算，请稍候....                 " +Integer.toString(count_flag_sys));
			}
			else
			{
				text_tag.setText("系统文件总数：" +Integer.toString(count_flag_sys) + "个"
						+ ",  正在计算，请稍候....                 " +Integer.toString(count_flag_file) );
			}
		} 
		else
		{ 
			String str = new String(byte_data);
			ShowResult(str); 
			tv.setTextColor(Color.RED); 
			text_tag.setText("CheckSum 计算终止！"); 
		} 
	}
	public static  void getUTF8ByteArray(byte[] fileName,int type)
	{
		String str = ""; 
		try {
			str = new String(fileName,"gb2312");
		} catch (UnsupportedEncodingException e) {  
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
		ret_type = 3; 
		if(type == 1)
		{
			count_flag_file++;
		}
		else
		{
			count_flag_sys++;
		}
		if(str.indexOf(":") >=0)
		{
			str_filename = "正在计算文件：" + str.substring(2,str.length());
		}
		else
		{
			str_filename = "正在计算文件：" + str;
		}
		
		handler_refesh.sendMessage(handler_refesh.obtainMessage());
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		file_type = type;
		byte[] new_data = str.getBytes();  
		setFileName(new_data);
	}  
	public void ShowResult(String str){    
	    tv.setText(str); 
	} 
	
	public synchronized byte[] Calculate(){  
		return CalculateJni(); 
	}     
	
	public native byte[] CalculateJni();
	public native static void setFileName(byte[] fileName);
    static
    { 
    	System.loadLibrary("checksumJni"); 
    }
    class check_cacl extends Thread{
    	public void run() 
    	{
    		thread_flag = false;
			byte_data = Calculate();
			thread_flag = true;
			if(byte_data[0] == 'C')
			{ 
				ret_type = 0;
			}
			else
			{
				ret_type = 2;
			}
			
			handler_refesh.sendMessage(handler_refesh.obtainMessage());
    		return ;
    	}
    };
    
    private void setValue(int value){
		FileOperate.setIndexValue(FileOperate.TestItemCheckSum, value);
		FileOperate.writeToFile(this);
	}
    
}