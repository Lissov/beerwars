package com.pl.beerwars.visual;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.pl.beerwars.*;
import com.pl.beerwars.data.*;
import com.pl.beerwars.visual.painter.*;

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
					if (isActive && !calculating){
						_shower.closeLastView(null);
					}
				}	
			});
			
		final Game.TurnMessageCallback cb = this;
		calculating = true;
		btnClose.setEnabled(!calculating);
		Runnable r = new Runnable(){
			public void run(){
				_game.makeTurn(cb);
				display(R.string.nextturn_autosave, null);
				GameHolder.saveGame(_context, "Autosave " + _game.date.toString());
			}
		};
		new Thread(r).start();
	}

	@Override
	public boolean onClosing(){
		_pressedButton.isDown = false;
		return super.onClosing();
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
	
	@Override
	public void displayStorageBuilt(final String cityId, final Constants.StorageSize newSize){
		((Activity)_context).runOnUiThread(new Runnable(){
				public void run(){
					showMessage(String.format(
									_context.getResources().getString(R.string.game_nt_constructedStorage),
									_translator.getStorageName(newSize),
									_translator.getCityName(cityId)
								));
				}
			});
	}
		
	
	@Override
	public void displayFactoryBuilt(final String cityId, final Constants.FactorySize newSize){
		((Activity)_context).runOnUiThread(new Runnable(){
				public void run(){
					showMessage(String.format(
						_context.getResources().getString(R.string.game_nt_constructedFactory),
						_translator.getFactoryName(newSize),
						_translator.getCityName(cityId)
					));
				}
			});
	}
	
	@Override
	public void displayUnitsExtension(final String cityId, final int built){
		((Activity)_context).runOnUiThread(new Runnable(){
				public void run(){
					showMessage(String.format(
									_context.getResources().getString(R.string.game_nt_constructedUnits),
									built,
									_translator.getCityName(cityId)
								));
				}
			});		
	}

	@Override
	public void displayConsumption(final String cityId, final String sortName, final int consumed, final int previous)
	{
		((Activity)_context).runOnUiThread(new Runnable(){
				public void run(){
					showMessage(String.format(
									_context.getResources().getString(R.string.game_nt_consumedSort),
									consumed,
									sortName,
									_translator.formatRelative(consumed, previous)
								));
				}
			});
	}

	@Override
	public void displayProcessingPlayerTurn(final String playerName)
	{
		showMessage(String.format(
									_context.getResources().getString(R.string.game_nt_processingPlayerTurn),
									playerName
								));
	}
	
	@Override
	public void displayCantSend(final String cityFrom, final String cityTo, final String sortName, final int packs){
		showMessage(String.format(
						_context.getResources().getString(R.string.game_nt_cantSend),
						packs, sortName,
						_translator.getCityName(cityFrom),
						_translator.getCityName(cityTo)
					));		
	}
	
	public void displaySent(final String cityFrom, final String cityTo, final String sortName, final int packs){
		showMessage(String.format(
						_context.getResources().getString(R.string.game_nt_sent),
						packs, sortName,
						_translator.getCityName(cityFrom),
						_translator.getCityName(cityTo)
					));		
	}
	
	public void displayReceivedDropped(String cityId, String sortName, int bottles) {
		showMessage(String.format(
						_context.getResources().getString(R.string.game_nt_receivedDropped),
						bottles, sortName,
						_translator.getCityName(cityId)
					));		
	}
	
	public void displayDebugMessage(final String message){
		showMessage("DEBUG: " + message);
	}
	
	private void showMessage(final String message){
		((Activity)_context).runOnUiThread(new Runnable(){
				public void run(){
					Resources res = _context.getResources();
					TextView tv = new TextView(_context);
					tv.setTextColor(res.getColor(R.color.overlay_text));
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.textSmall));
					tv.setText(message);
					llLog.addView(tv);		
				}
			});		
	}
}
