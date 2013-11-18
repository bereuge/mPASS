package com.example.fstest.log;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AndroidException;

public class LogDbManager 
{
	private SQLiteHelper sqlHelper;
	private SQLiteDatabase sqlDatabase;
	private Context context;
	
	private static final String database_name="Log";
	private static final int database_version=1;
	private static final String database_table="main";
	
	public LogDbManager(Context _context)
	{
		context=_context;
	}
	
	public LogDbManager openToRead() throws android.database.SQLException
	{
		sqlHelper=new SQLiteHelper(context, database_name, null, database_version);
		sqlDatabase=sqlHelper.getReadableDatabase();
		return this;
	}
	
	public LogDbManager openToWrite() throws android.database.SQLException
	{
		sqlHelper=new SQLiteHelper(context, database_name, null, database_version);
		sqlDatabase=sqlHelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
		sqlHelper.close();
	}
	
	public long insertEntry(String action, String date)
	{
		ContentValues values=new ContentValues();
		int id=getMaxId()+1;
		values.put("id", id);
		values.put("action", action);
		values.put("date", date);
		return sqlDatabase.insert(database_name, null, values);
	}
	
	public int deleteAll()
	{
		return sqlDatabase.delete(database_name, null, null);
	}
	
	public Cursor queueAll()
	{
		String[] columns=new String[]{"id","action","date"};
		Cursor cursor=sqlDatabase.query(database_name,columns,null,null,null,null,"date DESC");
		return cursor;
	}
	
	public ArrayList<LogEntry> getAllEntries()
	{
		ArrayList<LogEntry> entries=new ArrayList<LogEntry>();
		
		Cursor cursor=queueAll();
		if (cursor.getCount()>0)
		{
			while (cursor.moveToNext())
			{
				LogEntry entry=new LogEntry(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
				entries.add(entry);
			}
		}
		
		return entries;
	}
	
	private int getMaxId()
	{
		int id=0;
		final String query="SELECT MAX(id) as id FROM Log";
		try
		{
			Cursor cursor=sqlDatabase.rawQuery(query, null);
			if (cursor.getCount()>0)
			{
				cursor.moveToFirst();
				id=cursor.getInt(cursor.getColumnIndex("id"));
			}
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		return id;
	}
}
