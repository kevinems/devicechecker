package com.android.devicechecker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.devicechecker.interfaces.ItestActTemplate;
import com.android.util.FileOperate;
import com.android.util.enhanceToast;

public class testTouchScreen extends ItestActTemplate
{
	private static final int ID_BTN_OK = 1;
	private static final int ID_BTN_CANCEL = 2;
	private enhanceToast mEnhanceToast;
	private Button btn_ok;
	private Button btn_cancel;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		/* no title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		/* full screen */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	
		/* screen orientation landscape */
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				
		/* Create button and setting */
        btn_ok = new Button(this);
        btn_ok.setText(R.string.btn_ok_text);
        btn_ok.setId(ID_BTN_OK);
        setYesBtnOnClickListener(btn_ok, FileOperate.TestItemTouchScreen);
        btn_cancel = new Button(this);
        btn_cancel.setText(R.string.btn_cancel_text);
        btn_cancel.setId(ID_BTN_CANCEL);
        setNoBtnOnClickListener(btn_cancel, FileOperate.TestItemTouchScreen);

		/* main layout */
        RelativeLayout layout = new RelativeLayout(this);
		layout.addView(new TsTestView(this), new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		      
        /* button layout */
		RelativeLayout btn_layout = new RelativeLayout(this);
		RelativeLayout.LayoutParams btn_layout_param = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		btn_layout.setLayoutParams(btn_layout_param);
		
		/* ok button */
		RelativeLayout.LayoutParams btn_ok_param = new RelativeLayout.LayoutParams(
				(int) getResources().getDimension(
						R.dimen.test_item_bottom_button_width),
				(int) getResources().getDimension(
						R.dimen.test_item_bottom_button_heith));
		btn_ok_param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);	
		btn_layout.addView(btn_ok, btn_ok_param);
		
		/* cancel button */
		RelativeLayout.LayoutParams btn_cancel_param = new RelativeLayout.LayoutParams(
				(int) getResources().getDimension(
						R.dimen.test_item_bottom_button_width),
				(int) getResources().getDimension(
						R.dimen.test_item_bottom_button_heith));
		btn_cancel_param.addRule(RelativeLayout.ALIGN_TOP, ID_BTN_CANCEL);
		btn_cancel_param.addRule(RelativeLayout.LEFT_OF, ID_BTN_CANCEL);	
		btn_layout.addView(btn_cancel, btn_cancel_param);

		/* sub layout */
		RelativeLayout.LayoutParams layout_param = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		layout_param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		layout.addView(btn_layout, layout_param);
		
        /* set custom view */
		setContentView(layout);
		
		/* show tips */
		mEnhanceToast = new enhanceToast(this);
		mEnhanceToast.displayToast(getString(R.string.tstest_tips, Toast.LENGTH_SHORT));
	}
	
	public class TsTestView extends View
	{
		private final Paint mTextPaint;
		private final Paint mTextBackgroundPaint;
		private final Paint mPaint;
		private final Paint mTargetPaint;
		private final FontMetricsInt mTextMetrics = new FontMetricsInt();
		private final ArrayList<Float> mXs = new ArrayList<Float>();
		private final ArrayList<Float> mYs = new ArrayList<Float>();
		private int mHeaderBottom;
		private int mCurX;
		private int mCurY;
		private boolean mCurDown;
		
		public TsTestView(Context c)
		{
			super(c);
			
			mTextPaint = new Paint();
			mTextPaint.setAntiAlias(true);
			mTextPaint.setTextSize(10);
			mTextPaint.setARGB(255, 0, 0, 0);
			
			mTextBackgroundPaint = new Paint();
			mTextBackgroundPaint.setAntiAlias(true);
			mTextBackgroundPaint.setARGB(128, 255, 255, 255);
			
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setARGB(128, 255, 255, 0);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(2);
			
			mTargetPaint = new Paint();
			mTargetPaint.setAntiAlias(false);
			mTargetPaint.setARGB(128, 0, 255, 0);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
		{
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			mTextPaint.getFontMetricsInt(mTextMetrics);
			mHeaderBottom = -mTextMetrics.ascent + mTextMetrics.descent + 2;
			Log.i("foo", "Metrics: ascent=" + mTextMetrics.ascent + " descent="
					+ mTextMetrics.descent + " leading=" + mTextMetrics.leading
					+ " top=" + mTextMetrics.top + " bottom="
					+ mTextMetrics.bottom);
		}

		@Override
		protected void onDraw(Canvas canvas)
		{
			int w = getWidth() / 2;
			int base = -mTextMetrics.ascent + 1;
			int bottom = mHeaderBottom;
			canvas.drawRect(0, 0, w - 1, bottom, mTextBackgroundPaint);
			canvas.drawText("X = " + mCurX, 1, base, mTextPaint);
			canvas.drawRect(w, 0, (w * 2) - 1, bottom, mTextBackgroundPaint);
			canvas.drawText("Y = " + mCurY, 1 + w, base, mTextPaint);

			final int N = mXs.size();
			float lastX = 0, lastY = 0;
			for (int i = 0; i < N; i++)
			{
				float x = mXs.get(i);
				float y = mYs.get(i);
				if (i > 0)
				{
					canvas.drawLine(lastX, lastY, x, y, mTargetPaint);
					canvas.drawPoint(lastX, lastY, mPaint);
				}
				lastX = x;
				lastY = y;
			}

			if (mCurDown)
			{
				canvas.drawLine(0, (int) mCurY, getWidth(), (int) mCurY,
						mTargetPaint);
				canvas.drawLine((int) mCurX, 0, (int) mCurX, getHeight(),
						mTargetPaint);

				canvas.drawPoint(mCurX, mCurY, mPaint);
				canvas.drawCircle(mCurX, mCurY, 10, mPaint);
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event)
		{
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				mXs.clear();
				mYs.clear();
			}
			for(int i = 0; i < event.getHistorySize(); i++)
			{
				mXs.add(event.getHistoricalX(i));
				mYs.add(event.getHistoricalY(i));
			}
			mXs.add(event.getX());
			mYs.add(event.getY());
			mCurX = (int)event.getX();
			mCurY = (int)event.getY();
			mCurDown = (event.getAction() == MotionEvent.ACTION_DOWN)
					|| (event.getAction() == MotionEvent.ACTION_MOVE);
			
			invalidate();
			return true;
		}
	}
}
