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
	
	public HashMap<BeerSort, Integer> storage;
	public HashMap<BeerSort, FactoryProduction> factory;
	public HashMap<BeerSort, Float> prices;
	
	public int storageBuildRemaining = 0;
	public int factoryBuildRemaining = 0;
	
	public CityObjects(City cityRef, StorageSize storageSize, FactorySize factorySize) {
		this.cityRef = cityRef;
		this.storageSize = storageSize;
		this.factorySize = factorySize;
		
		this.storage = new HashMap<BeerSort, Integer>();
		this.factory = new HashMap<BeerSort, FactoryProduction>();
		this.prices = new HashMap<BeerSort, Float>();
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
	
	public int getUnitsCount(boolean onlyOperating){
		int s = 0;
		for (FactoryProduction sortUnits : factory.values()){
			s += onlyOperating ? sortUnits.workingUnits : sortUnits.totalUnits;
		}
		return s;
	}
	
	public ProductionResult generateProduction(){
		int stor = getTotalStorage();
		int maxS = getStorageMax();
		ProductionResult result = new ProductionResult();
		for (BeerSort sort : factory.keySet()){
			int produced = factory.get(sort).workingUnits * Constants.Economics.unitSize;
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
		for (FactoryProduction sortUnits : factory.values()){
			price += sortUnits.totalUnits * Constants.Economics.unitIdleCost;
		}
		price += Constants.FactorySupportCost(factorySize);
		price += Constants.StorageSupportCost(storageSize);
		return (int)price;
	}
	
	public class FactoryProduction{
		public int totalUnits;
		public int workingUnits;
	}
}
