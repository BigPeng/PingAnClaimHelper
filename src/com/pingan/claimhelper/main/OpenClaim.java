package com.pingan.claimhelper.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.pingan.clainmhelper.main.R;

public class OpenClaim extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_open_cliam);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.open_cliam, menu);
		return true;
	}

}
