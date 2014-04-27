package com.pl.beerwars.data;
import java.math.*;

public class Constants
{
	public static class Maps{
		public final static int Basic = 1;
	}
	
	public static class Players{
		public final static int MainHuman = 1;
	}
	
	public static class IntellectId {
		public final static int Human = 1;		
		public final static int AI = 10;		
	}
	
	public static class CityIds{
		public final static String Trinkburg = "trinkburg";
		public final static String Feldkirchen = "feldkirchen";
		public final static String Weissau = "weissau";
		public final static String Luisfeld = "luisfeld";
		public final static String Maishafen = "maishafen";
		public final static String SanMartin = "sanmartin";
		public final static String Steinfurt = "steinfurt";
		public final static String Freiburg = "freiburg";
		public final static String Prems = "prems";
		public final static String Regenwald = "regenwald";
		public final static String Hochstadt = "hochstadt";
	}
	
	public static final int ValueUnknown = -10;

	public static class SitySizes{
		public final static int Small = -1;
		public final static int Medium = 100000;
		public final static int Big = 500000;
		public final static int Mega = 1000000;
	}
	
	public static class Sizes{
		public final static float TouchRadiusSquare = 900; // in pixels
	}
	
	public static class Economics{
		public final static String Currency = "$";

		public final static int transportPerKm = 20;
		public final static int transportReload = 50;
		
		public final static int unitSize = 1000;

		public final static int startMoney = 1000;
		public final static int startUnits = 2;
		public final static int startBeer = startUnits * unitSize;
				
		public final static int unitIdleCost = 100;
	}
	
	public static enum StorageSize{
		None, Small, Medium, Big		
	}

	public static enum FactorySize{
		None, Small, Medium, Big
	}

	public static int FactoryVolume(FactorySize size){
		switch (size){
			case Small: return 5;
			case Medium: return 50;
			case Big: return 500;
			case None:
			default: return 0;
		}
	}

	public static int FactorySupportCost(FactorySize size){
		switch (size){
			case Small: return 200;
			case Medium: return 1500;
			case Big: return 7000;
			case None:
			default: return 0;
		}
	}
	
	public static int StorageVolume(StorageSize size){
		switch (size){
			case Small: return 20000;
			case Medium: return 200000;
			case Big: return 1000000;
			case None:
			default: return 0;
		}
	}
	
	public static int StorageBuildPrice(StorageSize size){
		switch (size){
			case Small: return 5000;
			case Medium: return 30000;
			case Big: return 100000;
			case None:
			default: return 0;
		}
	}

	public static int StorageSupportCost(StorageSize size){
		switch (size){
			case Small: return 100;
			case Medium: return 750;
			case Big: return 3500;
			case None:
			default: return 0;
		}
	}

	public static int StorageBuildingTime(StorageSize size){
		switch (size){
			case Small: return 4;
			case Medium: return 8;
			case Big: return 12;
			case None:
			default: return 0;
		}
	}

	public static StorageSize StorageNextSize(StorageSize size){
		switch (size){
			case None: return StorageSize.Small;
			case Small: return StorageSize.Medium;
			case Medium: return StorageSize.Big;
			case Big:
			default:
				return StorageSize.None;
		}
	}

	public static class startBeerParameters{
		public final static float selfprice = 0.7f;
		public final static float quality = 0.5f;		
		public final static float deviation = 0.4f;		
	}
	
	public enum ScreenButton {
		None,
		Player, NextTurn
	}
}
