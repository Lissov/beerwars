package com.pl.beerwars.visual.painter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.pl.beerwars.R;
import com.pl.beerwars.visual.Translator;

public class ButtonPainter extends BasePainter {

	private Paint pntButtonBorder = new Paint();
	private Paint pntButtonFill = new Paint();
	private Paint pntButtonFillDown = new Paint();
	private Paint pntImage = new Paint();

	public ButtonPainter(Translator translator, Context context) {
		super(translator, context);

		SetColor(pntButtonFill, R.color.game_panelButton);
		pntButtonFill.setStyle(Paint.Style.FILL);

		SetColor(pntButtonFillDown, R.color.game_panelButtonDown);
		pntButtonFillDown.setStyle(Paint.Style.FILL);

		SetColor(pntButtonBorder, R.color.game_panelButtonBorder);
		pntButtonBorder.setStyle(Paint.Style.STROKE);
		pntButtonBorder.setStrokeWidth(3);
		pntButtonBorder.setAntiAlias(true);
		
		pntImage.setAntiAlias(true);
		pntImage.setFilterBitmap(true);
		pntImage.setDither(true);
	}

	public void draw(Canvas canvas, ButtonData button)
	{
		if (button.isCircle){
			if (button.isDown)
				canvas.drawCircle(button.xc, button.yc, button.size, pntButtonFillDown);
			else
				canvas.drawCircle(button.xc, button.yc, button.size, pntButtonFill);
			canvas.drawCircle(button.xc, button.yc, button.size, pntButtonBorder);
			
			int sz = (int)button.size;
			Bitmap bmp = BitmapManager.getBitmap(context, button.resId, sz*2, sz*2);
			canvas.drawBitmap(bmp, button.xc - sz, button.yc - sz, pntImage);
		} else {
			
		}
	}
}
