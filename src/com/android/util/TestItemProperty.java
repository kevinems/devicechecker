package com.android.util;

import com.android.devicechecker.R.layout;

import android.R.integer;
import android.widget.Button;

public class TestItemProperty {
	private static final int DEFAULT_FONT_SIZE = 20;
	
	private int sequency;
	private int index;
	private Button button;
	private Class<?> cls;
	
	/**
	 * 
	 * @param sequency
	 * @param index
	 * @param button
	 * @param activity
	 */
	
	public TestItemProperty(int sequency, int index, Button button, Class<?> cls) {
		super();
		this.sequency = sequency;
		this.index = index;
		this.button = button;
		this.cls = cls;
		
		initButton();
	}
	
	private void initButton() {
		this.button.setTextSize(DEFAULT_FONT_SIZE);
		
	}

	public int getSequency() {
		return sequency;
	}
	public void setSequency(int sequency) {
		this.sequency = sequency;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Button getButton() {
		return button;
	}
	public Class<?> getCls() {
		return cls;
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}

	public void setButton(Button button) {
		this.button = button;
	}
}
