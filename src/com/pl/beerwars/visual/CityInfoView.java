package com.pl.beerwars.visual;
import com.pl.beerwars.*;
import com.pl.beerwars.data.Constants.*;
import android.view.*;
import android.content.*;
import com.pl.beerwars.data.*;
import android.widget.*;
import android.content.res.*;

import com.pl.beerwars.data.beer.BeerSort;
import com.pl.beerwars.data.facade.*;
import com.pl.beerwars.data.playerdata.CityObjects;
import com.pl.beerwars.data.playerdata.PlayerData;
import com.pl.beerwars.data.transport.*;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.*;

public class CityInfoView extends OverlayFrame
{
	private IViewShower _shower;
	private Translator _translator;
	private PlayerData _data;
	private String _cityId;

	public CityInfoView(Context context, IViewShower shower, Translator translator, PlayerData data, String cityId)
	{
		super(context);
		_shower = shower;
		_translator = translator;
		_data = data;
		_cityId = cityId;

		View.inflate(context, R.layout.cityinfo, this);

		Resources res = context.getResources();
		CityFacade city = _data.game.getCity(_cityId);

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

		//showConnections(city, translator, res);
		showPrices();
		showStorage();

		Button btnClose = (Button)findViewById(R.id.cityinfo_btnClose);
		btnClose.setOnClickListener(new View.OnClickListener(){
				public void onClick(View view)
				{
					if (isActive)
						_shower.closeLastView(null);
				}	
			});
	}

	private void showConnections(CityFacade city)
	{
		/*LinearLayout llTransport = (LinearLayout)findViewById(R.id.cityinfo_llTransports);
		 for (TransportPrice price : city.transportPrices){
		 TextView tv = new TextView(_context);
		 tv.setTextColor(res.getColor(R.color.overlay_text));
		 tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.textSmall));
		 tv.setText(translator.getCityName(price.cityTo) + "   " + price.price1000 + Constants.Economics.Currency);
		 llTransport.addView(tv);
		 }*/
	}

	private void showOthers(CityFacade city)
	{
		LinearLayout llOther = (LinearLayout)findViewById(R.id.cityinfo_llOthers);
		Resources res = _context.getResources();

		for (PlayerCityFacade pcf : city.others.values())
		{
			if (pcf.storageSize != StorageSize.None)
			{
				TextView tv = new TextView(_context);
				tv.setTextColor(res.getColor(R.color.overlay_text));
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.textNormal));
				tv.setText(String.format(res.getString(R.string.cityinfo_other_has),
										 pcf.playerName, _translator.getStorageName(pcf.storageSize)));
				llOther.addView(tv);
			}
			if (pcf.factorySize != FactorySize.None)
			{
				TextView tv = new TextView(_context);
				tv.setTextColor(res.getColor(R.color.overlay_text));
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.textNormal));
				tv.setText(String.format(res.getString(R.string.cityinfo_other_has),
										 pcf.playerName, _translator.getFactoryName(pcf.factorySize)));
				llOther.addView(tv);
			}
		}
	}

	private void showPrices()
	{

		Resources res = _context.getResources();
		final CityObjects obj = _data.cityObjects[_data.game.getCityIndex(_cityId)];

		LinearLayout llPrices = (LinearLayout)findViewById(R.id.cityinfo_llPrices);
		for (BeerSort sort : obj.prices.keySet())
		{
			LinearLayout llSort = new LinearLayout(_context);
			llSort.setOrientation(LinearLayout.HORIZONTAL);
			llSort.setWeightSum(3);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.weight = 1;
			params.setMargins(30, 5, 0, 0);

			TextView tv = new TextView(_context);
			tv.setTextColor(res.getColor(R.color.overlay_text));
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.textNormal));
			tv.setText(sort.name);
			tv.setLayoutParams(params);
			llSort.addView(tv);

			EditText et = new EditText(_context);
			et.setTextColor(res.getColor(R.color.overlay_text));
			et.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.textNormal));
			et.setText("" + obj.prices.get(sort));
			et.setLayoutParams(params);
			et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
			final BeerSort capturedS = sort; 
			et.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count)
					{}				
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after)
					{ }

					@Override
					public void afterTextChanged(Editable s)
					{
						float f = Float.parseFloat(s.toString());
						obj.prices.put(capturedS, f);
					}
				});
			llSort.addView(et);

			llPrices.addView(llSort);
		}		
	}

	private void showStorage()
	{

		Resources res = _context.getResources();
		final CityObjects obj = _data.cityObjects[_data.game.getCityIndex(_cityId)];

		TextView tvAvail = (TextView)findViewById(R.id.cityinfo_storage_usage);
		tvAvail.setText(obj.getTotalStorage() + "/" + obj.getStorageMax());

		LinearLayout llStorage = (LinearLayout)findViewById(R.id.cityinfo_llStorage);
		for (BeerSort sort : obj.storage.keySet())
		{
			TextView tv = new TextView(_context);
			tv.setTextColor(res.getColor(R.color.overlay_text));
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.textSmall));
			tv.setText(sort.name + ":\t\t" + obj.storage.get(sort));
			llStorage.addView(tv);
		}

		Button btnExpand = (Button)findViewById(R.id.cityinfo_btnStorageExpand);
		btnExpand.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v)
				{
					_shower.showView(new ExpandView(_context, _shower, _translator, obj, true));
				}
			});

		btnExpand.setEnabled(
			obj.storageSize != StorageSize.Big 
			&& obj.storageBuildRemaining == 0);
	}
}
