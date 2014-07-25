package com.pl.beerwars.data;
import com.pl.beerwars.*;
import android.annotation.SuppressLint;
import com.pl.beerwars.data.Constants.FactorySize;
import com.pl.beerwars.data.Constants.StorageSize;
import com.pl.beerwars.data.beer.BeerSort;
import com.pl.beerwars.data.consumption.ConsumptionModel;
import com.pl.beerwars.data.facade.*;
import java.util.*;

import com.pl.beerwars.data.map.City;
import com.pl.beerwars.data.playerdata.CityObjects;
import com.pl.beerwars.data.playerdata.PlayerData;
import com.pl.beerwars.data.transport.*;
import com.pl.beerwars.data.playerdata.*;

public class Game
{
	public Date date;
	public int turnNum;

	public com.pl.beerwars.data.map.Map map;
	//private Random rnd = new Random();

	public PlayerData getViewForPlayerId(int playerId)
	{
		return players.get(playerId);
	}

	@SuppressLint("UseSparseArrays")
	public HashMap<Integer, PlayerData> players = new HashMap<Integer, PlayerData>();

	public void start()
	{
		buildFacades();
		updateFacades();
	}

	private void buildFacades()
	{
		for (PlayerData player : players.values())
		{
			GameFacade gv = new GameFacade(this);

			gv.cities = new CityFacade[map.cities.length];
			for (int i = 0; i < map.cities.length; i++)
			{
				gv.cities[i] = new CityFacade(map.cities[i]);
				gv.cities[i].estConsumption = Constants.ValueUnknown;

				gv.cities[i].transportPrices = getTrPrices(gv.cities[i].getId());

				gv.cities[i].others = new HashMap<Integer, PlayerCityFacade>();
				for (PlayerData other : players.values())
				{
					if (other.id == player.id) continue;
					gv.cities[i].others.put(other.id, new PlayerCityFacade(other.name));
				}
			}

			player.game = gv;
		}
	}

	private void updateFacades()
	{
		for (PlayerData player : players.values())
		{
			player.game.date = date;
			player.game.turnNum = turnNum;

			for (int i = 0; i < player.cityObjects.length; i++)
			{
				CityObjects cobj = player.cityObjects[i];
				for (PlayerData op : players.values())
				{
					if (op.id == player.id) continue;
					PlayerCityFacade pcf = op.game.cities[i].others.get(player.id);
					pcf.factorySize = cobj.factorySize;
					pcf.storageSize = cobj.storageSize;
				}
			}
		}
	}

