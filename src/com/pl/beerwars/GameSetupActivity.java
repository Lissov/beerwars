package com.pl.beerwars;
import android.app.*;
import android.os.*;
import android.widget.*;
import com.pl.beerwars.data.*;
import com.pl.beerwars.visual.*;
import java.util.*;
import android.widget.AdapterView.*;
import android.view.*;
import com.pl.beerwars.data.map.*;
import android.content.*;

public class GameSetupActivity extends Activity
{
	private HashMap<String, Integer> maps;
	private Translator trans;
	private Spinner spMap;
	private Spinner spCity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gamesetup);
		
		trans = new Translator();
		trans.setup(0, 0, 0, this.getResources());
		
		spCity = (Spinner)findViewById(R.id.gamesetup_spStartCity);
		
		List<Integer> mapIds = GameHolder.getAvailableMaps();
		maps = new HashMap<String, Integer>();
		List<String> mapNames = new ArrayList<String>();
		for (Integer mapId: mapIds){
			String name = trans.getMapName(mapId);
			Toast.makeText(this, "map:" + name, Toast.LENGTH_LONG).show();
			maps.put(name, mapId);
			mapNames.add(name);
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mapNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spMap = (Spinner)findViewById(R.id.gamesetup_spMap);
		spMap.setAdapter(adapter);
		spMap.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView parentView, View selectedItemView, int position, long id){
				setCities();
			}
			@Override
			public void onNothingSelected(AdapterView parentView){
				setCities();
			}
		});

		List<String> oppCounts = new ArrayList<String>();
		for (int i = 1; i <=5; i++){
			oppCounts.add("" + i);			
		}
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, oppCounts);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)findViewById(R.id.gamesetup_spOpponentsCount)).setAdapter(adapter2);
	}
	
	private void setCities(){
		String selected = spMap.getSelectedItem() == null 
			? null
			: spMap.getSelectedItem().toString();

		List<String> cities = new ArrayList<String>();		
		
		if (maps.containsKey(selected)){
			com.pl.beerwars.data.map.Map map = GameHolder.getMap(maps.get(selected));
			for (City c : map.cities){
				cities.add(trans.getCityName(c.id));
			}
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cities);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCity = (Spinner)findViewById(R.id.gamesetup_spStartCity);
		spCity.setAdapter(adapter);
	}
	
	public void onStartClick(View view){
		String name = ((EditText)findViewById(R.id.gamesetup_etPlayerName)).getText().toString();

		if (name.isEmpty())	return;
		
		if (spMap.getSelectedItem() == null) return;
		String selected = spMap.getSelectedItem().toString();
		if (selected.isEmpty()) return;
		int mapId = maps.get(selected);
		
		if (spCity == null || spCity.getSelectedItem() == null) return;
		String cityName = spCity.getSelectedItem().toString();
		if (cityName.isEmpty()) return;
		String cityid = "";
		for (City c : GameHolder.getMap(mapId).cities) {
			if (trans.getCityName(c.id) == cityName)
				cityid = c.id;
		}
		if (cityid == "") return;
		
		Spinner spOC = (Spinner)findViewById(R.id.gamesetup_spOpponentsCount);
		if (spOC == null || spOC.getSelectedItem() == null) return;
		int opCount = Integer.parseInt(spOC.getSelectedItem().toString());
		
		GameHolder.constructNewGame(mapId, name, cityid, opCount);
		GameHolder.saveGame("Autosave New Game");
		
		startActivity(new Intent(this, GameActivity.class));
	}
}
