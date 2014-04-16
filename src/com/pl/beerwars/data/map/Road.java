package com.pl.beerwars.data.map;

public class Road
{
	public String[] cities;
	public float length;

	public Road(String city1, String city2, float length)
	{
		this.cities = new String[] { city1, city2 };
		this.length = length;
	}
}
