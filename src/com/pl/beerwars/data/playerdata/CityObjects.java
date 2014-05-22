package com.pl.beerwars.data.playerdata;
import com.pl.beerwars.data.*;
import com.pl.beerwars.data.Constants.*;
import com.pl.beerwars.data.map.City;
import com.pl.beerwars.data.beer.*;
import java.util.*;

public class CityObjects {
	
	public City cityRef;

	public StorageSize storageSize;
	public FactorySize factorySize;
	
	public Integer factoryUnits;
	public HashMap<BeerSort, Integer> storage;
	public HashMap<BeerSort, Integer> factory;
	public HashMap<BeerSort, Float> prices;
	public List<FactoryChange> factoryUnitsExtensions;
	
	public int storageBuildRemaining = 0;
	public int factoryBuildRemaining = 0;
	
	public CityObjects(City cityRef, StorageSize storageSize, FactorySize factorySize) {
		this.cityRef = cityRef;
		this.storageSize = storageSize;
		this.factorySize = factorySize;
		
		this.storage = new HashMap<BeerSort, Integer>();
		this.factory = new HashMap<BeerSort, Integer>();
		this.prices = new HashMap<BeerSort, Float>();
		this.factoryUnitsExtensions = new LinkedList<FactoryChange>();
		this.factoryUnits = 0;
	}
	
	public int getTotalStorage(){
		int s = 0;
		for (int stored : storage.values()){
			s += stored;
		}
		return s;
	}
	
	public int getStorageMax(){
		return Constants.StorageVolume(storageSize);
	}
	
	public int getFactoryMax(){
		return Constants.FactoryVolume(factorySize);
	}

	public int getOperatingUnitsCount(){
		int s = 0;
		for (Integer sortUnits : factory.values()){
			s += sortUnits;
		}
		return s;
	}
	
	public int getConstructedCount(){
		int s = 0;
		for (FactoryChange fc : factoryUnitsExtensions){
			if (fc.unitsCount > 0)
				s += fc.unitsCount;
		}
		return s;
	}
	
	public int getDestructedCount(){
		int s = 0;
		for (FactoryChange fc : factoryUnitsExtensions){
			if (fc.unitsCount < 0)
				s += -fc.unitsCount;
		}
		return s;
	}
	
	public ProductionResult generateProduction(){
		int stor = getTotalStorage();
		int maxS = getStorageMax();
		ProductionResult result = new ProductionResult();
		for (BeerSort sort : factory.keySet()){
			int produced = factory.get(sort) * Constants.Economics.unitSize;
			result.costs += produced * sort.selfprice;
			result.produced.put(sort, produced);
			if (stor + produced > maxS){
				result.dropped.put(sort, produced - (maxS - stor));
				produced = maxS - stor;
			}
			if (storage.containsKey(sort))
				storage.put(sort, storage.get(sort) + produced);
			else
				storage.put(sort, produced);
			stor += produced;
		}
		return result;
	}
	
	public int calculateCityCosts(){
		float price = 0;

		price += factoryUnits * Constants.Economics.unitIdleCost;

		price += Constants.FactorySupportCost(factorySize);
		price += Constants.StorageSupportCost(storageSize);
		return (int)price;
	}
	
	public int getPosibleUnitsExtension(){
		return getFactoryMax() - (factoryUnits + getConstructedCount());
	}
	
	public void updateBeerItems(PlayerData player){
		if (storageSize != StorageSize.None){
			for (BeerSort sort : player.ownedSorts){
				if (!storage.containsKey(sort))
					storage.put(sort, 0);
			}
		}
		if (factorySize != FactorySize.None){
			for (BeerSort sort : player.ownedSorts){
				if (!factory.containsKey(sort))
					factory.put(sort, 0);
			}
		}
	}
}
