package com.pingan.claimhelper.main;



import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.pingan.clainmhelper.main.R;

public class RecordList extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_list);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.record_list, menu);
		return true;
	}

}
