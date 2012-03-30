
package com.android.devicechecker;

import java.util.Calendar;

import com.android.util.FileOperate;
import com.android.util.ParseSeverData;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageParser.NewPermissionInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
 


public class RtcActivity extends Activity{
	//private Button btStart;
	private Long firsttime,secondtime;
	private String starttime,endtime;
	private AlertDialog.Builder mBuilder;
	private AlertDialog mAlert,progressAlert;
	//private Button mYes=null;
	//private Button mNo=null;
	private TextView mrtcTextView;
	private int openSleepstatus,screen_off;
	private int secondPerMin=60;
	private int screenOff=15;
	
	IWindowManager windowManageServce = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
	
	
	private Time m_time=null;
	private static final Uri CONTENT_URI_MESSAGE  = Uri.parse(Uri.parse("content://com.android.deskclock")+"/"+"alarm");
	public static final int clockid=100;
	public static final String ID = "_id";
	public static final String HOUR = "hour";
    /**
     * Minutes in localtime 0 - 59
     * <P>Type: INTEGER</P>
     */
    public static final String MINUTES = "minutes";
    /**
     * Days of week coded as integer
     * <P>Type: INTEGER</P>
     */
    public static final String DAYS_OF_WEEK = "daysofweek";
    /**
     * Alarm time in UTC milliseconds from the epoch.
     * <P>Type: INTEGER</P>
     */
    public static final String ALARM_TIME = "alarmtime";
    /**
     * True if alarm is active
     * <P>Type: BOOLEAN</P>
     */
    public static final String ENABLED = "enabled";
    /**
     * True if alarm should vibrate
     * <P>Type: BOOLEAN</P>
     */
    public static final String VIBRATE = "vibrate";
    //added by frankiewei
    public static final String APPCLS = "appcls";
    /**
     * Message to show when alarm triggers
     * Note: not currently used
     * <P>Type: STRING</P>
     */
    public static final String MESSAGE = "message";
    /**
     * Audio alert to play when alarm triggers
     * <P>Type: STRING</P>
     */
    public static final String ALERT = "alert";
    
    private final static String DM12 = "E h:mm aa";
    private final static String DM24 = "E k:mm";
    private final static String M12 = "h:mm aa";
    // Shared with DigitalClock
    final static String M24 = "kk:mm";
    
    
	private void setValue(int value){
		FileOperate.setIndexValue(FileOperate.TestItemRTC, value);
		FileOperate.writeToFile(this);
		
		//ParseSeverData.startUpTestItemThread("Rtc");
	}
	

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Window win = getWindow();
	    win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        
        setContentView(R.layout.rtc);
        //btStart=(Button)findViewById(R.id.rtcbutton);
        //btStart.setOnClickListener(this);
        mrtcTextView=(TextView)findViewById(R.id.rtc_state_text);
        
        /*mYes=(Button)findViewById(R.id.but_ok);
		mNo=(Button)findViewById(R.id.but_nook);
        mYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setValue(1);
				FileOperate.setCurTest(true);
				finish();
				
			}
		});
		
		mNo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setValue(2);
				FileOperate.setCurmode(false);
				finish();
			}
		});*/
        
    /*    openSleepstatus=Settings.System.getInt(getContentResolver(), Settings.System.STAY_ON_WHILE_PLUGGED_IN, 0);
        screen_off=Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 600000);     
        Settings.System.putInt(getContentResolver(), Settings.System.STAY_ON_WHILE_PLUGGED_IN, 0);
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, screenOff);
        setAlarmInfoEx();*/
        
