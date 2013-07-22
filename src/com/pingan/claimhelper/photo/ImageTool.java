package com.pingan.claimhelper.photo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class ImageTool {
	/**
	 * 设置bitmap的大小
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	/**
	 * 获取保存图片的路径
	 * 
	 * @return
	 */
	public static String getSavePath() {
		String basePath;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			basePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		else
			basePath = Environment.getDataDirectory().getAbsolutePath();
		String path = new File(basePath, "lipeizushou").getAbsolutePath();
		createDir(path);
		return path;
	}

	/**
	 * 当path不存在时 创建目录path
	 * 
	 * @param path
	 */

	public static void createDir(String path) {
		File file = new File(path);
		if (file.exists()) {
			return;
		}
		file.mkdirs();

	}

	/**
	 * 保存bitmap到path路径
	 * 
	 * @param bitmap
	 * @param path
	 * @param photoName
	 */
	public static void savePhotoToSDCard(Bitmap bitmap, String path,
			String photoName) {
		File photoFile = new File(path, photoName);
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(photoFile);
			if (bitmap != null) {
				if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						fileOutputStream)) {
					fileOutputStream.flush();
					fileOutputStream.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将bitmap转化为byte[]
	 * 
	 * @param bitmap
	 * @return
	 */
	public static byte[] bitmap2Bytes(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * byte[]转为bitmap
	 * 
	 * @param b
	 * @return
	 */
	public static Bitmap bytes2Bimap(byte[] b) {
		if (b.length == 0) {
			return null;
		}
		return BitmapFactory.decodeByteArray(b, 0, b.length);
	}
	/**
	 * 获取相册图片的绝对路径
	 * @param context
	 * @param uri
	 * @return
	 */
	public static String getAbsoluteImagePath(Context context, Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(uri, proj, null,
				null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
}
