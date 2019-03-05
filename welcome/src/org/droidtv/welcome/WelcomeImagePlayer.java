package org.droidtv.welcome;

import android.graphics.Bitmap;
import android.graphics.Shader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;

public class WelcomeImagePlayer implements SurfaceTextureListener {
	private static final String TAG = "WelcomeImagePlayer";
	private TextureView mTextureView = null;
	private Bitmap mBitmap = null;
    private Rect mSrcRect;
    private Rect mDstRect;
    private Paint mPaint;

	/**
	 * @param mTextureView
	 * @param mBitmap
	 */
	public WelcomeImagePlayer(TextureView ttv, Bitmap bm) {
		super();
		this.mTextureView = ttv;
		this.mBitmap = bm;
		init();
	}
	
	private void init() {
		// TODO Auto-generated method stub
		mTextureView.setSurfaceTextureListener(this);
		mSrcRect = new Rect();
		mDstRect = new Rect();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		LinearGradient lg = new LinearGradient(0,0,1920,1080,Color.RED,Color.BLUE, Shader.TileMode.MIRROR);
		mPaint.setShader(lg);
	}
	
	public void playBitmap(Bitmap bm) {
		recycleBitmap(mBitmap);
		mBitmap = bm;
		drawBitmap(mTextureView, mBitmap);
	}
	
	public void stop() {
		recycleBitmap(mBitmap);
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
		// TODO Auto-generated method stub
		drawBitmap(mTextureView, mBitmap);
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		// TODO Auto-generated method stub
		
	}

	private boolean drawBitmap(TextureView ttv, Bitmap bm) {
		// TODO Auto-generated method stub
		boolean temp = false;
		if (ttv == null) {
			Log.v(TAG, "drawBitmap:TextureView is null");
			return temp;
		}
		if (bm == null) {
			Log.v(TAG, "drawBitmap:Bitmap is null");
			return temp;
		}
		Canvas canvas = ttv.lockCanvas(new Rect(ttv.getLeft(), ttv.getTop(), ttv.getWidth(), ttv.getHeight()));
		if (canvas == null) {
			Log.v(TAG, "drawBitmap:Canvas is null");
			return temp;
		}
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		mSrcRect.set(0, 0, bm.getWidth(), bm.getHeight());
		mDstRect.set(0, 0, ttv.getWidth(), bm.getHeight() * ttv.getWidth() / bm.getWidth());
		canvas.drawBitmap(bm, mSrcRect, mDstRect, mPaint);
		ttv.unlockCanvasAndPost(canvas);

		return temp;
	}
	
	private void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
//		bitmap = null;
	}

}
