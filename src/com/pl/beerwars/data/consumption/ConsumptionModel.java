package com.pl.beerwars.data.consumption;

import java.util.HashMap;

import com.pl.beerwars.data.Game;
import com.pl.beerwars.data.beer.BeerSort;
import com.pl.beerwars.data.map.City;
import com.pl.beerwars.data.playerdata.CityObjects;
import com.pl.beerwars.data.playerdata.PlayerData;

public class ConsumptionModel {
	public HashMap<BeerSort, Integer> calculateConsumption(Game game, City city){
		HashMap<BeerSort, Integer> result = new HashMap<BeerSort, Integer>();
		
		HashMap<BeerSort, BeerData> av = new HashMap<BeerSort, ConsumptionModel.BeerData>();
		int cn = game.map.getCityIndex(city.id);
		for (PlayerData p : game.players.values()){
			CityObjects co = p.cityObjects[cn];
			for (BeerSort sort : co.storage.keySet()){
				int q = co.storage.get(sort);
				if (q == 0) continue;
				av.put(sort, new BeerData(co.prices.get(sort), q));
			}
		}
		
		for (BeerSort sort : av.keySet()){
			result.put(sort, av.get(sort).quantity);
		}
		
		return result;
	}
	
	private class BeerData{
		public float price;
		public int quantity;
		
		public BeerData(float price, int quantity) {
			super();
			this.price = price;
			this.quantity = quantity;
		}
	}
}
