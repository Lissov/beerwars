package com.pl.beerwars.data;

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
		
		public final static int startMoney = 1000;
	}
	
	public static class FactorySizes{
		public final static int none = 0;		
		public final static int small = 1;		
		public final static int medium = 2;		
		public final static int big = 3;		
	}
	
	public static class StorageSizes{
		public final static int none = 0;		
		public final static int small = 1;		
		public final static int medium = 2;		
		public final static int big = 3;		
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
