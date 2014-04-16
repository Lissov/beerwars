package com.pl.beerwars.data.map;

public class City
{
	public String id;
	public Location location;
	public int population;

	public City(String id, Location location, int population)
	{
		this.id = id;
		this.location = location;
		this.population = population;
	}
}
