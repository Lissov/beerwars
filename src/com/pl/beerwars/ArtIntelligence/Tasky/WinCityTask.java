package com.pl.beerwars.ArtIntelligence.Tasky;
import com.pl.beerwars.data.playerdata.*;
import com.pl.beerwars.data.*;
import com.pl.beerwars.ArtIntelligence.*;

public class WinCityTask extends Task
{
	protected String cityId;
	protected int prevProduction = 0;

	public WinCityTask(String cityId)
	{
		this.cityId = cityId;
	}
	
	public WinCityTask(){
		this("undefined");
	}
	
	
	@Override
	public int getType()
	{
		return TaskHelper.WinCity;
	}

	@Override
	public void serialize(StateSerializer ss, String prefix)
	{
		super.serialize(ss, prefix);
		ss.write(prefix + "_cityId", cityId);
		ss.write(prefix + "_prevProd", prevProduction);
	}

	@Override
	public void deserialize(StateSerializer ss, String prefix)
	{
		super.deserialize(ss, prefix);
		cityId = ss.readValueString(prefix + "_cityId", "undefined");
		prevProduction = ss.readValueInt(prefix + "_prevProd", 0);
	}

	@Override
	public float getPriority()
	{
		// TODO: Implement this method
		return 1;
	}

	@Override
	public float activationPrice()
	{
		// TODO: Implement this method
		return 1000000f; // shouldn't activate now
	}

	@Override
	public float remainingPrice()
	{
		return 1000000f; // calculate budget later
	}


	
	
	@Override
	public void execute(PlayerData data, Game.TurnMessageCallback callback)
	{
		// TODO: Implement this method
	}	
}
