package com.android.devicechecker.interfaces;

import com.android.devicechecker.MainActivity;
import com.android.util.FileOperate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class ItestActTemplate extends Activity implements IsetTestResult,
		IResultBtnOnClick {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		final Window win = getWindow();
		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	public void setValue(Context mContext, int testItemIndex, int value) {
		// TODO Auto-generated method stub
		FileOperate.setIndexValue(testItemIndex, value);
		FileOperate.writeToFile(mContext);
	}

	/**
	 * 
	 */
	public void setYesBtnOnClickListener(Button yesBtn,
			final int testItemIndex) {
		// TODO Auto-generated method stub
		yesBtn.setBackgroundColor(Color.GREEN);
		yesBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				setValue(getApplicationContext(), testItemIndex, RESULT_PASS);
				finish();

				if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {
					Intent mIntent = FileOperate.getCurIntent(
							getApplicationContext(), testItemIndex);
					if (mIntent != null) {
						mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(mIntent);
					}
					else {
						Intent mIntent2 = new Intent(getApplicationContext(), MainActivity.class);
						mIntent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(mIntent2);
					}
				}
			}
		});
	}

	public void setNoBtnOnClickListener(Button noBtn, final int testItemIndex) {
		// TODO Auto-generated method stub
		noBtn.setBackgroundColor(Color.RED);
		noBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				setValue(getApplicationContext(), testItemIndex, RESULT_FAIL);
				finish();
				Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mIntent);
			}
		});
	}

}
