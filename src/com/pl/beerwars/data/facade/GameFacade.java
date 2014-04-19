package com.pl.beerwars.data.facade;

import java.util.Date;

public class GameFacade
{
	public Date date;
	
	public CityFacade[] cities;

	public CityFacade getCity(String cityId){
		for (CityFacade c : cities){
			if (c.getId() == cityId)
				return c;
		}

		return null;
	}
}
