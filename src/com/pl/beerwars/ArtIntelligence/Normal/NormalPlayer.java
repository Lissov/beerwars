package com.pl.beerwars.ArtIntelligence.Normal;
import com.pl.beerwars.ArtIntelligence.*;
import com.pl.beerwars.data.*;
import com.pl.beerwars.data.Constants.FactorySize;
import com.pl.beerwars.data.beer.*;
import com.pl.beerwars.data.playerdata.*;
import java.util.*;

public class NormalPlayer extends BasePlayer<NormalState>
{
	public float riskLevel = 0.9f;

	@Override
	protected NormalState initState()
	{
		return new NormalState();
	}

	private final float factBuildRiskLevel = 0.8f;
	private final float TargetMinPriceK = 1.1f;
	private final float IncreaseProdDemand = 1.1f;
	private final float ExpandFactDemand = 1.2f;

	public void makeTurn(PlayerData data, Game.TurnMessageCallback callback){
		float sp = helper.getSupportPricePerBottle(data);
		
		int budget = (int)(riskLevel * data.money);
		
		for (CityObjects cobj : data.cityObjects) {
			for (BeerSort sort : cobj.prices.keySet()){
				float eqPr = sort.selfprice + sp;
				float refPr = eqPr * TargetMinPriceK;
				
				PlayerHelper.BeerPriceMove bpm = calcNewBeerPrice(sort, cobj, data, eqPr, refPr, callback);
				
				cobj.prices.put(sort, bpm.newPrice);
				
				//extend a factory?
				budget = makeFactoryChange(bpm, sort, cobj, data, budget, callback);
			}
		}
	}
	
	private int makeFactoryChange(PlayerHelper.BeerPriceMove bpm, BeerSort sort, CityObjects cobj, PlayerData data, int budget, Game.TurnMessageCallback callback){
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
						int haveSpace = cobj.getFactoryMax() - cobj.factoryUnits - currentIncr;
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

	private PlayerHelper.BeerPriceMove calcNewBeerPrice(BeerSort sort, CityObjects cobj, PlayerData data,
			float eqPr, float refPr, Game.TurnMessageCallback callback) {
				
		PlayerHelper.BeerPriceMove result = helper.calcNewAdjustedPrice(sort, cobj, data, 
							eqPr, refPr, getPreviousPriceStep(cobj.cityRef.id, sort.id), callback);
							
		
		if (result.newPriceStep < 10000){
			storePreviousPriceStep(cobj.cityRef.id, sort.id, result.newPriceStep);
		}
		
		return result;
	}
	
	private void storePreviousPriceStep(String cityId, int beerId, float price){
		if (!state.priceSteps.containsKey(cityId))
			state.priceSteps.put(cityId, new HashMap<Integer, Float>());
			
		state.priceSteps.get(cityId).put(beerId, price);
	}
}