	private TransportPrice[] getTrPrices(String fromCity)
	{
		int thisI = map.getCityIndex(fromCity);
		TransportPrice[] result = new TransportPrice[map.cities.length - 1];

		int n = 0;
		for (int i = 0; i < map.cities.length; i++)
		{
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

	public void makeTurn(TurnMessageCallback callback)
	{
		try
		{
			processNTpreconditions(callback);
			callback.display(R.string.game_nt_consumptionCalculations, null);
			processConsumption(callback);
			callback.display(R.string.game_nt_transportCalculations, null);
			Thread.sleep(1000);
			callback.display(R.string.game_nt_productionCalculations, null);
			processProduction(callback);

			callback.display(R.string.game_nt_supportCalculations, null);
			processSupportCosts();

			callback.display(R.string.game_nt_constructionCalculations, null);
			processConstruction(callback);

			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DAY_OF_YEAR, 7);
			date = c.getTime();
			turnNum++;

			updateFacades();
		}
		catch (InterruptedException ex)
		{}
		finally
		{
			callback.complete();
		}
	}

	private void processNTpreconditions(TurnMessageCallback callback)
	{
		for (PlayerData p : players.values())
		{
			p.bankrupt = false;
			if (p.money < 0)
			{
				p.bankrupt = true;
				callback.display(R.string.game_nt_bankrupt, new String[] { p.name });
			}
		}
	}

	private void processSupportCosts()
	{
		for (PlayerData p : players.values())
		{
			for (CityObjects co : p.cityObjects)
			{
				p.money -= co.calculateCityCosts();
			}
		}		
	}

	private void processProduction(TurnMessageCallback callback)
	{
		for (PlayerData p : players.values())
		{
			if (p.bankrupt) continue;

			for (CityObjects co : p.cityObjects)
			{
				ProductionResult prod = co.generateProduction();
				if (p.intellect_id == Constants.IntellectId.Human)
					reportProduction(callback, co.cityRef, prod);
				p.money -= prod.costs;
			}
		}
	}	

	private void reportProduction(TurnMessageCallback callback, City c, ProductionResult p)
	{
		if (p.produced.size() == 0)
			return;

		callback.displayCity(R.string.game_nt_producedCity, c.id);
		for (BeerSort sort : p.produced.keySet())
		{
			callback.display(R.string.game_nt_producedSort,
							 new String[] { "" + p.produced.get(sort), sort.name });
			if (p.dropped.containsKey(sort))
			{
				callback.display(R.string.game_nt_producedLost,
								 new String[] { "" + p.dropped.get(sort), sort.name });
			}
		}
	}

	private void processConsumption(TurnMessageCallback callback)
	{
		ConsumptionModel model = new ConsumptionModel();

		for (int i = 0; i<map.cities.length; i++)
		{
			City c = map.cities[i];
			HashMap<BeerSort, Integer> consumed = model.calculateConsumption(this, c);

			for (PlayerData p : players.values())
			{
				CityObjects co = p.cityObjects[i];
				
				HashMap<BeerSort, Integer> cityC = new HashMap<BeerSort, Integer>();
				for (BeerSort sort : co.factory.keySet())
				{
					if (!consumed.containsKey(sort)) continue;
					int cons = consumed.get(sort);
					cityC.put(sort, cons);
					co.storage.put(sort, co.storage.get(sort) - cons);
					p.money += cons * co.prices.get(sort);
				}

				co.consumptionHistory.put(turnNum, cityC);

				if (p.intellect_id == Constants.IntellectId.Human)
					reportConsumption(callback, co, cityC);
			}
		}
	}

	private void reportConsumption(TurnMessageCallback callback, CityObjects co, HashMap<BeerSort, Integer> consumed)
	{
		if (consumed.size() == 0)
			return;

		callback.displayCity(R.string.game_nt_consumedCity, co.cityRef.id);
		
		HashMap<BeerSort, Integer> prev = 
			co.consumptionHistory.containsKey(turnNum - 1)
				? co.consumptionHistory.get(turnNum-1)
				: null;
		for (BeerSort sort : consumed.keySet())
		{
			int c = consumed.get(sort);
			String text = "" + c;
			if (prev != null && prev.containsKey(sort)){
				int p = prev.get(sort);
				callback.displayConsumption(co.cityRef.id, sort.name, c, prev.get(sort));
			} else{
				callback.displayConsumption(co.cityRef.id, sort.name, c, 0);
			}
		}
	}

	private void processConstruction(TurnMessageCallback callback)
	{
		for (PlayerData p : players.values())
		{
			for (CityObjects co : p.cityObjects)
			{
				if (co.storageBuildRemaining > 0)
				{
					co.storageBuildRemaining -= 1;
					if (co.storageBuildRemaining == 0)
					{
						co.storageSize = Constants.StorageNextSize(co.storageSize);
						if (p.intellect_id == Constants.IntellectId.Human)
							callback.displayStorageBuilt(co.cityRef.id, co.storageSize);
					}
				}

				if (co.factoryBuildRemaining > 0)
				{
					co.factoryBuildRemaining -= 1;
					if (co.factoryBuildRemaining == 0)
					{
						co.factorySize = Constants.FactoryNextSize(co.factorySize);
						if (p.intellect_id == Constants.IntellectId.Human)
							callback.displayFactoryBuilt(co.cityRef.id, co.factorySize);
					}
				}

				int newUnits = 0;
				int i = 0;
				while (i < co.factoryUnitsExtensions.size()){
					FactoryChange ext = co.factoryUnitsExtensions.get(i);
					ext.weeksLeft -= 1;
					if (ext.weeksLeft == 0){
						newUnits += ext.unitsCount;
						co.factoryUnitsExtensions.remove(i);
					} else {
						i++;
					}
				}
				if (newUnits > 0){
					co.factoryUnits += newUnits;
					if (p.intellect_id == Constants.IntellectId.Human)
						callback.displayUnitsExtension(co.cityRef.id, newUnits);
				}
				
				co.updateBeerItems(p);
			}
		}
	}

	public interface TurnMessageCallback
	{
		void displayCity(int resId, String cityId);
		void display(int resId, Object[] parameters);
		void displayConsumption(String cityId, String sortName, int consumed, int previous);
		void complete();

		void displayStorageBuilt(String cityId, StorageSize newSize);
		void displayFactoryBuilt(String cityId, FactorySize newSize);
		void displayUnitsExtension(String cityId, int built);
	}
}
