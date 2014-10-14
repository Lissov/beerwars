package com.pl.beerwars.visual.painter;
import android.content.*;
import android.graphics.*;
import com.pl.beerwars.*;
import com.pl.beerwars.data.Constants.*;
import com.pl.beerwars.visual.*;
import com.pl.beerwars.data.map.*;
import com.pl.beerwars.data.*;
import com.pl.beerwars.data.playerdata.*;
import com.pl.beerwars.data.facade.*;

public class CityPainter extends BasePainter
{
	private Paint pntCityFill = new Paint();
	private Paint pntCityBorder = new Paint();
	private Paint pntCityName = new Paint();
	private Paint pntStorage = new Paint();
	private Paint pntStorageB = new Paint();
	private Paint pntFactory = new Paint();
	private Paint pntFactoryB = new Paint();
	private Paint pntUsedRect = new Paint();
	private Paint pntUsedFill = new Paint();
	private Paint pntIdleFill = new Paint();
	private Paint[] pntPlayers;
	
	public CityPainter(Translator translator, Context context){
		super(translator, context);

		SetColor(pntCityFill, R.color.cityBackground);
		pntCityFill.setStyle(Paint.Style.FILL);

		SetColor(pntCityBorder, R.color.cityBorder);
		pntCityBorder.setStyle(Paint.Style.STROKE);
		pntCityBorder.setStrokeWidth(2);

		SetColor(pntCityName, R.color.cityName);

		SetColor(pntStorage, R.color.city_storage);
		SetColor(pntStorageB, R.color.city_storage_build);
		SetColor(pntFactory, R.color.city_factory);
		SetColor(pntFactoryB, R.color.city_factory_build);
		
		SetColor(pntUsedRect, R.color.city_usage);
		pntUsedRect.setStyle(Paint.Style.STROKE);
		SetColor(pntUsedFill, R.color.city_usage);
		pntUsedFill.setStyle(Paint.Style.FILL);
		SetColor(pntIdleFill, R.color.city_usagepotential);
		pntIdleFill.setStyle(Paint.Style.FILL);
		
		pntPlayers = new Paint[6];
		for (int i = 0; i<6; i++){
			pntPlayers[i] = new Paint();
			pntPlayers[i].setStyle(Paint.Style.FILL);
		}
		SetColor(pntPlayers[0], R.color.player_1);
		SetColor(pntPlayers[1], R.color.player_2);
		SetColor(pntPlayers[2], R.color.player_3);
		SetColor(pntPlayers[3], R.color.player_4);
		SetColor(pntPlayers[4], R.color.player_5);
		SetColor(pntPlayers[5], R.color.player_6);
	}

	public void draw(Canvas canvas, City city, CityObjects cobj, int playerNum, CityFacade cityfacade){
		float size2 = getCitySize(city.population) * translator.scale / 2;
		float x = translator.getX(city.location.x);
		float y = translator.getY(city.location.y);
		canvas.drawRect(x - size2, y - size2, x + size2, y + size2, pntCityFill);
		canvas.drawRect(x - size2, y - size2, x + size2, y + size2, pntCityBorder);

		pntCityName.setTextSize(translator.scale * 2);
		String name = translator.getCityName(city.id);
		float w = pntCityName.measureText(name);
		canvas.drawText(name, 
			x - w/2, y + translator.scale * 2.5f,
			pntCityName);

		StorageSize ss = cobj.storageBuildRemaining > 0 
			? Constants.StorageNextSize(cobj.storageSize)
			: cobj.storageSize;
		float xn = paintStorage(canvas, x, y, ss, 
				(float)cobj.getTotalStorage() / Constants.StorageVolume(cobj.storageSize),
				cobj.storageBuildRemaining, playerNum);
		float fs = Constants.FactoryVolume(cobj.factorySize);
		float working = cobj.getOperatingUnitsCount();
		float all = cobj.factoryUnits;
		FactorySize facts = cobj.factoryBuildRemaining > 0 
			? Constants.FactoryNextSize(cobj.factorySize)
			: cobj.factorySize;
		xn += paintFactory(canvas, x + xn, y, facts, working/fs, all/fs,
			cobj.factoryBuildRemaining, playerNum);
			
		for (int playerN : cityfacade.others.keySet()){
			int pn = playerN;
			PlayerCityFacade pcf = cityfacade.others.get(pn);
			if (pcf.storageSize != StorageSize.None){
				xn += paintStorage(canvas, x + xn, y, pcf.storageSize, 
								  -1, 0, pn);
			}
			if (pcf.factorySize != FactorySize.None){
				xn += paintFactory(canvas, x + xn, y, pcf.factorySize, 
								  -1, -1, 0, pn);
			}
		}
	}
	
