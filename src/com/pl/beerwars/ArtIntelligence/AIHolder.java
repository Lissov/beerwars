package com.pl.beerwars.ArtIntelligence;
import com.pl.beerwars.data.*;
import com.pl.beerwars.ArtIntelligence.Normal.*;
import com.pl.beerwars.ArtIntelligence.Tasky.*;

public class AIHolder
{
	public static int[] getSupportedAis() {
		return new int[] {
			Constants.IntellectId.AI_Normal,
			Constants.IntellectId.AI_Tasky
		};
	}
	
	public static IPlayer getArtIntelligence(int aiId){
		switch (aiId){
			case Constants.IntellectId.AI_Normal:
				return new NormalPlayer();
			case Constants.IntellectId.AI_Tasky:
				return new TaskyPlayer();
				
			case Constants.IntellectId.Human:
			default:
				return null;
		}
	}
}
