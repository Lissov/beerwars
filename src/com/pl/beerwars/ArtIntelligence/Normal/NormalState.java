package com.pl.beerwars.ArtIntelligence.Normal;
import com.pl.beerwars.ArtIntelligence.*;
import java.util.*;

public class NormalState extends State
{
	public HashMap<String, HashMap<Integer, Float>> priceSteps
		= new HashMap<String, HashMap<Integer, Float>>();

	@Override
	public String serialize()
	{
		StateSerializer s = new StateSerializer();
		s.writeMap(priceSteps, "pst");
		return s.getString();
	}

	@Override
	public void deserialize(String data)
	{
		StateSerializer s = new StateSerializer(data);
		priceSteps = s.readHashMap("pst");
	}
}
