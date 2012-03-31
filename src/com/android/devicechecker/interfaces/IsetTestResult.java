package com.android.devicechecker.interfaces;

import android.content.Context;

public interface IsetTestResult {
	public static final int RESULT_PASS = 1;
	public static final int RESULT_FAIL = 2;
	public void setValue(Context mContext, int testItemIndex, int value);
}
