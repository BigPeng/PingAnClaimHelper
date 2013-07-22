package com.pingan.claimhelper.audioadapter;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.pingan.claimhelper.audio.FileUtils;
import com.pingan.clainmhelper.main.R;

/**
 * 音频列表中的每一项
 * @author qingzheng.xin
 *
 */
public class AudioListAdapter extends BaseAdapter {
	Context context;
	List<String> list;
	Cursor cursor;
	int index;

	public AudioListAdapter(Context _context, List<String> _list) {
		super();
		this.context = _context;
		this.list = _list;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.audiolist_item, null);
		}

		TextView audioName = (TextView) convertView
				.findViewById(R.id.audio_filename);

		Button delete = (Button) convertView.findViewById(R.id.audio_delete);

		final String fileName;
		fileName = list.get(position).substring(
				list.get(position).lastIndexOf("/") + 1);
		audioName.setText(fileName);
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setTitle("删除文件");
				builder.setMessage("是否删除文件" + fileName);
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								FileUtils.deleSDFile(new File(list
										.get(position)));
								// 实时更新列表
								list.remove(list.get(position));
								notifyDataSetChanged();

							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();

							}
						});
				builder.create();
				builder.show();

			}
		});
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String filePath = list.get(position);
				Log.e("filePath", filePath);
				Intent it = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.parse("file://"+filePath);
				it.setDataAndType(uri, "audio/mp3");
				context.startActivity(it);

			}

		});

		return convertView;
	}

}
