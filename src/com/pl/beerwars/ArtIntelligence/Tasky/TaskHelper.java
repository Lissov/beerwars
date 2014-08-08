package com.pl.beerwars.ArtIntelligence.Tasky;

public class TaskHelper
{
	public static final int FirstCity = 1;
	public static final int WinCity = 2;
	public static final int DistributeExtras = 10;
	
	public static Task getTask(int type){
		switch (type){
			case FirstCity:
				return new FirstCityTask();
			case WinCity:
				return new WinCityTask();
			default: 
				return null;
		}
	}
}
