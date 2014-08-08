package com.pl.beerwars.ArtIntelligence.Tasky;
import com.pl.beerwars.ArtIntelligence.*;
import com.pl.beerwars.data.playerdata.*;
import com.pl.beerwars.data.*;

public abstract class Task
{
	public String id;
	public float priority;
	public boolean isStarted;
	public boolean isFinished;

	public int getType()
	{
		return -1;
	}

	public void serialize(StateSerializer ss, String prefix)
	{
		ss.write(prefix + "_type", getType());
		ss.write(prefix + "_id", id);
		ss.write(prefix + "_priority", priority);
		ss.write(prefix + "_isStarted", isStarted);
		ss.write(prefix + "_isFinished", isFinished);
	}

	public void deserialize(StateSerializer ss, String prefix)
	{
		id = ss.readValueString(prefix + "_id", "undefined");
		priority = ss.readValueFloat(prefix + "_priority", 0);
		isStarted = ss.readValueBool(prefix + "_isStarted", false);
		isFinished = ss.readValueBool(prefix + "_isFinished", false);
	}
	
	public abstract float getPriority();
	public abstract float remainingPrice();
	public abstract float activationPrice();
	
	public abstract void execute(PlayerData data, Game.TurnMessageCallback callback);
}
