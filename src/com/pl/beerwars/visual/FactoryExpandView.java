package com.pl.beerwars.visual;
import com.pl.beerwars.*;
import android.content.*;
import com.pl.beerwars.data.playerdata.*;
import android.view.*;
import android.content.res.*;
import android.widget.*;

public class FactoryExpandView extends OverlayFrame
{
	Context _context;
	Translator _translator;
	CityObjects _obj;
	
	public FactoryExpandView(Context context, final IViewShower shower, Translator translator, CityObjects obj, boolean isStorage) {
		super(context);
		_context = context;
		_translator = translator;
		_obj = obj;
		
		View.inflate(context, R.layout.expand_factory, this);

		Resources res = context.getResources();
		
		((TextView)findViewById(R.id.expand_txtTitle)).setText(
			String.format(res.getString(
							  isStorage ? R.string.expand_title_storage : R.string.expand_title_factory
						  ), translator.getCityName(obj.cityRef.id)
						  )
		);
		
		//TextView tvUnitsBE = (TextView)findViewById(R.id.exp
	}
}
