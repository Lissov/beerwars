package com.pl.beerwars.data.consumption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.pl.beerwars.data.Game;
import com.pl.beerwars.data.beer.BeerSort;
import com.pl.beerwars.data.map.City;
import com.pl.beerwars.data.playerdata.CityObjects;
import com.pl.beerwars.data.playerdata.PlayerData;

public class ConsumptionModel {
	public HashMap<BeerSort, Integer> calculateConsumption(Game game, City city){
		List<BeerData> av = new LinkedList<ConsumptionModel.BeerData>();
		int cn = game.map.getCityIndex(city.id);
		for (PlayerData p : game.players.values()){
			CityObjects co = p.cityObjects[cn];
			for (BeerSort sort : co.storage.keySet()){
				int q = co.storage.get(sort);
				if (q == 0) continue;
				av.add(new BeerData(sort, co.prices.get(sort), q));
			}
		}
		
		return calculate(av, city);
	}
	
	private HashMap<BeerSort, Integer> calculate(List<BeerData> data, City cData)
    {
		HashMap<BeerSort, Integer> result = new HashMap<BeerSort, Integer>();
		for (BeerData d : data){
			result.put(d.sort, 0);
		}
		
        boolean overflow = false;
        int cnt = (int)Math.round(cData.PriceDemand.maxX / 0.01) + 1;
        float[] moneyDistr = new float[cnt];
        int n = 0;
        for (float m = 0; m <= cData.PriceDemand.maxX; m+=0.01f)
        {
            moneyDistr[n++] = cData.PriceDemand.getDistNorm(m);
        }
            
        do
        {
            BeerData[] darr = getAvailable(data, result);
            
            if (darr.length == 0)
                break;
            BeerDemand dem = getDemands(darr, moneyDistr, cData.PriceAdjuster, cData.QualityDemand);
            for (int i = 0; i < moneyDistr.length; i++) 
                moneyDistr[i] = 0;

            overflow = false;

            for (int i = 0; i < darr.length; i++)
            {
                float sortDemand = dem.getTotalDem(i);
                int currentRes = result.get(darr[i].sort);
                if (sortDemand + currentRes <= darr[i].quantity)
                {
                    result.put(darr[i].sort, currentRes + (int)sortDemand);
                }
                else
                {
                    overflow = true;
                    float noBeerLevel = 1f - (darr[i].quantity - currentRes) / sortDemand;
                    for (int j = 0; j < moneyDistr.length; j++)
                    {
                        if (dem.fillLevel[j] > 0)
                            moneyDistr[j] += (dem.demands[i][j] / dem.fillLevel[j]) * noBeerLevel;
                    }
                    result.put(darr[i].sort, darr[i].quantity);
                }
            }
        } while (overflow);

        return result;
    }
	
	private BeerData[] getAvailable(List<BeerData> data, HashMap<BeerSort, Integer> result){
		List<BeerData> res = new LinkedList<BeerData>();
		
		for (BeerData d : data){
			if (d.quantity > result.get(d.sort))
				res.add(d);
		}
		
		if (res.size() == 0)
			return new BeerData[0];
		
		return res.toArray(new BeerData[0]);
	}

    private BeerDemand getDemands(BeerData[] data, float[] moneyDistr, PriceAdjust priceAdj, Distribution qualityDem)
    {
        BeerDemand res = new BeerDemand(data.length, moneyDistr.length);
        
        int n = 0;
        for (int m = 0; m < moneyDistr.length; m += 1)
        {
            float amt = moneyDistr[m];
            float money = (float)m / 100f;
         
            float[] ddem = new float[data.length];
            for (int i = 0; i < data.length; i++)
            {
                ddem[i] = priceAdj.getAdjust(data[i].price, money) * qualityDem.getDist(data[i].sort.quality);
            }

            float totalDem = arraySum(ddem);
            float fillLevel = arrayMax(ddem);
            if (fillLevel > 1) fillLevel = 1;

            for (int i = 0; i < data.length; i++)
            {
                float share = totalDem < 1 ? 0 : (ddem[i] / totalDem);
                res.demands[i][n] += share * amt;
            }
            res.fillLevel[n] = fillLevel;
            n++;
        }

        return res;
    }
    
    private float arraySum(float[] array){
    	float s = 0;
    	for (float f : array)
    		s += f;
    	return s;
    }
    
    private float arrayMax(float[] array){
    	float m = array[0];
    	for (float f : array){
    		if (m < f) 
    			m = f;
    	}
    	return m;
    }
    
	private class BeerData{
		public BeerSort sort;
		public float price;
		public int quantity;
		
		public BeerData(BeerSort sort, float price, int quantity) {
			this.sort = sort;
			this.price = price;
			this.quantity = quantity;
		}
	}
}
