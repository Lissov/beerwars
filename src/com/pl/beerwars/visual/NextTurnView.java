package com.pl.beerwars.visual;
import com.pl.beerwars.*;
import android.content.*;
import com.pl.beerwars.visual.painter.*;
import com.pl.beerwars.data.*;
import android.view.*;
import android.widget.*;
import android.app.*;
import android.content.res.*;
import android.util.*;

public class NextTurnView extends OverlayFrame implements Game.TurnMessageCallback
{
	private IViewShower _shower;
	Translator _translator;
	private Game _game;
	private ButtonData _pressedButton;
	private boolean calculating;
	private LinearLayout llLog;
	private Button btnClose;
	
	public NextTurnView(Context context, IViewShower shower, Translator translator, Game game, ButtonData pressedButton)
	{
		super(context);
		_shower = shower;
		_translator = translator;
		_game = game;
		_pressedButton = pressedButton;

		View.inflate(context, R.layout.nextturn, this);

		llLog = (LinearLayout)findViewById(R.id.nextturn_llLog);
		
		btnClose = (Button)findViewById(R.id.nextturn_btnClose);
		btnClose.setOnClickListener(new View.OnClickListener(){
				public void onClick(View view){
					if (isActive && !calculating)
						_pressedButton.isDown = false;
						_shower.closeLastView(null);
				}	
			});
			
		final Game.TurnMessageCallback cb = this;
		calculating = true;
		btnClose.setEnabled(!calculating);
		Runnable r = new Runnable(){
			public void run(){
				_game.makeTurn(cb);
			}
		};
		new Thread(r).start();
	}

	@Override
	public void display(final int resId, final Object[] params)
	{
		((Activity)_context).runOnUiThread(new Runnable(){
				public void run(){
					if (params != null)
						showMessage(String.format(
							_context.getResources().getString(resId),
							params));
					else
						showMessage(_context.getResources().getString(resId));
				}
			});
	}
	
	@Override
	public void displayCity(final int resId, final String cityId)
	{
		((Activity)_context).runOnUiThread(new Runnable(){
				public void run(){
					showMessage(String.format(
						_context.getResources().getString(resId),
						_translator.getCityName(cityId)));
				}
			});
	}
	
	@Override
	public void complete()
	{
		((Activity)_context).runOnUiThread(new Runnable(){
			public void run(){
				showMessage(_context.getResources().getString(R.string.game_nt_complete));
				calculating = false;
				btnClose.setEnabled(true);
			}
		});
	}
	
	private void showMessage(String message){
		Resources res = _context.getResources();
		TextView tv = new TextView(_context);
		tv.setTextColor(res.getColor(R.color.overlay_text));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.textSmall));
		tv.setText(message);
		llLog.addView(tv);		
	}
}
