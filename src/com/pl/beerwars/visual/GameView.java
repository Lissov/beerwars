package com.pl.beerwars.visual;
import com.pl.beerwars.*;

import android.view.*;
import android.content.*;
import android.graphics.*;
import android.content.res.*;
import com.pl.beerwars.visual.painter.*;
import com.pl.beerwars.data.*;
import com.pl.beerwars.data.map.*;
import com.pl.beerwars.data.playerdata.PlayerData;

public class GameView extends View implements IOverlayView
{
	private Context _context;
	private IViewShower viewShower;
	
	private Game _game;
	
	private Translator translator;
	private Paint pntMap = new Paint();
	private CityPainter cityPainter;
	private MenuPainter menuPainter;
	
	public GameView(Context context, IViewShower shower){	
		super(context);
		this._context = context;
		
		_game = GameHolder.getGame(_context);
		viewShower = shower;	
		
		preparePaints();
		
		invalidate();
	}
	
	private void preparePaints(){
		//Resources res = _context.getResources();
		
		translator = new Translator();
		cityPainter = new CityPainter(translator, _context);
		menuPainter = new MenuPainter(translator, _context);

		pntMap.setAntiAlias(true);
		pntMap.setFilterBitmap(true);
		pntMap.setDither(true);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		translator.setup(this.getMeasuredWidth(), this.getMeasuredHeight(), 
				_context.getResources().getDimension(R.dimen.baseScale), _context.getResources());
		
		float xmin = translator.getX(0f);
		float xmax = translator.getX(1f);
		float ymin = translator.getY(0f);
		float ymax = translator.getY(1f);

		Bitmap map = BitmapManager.getBitmap(_context, translator.getMapResId(_game.map.mapId), (int)(xmax - xmin), (int)(ymax - ymin));
		canvas.drawBitmap(map, xmin, ymin, pntMap);
		
		PlayerData data = _game.getViewForPlayerId(Constants.Players.MainHuman);
		for (int i = 0; i < _game.map.cities.length; i++){
			cityPainter.draw(
				canvas, _game.map.cities[i], data.cityObjects[i],
				Constants.Players.MainHuman,
				data.game.cities[i]);
		}
		
		drawControls(canvas);
	}
	
	private void drawControls(Canvas canvas)
	{
		PlayerData data = _game.getViewForPlayerId(Constants.Players.MainHuman);
		menuPainter.draw(canvas, data);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (!isActive)
			return true;
			
		if (event.getAction() == MotionEvent.ACTION_DOWN){
			ButtonData buttonTouched = menuPainter.getTouchedButton(event.getX(), event.getY());
			if (buttonTouched != null){
				if (buttonTouched.id == Constants.ScreenButton.NextTurn){
					nextTurn(buttonTouched);
				}
				if (buttonTouched.id == Constants.ScreenButton.Player){
					showStats(buttonTouched);
				}
			}
			
			City touched = getTouchedCity(event.getX(), event.getY());
			if (touched != null)
			{
				showCityInfo(touched);
				return true;
			}			
		}
		return super.onTouchEvent(event);
	}
	
	private void nextTurn(ButtonData button){
		button.isDown = true;
		this.invalidate();
		viewShower.showView(new NextTurnView(_context, viewShower, translator, _game, button));		
	}

	private void showStats(ButtonData button){
		button.isDown = true;
		this.invalidate();
		viewShower.showView(
			new StatsView(
				_context, viewShower, translator, 
				_game.getViewForPlayerId(Constants.Players.MainHuman), 
				button
			)
		);	
	}

	private void showCityInfo(City city){
		viewShower.showView(new CityInfoView(_context, viewShower, translator, 
											 _game.getViewForPlayerId(Constants.Players.MainHuman), city.id));
	}
	
	private City getTouchedCity(float x, float y){
		for (City c : _game.map.cities){
			float cx = translator.getX(c.location.x);
			float cy = translator.getY(c.location.y);
			
			if ((cx - x) * (cx - x) <= Constants.Sizes.TouchRadiusSquare
				&& (cy - y) * (cy - y) <= Constants.Sizes.TouchRadiusSquare){
					return c;
				}
		}
		
		return null;
	}

	protected boolean isActive = true;

	public void deactivate(){
		isActive = false;
		this.invalidate();
	}

	public void activate(){
		isActive = true;
		this.invalidate();
	}

	@Override
	public boolean onClosing()
	{
		return false;
	}

	
	//TODO: remove
	/*private void drawRoads(Canvas canvas){
		Paint pntRoad = new Paint();
		pntRoad.setColor(Color.BLACK);
		for (Road road : _game.map.roads){
			City c1 = _game.map.getCityById(road.cities[0]);
			City c2 = _game.map.getCityById(road.cities[1]);
			canvas.drawLine(
				translator.getX(c1.location.x), translator.getY(c1.location.y),
				translator.getX(c2.location.x), translator.getY(c2.location.y),
				pntRoad);
		}
	}*/
}
