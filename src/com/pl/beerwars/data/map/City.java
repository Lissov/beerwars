package com.pl.beerwars.data.map;

import com.pl.beerwars.data.consumption.Distribution;
import com.pl.beerwars.data.consumption.PriceAdjust;

public class City
{
	public String id;
	public Location location;
	public int population;
	
    public Distribution PriceDemand;
    public PriceAdjust PriceAdjuster;
    public Distribution QualityDemand;

	public City(String id, Location location, int population)
	{
		this.id = id;
		this.location = location;
		this.population = population;
	}
}
