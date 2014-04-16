package com.pl.beerwars.visual;
import com.pl.beerwars.*;
import android.view.*;
import android.content.*;
import com.pl.beerwars.data.*;
import android.widget.*;
import com.pl.beerwars.data.map.*;
import android.content.res.*;

public class CityInfoView extends FrameLayout
{
	private Context _context;
	private IViewShower _shower;
	private Translator _translator;
	private Game _game;
	private String _cityId;

	public CityInfoView(Context context, IViewShower shower, Translator translator, Game game, String cityId)
	{
		super(context);
		_context = context;
		_shower = shower;
		_translator = translator;
		_game = game;
		_cityId = cityId;
		
		View.inflate(context, R.layout.cityinfo, this);
		
		Resources res = context.getResources();
		City city = _game.map.getCityById(_cityId);
		
		TextView tvName = (TextView)findViewById(R.id.cityinfo_txtName);
		tvName.setText(translator.getCityName(_cityId));
		TextView tvPopulation = (TextView)findViewById(R.id.cityinfo_txtPopulation);
		tvPopulation.setText(res.getString(R.string.cityinfo_population) + 
			"  " + city.population);
	}
}
