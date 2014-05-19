package com.pl.beerwars.data.playerdata;
import com.pl.beerwars.data.Constants;
import java.util.LinkedList;
import java.util.List;
import com.pl.beerwars.data.*;
import com.pl.beerwars.data.beer.BeerSort;
import com.pl.beerwars.data.facade.GameFacade;

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
}
