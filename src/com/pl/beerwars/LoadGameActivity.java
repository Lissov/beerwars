package com.pl.beerwars;
import android.app.*;
import android.os.*;
import android.widget.*;
import com.pl.beerwars.data.*;
import java.util.*;
import android.widget.AdapterView.*;
import android.view.*;
import android.content.*;

public class LoadGameActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final Context context = this;
		
		setContentView(R.layout.loadgame);
		
		final List<SaveData> savedGames = new Storage(this).getSaves();
		final String[] names = new String[savedGames.size()];
		for (int i=0; i<savedGames.size(); i++){
			names[i] = 
				(savedGames.get(i).consistent ? "* " : "  ")
				+ savedGames.get(i).name;
		}
		ListView lvList = (ListView)findViewById(R.id.loadgame_lvSaves);
		lvList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, names));
		lvList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView v, View vw, int p1, long p2){
				SaveData toLoad = savedGames.get(p1);
				GameHolder.loadGame(context, toLoad.id);
				if (GameHolder.getGame(context) != null)
					startActivity(new Intent(context, GameActivity.class));
			}
		});
	}
}
