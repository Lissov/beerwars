package com.pl.beerwars.data.playerdata;
import java.util.*;
import com.pl.beerwars.data.beer.*;

public class ProductionResult
{
	public HashMap<BeerSort, Integer> produced = new HashMap<BeerSort, Integer>();
	public HashMap<BeerSort, Integer> dropped = new HashMap<BeerSort, Integer>();
	
	public float price;
}
