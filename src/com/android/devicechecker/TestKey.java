package com.android.devicechecker;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.devicechecker.interfaces.ItestActTemplate;
import com.android.util.FileOperate;

public class TestKey extends ItestActTemplate {
	private static final String TAG = "TestKey";

	private TextView mTextView;

	private Button mYes = null;
	private Button mNo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Window win = getWindow();
		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.test_key);

		mTextView = (TextView) findViewById(R.id.press_key_status);

		mYes = (Button) findViewById(R.id.but_ok);
		mNo = (Button) findViewById(R.id.but_nook);
		
		setYesBtnOnClickListener(mYes, FileOperate.TestItemKey);
		setNoBtnOnClickListener(mNo, FileOperate.TestItemKey);
	}

	// 屏蔽Home键
	@Override
	public void onAttachedToWindow() {
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		String string = null;

		switch (keyCode) {
		case KeyEvent.KEYCODE_A: {
			string = getResources().getString(R.string.keycode_a);
			break;
		}
		case KeyEvent.KEYCODE_B: {
			string = getResources().getString(R.string.keycode_b);
			break;
		}
		case KeyEvent.KEYCODE_C: {
			string = getResources().getString(R.string.keycode_c);
			break;
		}
		case KeyEvent.KEYCODE_D: {
			string = getResources().getString(R.string.keycode_d);
			break;
		}
		case KeyEvent.KEYCODE_VOLUME_DOWN: {
			string = getResources().getString(R.string.keycode_volume_down);
			break;
		}
		case KeyEvent.KEYCODE_VOLUME_UP: {
			string = getResources().getString(R.string.keycode_volume_up);
			break;
		}
		case KeyEvent.KEYCODE_MUTE: {
			string = getResources().getString(R.string.keycode_mute);
			break;
		}
		case KeyEvent.KEYCODE_MENU: {
			string = getResources().getString(R.string.keycode_menu);
			break;
		}
		case KeyEvent.KEYCODE_BACK: {
			string = getResources().getString(R.string.keycode_back);
			break;
		}
		case KeyEvent.KEYCODE_SEARCH: {
			string = getResources().getString(R.string.keycode_search);
			break;
		}
		case KeyEvent.KEYCODE_HOME: {
			string = getResources().getString(R.string.keycode_home);
			break;
		}
		default:
			break;
		}

		mTextView.setText(string);

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		mTextView.setText("");

		return super.onKeyUp(keyCode, event);
	}

}
