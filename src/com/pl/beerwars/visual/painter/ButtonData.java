package com.pl.beerwars.visual.painter;

import com.pl.beerwars.data.Constants.ScreenButton;

public class ButtonData {
	public float xc;
	public float yc;
	public float size;
	public boolean isCircle;
	public boolean isDown;
	
	public ScreenButton id;
	public int resId;

	public ButtonData(float xc, float yc, float size, boolean isCircle, boolean isDown, ScreenButton id, int resId) {
		super();
		this.xc = xc;
		this.yc = yc;
		this.size = size;
		this.isCircle = isCircle;
		this.isDown = isDown;
		this.id = id;
		this.resId = resId;
	}
	
	public boolean isTouched(float x, float y){
		if (isCircle){
			return (x - xc) * (x - xc) + (y - yc) * (y - yc) <= size * size;
		} else {
			return x >= xc - size && x <= xc + size 
					&& y >= yc - size && y <= yc + size;
		}
	}
}
