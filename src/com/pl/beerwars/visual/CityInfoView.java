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
import com.michaelnovakjr.numberpicker.NumberPicker;
import java.util.*;
import android.graphics.*;

public class CityInfoView extends OverlayFrame
{
	private IViewShower _shower;
	private Translator _translator;
	private PlayerData _data;
	private String _cityId;
	private Resources res;

	public CityInfoView(Context context, IViewShower shower, Translator translator, PlayerData data, String cityId)
	{
		super(context);
		_shower = shower;
		_translator = translator;
		_data = data;
		_cityId = cityId;

		res = this.getResources();
		
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
		showBeers();
		showOthers(city);
		showStorage();
		showFactory();
		
		//final View t = this;
		Button btnClose = (Button)findViewById(R.id.cityinfo_btnClose);
		btnClose.setOnClickListener(new View.OnClickListener(){
				public void onClick(View view)
				{
					//float w = t.getMeasuredWidth();
					//float h = t.getMeasuredHeight();
					//Toast.makeText(_context, "" + w + " : " + h, Toast.LENGTH_LONG).show();
					
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

		boolean anything = false;
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
				anything = true;
			}
			if (pcf.factorySize != FactorySize.None)
			{
				TextView tv = new TextView(_context);
				tv.setTextColor(res.getColor(R.color.overlay_text));
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.textNormal));
				tv.setText(String.format(res.getString(R.string.cityinfo_other_has),
										 pcf.playerName, _translator.getFactoryName(pcf.factorySize)));
				llOther.addView(tv);
				anything = true;
			}
		}

