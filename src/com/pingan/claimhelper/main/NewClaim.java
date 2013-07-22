package com.pingan.claimhelper.main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.pingan.claimhelper.photo.CameraHelper;
import com.pingan.clainmhelper.main.R;

public class NewClaim extends Activity {

	private Button bnPhotoes, bnVoice, bnSave;
	private Button bnStartGps;
	private EditText mPlace;
	private boolean mIsStart;
	private static int count = 1;
	private Vibrator mVibrator01 = null;
	private LocationClient mLocClient;

	public static String TAG = "LocTestDemo";

	DatePicker mDatePicker;
	TimePicker mTimePicker;
	Calendar mCalendar;

	private Button pickDate = null;
	private Button pickTime = null;
	private static final int SHOW_DATAPICK = 0;
	private static final int DATE_DIALOG_ID = 1;
	private static final int SHOW_TIMEPICK = 2;
	private static final int TIME_DIALOG_ID = 3;
	private static boolean IS_DATE = false;

	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_claim);
		bnPhotoes = (Button) findViewById(R.id.photoes);
		bnVoice = (Button) findViewById(R.id.voice);
		bnSave = (Button) findViewById(R.id.savefile);
		// 设置日期
		initializeViews();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		pickDate.setText(sdf.format(new java.util.Date()));
		SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
		pickTime.setText(sdf1.format(new java.util.Date()));

		/******************************* 打开照相功能 ***********/
		bnPhotoes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent imageIntent = new Intent(NewClaim.this,
						CameraHelper.class);
				startActivity(imageIntent);
			}
		});
		/*************************************************/

		/**************************** 打开录音功能 *************/
		bnVoice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent IVocie = new Intent(NewClaim.this, VoiceClaim.class);
				startActivity(IVocie);
			}
		});
		/*************************************************/

		// GPS定位
		mPlace = (EditText) findViewById(R.id.place);
		bnStartGps = (Button) findViewById(R.id.gps);
		mIsStart = false;

		mLocClient = ((Location) getApplication()).mLocationClient;
		((Location) getApplication()).mPlace = mPlace;
		mVibrator01 = (Vibrator) getApplication().getSystemService(
				Service.VIBRATOR_SERVICE);
		((Location) getApplication()).mVibrator01 = mVibrator01;

		// 开始/停止按钮
		bnStartGps.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mIsStart) {
					setLocationOption();
					mLocClient.start();
					bnStartGps.setText("停止定位");
					mIsStart = true;
					if (mLocClient != null && mLocClient.isStarted()) {
						setLocationOption();
						mLocClient.requestLocation();
					} else
						Log.d(TAG, "locClient is null or not started");
					Log.d(TAG, "... mlocBtn onClick... pid=" + Process.myPid()
							+ " count=" + count++);
					Log.d(TAG, "version:" + mLocClient.getVersion());

				} else {
					mLocClient.stop();
					mIsStart = false;
					bnStartGps.setText("开始定位");
				}
				Log.d(TAG, "... mStartBtn onClick... pid=" + Process.myPid()
						+ " count=" + count++);
			}
		});
	}

	/************************** 定位 ****************************/
	@Override
	public void onDestroy() {
		mLocClient.stop();
		((Location) getApplication()).mPlace = null;
		super.onDestroy();
	}

	// 设置相关参数
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		// option.setOpenGps(mGpsCheck.isChecked()); //打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setServiceName("com.baidu.location.service_v2.9");

		option.setAddrType("all");

		if (null != "3000") {
			boolean b = isNumeric("3000");
			if (b) {
				option.setScanSpan(Integer.parseInt("3000")); // 设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
			}
		}

		option.setPriority(LocationClientOption.NetWorkFirst); // 设置网络优先
		option.setPoiNumber(10);
		option.disableCache(true);
		mLocClient.setLocOption(option);
	}

	protected boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/************************** 定位 ****************************/

	/**
	 * 初始化控件和UI视图
	 */
	private void initializeViews() {

		pickDate = (Button) findViewById(R.id.pickdate);
		pickTime = (Button) findViewById(R.id.picktime);

		pickDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg = new Message();
				if (pickDate.equals((Button) v)) {
					msg.what = NewClaim.SHOW_DATAPICK;
				}
				NewClaim.this.dateandtimeHandler.sendMessage(msg);
				IS_DATE = true;
				setDateTime();
			}
		});
		pickTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Message msgMessage = new Message();
				if (pickTime.equals((Button) v)) {
					msgMessage.what = NewClaim.SHOW_TIMEPICK;
				}
				NewClaim.this.dateandtimeHandler.sendMessage(msgMessage);

			}
		});

	}

	/**
	 * 设置日期
	 */
	private void setDateTime() {
		final Calendar c = Calendar.getInstance();

		if (IS_DATE) {
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);

			pickDate.setText(new StringBuilder()
					.append(mYear)
					.append("-")
					.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
							: (mMonth + 1)).append("-")
					.append((mDay < 10) ? "0" + mDay : mDay));

		} else {
			mHour = c.get(Calendar.HOUR);
			mMin = c.get(Calendar.MINUTE);

			pickTime.setText(new StringBuilder().append(mHour).append("：")
					.append((mMin + 1) < 10 ? "0" + (mMin + 1) : (mMin + 1)));
		}

	}

	/**
	 * 更新日期显示
	 */
	/**
	 * 日期控件的事件
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;

			pickDate.setText(new StringBuilder()
					.append(mYear)
					.append("-")
					.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
							: (mMonth + 1)).append("-")
					.append((mDay < 10) ? "0" + mDay : mDay));
		}
	};
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			mHour = hourOfDay;
			mMin = minute;

			pickTime.setText(new StringBuilder().append(mHour).append("-")
					.append((mMin + 1) < 10 ? "0" + (mMin + 1) : (mMin + 1)));
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, mHour, mMin,
					false);

		}

		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		case TIME_DIALOG_ID:
			((TimePickerDialog) dialog).updateTime(mHour, mMin);
			break;

		}
	}

	/**
	 * 处理日期和时间控件的Handler
	 */
	Handler dateandtimeHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NewClaim.SHOW_DATAPICK:
				showDialog(DATE_DIALOG_ID);
				break;
			case NewClaim.SHOW_TIMEPICK:
				showDialog(TIME_DIALOG_ID);
				break;

			}
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_claim, menu);
		return true;
	}

}
