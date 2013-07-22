package com.pingan.claimhelper.main;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pingan.claimhelper.audio.AudioRecorder;
import com.pingan.claimhelper.audio.Comment;
import com.pingan.clainmhelper.main.R;

public class VoiceClaim extends Activity implements OnClickListener {

	TextView record_tip;
	ImageView dialog_img;
	Button record_start;
	Button record_stop;
	Button record_list;

	private Thread recordThread;
	private AudioRecorder mr;
	Toast toast = null;

	private static int MIX_TIME = 1; // ���¼��ʱ�䣬��λ�룬0Ϊ��ʱ�����ƣ�������Ϊ1

	private static int RECORD_NO = 0; // ����¼��
	private static int RECORD_ING = 1; // ����¼��
	private static int RECODE_ED = 2; // ���¼��

	private static int RECODE_STATE = 0; // ¼����״̬

	private static int recodeTime = 0; // ¼����ʱ��
	private static double voiceValue = 0.0; // ��˷��ȡ������ֵ

	private static boolean playState = false; // ����״̬

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_claim);
		record_tip = (TextView) findViewById(R.id.record_tip);
		dialog_img = (ImageView) findViewById(R.id.record_volume);
		record_start = (Button) findViewById(R.id.record_start);
		record_stop = (Button) findViewById(R.id.record_stop);
		record_list = (Button) findViewById(R.id.record_list);

		// ��ʼ��
		record_tip.setText(VoiceClaim.this
				.getString(R.string.record_tip_pre_record));
		record_start.setOnClickListener(this);
		record_stop.setOnClickListener(this);
		record_list.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == record_start) {// ��ʼ
			record_start.setEnabled(false);
			record_stop.setEnabled(true);
			if (RECODE_STATE != RECORD_ING) {
				Comment.audio_index++;
				Log.e("Comment.audio_index",
						String.valueOf(Comment.audio_index));

				mr = new AudioRecorder(Comment.audio_id
						+ String.valueOf(Comment.audio_index));
				RECODE_STATE = RECORD_ING;
				try {
					mr.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mythread();
			}

		}
		if (v == record_stop) {// ֹͣ
			record_start.setEnabled(true);
			record_stop.setEnabled(false);
			if (RECODE_STATE == RECORD_ING) {
				RECODE_STATE = RECODE_ED;
				try {
					mr.stop();
					voiceValue = 0.0;
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (recodeTime < MIX_TIME) {
					showMsg("¼��ʱ��̫��");
					RECODE_STATE = RECORD_NO;
				}
				mythread();
			}

		}
		if (v == record_list) {// �鿴�б�
			Intent mAudioList = new Intent(VoiceClaim.this, AudioList.class);
			startActivity(mAudioList);
		}

	}

	// ����¼������
	void mythread() {
		recordThread = new Thread(ImgThread);
		recordThread.start();
		recordThread = new Thread(TimeThread);
		recordThread.start();
	}

	// ���������߳�
	private Runnable ImgThread = new Runnable() {

		@Override
		public void run() {

			while (RECODE_STATE == RECORD_ING) {

				try {
					Thread.sleep(200);

					if (RECODE_STATE == RECORD_ING) {
						voiceValue = mr.getAmplitude();
						imgHandle.sendEmptyMessage(1);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		Handler imgHandle = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 0:
					// ¼������15���Զ�ֹͣ
					// if (RECODE_STATE == RECORD_ING) {
					// RECODE_STATE = RECODE_ED;
					// if (dialog.isShowing()) {
					// dialog.dismiss();
					// }
					// try {
					// mr.stop();
					// voiceValue = 0.0;
					// } catch (IOException e) {
					// e.printStackTrace();
					// }
					//
					// if (recodeTime < 1.0) {
					// showWarnToast();
					// record.setText("��ס��ʼ¼��");
					// RECODE_STATE = RECORD_NO;
					// } else {
					// record.setText("¼�����!�������¼��");
					// luyin_txt.setText("¼��ʱ�䣺" + ((int) recodeTime));
					// luyin_path.setText("�ļ�·����" + getAmrPath());
					// }
					// }
					break;
				case 1:
					setDialogImage();
					break;
				default:
					break;
				}

			}
		};
	};

	// ��ʱ�߳�
	private Runnable TimeThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0;
			while (RECODE_STATE == RECORD_ING) {
				try {
					timeHandle.sendEmptyMessage(1);
					Thread.sleep(1000);
					recodeTime++;

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (RECODE_STATE == RECODE_ED) {
				timeHandle.sendEmptyMessage(0);

			}
		}

		Handler timeHandle = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 0:
					record_tip.setText(VoiceClaim.this
							.getString(R.string.record_tip_recorded)
							+ setTimeStyle(recodeTime));

					break;
				case 1:
					record_tip.setText(VoiceClaim.this
							.getString(R.string.record_tip_recording)
							+ setTimeStyle(recodeTime));
					break;
				default:
					break;
				}

			}
		};
	};

	// ¼��DialogͼƬ��������С�л�
	void setDialogImage() {
		if (voiceValue < 200.0) {
			dialog_img.setImageResource(R.drawable.record_animate_01);
		} else if (voiceValue > 200.0 && voiceValue < 400) {
			dialog_img.setImageResource(R.drawable.record_animate_02);
		} else if (voiceValue > 400.0 && voiceValue < 800) {
			dialog_img.setImageResource(R.drawable.record_animate_03);
		} else if (voiceValue > 800.0 && voiceValue < 1600) {
			dialog_img.setImageResource(R.drawable.record_animate_04);
		} else if (voiceValue > 1600.0 && voiceValue < 3200) {
			dialog_img.setImageResource(R.drawable.record_animate_05);
		} else if (voiceValue > 3200.0 && voiceValue < 5000) {
			dialog_img.setImageResource(R.drawable.record_animate_06);
		} else if (voiceValue > 5000.0 && voiceValue < 7000) {
			dialog_img.setImageResource(R.drawable.record_animate_07);
		} else if (voiceValue > 7000.0 && voiceValue < 10000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_08);
		} else if (voiceValue > 10000.0 && voiceValue < 14000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_09);
		} else if (voiceValue > 14000.0 && voiceValue < 17000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_10);
		} else if (voiceValue > 17000.0 && voiceValue < 20000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_11);
		} else if (voiceValue > 20000.0 && voiceValue < 24000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_12);
		} else if (voiceValue > 24000.0 && voiceValue < 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_13);
		} else if (voiceValue > 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_14);
		}
	}

	// ����ʱ���ʽ
	private String setTimeStyle(int time) {
		String str = "";
		int hour = 0;
		int minute = 0;
		int second = 0;

		hour = time / 3600;
		minute = time / 60;
		second = time % 60;
		if (hour < 10) {
			str += "0" + String.valueOf(hour) + ":";
		} else {
			str += String.valueOf(hour) + ":";
		}
		if (minute < 10) {
			str += "0" + String.valueOf(minute) + ":";
		} else {
			str += String.valueOf(minute) + ":";
		}
		if (second < 10) {
			str += "0" + String.valueOf(second);
		} else {
			str += String.valueOf(second);
		}
		return str;

	}

	// �����Ϣ
	public void showMsg(String arg) {

		if (toast == null) {
			toast = Toast.makeText(this, arg, Toast.LENGTH_SHORT);
		} else {
			toast.cancel();
			toast.setText(arg);
		}
		toast.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.voice_claim, menu);
		return true;
	}

}
