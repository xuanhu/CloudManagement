package com.tg.cloudmanagement.object;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class SlideItemObj implements Parcelable{
	public String name = "";
	public String id = "";
	public String description="";
	public boolean isChecked = false;
	public SlideItemObj(String name, String id) {
		super();
		this.name = name;
		this.id = id;
	}
	private SlideItemObj(Parcel in) {
		name = in.readString();
		id = in.readString();
		description = in.readString();
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(name);
		dest.writeString(id);
		dest.writeString(description);
	}
	public static final Creator<SlideItemObj> CREATOR
    	= new Creator<SlideItemObj>() {
		@Override
		public SlideItemObj createFromParcel(Parcel in) {
		    return new SlideItemObj(in);
		}
		
		@Override
		public SlideItemObj[] newArray(int size) {
		    return new SlideItemObj[size];
		}
	};
	
	public boolean equals(Object o) {
		SlideItemObj obj = (SlideItemObj)o;
		if(obj != null && obj.id == this.id){
			return true;
		}
		return false;
	}
}
