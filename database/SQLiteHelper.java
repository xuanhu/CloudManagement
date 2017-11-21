package com.tg.cloudmanagement.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 数据库
 * @author Administrator
 *
 */
public class SQLiteHelper extends SQLiteOpenHelper{
	private static final int version = 1;
	private static final String DATABASE_NAME = "park.db";
	private static final String TABLE = "Respone";
	private String SQLCreate = "create table if not exists Respone( key char(1000),respone text)";
	private SQLiteDatabase db;
	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(SQLCreate);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		onCreate(db);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
		this.db = db;
	}
	
	public String getCacheRespone(String apiName,String... params){
		
		return null;
	}
}
