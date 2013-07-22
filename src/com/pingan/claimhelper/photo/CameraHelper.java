package com.pingan.claimhelper.photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.pingan.clainmhelper.main.R;

public class CameraHelper extends Activity {
	private static final int TAKE_PICTURE = 0;// 调用相机拍照
	private static final int CHOOSE_PICTURE = 1;// 从相册中选择图片
	private static final int SHOWHEIGHT = 200;// 图片显示宽度大小
	private static final String SAVEPATH = ImageTool.getSavePath();
	private String imageName = "";	
	private Context context;
	private List<Bitmap> imgList = new ArrayList<Bitmap>();
	private ImageAdapter imgAdpter;
	private Handler handler = new Handler();
	private List<String> pathList = new ArrayList<String>();
	private List<String> descList = new ArrayList<String>();
	final static String NONE = "";
	private static boolean saved = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_camera_helper);
		context = this;
		Button addImgButton = (Button) this.findViewById(R.id.add_img_button);
		showPicturePicker(CameraHelper.this);
		ListView imgListView = (ListView) this
				.findViewById(R.id.img_desc_listview);
		imgAdpter = new ImageAdapter(context, imgList, pathList, descList);
		imgListView.setAdapter(imgAdpter);
		imgListView.setItemsCanFocus(true);
		imgListView.setClickable(true);

		// 添加图标按钮
		addImgButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showPicturePicker(CameraHelper.this);
			}
		});
		// 保存图片按钮
		Button saveButton = (Button) this.findViewById(R.id.save_desc_button);
		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				saveImgAndDesc();
			}
		});
		// 返回按钮
		Button backButton = (Button) this.findViewById(R.id.desc_back_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (saved == true)
					finish();
				else {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);

					builder.setTitle("图片信息尚未保存，是否先保存");
					builder.setNegativeButton("取消", null);
					builder.setItems(new String[] { "保存", "放弃" },
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										dialog.dismiss();
										saveImgAndDesc();
										finish();
										break;

									case 1:
										dialog.dismiss();
										finish();
										break;

									default:
										break;
									}
								}
							});
					builder.create().show();
				}
			}
		});
	}

	/**
	 * 保存照片和描述
	 */
	public void saveImgAndDesc() {
		String showText = new String();
		for (int i = 0; i < pathList.size(); i++) {
			showText += "图片地址" + pathList.get(i) + " 描述：" + descList.get(i);
		}
		saved = true;
		Toast.makeText(context, showText, Toast.LENGTH_LONG).show();
	}

	/**
	 * 照片选择对话框
	 * 
	 * @param context
	 */
	public void showPicturePicker(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("图片来源");
		builder.setNegativeButton("取消", null);
		builder.setItems(new String[] { "拍照", "相册" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case TAKE_PICTURE:
							Intent openCameraIntent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							imageName = System.currentTimeMillis() + ".jpg";
							Log.i("showPicturePicker", SAVEPATH + ":"
									+ imageName);
							Uri imageUri = Uri.fromFile(new File(SAVEPATH,
									imageName));
							// 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
							openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
									imageUri);
							startActivityForResult(openCameraIntent,
									TAKE_PICTURE);
							break;

						case CHOOSE_PICTURE:
							Intent openAlbumIntent = new Intent(
									Intent.ACTION_GET_CONTENT);
							openAlbumIntent.setType("image/*");
							startActivityForResult(openAlbumIntent,
									CHOOSE_PICTURE);
							break;

						default:
							break;
						}
					}
				});
		builder.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case TAKE_PICTURE:
				// 将保存在本地的图片取出并缩小后显示在界面上
				String imgPath = SAVEPATH + "/" + imageName;
				Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
				int scale = bitmap.getHeight() / SHOWHEIGHT;
				Bitmap newBitmap = ImageTool.zoomBitmap(bitmap,
						bitmap.getWidth() / scale, SHOWHEIGHT);
				// 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
				bitmap.recycle();
				// 将处理过的图片显示在界面上，并保存到本地
				imgList.add(newBitmap);
				pathList.add(imgPath);// 添加地址
				descList.add(NONE);
				notifyImageChanged();// 提醒更新图片
				break;

			case CHOOSE_PICTURE:
				ContentResolver resolver = getContentResolver();
				// 照片的原始资源地址
				Uri originalUri = intent.getData();
				try {
					// 使用ContentProvider通过URI获取原始图片
					Bitmap photo = MediaStore.Images.Media.getBitmap(resolver,
							originalUri);
					if (photo != null) {
						// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
						scale = photo.getHeight() / SHOWHEIGHT;
						Bitmap smallBitmap = ImageTool.zoomBitmap(photo,
								photo.getWidth() / scale, SHOWHEIGHT);
						// 释放原始图片占用的内存，防止out of memory异常发生
						photo.recycle();
						imgList.add(smallBitmap);
						pathList.add(ImageTool.getAbsoluteImagePath(
								CameraHelper.this, originalUri));
						descList.add(NONE);
						notifyImageChanged();// 提醒更新图片
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 提醒更新数据
	 */
	private void notifyImageChanged() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				imgAdpter.notifyDataSetChanged();
			}

		});
	}
}
