package com.pl.beerwars.ArtIntelligence;

public abstract class BasePlayer<TState extends State> implements IPlayer
{
	protected TState state = initState();
	protected PlayerHelper helper = new PlayerHelper();
	
	protected abstract TState initState();
	
	public void setState(String stateStr){
		state.deserialize(stateStr);
	}

	public String getState(){
		return state.serialize();
	}
}
