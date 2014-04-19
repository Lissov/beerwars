package com.pl.beerwars.visual.painter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.format.DateFormat;

import com.pl.beerwars.R;
import com.pl.beerwars.data.Constants;
import com.pl.beerwars.data.Constants.ScreenButton;
import com.pl.beerwars.data.playerdata.PlayerData;
import com.pl.beerwars.visual.Translator;

public class MenuPainter extends BasePainter {
	
	private Paint pntPanelBorder = new Paint();
	private Paint pntPanelFill = new Paint();
	private Paint pntText= new Paint();
	
	private ButtonData btnNextTurn;
	private ButtonData btnPlayerData;
	private ButtonPainter buttonPainter;
	private ButtonData[] buttons = new ButtonData[2];
	//private Paint pntPlayerData = new Paint();

	
	public MenuPainter(Translator translator, Context context){
		super(translator, context);

		SetColor(pntPanelFill, R.color.game_panelField);
		pntPanelFill.setStyle(Paint.Style.FILL);

		SetColor(pntPanelBorder, R.color.game_panelBorder);
		pntPanelBorder.setStyle(Paint.Style.STROKE);
		pntPanelBorder.setStrokeWidth(5);
		pntPanelBorder.setAntiAlias(true);

		buttonPainter = new ButtonPainter(translator, context);
				
		btnNextTurn = new ButtonData(0, 0, 0, true, false, ScreenButton.NextTurn, R.drawable.btn_next_week);
		btnPlayerData = new ButtonData(0, 0, 0, true, false, ScreenButton.Player, R.drawable.btn_player_economy);
		buttons[0] = btnNextTurn;
		buttons[1] = btnPlayerData;
		
		pntText.setColor(res.getColor(R.color.game_playerDataText));
		pntText.setAntiAlias(true);
	}

	public void draw(Canvas canvas, PlayerData data){
		float panelH = translator.scale * 2;
		float panelW = translator.scale * 12;
		float btR = translator.scale * 2f - 7;
		float btFR = translator.scale * 2f;
		float btD = 1.2f * translator.scale;
		
		float trX = translator.getX(1f);
		float trY = translator.getY(0f);
		canvas.drawCircle(trX - panelW, trY, panelH, pntPanelFill);
		canvas.drawCircle(trX - panelW, trY, panelH, pntPanelBorder);
		canvas.drawCircle(trX - btD, trY + btD, btFR, pntPanelFill);
		canvas.drawCircle(trX - btD, trY + btD, btFR, pntPanelBorder);
		float xe = trX - btD - (float)Math.sqrt(btFR*btFR - (panelH - btD)*(panelH - btD)) + 2;
		canvas.drawRect(trX - panelW, trY, trX, trY + panelH, pntPanelFill);
		canvas.drawLine(trX - panelW, trY + panelH, xe, trY + panelH, pntPanelBorder);
		
		btnPlayerData.xc = trX - btD;
		btnPlayerData.yc = trY + btD;
		btnPlayerData.size = btR;
		buttonPainter.draw(canvas, btnPlayerData);

		String m = "" + data.money + " " + Constants.Economics.Currency;
		pntText.setTextSize(1.5f * translator.scale);
		canvas.drawText(m, trX - panelW, trY + 1.5f*translator.scale, pntText);
		
		float tlX = translator.getX(0f);
		float tlY = translator.getY(0f);
		canvas.drawCircle(tlX + panelW, tlY, panelH, pntPanelFill);
		canvas.drawCircle(tlX + panelW, tlY, panelH, pntPanelBorder);
		canvas.drawCircle(tlX + btD, tlY + btD, btFR, pntPanelFill);
		canvas.drawCircle(tlX + btD, tlY + btD, btFR, pntPanelBorder);
		xe = tlX + btD + (float)Math.sqrt(btFR*btFR - (panelH - btD)*(panelH - btD)) - 2;
		canvas.drawRect(tlX, tlY, tlX + panelW, tlY + panelH, pntPanelFill);
		canvas.drawLine(xe, tlY + panelH, tlX + panelW, tlY + panelH, pntPanelBorder);
		
		btnNextTurn.xc = tlX + btD;
		btnNextTurn.yc = tlY + btD;
		btnNextTurn.size = btR;
		buttonPainter.draw(canvas, btnNextTurn);		
		
		String dt = (String)DateFormat.format("dd MMM yyyy", data.game.date);
		canvas.drawText(dt, tlX + btD + btFR + 0.3f*translator.scale, trY + 1.5f*translator.scale, pntText);
		
		/*float panelMH = translator.scale * 2;
		float panelMW = translator.scale * 10;
		float brX = translator.getX(1f);
		float brY = translator.getY(1f);
		canvas.drawCircle(brX - panelMW, brY, panelMH, pntPanelFill);
		canvas.drawCircle(brX - panelMW, brY, panelMH, pntPanelBorder);
		canvas.drawRect(brX - panelMW, brY - panelMH, brX, brY, pntPanelFill);
		canvas.drawLine(brX - panelMW, brY - panelMH, brX, brY - panelMH, pntPanelBorder);*/
	}
	
	public ButtonData getTouchedButton(float x, float y) {
		for (ButtonData b : buttons){
			if (b.isTouched(x, y))
				return b;
		}

		return null;
	}
}
