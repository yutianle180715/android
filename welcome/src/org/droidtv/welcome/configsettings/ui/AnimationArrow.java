package org.droidtv.welcome.configsettings.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import org.droidtv.welcome.R;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class AnimationArrow extends RelativeLayout {

	private ImageView mNextIndicatorImageView;
	private Animation mNextIndicatorTranslationAnimation;

	
	
	public AnimationArrow(Context context, AttributeSet attrs) {
		super(context, attrs);
		 initialize();
	}	  
    
	 private void initialize() {
		 mNextIndicatorImageView = (ImageView) findViewById(R.id.animation_arrow);
		 if (isRighToleft()) {
	         float rotation = 180;
	         mNextIndicatorImageView.setRotation(rotation);
	     }
		 
        mNextIndicatorTranslationAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.cast_indicator_anim);
        mNextIndicatorImageView.startAnimation(mNextIndicatorTranslationAnimation);
	 }
	 
	   private boolean isRighToleft() {
	        Configuration config = getResources().getConfiguration();
	        return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
	    }
	 
    
}
