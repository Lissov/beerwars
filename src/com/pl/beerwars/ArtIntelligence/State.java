package com.pl.beerwars.ArtIntelligence;
import java.util.*;

public abstract class State
{
	public abstract String serialize();
	public abstract void deserialize(String data);
	
	protected HashMap<String, String> keyvalues;
	protected void setStateStr(String stateStr){
		keyvalues = new HashMap<String, String>();
		String[] values = stateStr.split(";");
		for (String keyvalue : values){
			String[] kv = keyvalue.split(":");
			keyvalues.put(kv[0], kv[1]);
		}
	}
	protected float getValueFloat(String key, float defV){
		if (keyvalues.containsKey(key))
			return Float.parseFloat(keyvalues.get(key));
		else
			return defV;
	}
}
