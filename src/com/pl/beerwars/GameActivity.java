package com.pl.beerwars;
import android.app.*;
import android.os.*;
import android.widget.*;
import com.pl.beerwars.visual.*;
import android.view.*;

public class GameActivity extends Activity implements IViewShower
{
	private GameView _gameView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		
		LinearLayout llMain = (LinearLayout)findViewById(R.id.llGameMain);
		_gameView = new GameView(this, this);
		llMain.addView(_gameView);
	}
	
	public void showView(View view){
		LinearLayout overlapView = (LinearLayout)findViewById(R.id.llGameOverlap);
		overlapView.removeAllViews();
		
		overlapView.addView(view);
	}
	
}
