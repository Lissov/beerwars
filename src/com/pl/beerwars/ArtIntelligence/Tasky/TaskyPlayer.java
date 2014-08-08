package com.pl.beerwars.ArtIntelligence.Tasky;
import com.pl.beerwars.ArtIntelligence.*;
import com.pl.beerwars.data.playerdata.*;
import com.pl.beerwars.data.*;
import com.pl.beerwars.data.beer.*;
import android.app.*;
import java.util.*;

public class TaskyPlayer extends BasePlayer<TaskyState>
{
	//private final float riskLevel = 0.95f;
	//private final float TargetMinPriceK = 1.1f;
	
	@Override
	protected TaskyState initState()
	{
		TaskyState st = new TaskyState();
		
		return st;
	}

	@Override
	public void makeTurn(PlayerData data, Game.TurnMessageCallback callback)
	{
		if (state.tasks.size() == 0){
			initTasks(data);
		}
		
		SortedMap<Float, Task> execTasks = getPrioritizedTasks(data);
		
		for (Task t : execTasks.values()){
			t.execute(data, callback);
		}
		
		//float sp = helper.getSupportPricePerBottle(data);

		//int budget = (int)(riskLevel * data.money);

		/*for (CityObjects cobj : data.cityObjects) {
			for (BeerSort sort : cobj.prices.keySet()){
				float eqPr = sort.selfprice + sp;
				float refPr = eqPr * TargetMinPriceK;

				PlayerHelper.BeerPriceMove bpm = calcNewBeerPrice(sort, cobj, data, eqPr, refPr, callback);

				cobj.prices.put(sort, bpm.newPrice);
			}
		}*/
		
		// TODO: Implement this method
	}
	
	private void initTasks(PlayerData data){
		// win the city
		for (CityObjects cobj : data.cityObjects){
			Task t = cobj.factorySize == Constants.FactorySize.None
				? new WinCityTask(cobj.cityRef.id)
				: new FirstCityTask(cobj.cityRef.id);
			state.tasks.add(t);
		}
	}
	
	private SortedMap<Float, Task> getPrioritizedTasks(PlayerData data){

		float budget = data.money;
		SortedMap<Float, Task> res = new TreeMap<Float,Task>();

		for (Task t : state.tasks){
			t.priority = t.getPriority();
			if (t.isStarted && !t.isFinished){
				res.put(t.priority, t);
				budget -= t.remainingPrice();
			}
		}
		
		Task actT = getMaxPriorityAvailable(budget);
		while (actT != null){
			actT.isStarted = true;
			res.put(actT.getPriority(), actT);
			budget -= actT.activationPrice();
			actT = getMaxPriorityAvailable(budget);
		}
		
		return res;
	}
	
	private Task getMaxPriorityAvailable(float budget){
		Task t = null;
		for (Task tt : state.tasks){
			if (tt.activationPrice() < budget
				&& tt.isStarted == false
				&& tt.getPriority() > 0
				&& (t == null || tt.activationPrice() == 0 || 
					tt.getPriority() / tt.activationPrice() > t.getPriority() / t.activationPrice()) )
			{
				t = tt;
			}
		}
		
		return t;
	}
}