        startRtcTest();    
    }
	
	private void popRightWrong(){

		endtime=FileOperate.runCmd("busybox hwclock",true);
		if (!firsttime.equals(endtime)) {
			setValue(1);
			FileOperate.setCurTest(true);
			
			mrtcTextView.setText(R.string.rtcsucess);
			finish();
			
			
			 Intent mIntent = new Intent(RtcActivity.this, CameraActivity.class);
				startActivity(mIntent);

		}else {
			setValue(2);
			FileOperate.setCurmode(false);

			mrtcTextView.setText(R.string.rtcerror);
			finish();		
		}
		  
	}
	
	Runnable progressDismiss = new Runnable() {
		public void run() {
			progressAlert.dismiss();
			progressAlert=null;
				
			popRightWrong();
		}
	};
	
	Runnable outapp= new Runnable(){
		public void run() {
			finish();
		}
	};
	
	private void startRtcTest(){
		 firsttime=SystemClock.currentThreadTimeMillis();
		   starttime=FileOperate.runCmd("busybox hwclock",true);
		  
			LayoutInflater inflater = LayoutInflater.from(this);
			View progressView = inflater.inflate(R.layout.progress_layout, null);
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
			mBuilder.setView(progressView);
			TextView message=(TextView)progressView.findViewById(R.id.progress_message);
			message.setText(R.string.rtcisrunning);
			message.setTextSize(30);
			progressAlert = mBuilder.create();
			progressAlert.show();
	
			new Handler().postDelayed(progressDismiss, 4000);
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		 //setBackInfo();
		 //Settings.System.putInt(getContentResolver(), Settings.System.STAY_ON_WHILE_PLUGGED_IN, openSleepstatus);
	     //Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, screen_off);
	}
	
	
	
	
	/**
     * @return true if clock is set to 24-hour mode
     */
    static boolean get24HourMode(final Context context) {
        return android.text.format.DateFormat.is24HourFormat(context);
    }
	/**
     * Save time of the next alarm, as a formatted string, into the system
     * settings so those who care can make use of it.
     */
    static void saveNextAlarm(final Context context, String timeString) {
        Settings.System.putString(context.getContentResolver(),
                                  Settings.System.NEXT_ALARM_FORMATTED,
                                  timeString);
    }
	
	private static String formatDayAndTime(final Context context, Calendar c) {
        String format = get24HourMode(context) ? DM24 : DM12;
        return (c == null) ? "" : (String)DateFormat.format(format, c);
    }
	
	 private static void enableAlert(Context context, final Alarm alarm,
	            final long atTimeInMillis) {
	        AlarmManager am = (AlarmManager)
	                context.getSystemService(Context.ALARM_SERVICE);


	        Intent intent = new Intent("com.android.deskclock.ALARM_ALERT");

	        Parcel out = Parcel.obtain();
	        alarm.writeToParcel(out, 0);
	        out.setDataPosition(0);
	        intent.putExtra("intent.extra.alarm_raw", out.marshall());

	        PendingIntent sender = PendingIntent.getBroadcast(
	                context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

	        am.set(AlarmManager.RTC_WAKEUP, atTimeInMillis, sender);


	        Calendar c = Calendar.getInstance();
	        c.setTime(new java.util.Date(atTimeInMillis));
	        String timeString = formatDayAndTime(context, c);
	        saveNextAlarm(context, timeString);
	    }
	 
	 public static Alarm getAlarm(ContentResolver contentResolver, int alarmId) {
	        Cursor cursor = contentResolver.query(
	                ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarmId),
	                Alarm.Columns.ALARM_QUERY_COLUMNS,
	                null, null, null);
	        Alarm alarm = null;
	        if (cursor != null) {
	            if (cursor.moveToFirst()) {
	                alarm = new Alarm(cursor);
	            }
	            cursor.close();
	        }
	        return alarm;
	    }
	 
	 
	 static Calendar calculateAlarm(int hour, int minute, Alarm.DaysOfWeek daysOfWeek) {

	        // start with now
	        Calendar c = Calendar.getInstance();
	        c.setTimeInMillis(System.currentTimeMillis());

	        int nowHour = c.get(Calendar.HOUR_OF_DAY);
	        int nowMinute = c.get(Calendar.MINUTE);

	        // if alarm is behind current time, advance one day
	        if (hour < nowHour  ||
	            hour == nowHour && minute <= nowMinute) {
	            c.add(Calendar.DAY_OF_YEAR, 1);
	        }
	        c.set(Calendar.HOUR_OF_DAY, hour);
	        c.set(Calendar.MINUTE, minute);
	        c.set(Calendar.SECOND, 0);
	        c.set(Calendar.MILLISECOND, 0);

	        int addDays = daysOfWeek.getNextAlarm(c);
	
	        if (addDays > 0) c.add(Calendar.DAY_OF_WEEK, addDays);
	        return c;
	    }
	  // Private method to get a more limited set of alarms from the database.
	    private static Cursor getFilteredAlarmsCursor(
	            ContentResolver contentResolver) {
	        return contentResolver.query(Alarm.Columns.CONTENT_URI,
	                Alarm.Columns.ALARM_QUERY_COLUMNS, Alarm.Columns.WHERE_ENABLED,
	                null, null);
	    }
	
	    
	    private void setAlarmInfoEx(){
	    	long currmills=System.currentTimeMillis()+30000;
	    	int hour,minutes,second;
	        Calendar c = Calendar.getInstance();
	        c.setTimeInMillis(currmills);
	        hour = c.get(Calendar.HOUR_OF_DAY);
	        minutes = c.get(Calendar.MINUTE);
	        
		
			ContentValues values = new ContentValues(10);
			ContentResolver resolver = getContentResolver();
			resolver.delete(CONTENT_URI_MESSAGE, "appcls = ?", new String[]{"22"});
		
	        values.put(ID,clockid);
	        values.put(ENABLED, 1);
	        values.put(HOUR, hour);
	        values.put(MINUTES, minutes);
	        values.put(ALARM_TIME, 0);
	        values.put(DAYS_OF_WEEK, 127);
	        values.put(MESSAGE, "111");
	        values.put(VIBRATE, 0);
	        values.put(APPCLS, "22");
	        values.put(ALERT, "content://media/internal/audio/media/16");   
	        resolver.insert(CONTENT_URI_MESSAGE, values);
	        
	 
	        final Alarm alarm = getAlarm(getContentResolver(), clockid);
	        alarm.time = currmills;
	        enableAlert(this, alarm, alarm.time);
	        
	}
	
	private void setBackInfo(){
		ContentResolver resolver = getContentResolver();
		
		//Uri uri = ContentUris.withAppendedId(CONTENT_URI_MESSAGE, clockid);
		//if (uri!=null) {
			 //resolver.delete(uri, "", null);
		//}  
		
		
		resolver.delete(CONTENT_URI_MESSAGE, "appcls = ?", new String[]{"22"});
	}
	
	//得到当前时间
	private String GetCurTime() {
		
		ContentResolver cv = getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                                           android.provider.Settings.System.TIME_12_24);
        return strTimeFormat;
	}

	
	
	/*private void statusBarIconKeyAction(final int keycode){
	
	PowerManager pow;
	long now = SystemClock.uptimeMillis();
	new Thread(new Runnable() {			
		public void run() {
			try{
				KeyEvent down = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, keycode, 0);
				KeyEvent up = new KeyEvent(0, 0, KeyEvent.ACTION_UP, keycode, 0);					
				windowManageServce.injectKeyEvent(down, true);					
				windowManageServce.injectKeyEvent(up, true); 
			}catch (Exception e) {
			}
		}
	}).start();		
}

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
   if(keyCode == KeyEvent.KEYCODE_POWER){
      return true;
   }
   return super.onKeyDown(keyCode, event);
}*/
	
	 
}
