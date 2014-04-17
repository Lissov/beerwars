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

public class GameView extends View implements IOverlayView
{
	private Context _context;
	private IViewShower viewShower;
	
	private Game _game;
	
	private Translator translator;
	private Paint pntMap = new Paint();
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

		pntMap.setAntiAlias(true);
		pntMap.setFilterBitmap(true);
		pntMap.setDither(true);		
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		translator.setup(this.getMeasuredWidth(), this.getMeasuredHeight(), 
			Constants.Sizes.BaseScale, _context.getResources());
		
		float xmin = translator.getX(0f);
		float xmax = translator.getX(1f);
		float ymin = translator.getY(0f);
		float ymax = translator.getY(1f);

		Bitmap map = getMap(xmax - xmin, ymax - ymin, translator.getMapResId(_game.map.mapId));
		canvas.drawBitmap(map, xmin, ymin, pntMap);
		
		for (City city : _game.map.cities){
			cityPainter.draw(canvas, city);
		}
	}

	int map_w = -1;
	int map_h = -1;
	int map_resId = -1;
	Bitmap map_cache;
	private Bitmap getMap(float w, float h, int resId){
		int rw = (int)w;
		int rh = (int)h;
		
		if (rw != map_w || rh != map_h || resId != map_resId) {
			Resources res = _context.getResources();
			Bitmap bmp = BitmapFactory.decodeResource(res, resId);
			map_cache = Bitmap.createScaledBitmap(bmp, rw, rh, true);
			map_w = rw;
			map_h = rh;
			map_resId = resId;
		}
		return map_cache;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (!isActive)
			return true;
			
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
		viewShower.showView(new CityInfoView(_context, viewShower, translator, 
			_game.getViewForPlayer(Constants.Players.MainHuman), city.id));
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
	}

	public void activate(){
		isActive = true;
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
