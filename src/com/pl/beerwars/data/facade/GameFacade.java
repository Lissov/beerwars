package com.pl.beerwars.data.facade;

public class GameFacade
{
	public CityFacade[] cities;

	public CityFacade getCity(String cityId){
		for (CityFacade c : cities){
			if (c.getId() == cityId)
				return c;
		}

		return null;
	}
}
