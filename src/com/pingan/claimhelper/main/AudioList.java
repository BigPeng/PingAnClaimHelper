package com.pingan.claimhelper.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.pingan.claimhelper.audio.Comment;
import com.pingan.claimhelper.audio.FileUtils;
import com.pingan.claimhelper.audioadapter.AudioListAdapter;
import com.pingan.clainmhelper.main.R;

/**
 * 音频列表
 * 
 * @author qingzheng.xin
 * 
 */
public class AudioList extends Activity {
	ListView listview;
	AudioListAdapter adapter;
	List<String> list;
	List<String> listTemp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audiolist);

		list = new ArrayList<String>();
		listTemp = new ArrayList<String>();

		listview = (ListView) findViewById(R.id.audio_listview);

		listTemp = FileUtils.getFileList(Comment.audio_path);
		for (int i = 0; i < listTemp.size(); i++) {
			Log.e("++++++++++++++++++++++++++", listTemp.get(i));
			Log.e("--------------------------",
					FileUtils.getFileType(listTemp.get(i)));
			if ("audio/*".equals(FileUtils.getFileType(listTemp.get(i)))) {
				list.add(listTemp.get(i));
			}
		}
		listTemp.clear();
		// getFileList("/mnt/sdcard/videos");
		Log.e("/mnt/sdcard/myAudio文件夹下的视频个数", String.valueOf(list.size()));

		adapter = new AudioListAdapter(this, list);
		listview.setAdapter(adapter);
	}

}
