package com.pingan.claimhelper.photo;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pingan.clainmhelper.main.R;

/**
 * 图片适配器
 * 
 * @author pengjiqun
 * 
 */
public class ImageAdapter extends BaseAdapter {
	private Context context;
	private List<Bitmap> imgList;
	private List<String> textList;
	private List<String> pathList;

	public ImageAdapter(Context context, List<Bitmap> imgList,
			List<String> pathList, List<String> descList) {
		this.context = context;
		this.imgList = imgList;
		this.pathList = pathList;
		this.textList = descList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imgList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// convertView = LayoutInflater.from(context.getApplicationContext())
		// .inflate(R.layout.img_describe_items, null);
		// ImageView imgView = (ImageView) convertView
		// .findViewById(R.id.image_view);
		// imgView.setImageBitmap(imgList.get(position));
		// Log.i("getView ", position + "");
		// Log.i("convertView ", convertView + "");

		ImageView imgView;
		TextView delButton;
		EditText editText;
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context.getApplicationContext())
					.inflate(R.layout.img_describe_items, null);
			imgView = (ImageView) convertView.findViewById(R.id.image_view);
			delButton = (TextView) convertView
					.findViewById(R.id.del_img_button);
			editText = (EditText) convertView
					.findViewById(R.id.img_describe_edit);
			holder = new ViewHolder(imgView, delButton, editText);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
			imgView = holder.getImgView();
			delButton = holder.getDelButton();
			editText = holder.getEditText();
			Log.i("position ", position + " editText:" + editText);
		}
		imgView.setImageBitmap(imgList.get(position));
		imgView.setId(position);
		delButton.setId(position);
		editText.setId(position);
		editText.setText(textList.get(position));
		addListener(imgView, delButton, editText);
		return convertView;
	}

	private void addListener(ImageView imgView, TextView delButton,
			EditText editText) {
		imgView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ImageDetail.class);
				Bundle bundle = new Bundle();
				final int position = v.getId();
				bundle.putString("imgPathName", pathList.get(position));
				intent.putExtras(bundle);
				context.startActivity(intent);
				int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
				if (version >= 5) {
					((Activity) context).overridePendingTransition(
							R.anim.acivity_in, R.anim.acivity_out); // 此为自定义的动画效果，下面两个为系统的动画效果
				}
			}
		});
		delButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final int position = v.getId();
				imgList.remove(position);
				pathList.remove(position);
				textList.remove(position);
				notifyDataSetChanged();
			}

		});
		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					final int position = v.getId();
					final EditText Caption = (EditText) v;
					if (position < textList.size())
						textList.set(position, Caption.getText().toString());
				}
			}
		});
	}

	private static class ViewHolder {
		private ImageView imgView;
		private EditText editText;
		private TextView delButton;

		ViewHolder(ImageView imgView, TextView delButton, EditText editText) {
			this.imgView = imgView;
			this.editText = editText;
			this.delButton = delButton;
		}

		public TextView getDelButton() {
			return delButton;
		}

		public ImageView getImgView() {
			return imgView;
		}

		public EditText getEditText() {
			return editText;
		}

	}

}
