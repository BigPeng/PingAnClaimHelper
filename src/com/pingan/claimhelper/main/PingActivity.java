package com.pingan.claimhelper.main;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.pingan.clainmhelper.main.R;

public class PingActivity extends Activity {

	private static final int APP_EXIT = 0;
	private Button bnNew, bnOpen;
	private static int width, height;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ping);
		bnNew = (Button) findViewById(R.id.newclaim);
		bnOpen = (Button) findViewById(R.id.openclaim);

		// ���ð�ť����
		Display display = this.getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		bnNew.setWidth((int) (width / 2));
		bnOpen.setWidth((int) (width / 2));
		// �½���ť
		bnNew.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent newIntent = new Intent(PingActivity.this, NewClaim.class);
				startActivity(newIntent);
			}
		});
		bnOpen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent openIntent = new Intent(PingActivity.this, OpenClaim.class);
				startActivity(openIntent);
				
			}
		});
	}
	//��д�˳���ť
	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			showDialog(APP_EXIT);
			return true;
		}
		else{
			return super.onKeyDown(keyCode, event);
		}
	}
		
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == APP_EXIT) {
			return new AlertDialog.Builder(this)
					
					.setTitle("�Ƿ��˳�")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

									dialog.dismiss();
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
									finish();

								}
							})
					.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							}).create();
		}
		return null;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ping, menu);
		return true;
	}

}
