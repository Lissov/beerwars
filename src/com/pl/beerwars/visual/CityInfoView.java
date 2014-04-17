package com.pl.beerwars.visual;
import com.pl.beerwars.*;
import android.view.*;
import android.content.*;
import com.pl.beerwars.data.*;
import android.widget.*;
import com.pl.beerwars.data.map.*;
import android.content.res.*;
import com.pl.beerwars.data.facade.*;
import com.pl.beerwars.data.transport.*;
import android.util.*;

public class CityInfoView extends OverlayFrame
{
	private IViewShower _shower;
	private Translator _translator;
	private GameFacade _game;
	private String _cityId;

	public CityInfoView(Context context, IViewShower shower, Translator translator, GameFacade game, String cityId)
	{
		super(context);
		_shower = shower;
		_translator = translator;
		_game = game;
		_cityId = cityId;
		
		View.inflate(context, R.layout.cityinfo, this);
		
		Resources res = context.getResources();
		CityFacade city = _game.getCity(_cityId);
		
		TextView tvName = (TextView)findViewById(R.id.cityinfo_txtName);
		tvName.setText(translator.getCityName(_cityId));
		TextView tvPopulation = (TextView)findViewById(R.id.cityinfo_txtPopulation);
		tvPopulation.setText(res.getString(R.string.cityinfo_population) + 
			"  " + city.getPopulation());
		TextView tvConsumption = (TextView)findViewById(R.id.cityinfo_txtConsumption);
		int cons = city.estConsumption;
		String consS = cons == Constants.ValueUnknown 
			? res.getString(R.string.cityinfo_consumption_unknown) 
			: "" + cons + " " + res.getString(R.string.bpw);
		tvConsumption.setText(res.getString(R.string.cityinfo_consumption) + "  " + consS);
		
		LinearLayout llTransport = (LinearLayout)findViewById(R.id.cityinfo_llTransports);
		for (TransportPrice price : city.transportPrices){
			TextView tv = new TextView(_context);
			tv.setTextColor(res.getColor(R.color.cityinfo_text));
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.textSmall));
			tv.setText(translator.getCityName(price.cityTo) + "   " + price.price1000 + Constants.Economics.Currency);
			llTransport.addView(tv);
		}
		
			
		Button btnClose = (Button)findViewById(R.id.cityinfo_btnClose);
		btnClose.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				if (isActive)
					_shower.closeLastView(null);
			}	
		});
	}
}
