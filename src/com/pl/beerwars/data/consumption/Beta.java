package com.pl.beerwars.data.consumption;

public class Beta extends Distribution {
    private float a;
    private float b;
    public float scale;
    private float sum;

    public Beta(float a, float b, float minX, float maxX)
    {
        this.a = a;
        this.b = b;
        this.minX = minX;
        this.maxX = maxX;
        scale = 1;

        sum = 0;
        for (float x = minX; x <= maxX; x+=0.01f)
        {
            sum += getDist(x);
        }
    }

	@Override
	public float getDist(float x) {
        if (x <= minX || x >= maxX)
            return 0;

        float xn = (x - minX) / (maxX - minX);
        return (float)(Math.pow(xn, a) * Math.pow(1 - xn, b)) * scale;
    }

	@Override
	public float getDistNorm(float x) {
        return getDist(x) / sum;
    }
}
