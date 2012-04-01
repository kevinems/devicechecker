package com.android.devicechecker.interfaces;

import com.android.util.FileOperate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

public class ItestActTemplate extends Activity implements IsetTestResult,
		IResultBtnOnClick {

	public void setValue(Context mContext, int testItemIndex, int value) {
		// TODO Auto-generated method stub
		FileOperate.setIndexValue(testItemIndex, value);
		FileOperate.writeToFile(mContext);
	}

	/**
	 * 
	 */
	public void setYesBtnOnClickListener(Button yesBtn,
			final int testItemIndex, final String fileOperateStr) {
		// TODO Auto-generated method stub
		yesBtn.setBackgroundColor(Color.GREEN);
		yesBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				setValue(getApplicationContext(), testItemIndex, RESULT_PASS);
				finish();

				if (FileOperate.getCurMode() == FileOperate.TEST_MODE_ALL) {
					Intent mIntent = FileOperate.getCurIntent(
							getApplicationContext(), fileOperateStr);
					if (mIntent != null) {
						startActivity(mIntent);
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
			}
		});
	}

}
