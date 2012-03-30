package com.android.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlSerializer;

import android.R.integer;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.util.Xml;

public class ParseSeverData {
	public static String Checkid = null; // checkid
	public static String snCode = null; // sncode
	public static String getMac = ""; // 分配mac
	private static String Tag = "ParseSeverData";
	public static final String ServerIp = "192.168.0.193:8080";


	public static final int SUCCESS_CODE = 200;
	public static final int ERROR_CODE_NO_DATA = 0x100;

	public final static int CONNECT_WIFI = 0x100; // 连接wifi
	public final static int GET_CHECK_ID = 0x101; // 取check id

	private static String SAVE_INFO = "data/data/smit.com.factorytest/files/saveinfo.xml";



	public static LinkedList<UpTestItem> upItemAllThread = new LinkedList<UpTestItem>();

	// 关掉测试上传项线程
	public static void CloseAllThread() {
		while (upItemAllThread.size() > 0) {
			UpTestItem item = upItemAllThread.get(0);
			item.stopThread();
			upItemAllThread.remove(0);
		}
	}

	public static void AddThread(UpTestItem thread) {
		if (upItemAllThread != null)
			upItemAllThread.add(thread);
	}


	// encoded是16进制串 16进制字符串转为10进制字符串
	public static byte[] fromHexString(final String encoded) {
		if ((encoded.length() % 2) != 0)
			throw new IllegalArgumentException(
					"Input string must contain an even number of characters");

		final byte result[] = new byte[encoded.length() / 2];
		final char enc[] = encoded.toCharArray();
		for (int i = 0; i < enc.length; i += 2) {
			StringBuilder curr = new StringBuilder(2);
			curr.append(enc[i]).append(enc[i + 1]);
			result[i / 2] = (byte) Integer.parseInt(curr.toString(), 16);
		}
		return result;
	}

	

	
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
	


	

}
