package com.pl.beerwars.data.playerdata;

import com.pl.beerwars.data.map.City;

public class CityObjects {
	
	public City cityRef;

	public int storageSize;
	public int factorySize;
	
	public CityObjects(City cityRef, int storageSize, int factorySize) {
		this.cityRef = cityRef;
		this.storageSize = storageSize;
		this.factorySize = factorySize;
	}
}
