package com.android.devicechecker;

import com.android.util.FileOperate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class JudgeTestResult extends Activity implements OnClickListener{

	Button btOk;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Window win = getWindow();
	    win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        
        setContentView(R.layout.curtestitem);
        
        //setTitle(FileOperate.getCurName(this));
        
        btOk=(Button)findViewById(R.id.judgeid);
        btOk.setOnClickListener(this);
        

        
        RadioButton rb=(RadioButton)findViewById(R.id.off);
        rb.setOnCheckedChangeListener(new OnCheckedChangeListener(){ //为RadioButton添加监听器及开关灯业务代码
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				setBulbState(!isChecked);
			}
        });     
    }
	
	public void onClick(View v) {
		if (v==btOk) {
		/*	Intent mIntent = FileOperate.getCurIntent(this);
			startActivity(mIntent);*/
		}
	}
	
	   public void setBulbState(boolean state){
       	//设置图片状态
   		RadioButton rb=(RadioButton)findViewById(R.id.off);
   		rb.setChecked(!state);
   		rb=(RadioButton)findViewById(R.id.on);
   		rb.setChecked(state);							//设置单选按钮状态
       }
	
}
