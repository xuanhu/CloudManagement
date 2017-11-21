package com.tg.cloudmanagement.object;

import java.util.ArrayList;
import java.util.HashMap;

public class HttpResultObj {
	public HashMap<String,String> headMap;
	public ArrayList<HashMap<String,String>> bodyList;
	public HttpResultObj(HashMap<String, String> headMap,
			ArrayList<HashMap<String, String>> bodyList) {
		super();
		this.headMap = headMap;
		this.bodyList = bodyList;
	}
	
}
