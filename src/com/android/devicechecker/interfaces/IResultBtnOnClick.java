package com.android.devicechecker.interfaces;

import android.widget.Button;

public interface IResultBtnOnClick {
	
	/**
	 * set yes button event
	 * @param yesBtn
	 * @param testItemIndex
	 */
	public void setYesBtnOnClickListener(Button yesBtn, final int testItemIndex);
	
	/**
	 * set no button event
	 * @param noBtn
	 * @param testItemIndex
	 */
	public void setNoBtnOnClickListener(Button noBtn, final int testItemIndex);
}
