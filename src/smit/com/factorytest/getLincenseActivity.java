package smit.com.factorytest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Iterator;

import org.xmlpull.v1.XmlSerializer;

import smit.com.util.FileOperate;
import smit.com.util.ParseSeverData;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.storage.IMountService;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class getLincenseActivity extends Activity {
	private static final String LICENSE_PATH1 = "/sdcard/tflash/license1.xml";
	private static final String LICENSE_PATH2 = "/sdcard/tflash/license2.xml";
	private String curfile;
	private String lastfile;
	private TextView mCpuId, mCpuCount, mTips;
	private Button mGetCpuId, mYes, mNo;
	private String machineCpuid="";
	private IMountService mMountService = null;
	private int delaytime=3000;
	private SdCardReceiver receiverSd;
	private IntentFilter mIntentFilter;

	private void setValue(int value){
		FileOperate.setIndexValue(FileOperate.TestItemCpu, value);
		FileOperate.writeToFile(this);
		
		//ParseSeverData.startUpTestItemThread("HDMI");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mount();
		
		setContentView(R.layout.get_cpuid);

		setupView();
		
		machineCpuid=SystemProperties.get("ro.hardware.cpuid", "");

		if (!sdcardIsMounted()) {
			mTips.setText(R.string.sdcard_mounted);
		} else {
			mTips.setText(R.string.please_get_cpuid);
		}	
		setCurwriteFile();
		
		if(cpuIdExist(machineCpuid)){
			mTips.setText(R.string.cpuid_isexist_tonext);
			mGetCpuId.setEnabled(false);
			mHandler.postDelayed(mRunnable, delaytime);
			
		}else{
			
		}
		
		if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
			setValue(FileOperate.CHECK_FAILURE);
		}

	}

	private void setupView() {
		mCpuId = (TextView) findViewById(R.id.cpu_id);
		mCpuCount = (TextView) findViewById(R.id.cpu_count);
		mTips = (TextView) findViewById(R.id.cpu_tips);
		mGetCpuId = (Button) findViewById(R.id.get_cpuid);
		mNo = (Button) findViewById(R.id.but_nook);

		mGetCpuId.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(sdcardIsMounted()){
					if(machineCpuid.length()>0)
					{
						if(cpuIdExist(machineCpuid)){
							if(writeCpuId(machineCpuid)<0){
								mTips.setText(R.string.write_cpuiding);
								mHandler.postDelayed(mWrite, delaytime);
							}else{
								mTips.setText(R.string.write_cpu_id_error);
							}
						}else{
							Toast.makeText(getApplicationContext(),R.string.cpuid_isexist ,Toast.LENGTH_LONG).show();
						}
						
					}else{
						Toast.makeText(getApplicationContext(),R.string.no_cpu_id ,Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(getApplicationContext(),R.string.sdcard_mounted ,Toast.LENGTH_LONG).show();
				}
			}
			
		});

		mNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setValue(FileOperate.CHECK_FAILURE);
				finish();
			}
		});

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		registerSdIntentReceivers();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		unregisterSdIntentReceivers();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mount();
		if(mHandler!=null){
			if(mRunnable!=null){
				mHandler.removeCallbacks(mRunnable);
			}
			if(mWrite!=null){
				mHandler.removeCallbacks(mWrite);
			}
		}
	}

	// 设置当前文件
	private int setCurwriteFile() {
		try {
			if ((!fileisExist(LICENSE_PATH1)) && (!fileisExist(LICENSE_PATH2))) {
				if (sdcardIsMounted()) {
					createXML(LICENSE_PATH1);
					createXML(LICENSE_PATH2);
				}
				lastfile = LICENSE_PATH1;
				curfile = LICENSE_PATH2;
			} else {
				boolean bool1 = XMLFileIsRight(LICENSE_PATH1);
				boolean bool2 = XMLFileIsRight(LICENSE_PATH2);
				int count1 = cpuIdCount(LICENSE_PATH1);
				int count2 = cpuIdCount(LICENSE_PATH2);
				if (fileisExist(LICENSE_PATH1) && fileisExist(LICENSE_PATH2)) {

					if (!bool1 && !bool2) {
						return -1;
					} else {
						if (bool1 && bool2) {
							if (count1 > count2) {
								if (count1 - count2 == 1) {
									lastfile = LICENSE_PATH1;
									curfile = LICENSE_PATH2;
								} else {
									forTransfer(LICENSE_PATH1, LICENSE_PATH2);
									lastfile = LICENSE_PATH1;
									curfile = LICENSE_PATH2;
								}
							} else if (count1 < count2) {
								if (count2 - count1 == 1) {
									lastfile = LICENSE_PATH2;
									curfile = LICENSE_PATH1;
								} else {
									lastfile = LICENSE_PATH2;
									curfile = LICENSE_PATH1;
									forTransfer(LICENSE_PATH2, LICENSE_PATH1);
								}
							} else if (count1 == count2) {
								lastfile = LICENSE_PATH1;
								curfile = LICENSE_PATH2;
							}
						} else if (bool1) {
							forTransfer(LICENSE_PATH1, LICENSE_PATH2);
							lastfile = LICENSE_PATH1;
							curfile = LICENSE_PATH2;
						} else if (bool2) {
							forTransfer(LICENSE_PATH2, LICENSE_PATH1);
							lastfile = LICENSE_PATH2;
							curfile = LICENSE_PATH1;
						}
					}

				} else if (fileisExist(LICENSE_PATH1)) {
					if (bool1) {
						forTransfer(LICENSE_PATH1, LICENSE_PATH2);
						lastfile = LICENSE_PATH1;
						curfile = LICENSE_PATH2;
					} else {
						return -2;
					}

				} else if (fileisExist(LICENSE_PATH2)) {
					if (bool2) {
						forTransfer(LICENSE_PATH2, LICENSE_PATH1);
						lastfile = LICENSE_PATH2;
						curfile = LICENSE_PATH1;
					} else {
						return -3;
					}

				}
			}
		} catch (Exception e) {
			return -4;
		}

		return 0;
	}

	private boolean fileisExist(String paramString) {

		File localFile = new File(paramString);
		int i;
		if ((localFile.exists()) && (localFile.length() > 0L)) {
			return true;
		} else {
			return false;
		}

	}

	public boolean sdcardIsMounted() {
		boolean bool = Environment.getStorageState(
				Environment.getStorageInfo("sdcard").mDirectory.getPath())
				.equals("mounted");

		return bool;
	}

	private void createXML(String paramString) {
		try {
			RandomAccessFile localRandomAccessFile = new RandomAccessFile(
					paramString, "rw");
			StringBuffer localStringBuffer = new StringBuffer();
			localStringBuffer.append("<license>");
			localStringBuffer.append("\n");
			localStringBuffer.append("</license>");
			String str = localStringBuffer.toString();
			localRandomAccessFile.writeBytes(str);
			localRandomAccessFile.close();
			return;
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Create lincse error",
					Toast.LENGTH_LONG).show();
		}

	}

	// f1 src f2 dst
	public static long forTransfer(String f1, String f2) throws Exception {

		int length = 2097152;
		FileInputStream in = new FileInputStream(f1);
		FileOutputStream out = new FileOutputStream(f2);
		FileChannel inC = in.getChannel();
		FileChannel outC = out.getChannel();
		int i = 0;
		while (true) {
			if (inC.position() == inC.size()) {
				inC.close();
				outC.close();
				break;
			}
			if ((inC.size() - inC.position()) < 20971520) {
				length = (int) (inC.size() - inC.position());
			} else {
				length = 20971520;
			}
			inC.transferTo(inC.position(), length, outC);
			inC.position(inC.position() + length);
			i++;
		}

		return 1;
	}

	private boolean XMLFileIsRight(String paramString) {
/*		int mCount = 0;
		try {
			SAXReader localSAXReader = new SAXReader();
			File localFile = new File(paramString);
			mCount = localSAXReader.read(localFile).getRootElement().elements(
					"machine").size();
		} catch (Exception e) {
			return false;
		}*/

		return true;
	}

	private int cpuIdCount(String paramString) {

		int mCount = 0;
/*		try {
			SAXReader localSAXReader = new SAXReader();
			File localFile = new File(paramString);
			mCount = localSAXReader.read(localFile).getRootElement().elements(
					"machine").size();
		} catch (Exception e) {

		}*/

		return mCount;
	}

	public class Cpuid {
		public String cpuid="";
		public String cpukey="";
	}
	
	private Cpuid getLastCpuIdXML(String paramString) {

		Cpuid str = new Cpuid();
	/*	try {
			SAXReader localSAXReader = new SAXReader();
			File localFile = new File(paramString);
			Element localElement = localSAXReader.read(localFile)
					.getRootElement();
			Iterator localIterator = localElement.elements("machine")
					.iterator();


			Element last=null;
			 while(true){
				 if (localIterator.hasNext()) {
					last=(Element)localIterator.next();
					} else {
						if(last!=null){
							str.cpuid = last.element("CPUID").getText();
							str.cpukey= last.element("KEY").getText();
							break;
						}
					}
			 }

		} catch (Exception e) {

		}
*/
		return str;
	}

	private boolean cpuIdExist(String paramString){
		 
		/* String str="";
		 try {
			 SAXReader localSAXReader = new SAXReader();
			 File localFile = new File(curfile);
			 Element localElement = localSAXReader.read(localFile).getRootElement();
			 Iterator localIterator = localElement.elements("machine").iterator();
			 
			 while(localIterator.hasNext()){
				 
				 if(((Element)localIterator.next()).element("CPUID").getText().equals(paramString)){
					 return true;
				 }else{
					continue;
				 }
			 }
			 
			 
			 
		} catch (Exception e) {
			
		}*/
		
		return false;  
	 }

	private int writeCpuId(String cpuid) {

		try {
			
			RandomAccessFile randomFile = new RandomAccessFile(curfile, "rw");
			int fileLength = (int) randomFile.length();
			byte[] mFilebyte = new byte[fileLength];
			int mStartPos = 0, item = 9;
			int i = fileLength - 1;

			randomFile.read(mFilebyte);
			for (; i >= 0; --i) {
				if (mFilebyte[i] == '>') {
					break;
				}
			}
			mStartPos = i - item;
			randomFile.seek(mStartPos);
			int count1 = cpuIdCount(curfile);
			int count2 = cpuIdCount(lastfile);
			StringBuffer stBuf = new StringBuffer();
			if (count1 == count2) {
				stBuf.append("<machine>");
				stBuf.append("\n");
				stBuf.append("<CPUID>");
				stBuf.append(cpuid);
				stBuf.append("</CPUID>");
				stBuf.append("\n");
				stBuf.append("<KEY>");
				stBuf.append("</KEY>");
				stBuf.append("\n");
				stBuf.append("</machine>");
				stBuf.append("\n");
				stBuf.append("</license>");
			} else {
				Cpuid lastCpuid = getLastCpuIdXML(lastfile);
				
				stBuf.append("<machine>");
				stBuf.append("\n");
				stBuf.append("<CPUID>");
				stBuf.append(lastCpuid.cpuid);
				stBuf.append("</CPUID>");
				stBuf.append("\n");
				stBuf.append("<KEY>");
				if(lastCpuid.cpuid.length()>0){
					stBuf.append(lastCpuid.cpukey);
				}
				stBuf.append("</KEY>");
				stBuf.append("\n");
				stBuf.append("</machine>");
				
				stBuf.append("\n");
				stBuf.append("<machine>");
				stBuf.append("\n");
				stBuf.append("<CPUID>");
				stBuf.append(cpuid);
				stBuf.append("</CPUID>");
				stBuf.append("\n");
				stBuf.append("<KEY>");
				stBuf.append("</KEY>");
				stBuf.append("\n");
				stBuf.append("</machine>");
				stBuf.append("\n");
				stBuf.append("</license>");
			}
			randomFile.writeBytes(stBuf.toString());
			randomFile.close();
		} catch (IOException e) {
			return -1;
		}

		return 0;
	}
	
	private synchronized IMountService getMountService() {
	       if (mMountService == null) {
	           IBinder service = ServiceManager.getService("mount");
	           if (service != null) {
	               mMountService = IMountService.Stub.asInterface(service);
	           } else {
	               Log.e("getcpu", "Can't get mount service");
	           }
	       }
	       return mMountService;
	    }
	
	private void mount() {
        IMountService mountService = getMountService();
        try {
        	String path = Environment.getStorageInfo("sdcard").mDirectory.toString();
            if (mountService != null) {
                mountService.mountVolume(path);
            } else {
                Log.e("getcpu", "Mount service is null, can't mount");
            }
        } catch (RemoteException ex) {
        }
    }
	
    private void doUnmount() {
        IMountService mountService = getMountService();
        String extStoragePath = Environment.getStorageInfo("sdcard").mDirectory.toString();
    	{		
	 		try {
	            mountService.unmountVolume(extStoragePath, true);
	        } catch (RemoteException e) {
	    
	        }
    	}    	
    }
    
    private Runnable mRunnable = new Runnable() {
    	public void run() {
    		setValue(FileOperate.CHECK_SUCCESS);
			finish();
			
			if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
				Intent mIntent = FileOperate.getCurIntent(getLincenseActivity.this,"GetCpuID");
				 if (mIntent!=null) {
					 startActivity(mIntent);
				}
			}
    	}
    };
    
    private Runnable mWrite = new Runnable() {
    	public void run() {
    		if(cpuIdExist(machineCpuid)){
    			mTips.setText(R.string.cpuid_isexist_tonext);
    			doUnmount();
    			mHandler.postDelayed(mRunnable, delaytime);
    		}else{
    			mHandler.postDelayed(mWrite, delaytime);
    		}
    	}
    };
    
    Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {			
		}
	};
	
	
	private void registerSdIntentReceivers() {

		receiverSd = new SdCardReceiver();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		mIntentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		mIntentFilter.addDataScheme("file");
		registerReceiver(receiverSd, mIntentFilter);

	}

	private void unregisterSdIntentReceivers() {
		if (receiverSd != null) {
			unregisterReceiver(receiverSd);
			receiverSd=null;
		}
	}
	

	class SdCardReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {
			String str = intent.getAction();
			String str1 = intent.getData().getPath();

			if (str.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				if (!sdcardIsMounted()) {
					mTips.setText(R.string.sdcard_mounted);
				} else {
					mTips.setText(R.string.please_get_cpuid);
				}	
				setCurwriteFile();
				
				if(cpuIdExist(machineCpuid)){
					mTips.setText(R.string.cpuid_isexist_tonext);
					mGetCpuId.setEnabled(false);
					mHandler.postDelayed(mRunnable, delaytime);
				}else{
					
				}
			}
			return;
		}
	}

}
