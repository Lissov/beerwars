package com.pl.beerwars.data;
import android.annotation.SuppressLint;
import com.pl.beerwars.data.Constants.FactorySizes;
import com.pl.beerwars.data.Constants.StorageSizes;
import com.pl.beerwars.data.beer.BeerSort;
import com.pl.beerwars.data.facade.*;
import java.util.*;

import com.pl.beerwars.data.map.City;
import com.pl.beerwars.data.playerdata.CityObjects;
import com.pl.beerwars.data.playerdata.PlayerData;
import com.pl.beerwars.data.transport.*;

public class Game
{
	public Date date;
	
	public com.pl.beerwars.data.map.Map map;
	private Random rnd = new Random();
	
	public PlayerData getViewForPlayer(int playerNum){
		return players.get(playerNum);
	}
	
	@SuppressLint("UseSparseArrays")
	public HashMap<Integer, PlayerData> players = new HashMap<Integer, PlayerData>();
	
	public void start(String humanName, String humanCity, int playersCount){
		date = new Date(2014, 01, 06);
		buildPlayers(humanName, humanCity, playersCount);
		buildFacades();
		updateFacades();
	}

	private void buildFacades(){
		for (PlayerData player : players.values())
		{
			GameFacade gv = new GameFacade();
			
			gv.cities = new CityFacade[map.cities.length];
			for (int i = 0; i < map.cities.length; i++) {
				gv.cities[i] = new CityFacade(map.cities[i]);
				gv.cities[i].estConsumption = Constants.ValueUnknown;

				gv.cities[i].transportPrices = getTrPrices(gv.cities[i].getId());
			}

			player.game = gv;
		}
	}
	
	private void updateFacades(){
		for (PlayerData player : players.values())
		{
			player.game.date = date;
		}
	}
	
	private TransportPrice[] getTrPrices(String fromCity){
		int thisI = map.getCityIndex(fromCity);
		TransportPrice[] result = new TransportPrice[map.cities.length - 1];
		
		int n = 0;
		for (int i = 0; i < map.cities.length; i++){
			if (i == thisI) continue;
			
			result[n] = new TransportPrice();
			result[n].cityFrom = fromCity;
			result[n].cityTo = map.cities[i].id;
			result[n].price1000 = (int)(Constants.Economics.transportReload
				+ map.distances[thisI][i] * Constants.Economics.transportPerKm);
			
			n++;
		}
		
		return result;
	}
	
	private void buildPlayers(String humanName, String humanCity, int playersCount){
		players.put(Constants.Players.MainHuman, buildPlayer(humanName, Constants.IntellectId.Human, humanCity));
		LinkedList<String> owned = new LinkedList<String>();
		owned.add(humanCity);
		String[] names = new String[] { "Hanek'n", "Fraizer", "Praterer", "Klown" };
		for (int i=1; i<playersCount; i++){
			int id = Constants.Players.MainHuman + i;
			
			int cn = rnd.nextInt(map.cities.length);
			while (owned.contains(map.cities[cn].id))
				cn = rnd.nextInt(map.cities.length);
			
			players.put(id, buildPlayer(names[i-1], Constants.IntellectId.AI, map.cities[cn].id));  
		}
	}
	
	private PlayerData buildPlayer(String name, int intellectId, String cityId){
		PlayerData player = new PlayerData(intellectId, name);
		
		player.money = Constants.Economics.startMoney;
		player.name = name;
		player.intellect_id = intellectId;
		
		player.cityObjects = new CityObjects[map.cities.length];
		for (int i=0; i<map.cities.length; i++){
			City c = map.cities[i];
			player.cityObjects[i] = c.id == cityId
					? new CityObjects(c, StorageSizes.small, FactorySizes.small)
					: new CityObjects(c, StorageSizes.none, FactorySizes.none);
					
			float dev = (float)rnd.nextGaussian() * Constants.startBeerParameters.deviation;
			BeerSort sort = new BeerSort(name + " START", 
					Constants.startBeerParameters.selfprice * (1f + dev),
					Constants.startBeerParameters.quality * (1f + dev));
			player.ownedSorts.add(sort);
		}
		
		return player;
	}
}
