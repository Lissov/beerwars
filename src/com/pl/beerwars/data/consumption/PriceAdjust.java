package com.pl.beerwars.data.consumption;

public class PriceAdjust {
    float minP;
    float minPLevel;
    float minPReq;
    float expMult;
    public PriceAdjust(float minPrice, float minplevel, float minpreq, float expmult)
    {
        minP = minPrice;
        minPLevel = minplevel;
        minPReq = minpreq;
        expMult = expmult;
    }

    public float getAdjust(float price, float req)
    {
        float m = minPLevel + minPReq * req;
        if (price <= minP * req)
            return m;
        if (price <= req)
            return 1 + (m - 1) * (req - price) / (req - minP * req);

        return 1f / (float)Math.exp(expMult * (price - req) / req);
    }
}
