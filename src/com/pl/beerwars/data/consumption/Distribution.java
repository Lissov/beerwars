package com.pl.beerwars.data.consumption;

public abstract class Distribution {
    public float minX;
    public float maxX;

    public abstract float getDist(float x);
    public abstract float getDistNorm(float x);
    //public abstract float getInt(float x);
}
