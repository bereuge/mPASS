package com.example.fstest.log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper
{
	private String q_create="create table Log " +
			"(" +
			"id integer primary key, " +
			"action string not null, " +
			"date string not null" +
			");";

	public SQLiteHelper(Context context, String name, CursorFactory factory, int version) 
	{
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		db.execSQL(q_create);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		db.execSQL("DROP TABLE IF EXISTS Log");
		onCreate(db);
	}

}
