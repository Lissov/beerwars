package com.pl.beerwars.ArtIntelligence;
import com.pl.beerwars.data.playerdata.*;
import com.pl.beerwars.data.*;

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
}
