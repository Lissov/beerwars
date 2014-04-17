package com.pl.beerwars.visual;
import android.widget.*;
import android.content.*;

public class OverlayFrame extends FrameLayout implements IOverlayView
{
	protected boolean isActive = true;
	protected Context _context;

	public OverlayFrame(Context context){
		super(context);
		_context = context;
	}	

	public void deactivate(){
		isActive = false;
	}

	public void activate(){
		isActive = true;
	}
}
