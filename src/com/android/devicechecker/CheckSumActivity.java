package com.android.devicechecker;

import java.io.UnsupportedEncodingException;

import com.android.util.FileOperate;

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
	private boolean thread_flag = true;  
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
				ShowResult("���ڼ���CheckSum��ֵ....");
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
    public byte[] checkResultGetString(byte[] b)
    {
    	byte[] b_ret = new byte[b.length];
    	boolean flag = false;
    	int index = 0;

    	for(int i =0;i < b.length;i++)
    	{
    		if(b[i] == '/')
    		{
    			flag = true;
    		}
    		if(flag)
    		{
    			b_ret[index] = b[i];
    			b[i] =' ';
    			index ++;
    		}
    	}
    	b[b.length - 1] = '\n';
    	b_ret[index] = '\n';
    	return b_ret;
    }
	public void refreshContent(int type) throws UnsupportedEncodingException
	{ 
		if(ret_type == 0)
		{
			byte[] b = checkResultGetString(byte_data);
			String str = new String(byte_data,"gb2312");
			String str_ex = new String(b);
			
			ShowResult(str + str_ex);
			if(file_type == 1)
			{
				text_tag.setText("������ɣ�ϵͳ�ļ�����" +Integer.toString(count_flag_sys) + "����" + "Ԥװ�����ļ�����"
						+Integer.toString(count_flag_file) + "��");
			}
			else
			{
				text_tag.setText("������ɣ�ϵͳ�ļ�����" +Integer.toString(count_flag_sys) + "��");
			}
			
		}
		else if(ret_type == 3)
		{
			ShowResult(str_filename + "\n" );
			
			if(file_type == 0)
			{
				text_tag.setText("���ڼ��㣬���Ժ�....                 " +Integer.toString(count_flag_sys));
			}
			else
			{
				text_tag.setText("ϵͳ�ļ�����" +Integer.toString(count_flag_sys) + "��"
						+ ",  ���ڼ��㣬���Ժ�....                 " +Integer.toString(count_flag_file) );
			}
		} 
		else
		{ 
			String str = new String(byte_data);
			ShowResult(str); 
			tv.setTextColor(Color.RED); 
			text_tag.setText("CheckSum ������ֹ��"); 
		} 
	}
	public static int getByteArrayLength(byte[] b) {
		// TODO Auto-generated method stub
		int i; 
    	for(i = 0; i<b.length;i++) 
    	{
    		if(b[i] ==0)
    		{
    			break;
    		}
    	}
    	return i;
	}
	public static  void getUTF8ByteArray(byte[] fileName,int type)
	{
		String str = "";
		int len = 0;
		try {
			str = new String(fileName,"gb2312");
		} catch (UnsupportedEncodingException e) {  
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
		len = getByteArrayLength(fileName);
		if(len < str.length())
		{
			str = str.substring(0,len);
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
			str_filename = "���ڼ����ļ���" + str.substring(2,str.length());
		}
		else
		{
			str_filename = "���ڼ����ļ���" + str;
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