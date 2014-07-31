package com.pl.beerwars.ArtIntelligence.Normal;
import com.pl.beerwars.ArtIntelligence.*;
import com.pl.beerwars.data.*;
import com.pl.beerwars.data.beer.*;
import com.pl.beerwars.data.playerdata.*;
import java.util.*;

public class NormalPlayer implements IPlayer
{
	private NormalState state = new NormalState();
	PlayerHelper helper = new PlayerHelper();
	
	public void makeTurn(PlayerData data, Game.TurnMessageCallback callback){
		int baseexp = helper.calculateBaseExpenses(data);
		int totalProd = data.calculateTotalProduction();
		float sp = 1;
		if (totalProd > 0){
			sp = (float)baseexp / (float)totalProd;
		}
		
		for (CityObjects cobj : data.cityObjects){
			for (BeerSort sort : cobj.prices.keySet()){
				float eqPr = sort.selfprice + sp;
				float refPr = eqPr * 1.1f;
				
				float pr = cobj.prices.get(sort);
				float pps = getPreviousPriceStep(cobj.cityRef.id, sort.id);
				
				int coming = cobj.factory.containsKey(sort) 
					? cobj.factory.get(sort) * Constants.Economics.unitSize
					: 0;
				coming += 0;// transport
				
				int sold = cobj.consumptionHistory.containsKey(data.game.turnNum-1)
							? cobj.consumptionHistory.get(data.game.turnNum-1).containsKey(sort)
								? cobj.consumptionHistory.get(data.game.turnNum-1).get(sort)
								: 0
							: 0;

				if (sold == 0) {
					float newPrice = helper.roundPrice(refPr * 1.1f);
					cobj.prices.put(sort, newPrice);	//make bit higher initially
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
					storePreviousPriceStep(cobj.cityRef.id, sort.id, nps);
					
					float newPrice = helper.roundPrice( pr + nps );
					callback.displayDebugMessage(cobj.cityRef.id + ": " + act + " price for " + sort.name + " to " + newPrice);
				}
			}
		}
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
