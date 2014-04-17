package com.pl.beerwars;

import android.app.*;
import android.os.*;
import android.view.*;
import android.content.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
	
	public void btnStartClick(View view){
		startActivity(new Intent(this, GameActivity.class));
	}
}
