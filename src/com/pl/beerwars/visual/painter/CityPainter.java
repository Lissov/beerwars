package com.pl.beerwars.visual.painter;
import android.content.*;
import android.graphics.*;
import com.pl.beerwars.*;
import com.pl.beerwars.data.Constants.*;
import com.pl.beerwars.visual.*;
import com.pl.beerwars.data.map.*;
import com.pl.beerwars.data.*;
import com.pl.beerwars.data.playerdata.*;

public class CityPainter extends BasePainter
{
	private Paint pntCityFill = new Paint();
	private Paint pntCityBorder = new Paint();
	private Paint pntCityName = new Paint();
	private Paint pntStorage = new Paint();
	private Paint pntFactory = new Paint();
	private Paint pntUsedRect = new Paint();
	private Paint pntUsedFill = new Paint();
	private Paint pntIdleFill = new Paint();
	
	public CityPainter(Translator translator, Context context){
		super(translator, context);

		SetColor(pntCityFill, R.color.cityBackground);
		pntCityFill.setStyle(Paint.Style.FILL);

		SetColor(pntCityBorder, R.color.cityBorder);
		pntCityBorder.setStyle(Paint.Style.STROKE);
		pntCityBorder.setStrokeWidth(2);

		SetColor(pntCityName, R.color.cityName);

		SetColor(pntStorage, R.color.city_storage);
		SetColor(pntFactory, R.color.city_factory);

		SetColor(pntUsedRect, R.color.city_usage);
		pntUsedRect.setStyle(Paint.Style.STROKE);
		SetColor(pntUsedFill, R.color.city_usage);
		pntUsedFill.setStyle(Paint.Style.FILL);
		SetColor(pntIdleFill, R.color.city_usagepotential);
		pntIdleFill.setStyle(Paint.Style.FILL);
	}

	public void draw(Canvas canvas, City city, CityObjects cobj){
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

		float xn = paintStorage(canvas, x, y, cobj.storageSize, 
				(float)cobj.getTotalStorage() / Constants.StorageVolume(cobj.storageSize));
		float fs = Constants.FactoryVolume(cobj.factorySize);
		float working = cobj.getOperatingUnitsCount();
		float all = cobj.factoryUnits;
		xn = paintFactory(canvas, x + xn, y, cobj.factorySize, working/fs, all/fs);
	}
	
	private float paintStorage(Canvas canvas, float cx, float cy, StorageSize size, float storageUsage){
		float s = translator.scale;
		float xs = cx + s / 4;
		float ys = cy - s;
		switch (size){
			case Small:
				canvas.drawRect(xs, ys - s*2/3, xs + s/4, ys, pntStorage);
				canvas.drawRect(xs, ys - s*2/3, xs + s*3/4, ys-s/3, pntStorage);
				canvas.drawRect(xs + s*2/4, ys - s*2/3, xs + s*3/4, ys, pntStorage);
				return s + paintLevel(canvas, xs + s*5/6, ys, storageUsage, 0f);
			case None:
			default: return 0;
		}	
	}
	
	private float paintFactory(Canvas canvas, float cx, float cy, FactorySize size, float used, float potential){
		float s = translator.scale;
		float xs = cx + s / 2;
		float ys = cy - s;
		switch (size){
			case Small:
				canvas.drawRect(xs, ys - s*1/2, xs + s*3/4, ys, pntStorage);
				canvas.drawRect(xs + s*4/8, ys - s, xs + s*5/8, ys, pntStorage);
				return s + paintLevel(canvas, xs + s*5/6, ys, used, potential);
			case Medium:
				canvas.drawRect(xs, ys - s*1/2, xs + s, ys, pntStorage);
				canvas.drawRect(xs + s*3/8, ys - s*3/4, xs + s*4/8, ys, pntStorage);
				canvas.drawRect(xs + s*5/8, ys - s, xs + s*6/8, ys, pntStorage);
				return s + paintLevel(canvas, xs + s*7/6, ys, used, potential);				
			case None:
			default: return 0;
		}	
	}
	
	private float paintLevel(Canvas canvas, float x, float y, float fill, float potential){
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
}