		if (!anything){
			TextView tv = new TextView(_context);
			tv.setTextColor(res.getColor(R.color.overlay_text));
			tv.setTypeface(null, Typeface.ITALIC);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.textNormal));
			tv.setText(res.getString(R.string.cityinfo_other_none));
			llOther.addView(tv);				
		}
	}

	private void showBeers()
	{
		Resources res = _context.getResources();
		final CityObjects obj = _data.cityObjects[_data.game.getCityIndex(_cityId)];

		final HashMap<BeerSort, NumberPicker> npickers = new HashMap<BeerSort, NumberPicker>();

		//todo: put it on a screen
		final TextView tvUnused = new TextView(_context);
		tvUnused.setTextColor(res.getColor(R.color.overlay_text));
		tvUnused.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.textSmall));
		
		int totalConsumed = 0;
		int totalStored = 0;
		int totalProduced = 0;
		
		HashMap<BeerSort, Integer> consumedLast = 
			obj.consumptionHistory.containsKey(_data.game.turnNum-1) 
				? obj.consumptionHistory.get(_data.game.turnNum-1)
				: null;
		HashMap<BeerSort, Integer> consumedPrev =
			obj.consumptionHistory.containsKey(_data.game.turnNum-2) 
				? obj.consumptionHistory.get(_data.game.turnNum-2)
				: null;
		TableLayout table = (TableLayout)findViewById(R.id.cityinfo_tlBeers);
		for (BeerSort sort : obj.storage.keySet())
		{
			TableRow row = new TableRow(_context);
			row.setBackgroundColor(res.getColor(R.color.overlay_background));
			
			row.addView(getTextView(sort.name));

			if (consumedLast != null && consumedLast.containsKey(sort))
			{
				int c = consumedLast.get(sort);
				String text = "" + c;
				if (consumedPrev != null && consumedPrev.containsKey(sort))
				{
					int p = consumedPrev.get(sort);
					if (p > 0){
						text += " (" + _translator.formatRelative(c, p) + ")";
					}
				}
				row.addView(getTextView(text, Gravity.CENTER_HORIZONTAL));
				totalConsumed += c;
			} else {
				row.addView(getTextView("---", Gravity.CENTER_HORIZONTAL));
			}

			row.addView(getTextView(obj.storage.get(sort), Gravity.CENTER_HORIZONTAL));
			totalStored += obj.storage.get(sort);
						
			final BeerSort capturedS = sort;

			
			if (obj.factory.containsKey(sort)){
				int cnt = obj.factory.get(sort);
				NumberPicker nf = new NumberPicker(_context);
				nf.setIsInteger(true);
				nf.setStartRange(0);
				nf.setEndRange(10000);
				nf.setStep(1);
				nf.setCurrent(obj.factory.get(sort));
				row.addView(nf);
				totalProduced += obj.factory.get(sort);
				
				npickers.put(sort, nf);
				nf.setOnChangeListener(new NumberPicker.OnChangedListener(){
					@Override
					public void onChanged(NumberPicker picker, float oldVal, float newVal){
						obj.factory.put(capturedS, (int)newVal);
						updateRanges(obj, tvUnused, npickers);
					}
				});
			}
			else{
				row.addView(getTextView("-", Gravity.CENTER_HORIZONTAL));
			}


			NumberPicker np = new NumberPicker(_context);
			np.setIsInteger(false);
			np.setStartRange(0.01f);
			np.setEndRange(99.98f);
			np.setStep(0.05f);
			np.setCurrent(obj.prices.get(sort));
			np.setOnChangeListener(new NumberPicker.OnChangedListener(){
					@Override
					public void onChanged(NumberPicker picker, float oldVal, float newVal){
						obj.prices.put(capturedS, newVal); 	
					}
				});

			row.addView(np);
			
			table.addView(row);
		}
		
		TableRow summaryRow = new TableRow(_context);
		TableLayout.LayoutParams params = new TableLayout.LayoutParams();
		params.setMargins(0, 1, 0, 0);
		summaryRow.setBackgroundColor(res.getColor(R.color.overlay_background));
		summaryRow.setLayoutParams(params);
		summaryRow.addView(getTextView(""));
		summaryRow.addView(getTextView(totalConsumed, Gravity.CENTER_HORIZONTAL));
		summaryRow.addView(getTextView(totalStored, Gravity.CENTER_HORIZONTAL));		
		summaryRow.addView(getTextView(totalProduced, Gravity.CENTER_HORIZONTAL));
		table.addView(summaryRow);
		
		updateRanges(obj, tvUnused, npickers);		
	}
	
	private void showStorage()
	{
		Resources res = _context.getResources();
		final CityObjects obj = _data.cityObjects[_data.game.getCityIndex(_cityId)];

		TextView tvAvail = (TextView)findViewById(R.id.cityinfo_storage_usage);
		tvAvail.setText(obj.getTotalStorage() + "/" + obj.getStorageMax());

		Button btnExpand = (Button)findViewById(R.id.cityinfo_btnStorageExpand);
		btnExpand.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v)
				{
					_shower.showView(new ExpandView(_context, _shower, _translator, _data, obj.cityRef.id, true));
				}
			});

		btnExpand.setEnabled(obj.storageSize != StorageSize.Big);
	}
	
	private void showFactory()
	{
		Resources res = _context.getResources();
		final CityObjects obj = _data.cityObjects[_data.game.getCityIndex(_cityId)];

		TextView tvAvail = (TextView)findViewById(R.id.cityinfo_factory_usage);
		String avText = obj.factoryUnits
			+ "(" + obj.getOperatingUnitsCount() + ")"
			+ " / " + obj.getFactoryMax();
		if (obj.getConstructedCount() > 0){
			avText = avText + " (" + 
				String.format(res.getString(R.string.cityinfo_units_construct),
							  obj.getConstructedCount())
				+ ")";
		}
		if (obj.getDestructedCount() > 0){
			avText = avText + " (" + 
				String.format(res.getString(R.string.cityinfo_units_destruct),
							  obj.getDestructedCount())
				+ ")";
		}
		tvAvail.setText(avText);

		Button btnExpand = (Button)findViewById(R.id.cityinfo_btnFactoryExpand);
		btnExpand.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v)
				{
					_shower.showView(new ExpandView(_context, _shower, _translator, _data, obj.cityRef.id, false));
				}
			});
	}
	
	private void updateRanges(final CityObjects obj, TextView tvUnused, HashMap<BeerSort, NumberPicker> npickers){
		Resources res = _context.getResources();
		
		int idleUnits = obj.factoryUnits - obj.getOperatingUnitsCount();
		tvUnused.setText(String.format(
							 res.getString(R.string.cityinfo_factory_unused),
							 idleUnits
						 ));
		
		for (BeerSort sort : npickers.keySet()){
			npickers.get(sort).setEndRange(
				obj.factory.get(sort) + idleUnits
			);
		}
	}
}
