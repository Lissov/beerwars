package com.pl.beerwars.visual;
import com.pl.beerwars.*;
import android.widget.*;
import android.content.*;
import android.view.*;
import android.util.*;
import android.content.res.*;
import android.graphics.*;

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
	
	public boolean onClosing(){
		return true;
	}
	
	protected TextView getTextView(Object text){
		return getTextView(text, Gravity.LEFT);
	}

	protected TextView getTextView(Object text, int gravity){
		return getTextView(text, Gravity.LEFT, Typeface.NORMAL);
	}

	protected TextView getTextView(Object text, int gravity, int typeface){

		Resources res = _context.getResources();
		TextView tv = new TextView(_context);
		tv.setTextColor(res.getColor(R.color.overlay_text));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.textNormal));
		tv.setText(text.toString());
		tv.setGravity(gravity);
		tv.setTypeface(null, typeface);
		return tv;
	}
}
