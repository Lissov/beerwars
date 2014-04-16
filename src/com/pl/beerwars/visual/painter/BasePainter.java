package com.pl.beerwars.visual.painter;
import com.pl.beerwars.visual.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;

public abstract class BasePainter
{
	protected Translator translator;
	protected Context context;
	protected Resources res;

	public BasePainter(Translator translator, Context context)
	{
		this.translator = translator;
		this.context = context;
		
		res = context.getResources();
	}
	
	protected void SetColor(Paint paint, int color){
		paint.setColor(res.getColor(color));
	}
}
