package com.pl.beerwars;
import android.view.*;
import com.pl.beerwars.visual.*;

public interface IViewShower
{
	public void showView(IOverlayView view);
	public void closeLastView(Object parameter);
}
