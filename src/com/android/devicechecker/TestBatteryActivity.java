package com.android.devicechecker;

import com.android.util.FileOperate;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class TestBatteryActivity extends Activity{
	private static final String TAG = "TestBattery";
	
	
	private Button mYes=null;
	private Button mNo=null;
	
	private TextView mStatus = null;
	private TextView mLevel = null;
	
	boolean checkOk=false;	//是否是成功
	
	private PreferenceManager mPreferenceManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		 final Window win = getWindow();
 	    win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.test_battery);	
		
        mStatus = (TextView)findViewById(R.id.battery_status);
        mLevel = (TextView)findViewById(R.id.battery_level);
		
		mYes=(Button)findViewById(R.id.but_ok);
		mNo=(Button)findViewById(R.id.but_nook);
		mYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {			
				setValue(1);
				finish();
				
				if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
					Intent mIntent = FileOperate.getCurIntent(TestBatteryActivity.this,"Battery");
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
			FileOperate.setIndexValue(FileOperate.TestItemBattery, FileOperate.CHECK_FAILURE);
			FileOperate.writeToFile(this);
		}
		
	}
		
	private void setValue(int value){
		FileOperate.setIndexValue(FileOperate.TestItemBattery, value);
		FileOperate.writeToFile(this);
	}
	
	 /**
     * Finds a {@link Preference} based on its key.
     * 
     * @param key The key of the preference to retrieve.
     * @return The {@link Preference} with the key, or null.
     * @see PreferenceGroup#findPreference(CharSequence)
     */
    public Preference findPreference(CharSequence key) {
        
        if (mPreferenceManager == null) {
            return null;
        }
        
        return mPreferenceManager.findPreference(key);
    }
	
    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {

                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                
//                mBatteryLevel.setSummary(String.valueOf(level * 100 / scale) + "%");
                mLevel.setText(getString(R.string.battery_info_scale_label) + "\r\n" + String.valueOf(level * 100 / scale) + "%");
                
                int plugType = intent.getIntExtra("plugged", 0);
                int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                String statusString;
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    statusString = getString(R.string.battery_info_status_charging);
                    if (plugType > 0) {
                        statusString = statusString + " " + getString(
                                (plugType == BatteryManager.BATTERY_PLUGGED_AC)
                                        ? R.string.battery_info_status_charging_ac
                                        : R.string.battery_info_status_charging_usb);
                    }
                } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                    statusString = getString(R.string.battery_info_status_discharging);
                } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                    statusString = getString(R.string.battery_info_status_not_charging);
                } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                    statusString = getString(R.string.battery_info_status_full);
                } else {
                    statusString = getString(R.string.battery_info_status_unknown);
                }
//                mBatteryStatus.setSummary(statusString);
                mStatus.setText(getString(R.string.battery_info_status_label) + "\r\n" + statusString);
            }
        }
    };
    
    protected void onResume() {
    	super.onResume();
    	registerReceiver(mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    };
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	unregisterReceiver(mBatteryInfoReceiver);
    }
}
