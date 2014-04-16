package com.pl.beerwars.data.map;

public class Map
{
	public City[] cities;
	public Road[] roads;
	
	public City getCityById(String id){
		for (City c : cities){
			if (c.id == id)
				return c;
		}
		
		return null;
	}
	
	//public float[][] 
}
