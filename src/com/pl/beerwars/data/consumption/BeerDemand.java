package com.pl.beerwars.data.consumption;

public class BeerDemand {
	public float[][] demands;
	public float[] fillLevel;

	public BeerDemand(int dataCount, int length) {
		demands = new float[dataCount][];
		for (int i = 0; i < dataCount; i++) {
			demands[i] = new float[length + 1];
		}
		fillLevel = new float[length + 1];
	}

	public float getTotalDem(int i) {
		float s = 0;
		for (int j = 0; j < demands[i].length; j++)
			s += demands[i][j];
		
		return s;
	}
}
