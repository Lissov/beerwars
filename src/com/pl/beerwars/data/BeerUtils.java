package com.pl.beerwars.data;

public class BeerUtils {
	
	public static float roundPrice(float unrounded){
		return Math.round(unrounded * 100f) / 100f;
	}
	
}
