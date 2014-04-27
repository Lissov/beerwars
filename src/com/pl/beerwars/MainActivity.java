package com.pl.beerwars;

import android.app.*;
import android.os.*;
import android.view.*;
import android.content.*;
import com.pl.beerwars.data.*;
import android.widget.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		((Button)findViewById(R.id.main_btnContinue))
			.setEnabled(GameHolder.getGame(this) != null);
    }
	
	public void btnStartNewClick(View view){
		startActivity(new Intent(this, GameSetupActivity.class));
	}
	public void btnContinueClick(View view){
		startActivity(new Intent(this, GameActivity.class));
	}
}
