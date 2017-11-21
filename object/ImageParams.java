package com.tg.cloudmanagement.object;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageParams implements Parcelable{
	public String fileName;
	public int position;
	public int resId = -1;
	public String path;
	public String url;
	public ImageParams(){
		
	}
	
	public ImageParams(Parcel p){
		fileName = p.readString();
		path = p.readString();
		position = p.readInt();
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(fileName);
		dest.writeString(path);
		dest.writeInt(position);
	}
	
	public static final Creator<ImageParams> CREATOR = new Creator<ImageParams>(){

		@Override
		public ImageParams createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new ImageParams();
		}

		@Override
		public ImageParams[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ImageParams[size];
		}
		
	};

}
