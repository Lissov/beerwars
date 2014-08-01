package com.pl.beerwars.ArtIntelligence.Normal;
import com.pl.beerwars.ArtIntelligence.*;
import com.pl.beerwars.data.*;
import com.pl.beerwars.data.Constants.FactorySize;
import com.pl.beerwars.data.beer.*;
import com.pl.beerwars.data.playerdata.*;
import java.util.*;

public class NormalPlayer implements IPlayer
{
	public float riskLevel = 0.9f;
	
	private final float factBuildRiskLevel = 0.8f;
	private final float TargetMinPriceK = 1.1f;
	private final float IncreaseProdDemand = 1.1f;
	private final float ExpandFactDemand = 1.2f;
		
	private NormalState state = new NormalState();
	PlayerHelper helper = new PlayerHelper();
	
	public void makeTurn(PlayerData data, Game.TurnMessageCallback callback){
		int baseexp = helper.calculateBaseExpenses(data);
		int totalProd = data.calculateTotalProduction();
		float sp = 1;
		if (totalProd > 0){
			sp = (float)baseexp / (float)totalProd;
		}
		
		int budget = (int)(riskLevel * data.money);
		
		for (CityObjects cobj : data.cityObjects) {
			for (BeerSort sort : cobj.prices.keySet()){
				float eqPr = sort.selfprice + sp;
				float refPr = eqPr * TargetMinPriceK;
				
				BeerPriceMove bpm = calcNewBeerPrice(sort, cobj, data, eqPr, refPr, callback);
				
				cobj.prices.put(sort, bpm.newPrice);
				
				//extend a factory?
				budget = makeFactoryChange(bpm, sort, cobj, data, budget, callback);
			}
		}
	}
	
	private int makeFactoryChange(BeerPriceMove bpm, BeerSort sort, CityObjects cobj, PlayerData data, int budget, Game.TurnMessageCallback callback){
		if (bpm.allSold){
			int freeItemsCnt = cobj.factoryUnits - cobj.getOperatingUnitsCount();
			if (freeItemsCnt > 0){
				if (bpm.priceMult > IncreaseProdDemand){
					int current = cobj.factory.get(sort);
					int itemsToIncrease = (int) (((bpm.priceMult / IncreaseProdDemand) - 1f) * current);
					int canSupport = (int)Math.floor(budget / sort.selfprice);
					if (itemsToIncrease == 0)
						itemsToIncrease = 1;
					if (itemsToIncrease > freeItemsCnt)
						itemsToIncrease = freeItemsCnt;
					if (itemsToIncrease > canSupport)
						itemsToIncrease = canSupport;
					
					budget -= itemsToIncrease * sort.selfprice;
					if (itemsToIncrease > 0){
						cobj.factory.put(sort, current + itemsToIncrease);
						callback.displayDebugMessage(cobj.cityRef.id + ": increasing production of " + sort.name + " to " + (current + itemsToIncrease));
					}
				}
			} else {
				if (bpm.priceMult > ExpandFactDemand){
					int current = cobj.factory.get(sort);
					int itemsToIncrease = (int) (((bpm.priceMult / ExpandFactDemand) - 1f) * current);
					if (itemsToIncrease == 0)
						itemsToIncrease = 1;
					
					int currentIncr = cobj.getConstructedCount();
					if (itemsToIncrease > currentIncr){
						int haveMoney = (int)Math.floor(budget / Constants.Economics.unitBuildCost);
						int haveSpace = cobj.getFactoryMax() - cobj.factoryUnits;
						if (haveSpace > 0){
							int ii = itemsToIncrease - currentIncr;
							if (ii > haveMoney)
								ii = haveMoney;
							if (ii > haveSpace)
								ii = haveSpace;
							if (ii > 0){
								data.expandUnits(cobj.cityRef.id, ii);
								budget -= ii * Constants.Economics.unitBuildCost;
								callback.displayDebugMessage(cobj.cityRef.id + ": expanding factory, adding " + ii);
							}
						} else {
							if (cobj.factoryBuildRemaining == 0) {
								FactorySize next = Constants.FactoryNextSize(cobj.factorySize);
								int price = Constants.FactoryBuildPrice(next);
								if (next != FactorySize.None && price < factBuildRiskLevel * data.money){
									budget -= price;
									data.expandFactory(cobj.cityRef.id, next);
									callback.displayDebugMessage(cobj.cityRef.id + ": Expanding factory to " + next);
								}
							}
						}
					}
				}
			}
		}
		
		return budget;
	}

