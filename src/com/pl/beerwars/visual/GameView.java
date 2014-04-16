package com.pl.beerwars.visual;
import com.pl.beerwars.*;
import android.view.*;
import android.content.*;
import android.graphics.*;
import android.content.res.*;
import com.pl.beerwars.visual.painter.*;
import com.pl.beerwars.data.*;
import com.pl.beerwars.data.map.*;
import android.widget.*;

public class GameView extends View
{
	private Context _context;
	private IViewShower viewShower;
	
	private Game _game;
	
	private Translator translator;
	private Paint pntBackground = new Paint();
	private CityPainter cityPainter;
	
	public GameView(Context context, IViewShower shower){	
		super(context);
		this._context = context;
		
		_game = GameHolder.getGame();
		viewShower = shower;	
		
		preparePaints();
	}
	
	private void preparePaints(){
		Resources r = _context.getResources();
		
		translator = new Translator();
		cityPainter = new CityPainter(translator, _context);
		
		pntBackground.setColor(r.getColor(R.color.mapBackground));
		pntBackground.setStyle(Paint.Style.FILL);
		
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		translator.setup(this.getMeasuredWidth(), this.getMeasuredHeight(), 
			Constants.Sizes.BaseScale, _context.getResources());
		
		canvas.drawRect(
			translator.getX(0), translator.getY(0), 
			translator.getX(1f), translator.getY(1f), 
			pntBackground);

		drawRoads(canvas); //TODO: Remove
		
		for (City city : _game.map.cities){
			cityPainter.draw(canvas, city);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN){
			City touched = getTouchedCity(event.getX(), event.getY());
			if (touched != null)
			{
				showCityInfo(touched);
				return true;
			}
		}
		return super.onTouchEvent(event);
	}

	private void showCityInfo(City city){
		viewShower.showView(new CityInfoView(_context, viewShower, translator, _game, city.id));
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
	
	//TODO: remove
	private void drawRoads(Canvas canvas){
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
	}
}
