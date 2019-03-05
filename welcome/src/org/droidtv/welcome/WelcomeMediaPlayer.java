package org.droidtv.welcome;

import java.io.IOException;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

public class WelcomeMediaPlayer implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener {
	private MediaPlayer mMediaPlayer;
	private TextureView mTextureView;
	private SurfaceTexture mSurfaceTexture = null;
	private Surface mSurface = null;
	private String[] videoPath = null;
	private int videoResource;
	// the index of video
	private int index = 0;
	private int num = 0;
	private Context mContext;
	private String TAG = this.getClass().getSimpleName();

	public WelcomeMediaPlayer(TextureView tv, String[] videoPath) {
		super();
		this.mTextureView = tv;
		this.videoPath = videoPath;
		init();
	}

	public WelcomeMediaPlayer(Context context,TextureView tv, int videoResource) {
		super();
		this.mContext = context;
		this.mTextureView = tv;
		this.videoResource = videoResource;
		initMediaPlayer();
	}

	public String[] getVideoPath() {
		return videoPath;
	}

	public void setVideoPath(String[] videoPath) {
		this.videoPath = videoPath;
	}

	private void init() {
		mTextureView.setSurfaceTextureListener(this);
		mMediaPlayer = new MediaPlayer();
		index = 0;
		if (videoPath != null) {
			num = videoPath.length;
			Log.v("EAGLE", "init" + videoPath[0]);
		}
	}

	private void initMediaPlayer() {
		mTextureView.setSurfaceTextureListener(this);
		mMediaPlayer = MediaPlayer.create(mContext,videoResource);
	}

	public boolean play() {
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnCompletionListener(this);
		return true;
	}

	public void stop() {
		 mMediaPlayer.stop();
		 Log.d(TAG, "mMediaPlayer.stop()");
	/*******PR:TF518PHIEUMTK07-6481 Flicker seen while Welcome screen (Video 1/Video 2) is removed with RC_Back key
	 * 	onSurfaceTextureDestroyed() return true,so remove mSurfaceTexture.release();*/
	}

	public void release() {
		mMediaPlayer.release();
		Log.d(TAG, "mMediaPlayer.release()");
	}

	public void textureRelease() {
		mSurfaceTexture.release();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if ( (index+1) < num) {
			index++;
		} else {
			index = 0;
		}
		Log.v("EAGLE", "onCompletion: "+ index);
		setMediaPlayer();
		try {
			if (mp != null) {
				mp.start();

			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		try {
			if (mp != null) {
				mp.start();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	private void setMediaPlayer() {
		try {
		//	mMediaPlayer.setDataSource(videoPath[index]);

			mMediaPlayer.setSurface(mSurface);
			//mMediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	/*	catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
		Log.v(TAG, "onSurfaceTextureAvailable");
		mSurfaceTexture = surface;
		mSurface = new Surface(mSurfaceTexture);
		setMediaPlayer();
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
	/**If returns true, no rendering should happen inside the surface texture after this method is invoked. If returns false, the client needs to call SurfaceTexture.release().*/
		Log.v(TAG, "onSurfaceTextureDestroyed");
		return true;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		// TODO Auto-generated method stub

	}

}
