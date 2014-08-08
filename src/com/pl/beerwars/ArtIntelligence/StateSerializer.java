package com.pl.beerwars.ArtIntelligence;
import java.util.*;

public class StateSerializer
{
	private HashMap<String, String> keyvalues;
	
	public StateSerializer(){
		keyvalues = new HashMap<String, String>();
	}
	
	public StateSerializer(String stateStr){
		keyvalues = new HashMap<String, String>();
		String[] values = stateStr.split(";");
		for (String keyvalue : values){
			String[] kv = keyvalue.split(":");
			if (kv.length != 2)
				throw new ArrayIndexOutOfBoundsException("wrong keyvalue: " + keyvalue);
			keyvalues.put(kv[0], kv[1]);
		}
	}
	
	public String getString(){
		StringBuilder sb = new StringBuilder();
		
		for (String key : keyvalues.keySet()){
			sb.append(key + ":" + keyvalues.get(key) + ";");
		}
		
		return sb.toString();
	}
	
	
	// write 
	
	public void write(String key, String value){
		keyvalues.put(key, value);
	}

	public void write(String key, int value){
		keyvalues.put(key, Integer.toString(value));
	}

	public void write(String key, float value){
		keyvalues.put(key, Float.toString(value));
	}
	
	public void write(String key, boolean value){
		keyvalues.put(key, value ? "t" : "f");
	}

	public void writeMap(HashMap<String, HashMap<Integer, Float>> map, String prefix){
		for (String k1 : map.keySet()){
			HashMap<Integer, Float> hm = map.get(k1);
			for (Integer k2 : hm.keySet()){
				write(prefix + "_" + k1 + "_" + k2, hm.get(k2).toString());
			}
		}
	}

	
	// read 
	
	public String readValueString(String key, String defV){
		if (keyvalues.containsKey(key))
			return keyvalues.get(key);
		else
			return defV;
	}
	
	public float readValueFloat(String key, float defV){
		if (keyvalues.containsKey(key))
			return Float.parseFloat(keyvalues.get(key));
		else
			return defV;
	}
	
	public int readValueInt(String key, int defV){
		if (keyvalues.containsKey(key))
			return Integer.parseInt(keyvalues.get(key));
		else
			return defV;
	}
	
	public boolean readValueBool(String key, boolean defV){
		if (keyvalues.containsKey(key))
			return keyvalues.get(key) == "t";
		else
			return defV;
	}
	
	public HashMap<String, HashMap<Integer, Float>> readHashMap(String key){
		HashMap<String, HashMap<Integer, Float>> res = new HashMap<String, HashMap<Integer, Float>>();
		for (String k : keyvalues.keySet()){
			if (k.startsWith(key + "_")){
				String[] splits = k.split("_");
				if (!res.containsKey(splits[1]))
					res.put(splits[1], new HashMap<Integer, Float>());
				res.get(splits[1]).put(Integer.parseInt(splits[2]), readValueFloat(k, 0f));
			}
		}
		return res;
	}
}