	private float getPreviousPriceStep(String cityId, int beerId){
		if (state.priceSteps.containsKey(cityId)
			&& state.priceSteps.get(cityId).containsKey(beerId))
			{
				return state.priceSteps.get(cityId).get(beerId);
			}
			else {
				return 0.05f;
			}
	}

	private class BeerPriceMove {
		private float newPrice;
		private boolean allSold;
		private float priceMult;
		public BeerPriceMove(float newprice, boolean allsold, float pricemult){
			newPrice = newprice;
			allSold = allsold;
			priceMult = pricemult;
		}
	}
	private BeerPriceMove calcNewBeerPrice(BeerSort sort, CityObjects cobj, PlayerData data,
			float eqPr, float refPr, Game.TurnMessageCallback callback) {
				
		BeerPriceMove result;
		
		float pr = cobj.prices.get(sort);
		float pps = getPreviousPriceStep(cobj.cityRef.id, sort.id);
		
		int coming = cobj.factory.containsKey(sort) 
			? cobj.factory.get(sort) * Constants.Economics.unitSize
			: 0;
		coming += 0;// transport
		
		int sold = cobj.consumptionHistory.containsKey(data.game.turnNum-1)
					? cobj.consumptionHistory.get(data.game.turnNum-1).containsKey(sort)
						? cobj.consumptionHistory.get(data.game.turnNum-1).get(sort)
						: -1
					: -1;

		if (sold == -1) {
			float newPrice = helper.roundPrice(refPr * 1.1f);
			result = new BeerPriceMove(newPrice, false, newPrice / eqPr);
			if (cobj.storage.containsKey(sort) && cobj.storage.get(sort) > 0){
				callback.displayDebugMessage(cobj.cityRef.id + ": initial price for " + sort.name + " set to " + newPrice);
			}
		}
		else {
			float dir;
			String act = "";
			if (coming <= sold){
				dir = 1;
				act = "increasing";
			} else {
				dir = -1;
				act = "decreasing";						
			}
				
			float nps = (pps * dir > 0 ? pps * 1.5f : -pps / 3);
			if (nps >= 0 && nps < 0.01f) 
				nps = 0.01f;
			if (nps <= 0 && nps > -0.01f) 
				nps = -0.01f;
			
			// make smooth decrease near the minimum economic valuable price
			float newPrice = pr + nps;
			if (pr > refPr && newPrice < refPr) {
				newPrice = refPr;
				nps = -0.01f;
			}
			if (pr > eqPr && newPrice < eqPr) {
				newPrice = eqPr;
				nps = -0.01f;
			}
			if (newPrice < sort.selfprice) {
				newPrice = sort.selfprice;
				nps = -0.01f;
			}
			storePreviousPriceStep(cobj.cityRef.id, sort.id, nps);
			newPrice = helper.roundPrice( newPrice );
			
			result = new BeerPriceMove(newPrice, coming <= sold, newPrice / eqPr);
			
			callback.displayDebugMessage(cobj.cityRef.id + ": " + act + " price for " + sort.name + " to " + newPrice);
		}
		
		return result;
	}
	
	private void storePreviousPriceStep(String cityId, int beerId, float price){
		if (!state.priceSteps.containsKey(cityId))
			state.priceSteps.put(cityId, new HashMap<Integer, Float>());
			
		state.priceSteps.get(cityId).put(beerId, price);
	}
	
	public void setState(String stateStr){
		state.deserialize(stateStr);
	}
	
	public String getState(){
		return state.serialize();
	}
}
