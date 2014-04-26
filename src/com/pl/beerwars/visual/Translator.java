package com.pl.beerwars.visual;
import com.pl.beerwars.*;
import android.content.res.*;
import com.pl.beerwars.data.*;

public class Translator
{
	private Resources res;
	private float sizex;
	private float sizey;
	public float scale;

	public void setup(float sizex, float sizey, float scale, Resources resources)
	{
		this.sizex = sizex;
		this.sizey = sizey;
		this.scale = scale;
		this.res = resources;
	}

	public float getX(float gameX)
	{
		return gameX * sizex;
	}

	public float getY(float gameY)
	{
		return gameY * sizey;
	}

	public String getCityName(String cityId)
	{
		if (cityId == Constants.CityIds.Trinkburg)
			return res.getString(R.string.city_trinkburg);
		if (cityId == Constants.CityIds.Feldkirchen)
			return res.getString(R.string.city_feldkirchen);
		if (cityId == Constants.CityIds.Weissau)
			return res.getString(R.string.city_weissau);
		if (cityId == Constants.CityIds.Luisfeld)
			return res.getString(R.string.city_luisfeld);
		if (cityId == Constants.CityIds.Maishafen)
			return res.getString(R.string.city_maishafen);
		if (cityId == Constants.CityIds.SanMartin)
			return res.getString(R.string.city_sanmartin);
		if (cityId == Constants.CityIds.Steinfurt)
			return res.getString(R.string.city_steinfurt);
		if (cityId == Constants.CityIds.Freiburg)
			return res.getString(R.string.city_freiburg);
		if (cityId == Constants.CityIds.Prems)
			return res.getString(R.string.city_prems);
		if (cityId == Constants.CityIds.Regenwald)
			return res.getString(R.string.city_regenwald);
		if (cityId == Constants.CityIds.Hochstadt)
			return res.getString(R.string.city_hochstadt);

		return "unknown";
	}

	public int getMapResId(int mapId)
	{
		if (mapId == Constants.Maps.Basic)
			return R.drawable.map_basic;

		return -1;
	}

	public String getMapName(int mapId)
	{
		if (mapId == Constants.Maps.Basic)
			return res.getString(R.string.map_basic);

		return "unknown";
	}

	public String getStorageName(Constants.StorageSize size)
	{
		switch (size)
		{
			case Small: return res.getString(R.string.storage_small);
			case Medium: return res.getString(R.string.storage_medium);
			case Big: return res.getString(R.string.storage_big);
			case None:
			default:
				return res.getString(R.string.storage_none);
		}
	}

	public String getFactoryName(Constants.FactorySize size)
	{
		switch (size)
		{
			case Small: return res.getString(R.string.factory_small);
			case Medium: return res.getString(R.string.factory_medium);
			case Big: return res.getString(R.string.factory_big);
			case None:
			default:
				return res.getString(R.string.factory_none);
		}
	}
}
