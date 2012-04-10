package com.android.devicechecker;

import com.android.devicechecker.testCameraActivity.Preview;
import com.android.devicechecker.interfaces.ItestActTemplate;
import com.android.util.FileOperate;
import com.android.util.PublicConstant;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

//public class CompassdemoActivity extends GraphicsActivity {
public class TestCompassActivity extends ItestActTemplate {

	private static final String TAG = "Compass";

	private Button btn_ok;
	private Button btn_cancel;
	private static final int ID_BTN_OK = 1;
	private static final int ID_BTN_CANCEL = 2;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private SampleView mView;
	private float[] mValues;

	private final SensorEventListener mListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent event) {
			if (false)
				Log.d(TAG, "sensorChanged (" + event.values[0] + ", "
						+ event.values[1] + ", " + event.values[2] + ")");
			mValues = event.values;
			if (mView != null) {
				mView.invalidate();
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		
		/* Create button and setting */
		btn_ok = new Button(this);
		btn_ok.setText(R.string.btn_ok_text);
		btn_ok.setId(ID_BTN_OK);
		setYesBtnOnClickListener(btn_ok, FileOperate.TestItemCompass);
		btn_cancel = new Button(this);
		btn_cancel.setText(R.string.btn_cancel_text);
		btn_cancel.setId(ID_BTN_CANCEL);
		setNoBtnOnClickListener(btn_cancel, FileOperate.TestItemCompass);
		
		/* main layout */
		RelativeLayout layout = new RelativeLayout(this);
		mView = new SampleView(this);
		layout.addView(mView, new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		/* button layout */
		RelativeLayout btn_layout = new RelativeLayout(this);
		RelativeLayout.LayoutParams btn_layout_param = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		btn_layout.setLayoutParams(btn_layout_param);

		/* ok button */
		RelativeLayout.LayoutParams btn_ok_param = new RelativeLayout.LayoutParams(
				(int) getResources().getDimension(
						R.dimen.test_item_bottom_button_width),
				(int) getResources().getDimension(
						R.dimen.test_item_bottom_button_heith));
		btn_ok_param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
				RelativeLayout.TRUE);
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
		RelativeLayout.LayoutParams layout_param = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		layout_param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
				RelativeLayout.TRUE);
		layout.addView(btn_layout, layout_param);

		/* set custom view */
		setContentView(layout);		
	}

	@Override
	protected void onResume() {
		if (false)
			Log.d(TAG, "onResume");
		super.onResume();

		mSensorManager.registerListener(mListener, mSensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onStop() {
		if (false)
			Log.d(TAG, "onStop");
		mSensorManager.unregisterListener(mListener);
		super.onStop();
	}

	private class SampleView extends View {
		private Paint mPaint = new Paint();
		private Path mPath = new Path();
		private boolean mAnimate;

		public SampleView(Context context) {
			super(context);

			// Construct a wedge-shaped path
			mPath.moveTo(0, -50);
			mPath.lineTo(-20, 60);
			mPath.lineTo(0, 50);
			mPath.lineTo(20, 60);
			mPath.close();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			Paint paint = mPaint;

			canvas.drawColor(Color.WHITE);

			paint.setAntiAlias(true);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.FILL);

			int w = canvas.getWidth();
			int h = canvas.getHeight();
			int cx = w / 2;
			int cy = h / 2;

			canvas.translate(cx, cy);
			if (mValues != null) {
				canvas.rotate(-mValues[0]);
			}

			mPaint.setTextSize(100);
			canvas.drawText(getResources().getString(R.string.direction_north),
					-50, -100, mPaint);

			canvas.drawPath(mPath, mPaint);

		}

		@Override
		protected void onAttachedToWindow() {
			mAnimate = true;
			if (false)
				Log.d(TAG, "onAttachedToWindow. mAnimate=" + mAnimate);
			super.onAttachedToWindow();
		}

		@Override
		protected void onDetachedFromWindow() {
			mAnimate = false;
			if (false)
				Log.d(TAG, "onDetachedFromWindow. mAnimate=" + mAnimate);
			super.onDetachedFromWindow();
		}
	}
}
