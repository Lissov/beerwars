package com.pl.beerwars.ArtIntelligence.Tasky;
import com.pl.beerwars.ArtIntelligence.*;
import com.pl.beerwars.data.playerdata.*;
import com.pl.beerwars.data.beer.*;
import com.pl.beerwars.data.*;
import com.pl.beerwars.data.Constants.*;

public class FirstCityTask extends WinCityTask
{
	private boolean goingDown = false;
	private int otherStorageSize = 0;
	PlayerHelper helper = new PlayerHelper();
	
	private final float increaseBudgetK = 0.8f;
	private final float extendBudgetK = 0.7f;
	private final float unlimitedBudgetPrice = 1.3f;
	private final float otherCityPriceK = 1.1f;
	
	public FirstCityTask(String cityId)
	{
		super(cityId);
	}
	
	public FirstCityTask() {
		super();
	}
	
	@Override
	public int getType()
	{
		return TaskHelper.FirstCity;
	}

	@Override
	public void serialize(StateSerializer ss, String prefix)
	{
		super.serialize(ss, prefix);
		ss.write(prefix + "_godown", goingDown);
		ss.write(prefix + "_otherStSize", otherStorageSize);
	}

	@Override
	public void deserialize(StateSerializer ss, String prefix)
	{
		super.deserialize(ss, prefix);
		goingDown = ss.readValueBool(prefix + "_godown", false);
		otherStorageSize = ss.readValueInt(prefix + "_otherStSize", 0);
	}
	
	@Override
	public float getPriority()
	{
		return 1000f;
	}

	@Override
	public float remainingPrice()
	{
		return 1000000f; // 1 million - just to avoid activation of other tasks
	}

	@Override
	public float activationPrice()
	{
		return 0; // should be activated when exists
	}

	
	@Override
	public void execute(PlayerData data, Game.TurnMessageCallback callback)
	{
		int cid = data.game.getCityIndex(cityId);
		
		float sp = helper.getSupportPricePerBottle(data);
		CityObjects curr = data.cityObjects[cid];
		BeerSort sort = data.ownedSorts.get(0);

		int pp = curr.factory.get(sort) * Constants.Economics.unitSize;
		
		boolean atMin = setBeerPrice(
				curr,
				data,
				sort,
				data.game.turnNum,
				sp,
				callback);
				
		float priceK = curr.prices.get(sort) / (sort.selfprice +sp);
				
		setOtherBeerPrice(data, curr, sort, sp, callback);
				
		if (atMin){
			isFinished = true;
			//TODO: finish unfinished stuff
		} else {
			otherStorageSize = getOtherStorageSize(curr, data);
			useAllBarrels(curr, sort, callback);
			extendBarrels(curr, data, priceK, callback);
			checkExtendFactory(curr, data, priceK, callback);
			checkExtendStorage(curr, data, priceK, callback);
			checkBuildOtherCityStorage(curr, data, priceK, callback);
			//checkSendUnsoldBeer(curr, data, sort, callback); // will be done by other task
			
			prevProduction = pp;
		}
	}
	
	private int getOtherStorageSize(CityObjects cobj, PlayerData data){
		for (CityObjects c : data.cityObjects){
			if (c.cityRef.id == cobj.cityRef.id) continue;
			if (c.storageSize != StorageSize.None)
				return c.getStorageMax() - c.getTotalStorage();
		}
		return 0;
	}
	
	private void setOtherBeerPrice(PlayerData data, CityObjects cobjThis, BeerSort sort, float supportPerBottle, Game.TurnMessageCallback callback){
		CityObjects cc = getClosestCityObject(data, cobjThis);
		float tp = data.game.cities[
						data.game.getCityIndex(cobjThis.cityRef.id)
					].getTransportPrice(cc.cityRef.id);
		tp = tp / Constants.Economics.packSize;
		float newPrice = helper.roundPrice( (sort.selfprice + supportPerBottle + tp) * otherCityPriceK );
		if (newPrice != cc.prices.get(sort)){
			cc.prices.put(sort, newPrice);
			callback.displayDebugMessage(cc.cityRef.id + ": setting price of " + sort.name + " to " + newPrice);			
		}
	}
	
	private void useAllBarrels(CityObjects cobj, BeerSort sort, Game.TurnMessageCallback callback){
		int avail = cobj.factoryUnits - cobj.getOperatingUnitsCount();
		if (avail > 0){
			int working = cobj.factory.get(sort);
			cobj.factory.put(sort, working + avail);
			callback.displayDebugMessage(cobj.cityRef.id + ": using all " + (working + avail) + " units for " + sort.name);
		}
	}
	
	private void extendBarrels(CityObjects cobj, PlayerData data, float priceK, Game.TurnMessageCallback callback){
		int haveSpace = cobj.getFactoryMax() - cobj.factoryUnits - cobj.getConstructedCount();
		
		float budget = getAvailableBudget(cobj, data, priceK, increaseBudgetK);
		int canBuild = (int)Math.floor(budget / (float)Constants.Economics.unitBuildCost);
		if (canBuild > haveSpace)
			canBuild = haveSpace;
		
		if (canBuild > 0){
			data.expandUnits(cobj.cityRef.id, canBuild);
			callback.displayDebugMessage(cobj.cityRef.id + ": Extending units count +" + canBuild);
		}
	}

	private float getAvailableBudget(CityObjects cobj, PlayerData data, float priceK, float budgK){
		if (!goingDown)
			return data.money;
		
		if (priceK > unlimitedBudgetPrice)
			return data.money;
		return budgK * data.money;
	}
	
