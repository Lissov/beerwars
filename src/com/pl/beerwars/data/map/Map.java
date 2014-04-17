package com.pl.beerwars.data.map;

public class Map
{
	public int mapId;
	public City[] cities;
	public Road[] roads;
	
	public City getCityById(String id){
		for (City c : cities){
			if (c.id == id)
				return c;
		}
		
		return null;
	}

	public int getCityIndex(String id){
		for (int i = 0; i<cities.length; i++){
			if (cities[i].id == id)
				return i;
		}

		return -1;
	}
	
	public float[][] distances;
	
	public void calculateDistances(){
		distances = new float[cities.length][cities.length];
		
		for (int i = 0; i<cities.length; i++){
			
			distances[i][i] = 0;
			for (int j = i+1; j<cities.length; j++)
				distances[i][j] = -1;
			
			int[] queue = new int[20];
			queue[0] = i;
			int queuePos = 0;
			int queueLen = 1;
			while (queuePos < queueLen)
			{
				String cid = cities[queue[queuePos]].id;
				float currentD = distances[i][queue[queuePos]];
				for (Road r : roads){
					if (r.cities[0] != cid && r.cities[1] != cid)
						continue;
					String toId = r.cities[0] == cid ? r.cities[1] : r.cities[0];
					int toI = getCityIndex(toId);
					float newD = currentD + r.length;
					
					if (distances[i][toI] >= 0 && distances[i][toI] <= newD)
						continue;
						
					distances[i][toI] = newD;
					queue[queueLen] = toI;
					queueLen++;
				}
				
				queuePos++;
			}

			for (int j = i+1; j<cities.length; j++)
				distances[j][i] = distances[i][j];
		}
	}
}