	private float paintStorage(Canvas canvas, float cx, float cy, StorageSize size, float storageUsage, int buildRemaining, int playerNum){
		Paint p = buildRemaining > 0 ? pntStorageB : pntStorage;
		Paint plr = getPlayerPaint(playerNum);
		float s = translator.scale;
		float xs = cx + s / 4;
		float ys = cy - s;
		switch (size){
			case Small:
				canvas.drawRect(xs, ys - s*5/12, xs + s/4, ys, p);
				canvas.drawRect(xs, ys - s*5/12, xs + s*3/4, ys-s/3, p);
				canvas.drawRect(xs + s*2/4, ys - s*5/12, xs + s*3/4, ys, p);
				canvas.drawRect(xs, ys - s*5/12, xs + s*3/4, ys-s*1/2, plr);
				return s*5/6 + paintLevel(canvas, xs + s*5/6, ys, storageUsage, 0f);
			case Medium:
				canvas.drawRect(xs, ys - s*5/12, xs + s/4, ys, p);
				canvas.drawRect(xs + s*2/4, ys - s*5/12, xs + s*3/4, ys, p);
				canvas.drawRect(xs + s, ys - s*5/12, xs + s*5/4, ys, p);
				canvas.drawRect(xs, ys - s*5/12, xs + s*5/4, ys-s/3, p);
				canvas.drawRect(xs, ys - s*5/12, xs + s*5/4, ys-s*1/2, plr);
				return s*(8/6) + paintLevel(canvas, xs + s*(8/6), ys, storageUsage, 0f);				
			case Big:
				canvas.drawRect(xs, ys - s/2, xs + s/4, ys, p);
				canvas.drawRect(xs + s*2/4, ys - s/2, xs + s*3/4, ys, p);
				canvas.drawRect(xs + s, ys - s/2, xs + s*5/4, ys, p);
				canvas.drawRect(xs + s*6/4, ys - s/2, xs + s*7/4, ys, p);
				canvas.drawRect(xs, ys - s/2, xs + s*7/4, ys-s/3, p);
				canvas.drawRect(xs, ys - s*5/6, xs + s*7/4, ys-s/2, plr);
				return s*11/6 + paintLevel(canvas, xs + s*11/6, ys, storageUsage, 0f);				
			case None:
				return 0;
			default:
				canvas.drawRect(xs, ys, xs+s, ys-s, plr);
				return s*1.2f;
		}	
	}
	
	private float paintFactory(Canvas canvas, float cx, float cy, FactorySize size, float used, float potential, int buildRemaining, int playerNum){
		Paint p = buildRemaining > 0 ? pntFactoryB : pntFactory;
		Paint plr = getPlayerPaint(playerNum);
		float s = translator.scale;
		float xs = cx + s / 2;
		float ys = cy - s;
		switch (size){
			case Small:
				canvas.drawRect(xs, ys - s*1/2, xs + s*3/4, ys, p);
				canvas.drawRect(xs + s*4/8, ys - s, xs + s*5/8, ys - s*1/2, plr);
				return s*5/6 + paintLevel(canvas, xs + s*5/6, ys, used, potential);
			case Medium:
				canvas.drawRect(xs, ys - s*1/2, xs + s, ys, p);
				canvas.drawRect(xs + s*3/8, ys - s*3/4, xs + s*4/8, ys-s*1/2, plr);
				canvas.drawRect(xs + s*5/8, ys - s, xs + s*6/8, ys-s*1/2, plr);
				return s*7/6 + paintLevel(canvas, xs + s*7/6, ys, used, potential);				
			case Big:
				canvas.drawRect(xs, ys - s*1/2, xs + s*10/8, ys, p);
				canvas.drawRect(xs + s*3/8, ys - s*3/4, xs + s*4/8, ys-s*1/2, plr);
				canvas.drawRect(xs + s*5/8, ys - s, xs + s*6/8, ys-s*1/2, plr);
				canvas.drawRect(xs + s*7/8, ys - s, xs + s*8/8, ys-s*3/4, plr);
				return s*5/4 + paintLevel(canvas, xs + s*5/4, ys, used, potential);				
			case None:
				return 0;
			default:
				canvas.drawRect(xs, ys, xs+s, ys-s, plr);
				return s*1.2f;
		}	
	}
	
	private float paintLevel(Canvas canvas, float x, float y, float fill, float potential){
			if (fill == -1){
			return 0;
		}
		
		float s = translator.scale;
		float w = s/5;
		canvas.drawRect(x, y-s*(potential), x + w, y, pntIdleFill);
		canvas.drawRect(x, y-s*(fill), x + w, y, pntUsedFill);
		canvas.drawRect(x, y-s, x + w, y, pntUsedRect);
		
		return 2 * w;
	}
		
	private float getCitySize(int population){
		if (population >= Constants.SitySizes.Mega)
			return 2.0f;
		if (population >= Constants.SitySizes.Big)
			return 1.5f;
		if (population >= Constants.SitySizes.Medium)
			return 1.0f;
		return 0.5f;
	}
	
	private Paint getPlayerPaint(int playerNum){
		return pntPlayers[playerNum - Constants.Players.MainHuman];
	}
}
