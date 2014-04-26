package com.pl.beerwars.data.facade;
import com.pl.beerwars.data.Constants.*;

public class PlayerCityFacade
{
	public String playerName;
	
	public StorageSize storageSize;
	public FactorySize factorySize;

	public PlayerCityFacade(String playerName)
	{
		this.playerName = playerName;
	}	
}
