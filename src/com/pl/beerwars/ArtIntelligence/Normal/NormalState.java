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
		StringBuilder sb = new StringBuilder();
		for (String city : priceSteps.keySet()){
			HashMap<Integer, Float> hm = priceSteps.get(city);
			for (Integer sortId : hm.keySet()){
				sb.append("pst_" + city + "_" + sortId + ":" + hm.get(sortId) + ";");
			}
		}
		return sb.toString();
	}

	@Override
	public void deserialize(String data)
	{
		setStateStr(data);
		for (String key : keyvalues.keySet()){
			if (key.startsWith("pst_")){
				String[] splits = key.split("_");
				if (!priceSteps.containsKey(splits[1]))
					priceSteps.put(splits[1], new HashMap<Integer, Float>());
				priceSteps.get(splits[1]).put(Integer.parseInt(splits[2]), getValueFloat(key, 0f));
			}
		}
	}
}
