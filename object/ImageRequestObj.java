package com.tg.cloudmanagement.object;


public class ImageRequestObj {
	public String url;
	public int requestCode;
	public int groupPosition;
	public int childPosition;
	public ImageRequestObj(String url,
			int requestCode) {
		super();
		this.url = url;
		this.requestCode = requestCode;
	}
	
}
