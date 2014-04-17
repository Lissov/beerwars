package com.pl.beerwars.data.facade;
import com.pl.beerwars.data.map.*;
import com.pl.beerwars.data.transport.*;

public class CityFacade
{
	private City _city;
	public CityFacade(City city)
	{
		_city = city;
	}

	public String getId() {
		return _city.id;	
	}
	public int getPopulation(){
		return _city.population;	
	}

	public TransportPrice[] transportPrices;
	
	public int estConsumption;
}
