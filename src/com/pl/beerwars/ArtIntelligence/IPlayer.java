package com.pl.beerwars.ArtIntelligence;
import com.pl.beerwars.data.facade.*;
import com.pl.beerwars.data.playerdata.*;
import com.pl.beerwars.data.*;

public interface IPlayer
{
	void makeTurn(PlayerData data, Game.TurnMessageCallback callback);
	
	String getState();
	void setState(String state);
}
