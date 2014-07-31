package com.pl.beerwars.visual;
import android.content.*;
import com.pl.beerwars.*;
import com.pl.beerwars.data.playerdata.*;
import android.content.res.*;
import android.view.*;
import com.pl.beerwars.visual.painter.*;
import android.widget.*;
import com.pl.beerwars.data.facade.*;
import java.util.*;
import android.graphics.*;

public class StatsView extends OverlayFrame
{
	private IViewShower _shower;
	private Translator _translator;
	private PlayerData _data;
	private Resources res;
	private Button btnClose;
	private ButtonData _pressedButton;
	
	public StatsView(Context context, IViewShower shower, Translator translator, PlayerData data, ButtonData pressedButton)
	{
		super(context);
		_shower = shower;
		_translator = translator;
		_data = data;
		_pressedButton = pressedButton;
		
		res = this.getResources();

		View.inflate(context, R.layout.stats, this);

		Resources res = context.getResources();

		btnClose = (Button)findViewById(R.id.stats_btnClose);
		btnClose.setOnClickListener(new View.OnClickListener(){
				public void onClick(View view){
					_shower.closeLastView(null);
				}	
			});
			
		showPlayersTable();
	}
	
	@Override
	public boolean onClosing(){
		_pressedButton.isDown = false;
		return super.onClosing();
	}
	
	private void showPlayersTable()
	{
		Resources res = _context.getResources();

		Arrays.sort(
			_data.game.otherPlayerData,
			new Comparator<OtherPlayerStats>() {
				public int compare(OtherPlayerStats one, OtherPlayerStats two){
					return two.totalProduction - one.totalProduction;
				}
			}
		);
		
		int totalMarket = 0;
		for (OtherPlayerStats stats : _data.game.otherPlayerData)
			totalMarket += stats.totalSold;

		TableLayout table = (TableLayout)findViewById(R.id.stats_tlPlayers);
		for (OtherPlayerStats stats : _data.game.otherPlayerData)
		{
			TableRow row = new TableRow(_context);
			row.setBackgroundColor(res.getColor(R.color.overlay_background));
			
			int tf = (stats.playerId == _data.id)
							? Typeface.BOLD
							: Typeface.NORMAL;

			row.addView(getTextView(stats.playerName, Gravity.LEFT, tf));
			row.addView(getTextView(stats.sortsOwnedCnt, Gravity.CENTER, tf));
			row.addView(getTextView(stats.totalProduction, Gravity.CENTER, tf));
			row.addView(getTextView(stats.totalSold, Gravity.CENTER, tf));
			row.addView(getTextView(_translator.formatPecentage((float)stats.totalSold / (float)totalMarket), Gravity.CENTER, tf));
			row.addView(getTextView(stats.capital, Gravity.CENTER, tf));

			table.addView(row);
		}
	}
	
}
