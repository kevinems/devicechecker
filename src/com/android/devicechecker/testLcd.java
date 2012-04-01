package com.android.devicechecker;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.devicechecker.interfaces.ItestActTemplate;
import com.android.util.FileOperate;
import com.android.util.enhanceToast;

public class testLcd extends ItestActTemplate {
	private static final String TAG = "TestLcd";

	private Button mYes = null;
	private Button mNo = null;
	private TextView mDisplayColor = null;	//for display color
	private int mNum = 0;
	private enhanceToast mEnhanceToast;
	boolean checkOk = false; //

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.i(TAG, "onCreate");

		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.test_lcd);

		initView();
	}

	private void initView() {
		mDisplayColor = (TextView) findViewById(R.id.test_color_text1);
		mYes = (Button) findViewById(R.id.but_ok);
		mNo = (Button) findViewById(R.id.but_nook);

		mYes.setVisibility(View.GONE);
		mNo.setVisibility(View.GONE);
		
		setYesBtnOnClickListener(mYes, FileOperate.TestItemLcd, FileOperate.TEST_LCD_STRING);
		setNoBtnOnClickListener(mNo, FileOperate.TestItemLcd);

		mEnhanceToast = new enhanceToast(this);
		toNextTest();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mNum <= 7) {
				mYes.setVisibility(View.GONE);
				mNo.setVisibility(View.GONE);
				toNextTest();
			} else {
				mYes.setVisibility(View.VISIBLE);
				mNo.setVisibility(View.VISIBLE);
			}
			break;

		default:
			break;
		}

		return true;
	}

	private void brightness(float f) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = f;
		getWindow().setAttributes(lp);
	}

	public void toNextTest() {

		if (mNum == 0) {
			mDisplayColor.setBackgroundColor(Color.RED);
			mEnhanceToast.displayToast(getString(R.string.next_tips));
		}
		if (mNum == 1) {
			mDisplayColor.setBackgroundColor(Color.GREEN);
			mEnhanceToast.displayToast(getString(R.string.next_tips));
		}
		if (mNum == 2) {
			mDisplayColor.setBackgroundColor(Color.BLUE);
			mEnhanceToast.displayToast(getString(R.string.next_tips));
		}
		if (mNum == 3) {
			mDisplayColor.setBackgroundColor(Color.BLACK);
			mEnhanceToast.displayToast(getString(R.string.next_tips));
		}
		if (mNum == 4) {
			mDisplayColor.setBackgroundColor(Color.WHITE);
			mEnhanceToast.displayToast(getString(R.string.next_tips));
		}
		if (mNum == 5) {
			mDisplayColor.setBackgroundColor(Color.WHITE);
			mEnhanceToast.displayToast(getString(R.string.test_brightness_low));
			brightness(0.1f);
		}
		if (mNum == 6) {
			mEnhanceToast
					.displayToast(getString(R.string.test_brightness_middle));
			brightness(0.5f);
		}
		if (mNum == 7) {
			brightness(1.0f);
			mEnhanceToast
					.displayToast(getString(R.string.test_brightness_height));
			mYes.setVisibility(View.VISIBLE);
			mNo.setVisibility(View.VISIBLE);
			mNo.setBackgroundColor(Color.RED);
		}

		mNum++;
	}
}
