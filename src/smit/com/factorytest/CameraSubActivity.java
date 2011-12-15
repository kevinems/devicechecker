package smit.com.factorytest;

import smit.com.util.FileOperate;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class CameraSubActivity extends Activity implements SurfaceHolder.Callback{
	
	 private static final String 	TAG = "CameraSubActivityDemo";
	    private SurfaceView 		mPreview;
	    private SurfaceHolder 		holder;
	    private Camera        		m_camera;
	    
		private Button 				mYes=null;
		private Button 				mNo=null;
		private Button				mCamera=null;
		TextView isTakepic;
		
		boolean startpic=false;
		
		boolean checkOk=false;	//是否是成功
	
		private void setValue(int value){
			FileOperate.setIndexValue(FileOperate.TestItemCameraSub, value);
			FileOperate.writeToFile(this);
					
		}
		
	 @Override
	    public void onCreate(Bundle icicle) {
	        super.onCreate(icicle);
	        final Window win = getWindow();
		    win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);       
	        setContentView(R.layout.test_camera);
	        mPreview = (SurfaceView) findViewById(R.id.surface);
	        holder = mPreview.getHolder();
	        holder.addCallback(this);
	        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	            
	        mNo=(Button)findViewById(R.id.but_nook);
	        mNo.setOnClickListener(new View.OnClickListener(){
	        	public void onClick(View v) {
	        		if (startpic) {
	        			setValue(2);
		    			finish();        		
					}
	        		
				}  	
	        });
	        
	        mYes=(Button)findViewById(R.id.but_ok);
	        mYes.setOnClickListener(new View.OnClickListener(){
	        	public void onClick(View v) {
	        		if (startpic) {
	        		setValue(1);
	    			//FileOperate.setCurmode(false);
	    			finish();
	    			 if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
	    				 
	    				 Intent mIntent = FileOperate.getCurIntent(CameraSubActivity.this,"CameraSub");
						 if (mIntent!=null) {
							 if (mHandler!=null) {	//release resource to avoid force close
									
									mHandler.removeCallbacks(progressDismiss);
									mHandler=null;
								}
								
								if (m_camera!=null) {
									  m_camera.stopPreview();
							          m_camera.release();
							          m_camera = null;         
								}
		    				 startActivity(mIntent);
						}
					}     		
	        			/*checkOk=true;
						showDialog(FileOperate.DIALOG_UP_TEST_ITEM);
						ParseSeverData.startUpTestItemThread("Camera",mHandler,FileOperate.DIALOG_UP_TEST_ITEM,FileOperate.CHECK_SUCCESS);*/
				}  	
	        	}
	        });
	        
	        mCamera = (Button)findViewById(R.id.test_camera);
	        mCamera.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					mCamera.setVisibility(View.GONE);
					takePicture();  
				}
			});
	        
	        isTakepic=(TextView)findViewById(R.id.istakepic);
	        
	        if (FileOperate.getCurMode()==FileOperate.TEST_MODE_ALL){
	        FileOperate.setIndexValue(FileOperate.TestItemCameraSub, FileOperate.CHECK_FAILURE);
	        FileOperate.writeToFile(this);
	        }
	     }
	 
	 @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(startpic){
			super.onBackPressed();
		}
	}
	 
	 @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mHandler!=null) {
			
			mHandler.removeCallbacks(progressDismiss);
			mHandler=null;
		}
		
		if (m_camera!=null) {
			  m_camera.stopPreview();
	          m_camera.release();
	          m_camera = null;         
		}

	}
	 
	 
	 public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
	        Log.d(TAG, "surfaceChanged called");
	        if (m_camera!=null){
	        	
	        	try {
	        		Camera.Parameters parameters = m_camera.getParameters();
		            parameters.setPictureFormat(PixelFormat.JPEG);
		            //parameters.setPreviewSize(320, 240);
		            parameters.setPreviewSize(480, 360);
		            m_camera.setParameters(parameters);
		            m_camera.setDisplayOrientation(90);
		            m_camera.startPreview();
				} catch (Exception e) {
					Log.d(TAG, "surfaceChanged error"+e.toString());
				}
	        
	            }
	    }

	    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
	        Log.d(TAG, "surfaceDestroyed called");
	        
	        if (m_camera!=null) {
				  m_camera.stopPreview();
		          m_camera.release();
		          m_camera = null;
			}
	    }
	    
	    ErrorCallback cb=new ErrorCallback() {
			
			@Override
			public void onError(int arg0, Camera arg1) {
				// TODO Auto-generated method stub
				Log.d(TAG, "ErrorCallback Error");
			}
		};

	    public void surfaceCreated(SurfaceHolder holder) {
	        Log.d(TAG, "surfaceCreated called");
	                
	        try 
            {        	
	        	m_camera = Camera.open(m_camera.getNumberOfCameras()-1);	//sub camera
	        	if (m_camera!=null){
           		 m_camera.setPreviewDisplay(holder);
               }
            } catch (RuntimeException e) {       
            	if (m_camera!=null){
                m_camera.release();
                m_camera = null;
                }
            } catch (Exception e) {
            	if (m_camera!=null){
            	 m_camera.release();
                 m_camera = null;
            	}
			}
                 
//            startCameraTest();
	    }
	    
	    public void takePicture() 
        {
            if (m_camera != null) 
            {
                m_camera.takePicture(null, null, jpegCallback);
            }
        }
	    
	    private PictureCallback jpegCallback = new PictureCallback() 
        {

			@Override
			public void onPictureTaken(byte[] arg0, Camera arg1) {
				
				startpic=true;
    			isTakepic.setVisibility(View.GONE);
			}
        };
        
        
        Runnable progressDismiss = new Runnable() {
    		public void run() {
    			
    			
    			takePicture();    			
    			
    		}
    	};
        
        private void startCameraTest(){
   		  
   			mHandler.postDelayed(progressDismiss, 3000);//
   	}
        
        Handler mHandler = new Handler(){
 		   public void handleMessage(Message msg){
 			   switch (msg.what) {
 			   }
 			   
 			   }
 	 };
 	 
}
