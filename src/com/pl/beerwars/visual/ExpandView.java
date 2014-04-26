package com.pl.beerwars.visual;
import com.pl.beerwars.*;
import com.pl.beerwars.data.playerdata.CityObjects;
import android.content.Context;
import android.view.*;
import android.content.res.*;
import android.widget.*;
import com.pl.beerwars.data.Constants.*;
import android.util.*;
import com.pl.beerwars.data.*;

public class ExpandView extends OverlayFrame {

	Context _context;
	Translator _translator;
	CityObjects _obj;
	
	public ExpandView(Context context, final IViewShower shower, Translator translator, CityObjects obj, boolean isStorage) {
		super(context);
		_context = context;
		_translator = translator;
		_obj = obj;
		
		View.inflate(context, R.layout.expand, this);

		Resources res = context.getResources();
		
		((TextView)findViewById(R.id.expand_txtTitle)).setText(
			String.format(res.getString(
					isStorage ? R.string.expand_title_storage : R.string.expand_title_factory
				), translator.getCityName(obj.cityRef.id)
			)
		);
		
		if (isStorage)
			addStorageData(obj);
		
		//addOptions(isStorage);
		
		Button btnClose = (Button)findViewById(R.id.expand_btnClose);
		btnClose.setOnClickListener(new View.OnClickListener(){
				public void onClick(View view){
					if (isActive)
						shower.closeLastView(null);
				}	
			});
	}
	
	private void addStorageData(CityObjects obj){
		Resources res = _context.getResources();
		
		StorageSize sNext = Constants.StorageNextSize(obj.storageSize);

		((TextView)findViewById(R.id.expand_txtCurrent)).setText(
			String.format(res.getString(
					R.string.expand_storage_current,
					_translator.getStorageName(obj.storageSize),
					Constants.StorageVolume(obj.storageSize),
					Constants.StorageSupportCost(obj.storageSize) + Constants.Economics.Currency
				)
			)
		);
		
		String sizeS = _translator.getStorageName(sNext);
		((TextView)findViewById(R.id.expand_txtExpandTo)).setText(
			String.format(res.getString(R.string.expand_storage_new), sizeS)
		);
		
		((TextView)findViewById(R.id.expand_txtNew)).setText(
			String.format(res.getString(R.string.expand_storage_new_expl), 
				Constants.StorageVolume(sNext),
				"" + Constants.StorageSupportCost(sNext) + Constants.Economics.Currency,
				Constants.StorageBuildingTime(sNext),
				"" + Constants.StorageBuildPrice(sNext) + Constants.Economics.Currency
			)
		);
		
		Button btnBuild = (Button)findViewById(R.id.expand_btnBuild);
		btnBuild.setText(String.format(
				res.getString(R.string.expand_build),
				Constants.StorageBuildPrice(sNext)
			)
		);
		/*btnBuild.setOnClickListener(new View.OnClickListener(){
				public void onClick(View view){
					if (isActive)
						shower.closeLastView(null);
				}	
			});*/
	}
	/*
	private void addOptions(boolean isStorage){
		LinearLayout llOptions = (LinearLayout)findViewById(R.id.expand_llVariants);
		if (isStorage){
			switch (_obj.storageSize){
				case None:
					llOptions.addView(addOptionStorage(StorageSize.Small));
				case Small:
					llOptions.addView(addOptionStorage(StorageSize.Medium));
				case Medium:
				case Big:
				default: return;
			}
		}
	}
	
	private LinearLayout addOptionStorage(StorageSize size){
		Resources res = _context.getResources();
		
		LinearLayout llOption = new LinearLayout(_context);
		llOption.setWeightSum(4);
		llOption.setOrientation(LinearLayout.HORIZONTAL);
		
		LinearLayout llTexts = new LinearLayout(_context);
		llTexts.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.weight = 3;
		params.setMargins(30, 5, 0, 0);
		llTexts.setLayoutParams(params);
		
		TextView tv = new TextView(_context);
		tv.setTextColor(res.getColor(R.color.overlay_text));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.textNormal));
		switch (size){
			case Small: tv.setText(res.getString(R.string.storage_small)); break;
			case Medium: tv.setText(res.getString(R.string.storage_medium)); break;
			case Big: tv.setText(res.getString(R.string.storage_big)); break;
		}
		llTexts.addView(tv);

		TextView tvE = new TextView(_context);
		tvE.setTextColor(res.getColor(R.color.overlay_text));
		tvE.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.textSmall));
		tvE.setText(String.format(res.getString(R.string.expand_storage_expl),
			Constants.StorageVolume(size),
			Constants.StorageSupportCost(size),
			Constants.StorageBuildingTime(size)
		));
		llTexts.addView(tvE);
		
	}*/
}
