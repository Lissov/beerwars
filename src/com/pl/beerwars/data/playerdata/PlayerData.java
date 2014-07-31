package com.pl.beerwars.data.playerdata;
import com.pl.beerwars.data.Constants;
import java.util.LinkedList;
import java.util.List;
import com.pl.beerwars.data.*;
import com.pl.beerwars.data.beer.BeerSort;
import com.pl.beerwars.data.facade.GameFacade;
import com.pl.beerwars.ArtIntelligence.*;

public class PlayerData {
	
	public int id;
	public int intellect_id;
	
	public String name;
	
	public int money;
	
	public List<BeerSort> ownedSorts;
	
	public CityObjects[] cityObjects;
	
	public List<TransportOrder> recurringOrders;
	public List<TransportOrder> oneTimeOrders;
	
	public GameFacade game;
	
	public boolean bankrupt;
	
	public IPlayer artIntelligence;

	
	public PlayerData(int intellect_id, String name) {
		super();
		this.intellect_id = intellect_id;
		this.name = name;
		
		ownedSorts = new LinkedList<BeerSort>();
		recurringOrders = new LinkedList<TransportOrder>();
		oneTimeOrders = new LinkedList<TransportOrder>();
	}
	
	public BeerSort getSort(int id){
		for (BeerSort sort: ownedSorts){
			if (sort.id == id)
				return sort;
		}
		return null;
	}
	

	public boolean expandUnits(String cityId, int count){

		int price = count * Constants.Economics.unitBuildCost;
		if (money < price)
			return false;

		CityObjects cobj = cityObjects[game.getCityIndex(cityId)];
		if (count > cobj.getPosibleUnitsExtension())
			return false;
		
		money -= price;
		cobj.factoryUnitsExtensions.add(new FactoryChange(
			count, Constants.Economics.unitBuildTime
		));
		
		return true;
	}
	
	public boolean expandStorage(String cityId, Constants.StorageSize newSize)
	{
		int price = Constants.StorageBuildPrice(newSize);
		if (money < price)
			return false;
		
		CityObjects cobj = cityObjects[game.getCityIndex(cityId)];
		if (Constants.StorageNextSize(cobj.storageSize) != newSize)
			return false;
			
		money -= price;
		cobj.storageBuildRemaining = Constants.StorageBuildingTime(newSize);
		return true;
	}
	
	public boolean expandFactory(String cityId, Constants.FactorySize newSize)
	{
		int price = Constants.FactoryBuildPrice(newSize);
		if (money < price)
			return false;

		CityObjects cobj = cityObjects[game.getCityIndex(cityId)];
		if (Constants.FactoryNextSize(cobj.factorySize) != newSize)
			return false;

		money -= price;
		cobj.factoryBuildRemaining = Constants.FactoryBuildingTime(newSize);
		return true;
	}
	
	public int calculateTotalSold(){
		
		int total = 0;
		
		for (CityObjects cobj : cityObjects){
			if (!cobj.consumptionHistory.containsKey(game.turnNum-1))
				continue;
			for (int sold : cobj.consumptionHistory.get(game.turnNum - 1).values()){
				total += sold;
			}
		}
		
		return total;
	}
	
	public int calculateTotalProduction(){

		int total = 0;

		for (CityObjects cobj : cityObjects){
			total += cobj.getOperatingUnitsCount();
		}

		return total * Constants.Economics.unitSize;
	}
	
	public int calculateMarketValue(){
		int total = money;
		
		for (CityObjects cobj : cityObjects){
			total += Constants.FactorySellPrice(cobj.factorySize);
			
			total += Constants.StorageSellPrice(cobj.storageSize);
			for (BeerSort sort : cobj.storage.keySet()){
				total += getSort(sort.id).selfprice * cobj.storage.get(sort);			
			}
		}
		
		return total;
	}
}
