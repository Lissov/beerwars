package com.pl.beerwars.data;
import com.pl.beerwars.data.map.*;
import com.pl.beerwars.data.Constants.*;
import com.pl.beerwars.data.playerdata.*;
import com.pl.beerwars.data.playerdata.CityObjects.FactoryProduction;

import java.util.*;

import com.pl.beerwars.data.beer.*;

public class GameHolder
{
	private static Game _game = null;
	private static Random rnd = new Random();
	
	public static Game getGame(){
		if (_game == null)
			constructGame();
			
		return _game;
	}
	
	private static void constructGame(){
		_game = new Game();
		_game.map = get2IslandsMap();
		_game.players = buildPlayers(_game.map, "PLAYER", "freiburg", 3);
		
		Calendar c = Calendar.getInstance(); 
		c.set(2014, 00, 06);
		_game.date = c.getTime();
		
		_game.start();
	}
	
	private static HashMap<Integer, PlayerData> buildPlayers(
		com.pl.beerwars.data.map.Map map,
		String humanName, String humanCity, int playersCount)
	{
		HashMap<Integer, PlayerData> players = new HashMap<Integer, PlayerData>();
		players.put(Constants.Players.MainHuman, buildPlayer(map, humanName, Constants.IntellectId.Human, humanCity));
		java.util.LinkedList<String> owned = new java.util.LinkedList<String>();
		owned.add(humanCity);
		String[] names = new String[] { "Hanek'n", "Fraizer", "Praterer", "Klown" };
		for (int i=1; i<playersCount; i++){
			int id = Constants.Players.MainHuman + i;

			int cn = rnd.nextInt(map.cities.length);
			while (owned.contains(map.cities[cn].id))
				cn = rnd.nextInt(map.cities.length);

			players.put(id, buildPlayer(map, names[i-1], Constants.IntellectId.AI, map.cities[cn].id));  
		}
		return players;
	}

	private static PlayerData buildPlayer(
		com.pl.beerwars.data.map.Map map,
		String name, int intellectId, String cityId)
	{
		PlayerData player = new PlayerData(intellectId, name);

		player.money = Constants.Economics.startMoney;
		player.name = name;
		player.intellect_id = intellectId;

		float dev = (float)rnd.nextGaussian() * Constants.startBeerParameters.deviation;
		BeerSort sort = new BeerSort(name + " START", 
									 Constants.startBeerParameters.selfprice * (1f + dev),
									 Constants.startBeerParameters.quality * (1f + dev));
		player.ownedSorts.add(sort);

		player.cityObjects = new CityObjects[map.cities.length];
		for (int i=0; i<map.cities.length; i++){
			City c = map.cities[i];
			
			if (c.id == cityId){
				player.cityObjects[i] = new CityObjects(c, StorageSize.Small, FactorySize.Small); 	
				FactoryProduction fp = player.cityObjects[i].new FactoryProduction();
				fp.totalUnits = Constants.Economics.startUnits;
				fp.workingUnits = Constants.Economics.startUnits;
				player.cityObjects[i].factory.put(sort, fp);
			}
			else
				player.cityObjects[i] = new CityObjects(c, StorageSize.None, FactorySize.None);
		}

		return player;
	}
	
	private static com.pl.beerwars.data.map.Map get2IslandsMap(){
		com.pl.beerwars.data.map.Map map = new com.pl.beerwars.data.map.Map();
		
		map.mapId = Constants.Maps.Basic;

		map.cities = new City[11];

		map.cities[0] = new City(Constants.CityIds.Trinkburg, new Location(0.2f, 0.5f), 1200000);
		map.cities[1] = new City(Constants.CityIds.Feldkirchen, new Location(0.1f, 0.7f), 170000);
		map.cities[2] = new City(Constants.CityIds.Weissau, new Location(0.15f, 0.3f), 125000);
		map.cities[3] = new City(Constants.CityIds.Luisfeld, new Location(0.3f, 0.15f), 440000);
		map.cities[4] = new City(Constants.CityIds.Maishafen, new Location(0.3f, 0.67f), 240000);
		map.cities[5] = new City(Constants.CityIds.Steinfurt, new Location(0.5f, 0.1f), 520000);
		map.cities[6] = new City(Constants.CityIds.SanMartin, new Location(0.45f, 0.8f), 750000);
		map.cities[7] = new City(Constants.CityIds.Prems, new Location(0.645f, 0.6f), 250000);
		map.cities[8] = new City(Constants.CityIds.Freiburg, new Location(0.8f, 0.4f), 790000);
		map.cities[9] = new City(Constants.CityIds.Regenwald, new Location(0.9f, 0.62f), 330000);
		map.cities[10] = new City(Constants.CityIds.Hochstadt, new Location(0.9f, 0.8f), 260000);
		// total population 5 025 000

		map.roads = new Road[13];
		map.roads[0] = new Road(Constants.CityIds.Trinkburg, Constants.CityIds.Weissau, 2.5f);
		map.roads[1] = new Road(Constants.CityIds.Trinkburg, Constants.CityIds.Luisfeld, 3.5f);
		map.roads[2] = new Road(Constants.CityIds.Trinkburg, Constants.CityIds.Feldkirchen, 3);
		map.roads[3] = new Road(Constants.CityIds.Trinkburg, Constants.CityIds.Maishafen, 2.5f);
		map.roads[4] = new Road(Constants.CityIds.Weissau, Constants.CityIds.Luisfeld, 3);
		map.roads[5] = new Road(Constants.CityIds.Luisfeld, Constants.CityIds.Steinfurt, 2);
		map.roads[6] = new Road(Constants.CityIds.Steinfurt, Constants.CityIds.Freiburg, 4);
		map.roads[7] = new Road(Constants.CityIds.Steinfurt, Constants.CityIds.Prems, 7);
		map.roads[8] = new Road(Constants.CityIds.Prems, Constants.CityIds.Freiburg, 4);
		map.roads[9] = new Road(Constants.CityIds.Freiburg, Constants.CityIds.Regenwald, 4);
		map.roads[10] = new Road(Constants.CityIds.Regenwald, Constants.CityIds.Hochstadt, 4);
		map.roads[11] = new Road(Constants.CityIds.Maishafen, Constants.CityIds.SanMartin, 11);
		map.roads[12] = new Road(Constants.CityIds.Prems, Constants.CityIds.SanMartin, 15);
		map.calculateDistances();
		
		return map;
	}
}
