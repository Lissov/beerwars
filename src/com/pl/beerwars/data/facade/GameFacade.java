package com.pl.beerwars.data.facade;

import java.util.Date;

import com.pl.beerwars.data.Game;

public class GameFacade
{
	private Game _game;
	public GameFacade(Game game){
		_game = game;
	}
	
	public Date date;
	
	public int turnNum;
	
	public CityFacade[] cities;

	public CityFacade getCity(String cityId){
		for (CityFacade c : cities){
			if (c.getId() == cityId)
				return c;
		}

		return null;
	}
	
	public int getCityIndex(String id){
		return _game.map.getCityIndex(id);
	}
	
	public OtherPlayerStats[] otherPlayerData;
	public OtherPlayerStats getStatsForPlayer(int playerId){
		for (OtherPlayerStats d : otherPlayerData){
			if (d.playerId == playerId)
				return d;
		}
		
		return null;
	}
}
