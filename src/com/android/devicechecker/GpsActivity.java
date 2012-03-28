package com.android.devicechecker;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.android.util.FileOperate;
import com.android.util.ParseSeverData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class GpsActivity extends Activity implements OnItemClickListener,OnItemLongClickListener{
	
	 	private static final String TAG = "GpsActivityDemo";  
		private Button 				mYes=null;
		private Button 				mNo=null;	
		private AlertDialog.Builder mBuilder;
		private AlertDialog 		mAlert,progressAlert;
		private LocationManager     mLocationManager;
		
		private LayoutInflater mLayoutInflater;
		private GridView mGridView;
		
		boolean checkOk=false;	//�Ƿ��ǳɹ�
		
		private void setValue(int value){
			FileOperate.setIndexValue(FileOperate.TestItemGps, value);
			FileOperate.writeToFile(this);
			
			//ParseSeverData.startUpTestItemThread("Gps");
		}
		
		
		
	 @Override
	    public void onCreate(Bundle icicle) {
	        super.onCreate(icicle);
	        final Window win = getWindow();
		    win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);       
	        setContentView(R.layout.test_gps);
	     
	            
	        mNo=(Button)findViewById(R.id.but_nook);
	        mNo.setOnClickListener(new View.OnClickListener(){
	        	public void onClick(View v) {
	        		/* checkOk=false;
					    showDialog(FileOperate.DIALOG_UP_TEST_ITEM);
					    ParseSeverData.startUpTestItemThread("Gps",mHandler,FileOperate.DIALOG_UP_TEST_ITEM,FileOperate.CHECK_FAILURE);
					   */
	        		setValue(2);
	    			//FileOperate.setCurTest(true);
	    			finish();
				}  	
	        });
	        
	        mYes=(Button)findViewById(R.id.but_ok);
	        mYes.setOnClickListener(new View.OnClickListener(){
	        	public void onClick(View v) {
	        		/*checkOk=true;
					showDialog(FileOperate.DIALOG_UP_TEST_ITEM);
					ParseSeverData.startUpTestItemThread("Gps",mHandler,FileOperate.DIALOG_UP_TEST_ITEM,FileOperate.CHECK_SUCCESS);
				*/
	        		setValue(1);
	    			//FileOperate.setCurmode(false);
	    			finish();
	    			if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
	    			Intent mIntent = FileOperate.getCurIntent(GpsActivity.this,"Gps");
	    			startActivity(mIntent);
	    			}
				}  	
	        });
	        mGridView = (GridView)findViewById(R.id.gpsinfo);
	        
	        mLocationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
	        openGPSSettings();
	        setGpsListten();
	        startCameraGps();
			if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
				FileOperate.setIndexValue(FileOperate.TestItemGps, FileOperate.CHECK_FAILURE);
				FileOperate.writeToFile(this);
			}

	    }
	 
	 
	 private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {

         public void onGpsStatusChanged(int event) {
             Log.w(TAG, "now gps status changed" + event);
             GpsStatus status = mLocationManager.getGpsStatus(null); // ȡ��ǰ״̬
             
             bindApplications(event, status);
         } // GPS״̬�仯ʱ�Ļص������������ź�ǿ�ȵ�
    };
	
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.w("xieyan", "now location changed");
            updateWithNewLocation(location);
        } // ��γ�ȱ仯ʱ�Ļص�
        
        public void onProviderDisabled(String provider) {
            Log.w("xieyan", "now provider disable");
            updateWithNewLocation(null);
        }

        public void onProviderEnabled(String provider) {
            Log.w("xieyan", "now provider enable");
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.w("xieyan", "now provider status changed" + status);
        }
    };
    
	 public void setGpsListten(){
		 
		 String provider = mLocationManager.GPS_PROVIDER;
         Location location = mLocationManager.getLastKnownLocation(provider);
         mLocationManager.requestLocationUpdates(provider, 2000, 10,
                  locationListener);  // 2��һ�Σ���ʼ����gps���
         mLocationManager.addGpsStatusListener(statusListener); // ע��״̬��Ϣ�ص�
         updateWithNewLocation(location);
	 }
	 
	 //gps����
	 private void toggleGPS() {
			Intent gpsIntent = new Intent();
			gpsIntent.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
			gpsIntent.setData(Uri.parse("custom:3"));
			try {
				PendingIntent.getBroadcast(this, 0, gpsIntent, 0).send();
			}
			catch (CanceledException e) {
				e.printStackTrace();
			}
		}
	 
	 private void openGPSSettings() {           
	        if (!mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {   
	        	toggleGPS();
	        }
	       
	    }
	 	 
	 private int getGpsCount(int event, GpsStatus status){
		 int count=0;
		 
		 if (status == null) {
			 count=0;
         } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
             int maxSatellites = status.getMaxSatellites();
             Iterator<GpsSatellite> it = status.getSatellites().iterator();
             
             while (it.hasNext() && count <= maxSatellites) {
                  GpsSatellite s = it.next();
                  count++;
             } 
         }		
		 
		 return count;
	 }
	 
	 private GpsSatellite getGpsInfo(int event, GpsStatus status, int index){
		 GpsSatellite gpsSatellite=null;
		 
		 if (status == null) {
			 return null;
         } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
             int maxSatellites = status.getMaxSatellites();
             Iterator<GpsSatellite> it = status.getSatellites().iterator();
             
             int i=0;
             while (it.hasNext() && i <= index) {
                  gpsSatellite = it.next();
                  i++;
             } 
         }		
		 
		 return gpsSatellite;
	 }
	 
	 
	 
	 
	 
	 
	 private void updateWithNewLocation(Location location) {
        /* if (location != null) {
             double lat = location.getLatitude();
             double lng = location.getLongitude();     // ȡ��γ��
             TextView latView = (TextView) findViewById(R.id.TextViewLng);
             TextView lngView = (TextView) findViewById(R.id.TextViewLat);
             latView.setText(getString(R.string.latitude)
                       + String.format("%.5f", lat));
             lngView.setText(getString(R.string.longitude)
                       + String.format("%.5f", lng));
         } else {
             TextView latView = (TextView) findViewById(R.id.TextViewLng);
             TextView lngView = (TextView) findViewById(R.id.TextViewLat);
             latView.setText(getString(R.string.getinfo_fail));
             lngView.setText("");
         }
*/
    }
	 
	 @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			toggleGPS();
		}

	}
        
	 
        Runnable progressDismiss = new Runnable() {
    		public void run() {
    			progressAlert.dismiss();
    			progressAlert=null;
    		}
    	}; 	
    	
        private void startCameraGps(){
   		  
   			LayoutInflater inflater = LayoutInflater.from(this);
   			View progressView = inflater.inflate(R.layout.progress_layout, null);
   			AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
   			mBuilder.setView(progressView);
   			TextView message=(TextView)progressView.findViewById(R.id.progress_message);
   			message.setText(R.string.test_gps_testing);
   			message.setTextSize(30);
   			progressAlert = mBuilder.create();
   			progressAlert.show();
   	
   			new Handler().postDelayed(progressDismiss, 6000);//
   	}
        

    	public void bindApplications(int event, GpsStatus status){	
    		mGridView.setNumColumns(1);
    		mGridView.setAdapter(new ApplicationsAdapter(this,event,status));
    		mGridView.setOnItemClickListener(this);
    		mGridView.setOnItemLongClickListener(this);		
    	}
    	
    	
    	  /**
         * GridView adapter to show the list of all installed applications.
         */
        private class ApplicationsAdapter extends ArrayAdapter {

        	Context mContext;
        	GpsStatus mStatus;
        	int mEvent;
        	
            public ApplicationsAdapter(Context context, int event, GpsStatus status) {
                super(context, 0);
                mContext=context;
                mEvent=event;
                mStatus=status;
            }

            public int getCount() {	
    			return getGpsCount(mEvent,mStatus);
    		}
            
            public View getView(int position, View convertView, ViewGroup parent) {       
            	View myview=null;
    			TextView  prn;
    			TextView  snr;
    			
    			if (convertView==null) {
    				LayoutInflater inflater = LayoutInflater.from(mContext);
    				try {
    					myview = inflater.inflate(R.layout.showgpsitem, null);				
    				} catch (Exception e) {
    				}	
    				
    				prn=(TextView)myview.findViewById(R.id.prn);
    				snr=(TextView)myview.findViewById(R.id.snr);		
    				
    				GpsSatellite gpsinfo=getGpsInfo(mEvent,mStatus,position);
    				if (gpsinfo!=null) {
    					String str;
    					str=""+gpsinfo.getPrn();
    					prn.setText(str);
    					str=""+gpsinfo.getSnr();
    					snr.setText(str);	
					}
    				
    				
    				return myview;
    			}else {
    				return convertView;
    			} 			
            }
        }
       
        
    	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

    	}
    	
    	public boolean onLongClick(View v){
    		//Log.e(Launcher.LOG_TAG,"~~~~~~~~~~~~~~AllAppsView onLongClick111111.....");
    		return false;
    	}
    	
    	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
    		
            return false;
        }
    	
    	 Handler mHandler = new Handler(){
  		   public void handleMessage(Message msg){
  			   switch (msg.what) {
  			   }
  			   
  			   }
  	 };

  	public Dialog onCreateDialog(int id){
      	Dialog dialog=null;
      	if (id==FileOperate.DIALOG_UP_TEST_ITEM) {
      		LayoutInflater inflater = LayoutInflater.from(this);
  			View progressView = inflater.inflate(R.layout.progress_layout, null);
  			AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
  			mBuilder.setView(progressView);
  			TextView message=(TextView)progressView.findViewById(R.id.progress_message);
  			message.setText(R.string.up_test_item);
  			message.setTextSize(30);
  			message.setTextColor(getResources().getColor(R.color.yellow));
  			progressAlert = mBuilder.create();
  			progressAlert.show();
  			dialog=progressAlert;
  			progressAlert.setOnKeyListener(new OnKeyListener(){
  				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
  					if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN) {
  						
  					}
  					return false;
  				}
  			});
  		}
  	
      	return dialog;
  	}
}
