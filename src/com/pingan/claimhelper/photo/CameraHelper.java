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
	private static final int TAKE_PICTURE = 0;// �����������
	private static final int CHOOSE_PICTURE = 1;// �������ѡ��ͼƬ
	private static final int SHOWHEIGHT = 200;// ͼƬ��ʾ��ȴ�С
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

		// ���ͼ�갴ť
		addImgButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showPicturePicker(CameraHelper.this);
			}
		});
		// ����ͼƬ��ť
		Button saveButton = (Button) this.findViewById(R.id.save_desc_button);
		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				saveImgAndDesc();
			}
		});
		// ���ذ�ť
		Button backButton = (Button) this.findViewById(R.id.desc_back_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (saved == true)
					finish();
				else {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);

					builder.setTitle("ͼƬ��Ϣ��δ���棬�Ƿ��ȱ���");
					builder.setNegativeButton("ȡ��", null);
					builder.setItems(new String[] { "����", "����" },
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
	 * ������Ƭ������
	 */
	public void saveImgAndDesc() {
		String showText = new String();
		for (int i = 0; i < pathList.size(); i++) {
			showText += "ͼƬ��ַ" + pathList.get(i) + " ������" + descList.get(i);
		}
		saved = true;
		Toast.makeText(context, showText, Toast.LENGTH_LONG).show();
	}

	/**
	 * ��Ƭѡ��Ի���
	 * 
	 * @param context
	 */
	public void showPicturePicker(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("ͼƬ��Դ");
		builder.setNegativeButton("ȡ��", null);
		builder.setItems(new String[] { "����", "���" },
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
							// ָ����Ƭ����·����SD������image.jpgΪһ����ʱ�ļ���ÿ�����պ����ͼƬ���ᱻ�滻
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
				// �������ڱ��ص�ͼƬȡ������С����ʾ�ڽ�����
				String imgPath = SAVEPATH + "/" + imageName;
				Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
				int scale = bitmap.getHeight() / SHOWHEIGHT;
				Bitmap newBitmap = ImageTool.zoomBitmap(bitmap,
						bitmap.getWidth() / scale, SHOWHEIGHT);
				// ����Bitmap�ڴ�ռ�ýϴ�������Ҫ�����ڴ棬����ᱨout of memory�쳣
				bitmap.recycle();
				// ���������ͼƬ��ʾ�ڽ����ϣ������浽����
				imgList.add(newBitmap);
				pathList.add(imgPath);// ��ӵ�ַ
				descList.add(NONE);
				notifyImageChanged();// ���Ѹ���ͼƬ
				break;

			case CHOOSE_PICTURE:
				ContentResolver resolver = getContentResolver();
				// ��Ƭ��ԭʼ��Դ��ַ
				Uri originalUri = intent.getData();
				try {
					// ʹ��ContentProviderͨ��URI��ȡԭʼͼƬ
					Bitmap photo = MediaStore.Images.Media.getBitmap(resolver,
							originalUri);
					if (photo != null) {
						// Ϊ��ֹԭʼͼƬ�������ڴ��������������Сԭͼ��ʾ��Ȼ���ͷ�ԭʼBitmapռ�õ��ڴ�
						scale = photo.getHeight() / SHOWHEIGHT;
						Bitmap smallBitmap = ImageTool.zoomBitmap(photo,
								photo.getWidth() / scale, SHOWHEIGHT);
						// �ͷ�ԭʼͼƬռ�õ��ڴ棬��ֹout of memory�쳣����
						photo.recycle();
						imgList.add(smallBitmap);
						pathList.add(ImageTool.getAbsoluteImagePath(
								CameraHelper.this, originalUri));
						descList.add(NONE);
						notifyImageChanged();// ���Ѹ���ͼƬ
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
	 * ���Ѹ�������
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
