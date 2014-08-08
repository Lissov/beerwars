package com.pl.beerwars.ArtIntelligence;
import com.pl.beerwars.data.playerdata.*;
import com.pl.beerwars.data.*;
import com.pl.beerwars.data.beer.*;

public class PlayerHelper
{
	public int calculateBaseExpenses(PlayerData player){
		int total = 0;
		for (CityObjects co : player.cityObjects){
			total += Constants.StorageSupportCost(co.storageSize)
				+ Constants.FactorySupportCost(co.factorySize);
				
			total += (co.factoryUnits - co.getOperatingUnitsCount()) 
				* Constants.Economics.unitIdleCost;
		}
		
		return total;
	}
	
	public float roundPrice(float price){
		return Math.round(price * 100f) / 100f;
	}
	
	public float getSupportPricePerBottle(PlayerData data){
		int baseexp = calculateBaseExpenses(data);
		int totalProd = data.calculateTotalProduction();
		float sp = 1;
		if (totalProd > 0){
			sp = (float)baseexp / (float)totalProd;
		}
		return sp;
	}
	
	public int getPrevSold(CityObjects cobj, PlayerData data, BeerSort sort){
		return cobj.consumptionHistory.containsKey(data.game.turnNum-1)
			? cobj.consumptionHistory.get(data.game.turnNum-1).containsKey(sort)
				? cobj.consumptionHistory.get(data.game.turnNum-1).get(sort)
				: -1
			: -1;
	}
	
	public BeerPriceMove calcNewAdjustedPrice(BeerSort sort, CityObjects cobj, PlayerData data,
										   float eqPr, float refPr, float prevPriceStep,
										   Game.TurnMessageCallback callback) {

		BeerPriceMove result;

		float pr = cobj.prices.get(sort);
		float pps = prevPriceStep;

		int coming = cobj.factory.containsKey(sort) 
			? cobj.factory.get(sort) * Constants.Economics.unitSize
			: 0;
		coming += 0;// transport

		int sold = getPrevSold(cobj, data, sort);

		if (sold == -1) {
			float newPrice = roundPrice(refPr * 1.1f);
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
			
			newPrice = roundPrice( newPrice );

			result = new BeerPriceMove(newPrice, coming <= sold, newPrice / eqPr);
			result.newPriceStep = nps;

			callback.displayDebugMessage(cobj.cityRef.id + ": " + act + " price for " + sort.name + " to " + newPrice);
		}

		return result;
	}

	public class BeerPriceMove {
		public float newPrice;
		public boolean allSold;
		public float priceMult;
		public float newPriceStep;
		public BeerPriceMove(float newprice, boolean allsold, float pricemult){
			newPrice = newprice;
			allSold = allsold;
			priceMult = pricemult;
			newPriceStep = 10000;
		}
	}
}
