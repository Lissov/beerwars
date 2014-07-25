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
	
	private IOverlayView[] displayedViews = new IOverlayView[5];
	private int currentViewCount = 0;
	public void showView(IOverlayView view){
		LinearLayout overlapView = (LinearLayout)findViewById(R.id.llGameOverlap);
		overlapView.removeAllViews();
		
		if (currentViewCount > 0)
			displayedViews[currentViewCount-1].deactivate();
		else
			_gameView.deactivate();
			
		displayedViews[currentViewCount] = view;
		currentViewCount++;
		view.activate();
		
		overlapView.addView((View)view);
	}
	
	public void closeLastView(Object parameter){
		LinearLayout overlapView = (LinearLayout)findViewById(R.id.llGameOverlap);
		overlapView.removeAllViews();

		currentViewCount--;
		if (currentViewCount > 0){
			overlapView.addView((View)displayedViews[currentViewCount-1]);
			displayedViews[currentViewCount-1].activate();
		} else
			_gameView.activate();
	}

	@Override
	public void onBackPressed()
	{
		if (currentViewCount > 0)
			closeLastView(null);
		else
			super.onBackPressed();
	}
}
