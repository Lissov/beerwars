package com.pl.beerwars.data.consumption;

public class QualityDemand extends Distribution {

	@Override
	public float getDist(float x) {
        return x * x;
    }

	@Override
	public float getDistNorm(float x) {
        return getDist(x);
	}
}
