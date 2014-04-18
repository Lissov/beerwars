package com.pl.beerwars.visual.painter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.pl.beerwars.R;
import com.pl.beerwars.data.Constants;
import com.pl.beerwars.data.playerdata.PlayerData;
import com.pl.beerwars.visual.Translator;

public class MenuPainter extends BasePainter {
	
	private Paint pntPanelBorder = new Paint();
	private Paint pntPanelFill = new Paint();
	//private Paint pntPlayerDataBig = new Paint();
	//private Paint pntPlayerData = new Paint();

	
	public MenuPainter(Translator translator, Context context){
		super(translator, context);

		SetColor(pntPanelFill, R.color.cityBackground);
		pntPanelFill.setStyle(Paint.Style.FILL);

		SetColor(pntPanelBorder, R.color.game_panelBorder);
		pntPanelBorder.setStyle(Paint.Style.STROKE);
		pntPanelBorder.setStrokeWidth(5);

		
		//pntPlayerDataBig.setColor(res.getColor(R.color.game_playerDataText));
		//pntPlayerDataBig.setTextSize(res.getDimension(R.dimen.textBig));
		//pntPlayerData.setColor(res.getColor(R.color.game_playerDataText));
		//pntPlayerData.setTextSize(res.getDimension(R.dimen.textNormal));
	}

	public void draw(Canvas canvas, PlayerData data){
		float panelH = translator.scale * 4;
		float panelW = translator.scale * 12;
		
		float trX = translator.getX(1f);
		float trY = translator.getY(0f);
		canvas.drawCircle(trX - panelW, trY, panelH, pntPanelFill);
		canvas.drawCircle(trX - panelW, trY, panelH, pntPanelBorder);
		canvas.drawRect(trX - panelW, trY, trX, trY + panelH, pntPanelFill);
		canvas.drawLine(trX - panelW, trY + panelH, trX, trY + panelH, pntPanelFill);
		
		float tlX = translator.getX(0f);
		float tlY = translator.getY(0f);
		canvas.drawCircle(tlX + panelW, tlY, panelH, pntPanelFill);
		canvas.drawCircle(tlX + panelW, tlY, panelH, pntPanelBorder);
		canvas.drawRect(tlX, tlY, tlX + panelW, tlY + panelH, pntPanelFill);
		canvas.drawLine(tlX, tlY + panelH, tlX + panelW, tlY + panelH, pntPanelFill);
		
		float panelMH = translator.scale * 2;
		float panelMW = translator.scale * 10;
		float brX = translator.getX(1f);
		float brY = translator.getY(1f);
		canvas.drawCircle(brX - panelMW, brY, panelMH, pntPanelFill);
		canvas.drawCircle(brX - panelMW, brY, panelMH, pntPanelBorder);
		canvas.drawRect(brX - panelMW, trY, trX, trY - panelMH, pntPanelFill);
		canvas.drawLine(trX - panelW, trY - panelMH, trX, trY - panelMH, pntPanelFill);

		/*float w = pntPlayerData.measureText(data.name);
		canvas.drawText(data.name, translator.getX(0.98f) - w, translator.getY(0.02f), pntPlayerDataBig);
		String m = "" + data.money + " " + Constants.Economics.Currency;
		w = pntPlayerData.measureText(m);
		canvas.drawText(m, translator.getX(0.98f) - w, translator.getY(0.02f) + pntPlayerDataBig.getTextSize(), pntPlayerData);
*/
	}
}
