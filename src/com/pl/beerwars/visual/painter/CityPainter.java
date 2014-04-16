package com.pl.beerwars.visual.painter;
import android.content.*;
import android.graphics.*;
import com.pl.beerwars.*;
import com.pl.beerwars.visual.*;
import com.pl.beerwars.data.map.*;
import com.pl.beerwars.data.*;
import android.content.res.*;

public class CityPainter extends BasePainter
{
	private Paint pntCityFill = new Paint();
	private Paint pntCityBorder = new Paint();
	private Paint pntCityName = new Paint();
	
	public CityPainter(Translator translator, Context context){
		super(translator, context);

		SetColor(pntCityFill, R.color.cityBackground);
		pntCityFill.setStyle(Paint.Style.FILL);

		SetColor(pntCityBorder, R.color.cityBorder);
		pntCityBorder.setStyle(Paint.Style.STROKE);
		pntCityBorder.setStrokeWidth(2);

		SetColor(pntCityName, R.color.cityName);
	}

	public void draw(Canvas canvas, City city){
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