	private void checkExtendFactory(CityObjects cobj, PlayerData data, float priceK, Game.TurnMessageCallback callback){
		if (cobj.factoryBuildRemaining > 0)
			return;	// already building
		FactorySize next = Constants.FactoryNextSize(cobj.factorySize);
		if (next == FactorySize.None)
			return; // already at max
		
		int productionM = cobj.getFactoryMax() * Constants.Economics.unitSize;
		float storageUse = productionM / (float)cobj.getStorageMax();
		
		boolean storageOverused = (storageUse > 0.3f && otherStorageSize > productionM)
				|| storageUse > 0.6f
				|| (cobj.getTotalStorage() > productionM && (storageUse > 0.2f));
		if (storageOverused){
			// first extend storage, otherwise will overfill storage
			return;
		}

		float budget = getAvailableBudget(cobj, data, priceK, extendBudgetK);

		if (Constants.FactoryBuildPrice(next) <= budget){
			data.expandFactory(cobj.cityRef.id, next);
			callback.displayDebugMessage(cobj.cityRef.id + ": Expanding factory to " + next);
		}
	}
	
	private void checkExtendStorage(CityObjects cobj, PlayerData data, float priceK, Game.TurnMessageCallback callback){
		if (cobj.storageBuildRemaining > 0)
			return;	// already building
		StorageSize next = Constants.StorageNextSize(cobj.storageSize);
		if (next == StorageSize.None)
			return; // already at max

		int productionM = cobj.getFactoryMax() * Constants.Economics.unitSize;
		float storageUse = productionM / (float)cobj.getStorageMax();
		if (storageUse < 0.1f)
			return; // no sense of expanding

		float budget = getAvailableBudget(cobj, data, priceK, extendBudgetK);

		if (Constants.StorageBuildPrice(next) <= budget){
			data.expandStorage(cobj.cityRef.id, next);
			callback.displayDebugMessage(cobj.cityRef.id + ": Expanding storage to " + next);
		}
	}
	
	private void checkBuildOtherCityStorage(CityObjects cobj, PlayerData data, float priceK, Game.TurnMessageCallback callback)
	{
		float budget = getAvailableBudget(cobj, data, priceK, extendBudgetK);
		
		if (cobj.factorySize == FactorySize.Small && cobj.factoryBuildRemaining == 0)
			return; // first factory
			
		CityObjects co = getClosestCityObject(data, cobj);
		if (co.getStorageMax() >= cobj.getStorageMax())
			return; // no sense in so big;
			
		if (co.storageBuildRemaining > 0) 
			return; // already building
		
		StorageSize next = Constants.StorageNextSize(co.storageSize);
		if (next == StorageSize.None)
			return; // already at max
		if (Constants.StorageBuildPrice(next) <= budget){
			data.expandStorage(co.cityRef.id, next);
			callback.displayDebugMessage(co.cityRef.id + ": Expanding storage to " + next);
		}		
	}
	
	private CityObjects getClosestCityObject(PlayerData data, CityObjects thisCo){
		String closest = getClosestCity(data, thisCo.cityRef.id);
		int closestInd = data.game.getCityIndex(closest);
		return data.cityObjects[closestInd];
	}
	
	private String getClosestCity(PlayerData data, String city){
		int thisNum = data.game.getCityIndex(city);
		float dist = Constants.Impossible;
		String res = "";
		for (CityObjects other : data.cityObjects){
			String oId = other.cityRef.id;
			if (oId == city) continue;
			
			float tp = data.game.cities[thisNum].getTransportPrice(oId);
			if (tp < dist){
				dist = tp;
				res = oId;
			}
		}
		
		return res;
	}
	
	private boolean setBeerPrice(CityObjects cobj, PlayerData data, BeerSort sort, int turnNum, float supPerBottle, Game.TurnMessageCallback callback){
		float targetPr = helper.roundPrice((sort.selfprice + supPerBottle) * 1.1f);
		if (prevProduction == 0){
			float newPr = helper.roundPrice(targetPr*1.2f);
			cobj.prices.put(sort, newPr);
			callback.displayDebugMessage(cobj.cityRef.id + ": Initial price of " + sort.name + " set to " + newPr);
			goingDown = false;
			
			return false;
		}
		//else
		
		int sold = helper.getPrevSold(cobj, data, sort);
		float oldPr = cobj.prices.get(sort);
		if (oldPr <= targetPr)
		{
			callback.displayDebugMessage(cobj.cityRef.id + ": got to maximum consumption of " + sort.name + " on " + oldPr);
			return sold < prevProduction;
		}
		//else
		
		if (sold < prevProduction){
			float step = goingDown
				? sold > 0 ? 0.05f : (oldPr - targetPr) / 2f
				: 0.05f;
			if (step < 0.05f) step = 0.05f;
			float newPr = oldPr <= targetPr + step 
				? targetPr
				: oldPr - step;
			newPr = helper.roundPrice(newPr);
			cobj.prices.put(sort, newPr);
			callback.displayDebugMessage(cobj.cityRef.id + ": decreasing price of " + sort.name + " to " + newPr + "(sold " + sold + " produced " + prevProduction + ")");
			goingDown = true;
			return false;
		}
		//else
		
		// sold >= prevProduction
		if (!goingDown){
			float newPr = helper.roundPrice(oldPr + 0.05f);
			cobj.prices.put(sort, newPr);
			callback.displayDebugMessage(cobj.cityRef.id + ": still increasing price of " + sort.name + " to " + newPr);
			return false;
		}
		
		return false;
	}
}
