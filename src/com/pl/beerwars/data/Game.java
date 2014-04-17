package com.pl.beerwars.data;
import com.pl.beerwars.data.map.*;
import com.pl.beerwars.data.facade.*;
import java.util.*;
import com.pl.beerwars.data.transport.*;

public class Game
{
	public com.pl.beerwars.data.map.Map map;
	
	private HashMap<Integer,GameFacade> facades = new HashMap<Integer,GameFacade>();
	public GameFacade getViewForPlayer(int playerNum){
		return facades.get(playerNum);
	}
	
	private void buildFacades(){

		GameFacade gv = new GameFacade();

		gv.cities = new CityFacade[map.cities.length];
		for (int i = 0; i < map.cities.length; i++){
			gv.cities[i] = new CityFacade(map.cities[i]);
			gv.cities[i].estConsumption = Constants.ValueUnknown;
			
			gv.cities[i].transportPrices = getTrPrices(gv.cities[i].getId());
		}

		facades.put(Constants.Players.MainHuman, gv);
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
	
	public void start(){
		buildFacades();
	}
}
