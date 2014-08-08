package com.pl.beerwars.ArtIntelligence.Tasky;
import com.pl.beerwars.ArtIntelligence.*;
import com.pl.beerwars.data.*;
import com.pl.beerwars.data.playerdata.*;
import com.pl.beerwars.data.Game.*;
import com.pl.beerwars.data.beer.*;
import java.util.*;

public class DistributeExtrasTask extends Task
{
	protected PlayerHelper helper = new PlayerHelper();
	
	@Override
	public int getType()
	{
		return TaskHelper.DistributeExtras;
	}
	
	@Override
	public float getPriority()
	{
		return 10;
	}

	@Override
	public float activationPrice()
	{
		return 0;
	}

	@Override
	public float remainingPrice()
	{
		return 0;
	}

	@Override
	public void execute(PlayerData data, Game.TurnMessageCallback callback)
	{
		HashMap<BeerSort, HashMap<String, Integer>> availabilities = calcAvailabilities(data);

		HashMap<String, Integer> space = getSpace(data);
		
		for (BeerSort sort : availabilities.keySet()){
			HashMap<String, Integer> sortAv = availabilities.get(sort);
			
			if (!hasToSend(sortAv)) continue;
			
			//HashMap<String, Integer> demand = calcSortDemand(data, sort);
			
			/*for (String cityTo : demand.keySet()){
				while (demand.get(cityTo) > 0 && hasToSend(sortAv)) {
					String cityFrom = sortAv.keySet().iterator().next();
					int d = demand.get(cityTo);
					int av = sortAv.get(cityFrom);
					int pc = (int)Math.ceil((float)av / (float)Constants.Economics.packSize);
					if (av <= (pc -1)* Constants.Economics.packSize)
						pc -= 1;
					
					data.createTransportOrder(cityFrom, cityTo, sort, pc, false);
					callback.displayDebugMessage("Redistr: Sending " + pc + " packs of " + sort.name + " from " + cityFrom + " to " + cityTo);
					... remove!
				}
			}*/
			
			for (String city : sortAv.keySet()){
				int send = sortAv.get(city);
				while (send > 0 && space.size() > 0){
					String closest = getNearest(data, city, space);
					int place = space.get(closest);
					int s = place > send ? send : place;
					
					data.createTransportOrder(city, closest, sort, s, false);
					callback.displayDebugMessage("Redistr: Sending " + s + " packs of " + sort.name + " from " + city + " to " + closest);

					send -= s;
					if (s - place == 0) 
						space.remove(closest);
					else
						space.put(closest, s - place);			
				}
			}
		}
	}
	
	private String getNearest(PlayerData data, String from, HashMap<String, Integer> spaces){
		int cn = data.game.getCityIndex(from);
		String n = "";
		float best = Constants.Impossible;
		for (String c : spaces.keySet()){
			float pr = data.game.cities[cn].getTransportPrice(c);
			if (pr < best){
				best = pr;
				n = c;
			}
		}
		
		return n;
	}
	
	private boolean hasToSend(HashMap<String, Integer> avail){
		for (Integer i : avail.values()){
			if (i != 0) return true;
		}
		return false;
	}
	
	private HashMap<String, Integer> calcSortDemand(PlayerData data, BeerSort sort){
		HashMap<String, Integer> sortDem = new HashMap<String, Integer>();
		
		for (CityObjects cobj : data.cityObjects){
			if (cobj.storageSize == Constants.StorageSize.None) 
				continue;
			
			int prod = cobj.factory.get(sort);
			int stored = cobj.storage.get(sort);
			int expected = getIncomingAmt(data, cobj);
			
			int consumed = helper.getPrevSold(cobj, data, sort);
			if (consumed > prod 
				|| (stored < consumed * 2 && cobj.getTotalStorage() + expected < cobj.getStorageMax())
				)
			{
				sortDem.put(cobj.cityRef.id, stored - consumed);
			}
		}
		
		return sortDem;
	}
	
	private HashMap<String, Integer> getSpace(PlayerData data){
		HashMap<String, Integer> res = new HashMap<String, Integer>();
		for (CityObjects cobj : data.cityObjects){
			if (cobj.storageSize == Constants.StorageSize.None) 
				continue;

			int prod = cobj.getOperatingUnitsCount() * Constants.Economics.unitSize;
			int stored = cobj.getTotalStorage();
			int expected = getIncomingAmt(data, cobj);
			
			int rem = cobj.getStorageMax() - stored - 2*(prod + expected);
			rem = (int)Math.floor(rem / Constants.Economics.packSize);
			if (rem > 0){
				res.put(cobj.cityRef.id, rem);
			}	
		}

		return res;
	}
	
	private int getIncomingAmt(PlayerData data, CityObjects cobj){
		int total = 0;
		for (TransportOrder t : data.oneTimeOrders){
			if (t.toCity.id == cobj.cityRef.id)
				total += t.packageQuantity;
		}
		for (TransportOrder t : data.recurringOrders){
			if (t.toCity.id == cobj.cityRef.id)
				total += t.packageQuantity;
		}
		
		return total * Constants.Economics.packSize;
	}
	
	private HashMap<BeerSort, HashMap<String, Integer>> calcAvailabilities(PlayerData data)
	{
		HashMap<BeerSort, HashMap<String, Integer>> availabilities
			= new HashMap<BeerSort, HashMap<String, Integer>>(); 
		
		for (CityObjects cobj : data.cityObjects){			
			int storeRem = cobj.getStorageMax() - cobj.getTotalStorage();
			int prod = cobj.getOperatingUnitsCount();
			if (storeRem < prod){
				float toSend = (float)(storeRem - prod) / (float)Constants.Economics.packSize;
				
				HashMap<BeerSort, Integer> av = new HashMap<BeerSort, Integer>();
				for (BeerSort s : cobj.storage.keySet()){
					av.put(s, cobj.storage.get(s) - helper.getPrevSold(cobj, data, s));
				}
				int total = 0;
				for (Integer avc : av.values()) total += avc;
				
				if (total == 0) continue;
				
				for (BeerSort s : av.keySet()){
					float send = toSend * ((float)av.get(s) / (float)total);
					int sendi = (int) Math.ceil(send);
					sendi = (int)Math.ceil((float)sendi / (float)Constants.Economics.packSize);
					
					if (!availabilities.containsKey(s))
						availabilities.put(s, new HashMap<String, Integer>());
					availabilities.get(s).put(cobj.cityRef.id, sendi);
				}
			}
		}
		return availabilities;
	}	
	
	/*private void checkSendUnsoldBeer(CityObjects cobjThis, PlayerData data, BeerSort sort, Game.TurnMessageCallback  callback){
		int prod = cobjThis.getOperatingUnitsCount() * Constants.Economics.unitSize;
		int send = prod - (cobjThis.getStorageMax() - cobjThis.getTotalStorage());

		int packs = (int)Math.ceil(send / Constants.Economics.packSize);
		if (packs > 0){
			CityObjects cc = getClosestCityObject(data, cobjThis);
			data.createTransportOrder(cobjThis.cityRef.id, cc.cityRef.id, sort, packs, false);
			callback.displayDebugMessage(cobjThis.cityRef.id + ": sending " + packs + " packs of " + sort.name + " to " + cc.cityRef.id);			
		}
	}*/

	
}
