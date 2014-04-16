package com.pl.beerwars;
import android.app.*;
import android.os.*;
import android.widget.*;
import com.pl.beerwars.visual.*;

public class GameActivity extends Activity
{
	private GameView _gameView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		
		LinearLayout llMain = (LinearLayout)findViewById(R.id.llGameMain);
		_gameView = new GameView(this);
		llMain.addView(_gameView);
	}
}
