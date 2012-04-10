package com.android.devicechecker;

import android.content.pm.PackageParser.NewPermissionInfo;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.android.devicechecker.interfaces.ItestActTemplate;
import com.android.util.FileOperate;

public class testNfc extends ItestActTemplate {
	private TextView mTipTextView;
	private Button mYes = null;
	private Button mNo = null;
	private Button mOpenNfc = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test_nfc);
		mTipTextView = (TextView) findViewById(R.id.test_nfc_tips);

		mOpenNfc = (Button) findViewById(R.id.but_open_nfc);
		mYes = (Button) findViewById(R.id.but_ok);
		mNo = (Button) findViewById(R.id.but_nook);
		mOpenNfc.setOnClickListener(btnOpenNfOcl);
		setYesBtnOnClickListener(mYes, FileOperate.TestItemNfc);
		setNoBtnOnClickListener(mNo, FileOperate.TestItemNfc);

		resolveIntent(getIntent());
	}
	

	void resolveIntent(Intent intent) {
		// Parse the intent
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
			//start a new activity to display the result.
			Intent mIntent = new Intent(getApplicationContext(), testNfcOk.class);
			startActivity(mIntent);
		} else {
			mTipTextView.setText(R.string.test_nfc_tips);
		}
	}

	/* ok button click */
	OnClickListener btnOpenNfOcl = new OnClickListener()
	{
		public void onClick(View v)
		{
			startActivityForResult(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), 0);
		}
	};

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(mIntent);
	}
}
