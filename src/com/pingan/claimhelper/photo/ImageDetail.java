package com.pingan.claimhelper.photo;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.pingan.clainmhelper.main.R;

public class ImageDetail extends Activity implements OnTouchListener {
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	DisplayMetrics dm;
	ImageView imgView;
	Bitmap bitmap;

	float scale = 0.5f;// ���ű���
	static final float MIN_SCALE = 0.2f;// ��С���ű���
	static final float MAX_SCALE = 10f;// ������ű���
	private static final int SCALE = 2;

	static final int NONE = 0;// ��ʼ״̬
	static final int DRAG = 1;// �϶�
	static final int ZOOM = 2;// ����
	int mode = NONE;

	PointF prev = new PointF();
	PointF mid = new PointF();
	float dist = 1f;
	Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_detail);
		imgView = (ImageView) findViewById(R.id.image_detail_view);// ��ȡ�ؼ�
		imgView.setOnTouchListener(this);// ���ô�������
		Bundle bundle = this.getIntent().getExtras();
		String imgPath = bundle.getString("imgPathName");
		Log.i("imgPath", imgPath);
		handler.post(new ImgLoader(imgPath));
		Button confirmButton = (Button) this
				.findViewById(R.id.confirm_img_button);
		confirmButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	class ImgLoader implements Runnable {
		String imgPath;

		ImgLoader(String imgPath) {
			this.imgPath = imgPath;
		}

		@Override
		public void run() {
			Bitmap imgBitmap = BitmapFactory.decodeFile(imgPath);
			bitmap = ImageTool.zoomBitmap(imgBitmap, imgBitmap.getWidth()
					/ SCALE, imgBitmap.getHeight() / SCALE);
			imgView.setImageBitmap(bitmap);// ���ؼ�
			dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);// ��ȡ�ֱ���
			minZoom();
			imgView.setImageMatrix(matrix);
		}

	}

	/**
	 * ��������
	 */
	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// ���㰴��
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			prev.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		// ���㰴��
		case MotionEvent.ACTION_POINTER_DOWN:
			dist = distence(event);
			// �����������������10�����ж�Ϊ���ģʽ
			if (distence(event) > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - prev.x, event.getY()
						- prev.y);
			} else if (mode == ZOOM) {
				float newDist = distence(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float tScale = newDist / dist;
					matrix.postScale(tScale, tScale, mid.x, mid.y);
				}
			}
			break;
		}
		imgView.setImageMatrix(matrix);
		CheckView();
		return true;
	}

	/**
	 * ���������С���ű������Զ�����
	 */
	private void CheckView() {
		float p[] = new float[9];
		matrix.getValues(p);
		Log.i("CheckView", p[0] + "");
		if (mode == ZOOM) {
			if (p[0] < MIN_SCALE) {
				matrix.set(savedMatrix);
			}
			if (p[0] > MAX_SCALE) {
				matrix.set(savedMatrix);
			}
		}
		center();
	}

	/**
	 * ��С���ű��������Ϊ100%
	 */
	private void minZoom() {
		matrix.postScale(scale, scale);
		// scale = Math.min((float) dm.widthPixels / (float) bitmap.getWidth(),
		// (float) dm.heightPixels / (float) bitmap.getHeight());
		// if (scale < 1.0) {
		// matrix.postScale(scale, scale);
		// }
	}

	private void center() {
		// center(true, true);
	}

	/**
	 * �����������
	 */
	@SuppressWarnings("unused")
	private void center(boolean horizontal, boolean vertical) {

		Matrix m = new Matrix();
		m.set(matrix);
		RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;

		if (vertical) {
			// ͼƬС����Ļ��С���������ʾ��������Ļ���Ϸ������������ƣ��·�������������
			int screenHeight = dm.heightPixels;
			if (height < screenHeight) {
				deltaY = (screenHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < screenHeight) {
				deltaY = imgView.getHeight() - rect.bottom;
			}
		}

		if (horizontal) {
			int screenWidth = dm.widthPixels;
			if (width < screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < screenWidth) {
				deltaX = screenWidth - rect.right;
			}
		}
		matrix.postTranslate(deltaX, deltaY);
	}

	/**
	 * ����ľ���
	 */
	private float distence(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * ������е�
	 */
	@SuppressLint("FloatMath")
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

}
