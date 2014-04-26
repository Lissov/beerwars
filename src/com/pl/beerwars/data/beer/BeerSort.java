package com.pl.beerwars.data.beer;

public class BeerSort {

	public static int nextSortId = 1;
	
	public int id;
	public String name;
	public float selfprice;
	public float quality;
	
	public BeerSort(String name, float selfprice, float quality) {
		this.id = nextSortId++;
		this.name = name;
		this.selfprice = selfprice;
		this.quality = quality;
	}

}
