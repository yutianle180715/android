package org.droidtv.mychoice;

import android.graphics.Bitmap;
import android.media.tv.TvContract;

public class ChannelData {

	private Bitmap channelLogo;
	private int channelNum;
	private String channelName;
	private long channelId;
	private int version_number;
	private boolean skip;
	private boolean scramble;
	private boolean lock;
	private int itemType;//0: channel, 1: source , 2: apk
	private int mychoicefree = 1;
	private int mychoicepkg1 = 0;
	private int mychoicepkg2 = 0;
	public int getMychoicefree() {
		return mychoicefree;
	}
	public void setMychoicefree(int mychoicefree) {
		this.mychoicefree = mychoicefree;
	}
	public int getMychoicepkg1() {
		return mychoicepkg1;
	}
	public void setMychoicepkg1(int mychoicepkg1) {
		this.mychoicepkg1 = mychoicepkg1;
	}
	public int getMychoicepkg2() {
		return mychoicepkg2;
	}
	public void setMychoicepkg2(int mychoicepkg2) {
		this.mychoicepkg2 = mychoicepkg2;
	}
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
		return channelNum;
	}

	public void setChannelNum(int channelNum) {
		this.channelNum = channelNum;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public long getChannelId() {
		return channelId;
	}

	public void setChannelId(long channelId) {
		this.channelId = channelId;
	}

	public void setVersionNumber(int version_number) {
		this.version_number=version_number;
	}
		public int getVersionNumber() {
		return version_number;
	}
		public void setScramble(boolean scramble) {
		this.scramble=scramble;
	}
		public boolean getScramble() {
		return scramble;
	}
			public void setSkip(boolean skip) {
		this.skip=skip;
	}
		public boolean getSkip() {
		return skip;
	}
				public void setLock(boolean lock) {
		this.lock=lock;
	}
		public boolean getLock() {
		return lock;
	}


	public ChannelData(Bitmap res, int num, String name, long channelId,int version_number, boolean scramble,boolean skip,boolean lock,int value) {
		channelLogo = res;
		channelNum = num;
		channelName = name;
		this.channelId = channelId;
		this.version_number=version_number;
		this.scramble=scramble;
		this.skip=skip;
		this.lock=lock;
		itemType = value;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String display = " channelNum: " + channelNum + " channelName : " + channelName + " id: " + channelId+"version number:"+version_number;
		return display;
	}

}
