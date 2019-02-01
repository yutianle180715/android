package org.droidtv.mychoice;

import android.graphics.Bitmap;
import android.media.tv.TvContract;

public class Channel_Theme_Data {

	private Bitmap channelLogo;
	private int skip;
	private int scramble;
	private int lock;
	private int dispNum;
	private int themeNum;
	private int active;
	private int itemType;//0: channel, 1: source , 2: apk
	public int getItemType(){
		return itemType;
	}
	public void setitemType(int value) {
		this.itemType = value;
	}
	public Bitmap getChannelLogo() {
		return channelLogo;
	}

	public void setChannelLogo(Bitmap channelLogo) {
		this.channelLogo = channelLogo;
	}

	public int getChannelNum() {
		return dispNum;
	}

	public void setChannelNum(int channelNum) {
		this.dispNum = channelNum;
	}

	public int getThemeNum() {
		return themeNum;
	}

	public void setThemeNum(int channelNum) {
		this.themeNum = channelNum;
	}

	/* YanFu 2017/06/14 fix  [TF415PHIEUMTK03-1671] (Mychoice)change location of channel in Mychoice group,the result would recovery to original after pagedown */
	public int setThemeActive(int channelactive) {
		return active = channelactive;
	}

	public int getThemeActive() {
		return active;
	}

	Channel_Theme_Data(Bitmap res, int num, int theme, int channellock, int channelscramble, int channelskip,
			int channelactive,int value) {
		channelLogo = res;
		dispNum = num;
		themeNum = theme;
		lock = channellock;
		scramble = channelscramble;
		skip = channelskip;
		active = channelactive;
		itemType = value;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String display = " channelNum: " + dispNum + " themeNumber : " + themeNum + " lock: " + lock + "scramble:"
				+ scramble + "skip" + skip;
		return display;
	}

}
