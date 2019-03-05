package org.droidtv.welcome.configsettings.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import org.droidtv.welcome.data.manager.WelcomeDataManager;
import org.droidtv.welcome.configsettings.ui.MultiSliderNode;

public class SidePanelHighlightedTextColorNode extends MultiSliderNode {

    private int mCurrentColor;

    public SidePanelHighlightedTextColorNode(Context context) {
        this(context, null);
    }

    public SidePanelHighlightedTextColorNode(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SidePanelHighlightedTextColorNode(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        mCurrentColor = WelcomeDataManager.getInstance().getSidePanelHighlightedTextColor();
    }

    public void setCurrentColor(int currentColor) {
        mCurrentColor = currentColor;
    }

    public int getCurrentColor() {
        return mCurrentColor;
    }


    protected int[] getSliderMaximumValues() {
        return getContext().getResources().getIntArray(org.droidtv.welcome.R.array.color_max_array);
    }

    @Override
    protected int[] getSliderMinimumValues() {
        return getContext().getResources().getIntArray(org.droidtv.welcome.R.array.color_min_array);
    }

    @Override
    protected int[] getSliderStepSizes() {
        return getContext().getResources().getIntArray(org.droidtv.welcome.R.array.color_step_array);
    }

    @Override
    protected String[] getSliderLabels() {
        return getContext().getResources().getStringArray(org.droidtv.welcome.R.array.color_label_array);
    }

    @Override
    protected int[] getSliderValues() {
        return getColorValue();
    }

    @Override
    public void setColorValue(Context context, int index, int value) {
        int color = mCurrentColor;
        int A = Color.alpha(color);
        int R = Color.red(color);
        int G = Color.green(color);
        int B = Color.blue(color);
        switch (index) {
            case 0:
                color = Color.argb(A, value, G, B); //Red
                break;
            case 1:
                color = Color.argb(A, R, value, B);//Green
                break;
            case 2:
                color = Color.argb(A, R, G, value); //Blue
                break;
            case 3:
                value = (value * 255) / 100;
                color = Color.argb(value, R, G, B); // Opacity
                break;
        }
        mCurrentColor = color;
        WelcomeDataManager.getInstance().changeSidePanelHighlightedTextColor(color);
    }

    @Override
    public int[] getColorValue() {
        int alpha = Color.alpha(mCurrentColor);
        int opacity = (int) Math.rint((alpha * 100) / 255d);
        int red = Color.red(mCurrentColor);
        int green = Color.green(mCurrentColor);
        int blue = Color.blue(mCurrentColor);
        return new int[]{red, green, blue, opacity};
    }

    public void onDismissed() {
    //	WelcomeDataManager.getInstance().saveSidePanelHighlightedTextColor(mCurrentColor);
    }

}
