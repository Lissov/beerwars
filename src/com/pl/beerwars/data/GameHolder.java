package com.pl.beerwars.data;
import com.pl.beerwars.data.map.*;

public class GameHolder
{
	private static Game _game = null;
	
	public static Game getGame(){
		if (_game == null)
			constructGame();
			
			return _game;
	}
	
	private static void constructGame(){
		_game = new Game();
		_game.map = get2IslandsMap();
	}
	
	private static Map get2IslandsMap(){
		Map map = new Map();

		map.cities = new City[11];

		map.cities[0] = new City(Constants.CityIds.Trinkburg, new Location(0.2f, 0.5f), 1200000);
		map.cities[1] = new City(Constants.CityIds.Feldkirchen, new Location(0.1f, 0.7f), 170000);
		map.cities[2] = new City(Constants.CityIds.Weissau, new Location(0.15f, 0.3f), 125000);
		map.cities[3] = new City(Constants.CityIds.Luisfeld, new Location(0.3f, 0.1f), 440000);
		map.cities[4] = new City(Constants.CityIds.Maishafen, new Location(0.3f, 0.7f), 240000);
		map.cities[5] = new City(Constants.CityIds.Steinfurt, new Location(0.5f, 0.1f), 520000);
		map.cities[6] = new City(Constants.CityIds.SanMartin, new Location(0.45f, 0.8f), 750000);
		map.cities[7] = new City(Constants.CityIds.Prems, new Location(0.65f, 0.6f), 250000);
		map.cities[8] = new City(Constants.CityIds.Freiburg, new Location(0.8f, 0.4f), 790000);
		map.cities[9] = new City(Constants.CityIds.Regenwald, new Location(0.9f, 0.55f), 330000);
		map.cities[10] = new City(Constants.CityIds.Hochstadt, new Location(0.9f, 0.7f), 260000);

		map.roads = new Road[13];
		map.roads[0] = new Road(Constants.CityIds.Trinkburg, Constants.CityIds.Weissau, 2.5f);
		map.roads[1] = new Road(Constants.CityIds.Trinkburg, Constants.CityIds.Luisfeld, 4);
		map.roads[2] = new Road(Constants.CityIds.Trinkburg, Constants.CityIds.Feldkirchen, 3);
		map.roads[3] = new Road(Constants.CityIds.Trinkburg, Constants.CityIds.Maishafen, 3);
		map.roads[4] = new Road(Constants.CityIds.Weissau, Constants.CityIds.Luisfeld, 3.5f);
		map.roads[5] = new Road(Constants.CityIds.Luisfeld, Constants.CityIds.Steinfurt, 4);
		map.roads[6] = new Road(Constants.CityIds.Steinfurt, Constants.CityIds.Freiburg, 7);
		map.roads[7] = new Road(Constants.CityIds.Steinfurt, Constants.CityIds.Prems, 5);
		map.roads[8] = new Road(Constants.CityIds.Prems, Constants.CityIds.Freiburg, 4);
		map.roads[9] = new Road(Constants.CityIds.Freiburg, Constants.CityIds.Regenwald, 4);
		map.roads[10] = new Road(Constants.CityIds.Regenwald, Constants.CityIds.Hochstadt, 4);
		
		map.roads[11] = new Road(Constants.CityIds.Maishafen, Constants.CityIds.SanMartin, 7);
		map.roads[12] = new Road(Constants.CityIds.Prems, Constants.CityIds.SanMartin, 10);
		
		return map;
	}
}
