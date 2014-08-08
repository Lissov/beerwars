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
			
			int j = 0;
			gv.otherPlayerData = new OtherPlayerStats[players.size()];
			for (PlayerData other : players.values()){ // others inkl this
				gv.otherPlayerData[j] = new OtherPlayerStats();
				gv.otherPlayerData[j].playerId = other.id;
				gv.otherPlayerData[j].playerName = other.name;
				j++;
			}

			player.game = gv;
		}
	}

	private void updateFacades()
	{
		int[] consumedAvgByCities = getConsumedAverages();
		
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
		
		for (PlayerData player : players.values()){
			// TODO: this is moved here so that player has info about the latest turn of opponents. 
			// Think - maybe delay in one or more turns is desired?  
			setFacadeIntels(player, player.game, consumedAvgByCities);
		}
	}
	
	private int[] getConsumedAverages(){
		int[] consumedC = new int[map.cities.length];
		
		int mt = turnNum >= 4 ? 4 : turnNum;
		for (int cityn = 0; cityn < map.cities.length; cityn++){
			int[] consumed = new int[4];
			boolean any = false;
			for (PlayerData p : players.values()){
				for (int tn = 1; tn <= mt; tn++)
				{
					int t = turnNum - tn;
					
					if (!p.cityObjects[cityn].consumptionHistory.containsKey(t))
						continue;
					HashMap<BeerSort, Integer> ch = p.cityObjects[cityn].consumptionHistory.get(t);

					if (ch.values().size() == 0)
						continue;
					
					any = true;
					for (int cons : ch.values()){
						consumed[tn-1] += cons;
					}
				}
			}
			
			if (!any)
				consumedC[cityn] = -1;
			else{
				int total = 0;
				for (int c : consumed)
					total += c;
				consumedC[cityn] = total / mt;
			}
		}
		
		return consumedC;
	}
	
	private void setFacadeIntels(PlayerData player, GameFacade pfacade, int[] consAverages){
		// TODO: design how known consumption depends on intelligence level
		for (int cind = 0; cind < map.cities.length; cind++){
			int c = consAverages[cind];
			pfacade.cities[cind].estConsumption = c >= 0 ? c : Constants.ValueUnknown;
		}
		
		//TODO: design estimates of other players statistics
		for (PlayerData other : players.values()){
			OtherPlayerStats ps = player.game.getStatsForPlayer(other.id);
			ps.totalSold = other.calculateTotalSold();
			ps.totalProduction = other.calculateTotalProduction();
			ps.capital = other.calculateMarketValue();
			ps.sortsOwnedCnt = other.ownedSorts.size();
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
			result[n].pricePack = (int)(Constants.Economics.transportReload
				+ map.distances[thisI][i] * Constants.Economics.transportPerKm);

			n++;
		}

		return result;
	}

	public void makeTurn(TurnMessageCallback callback)
	{
		try
		{
			for (PlayerData player : players.values()) {
				callback.displayProcessingPlayerTurn(player.name);
				if (player.artIntelligence != null)
					player.artIntelligence.makeTurn(player, callback);
			}
			
			processNTpreconditions(callback);
			callback.display(R.string.game_nt_transportCalculations, null);
			processTransportSend(callback);
			callback.display(R.string.game_nt_consumptionCalculations, null);
			processConsumption(callback);
			callback.display(R.string.game_nt_transportCalculations, null);
			processTransportReceive(callback);
			
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
		finally
		{
			callback.complete();
		}
	}

	private HashMap<PlayerData, List<TransportOrder>> executed;
	private void processTransportSend(TurnMessageCallback callback)
	{
		executed = new HashMap<PlayerData, List<TransportOrder>>();
		for (PlayerData p : players.values())
		{
			executed.put(p, new LinkedList<TransportOrder>());
			if (p.bankrupt) continue;
			
			executeOrders(p, p.recurringOrders, callback);
			executeOrders(p, p.oneTimeOrders, callback);
			
			p.oneTimeOrders.clear();
		}
	}
	
	private void executeOrders(PlayerData player, List<TransportOrder> orders, TurnMessageCallback callback){
		for (TransportOrder order : orders){
			CityObjects cf = player.cityObjects[player.game.getCityIndex(order.fromCity.id)];
			CityObjects ct = player.cityObjects[player.game.getCityIndex(order.toCity.id)];
			int ordered = order.packageQuantity * Constants.Economics.packSize;
			if (cf.storage.get(order.sort) <= ordered){
				if (player.intellect_id == Constants.IntellectId.Human){
					callback.displayCantSend(cf.cityRef.id, ct.cityRef.id, order.sort.name, order.packageQuantity);
				}
				continue;
			} else {
				executed.get(player).add(order);
				callback.displaySent(cf.cityRef.id, ct.cityRef.id, order.sort.name, order.packageQuantity);
			}
		}		
	}
	
	private void processTransportReceive(TurnMessageCallback callback)
	{
		for (PlayerData p : players.values())
		{
			for (TransportOrder order : executed.get(p)) {
				CityObjects co = p.cityObjects[p.game.getCityIndex(order.toCity.id)];
				int ordered = order.packageQuantity * Constants.Economics.packSize;
				
				int free = co.getStorageMax() - co.getTotalStorage();
				int stored = co.storage.containsKey(order.sort) 
								? co.storage.get(order.sort)
								: 0;
				if (ordered < free){
					co.storage.put(order.sort, stored + ordered);
				} else {
					co.storage.put(order.sort, stored + free);
					callback.displayReceivedDropped(co.cityRef.id, order.sort.name, ordered - free);
				}
			}
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

				//if (p.intellect_id == Constants.IntellectId.Human)
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
		void displayCantSend(String cityFrom, String cityTo, String sortName, int packs);
		void displaySent(String cityFrom, String cityTo, String sortName, int packs);
		void displayReceivedDropped(String cityId, String sortName, int bottles);
		
		void displayProcessingPlayerTurn(String playerName);
		
		void displayDebugMessage(String message);
	}
}
