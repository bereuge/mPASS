package com.example.fstest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class User 
{
	private static final String name="name";
	private static final String type="user";
	private static final String imagePath="path";
	private static final String nreports="nreports";
	
	private SharedPreferences pref;
	
	public User (Context context)
	{
		pref=context.getSharedPreferences("user", Context.MODE_PRIVATE);
	}
	
	public void setName(String _name)
	{
		Editor editor = pref.edit();
		editor.putString(name, _name);
		editor.commit();
	}
	
	public void setType(String _type)
	{
		Editor editor = pref.edit();
		editor.putString(type, _type);
		editor.commit();
	}
	
	public void setImagePath(String _imagePath)
	{
		Editor editor = pref.edit();
		editor.putString(imagePath, _imagePath);
		editor.commit();
	}
	
	public void addReport()
	{
		int tempreports=getNReports();
		Editor editor = pref.edit();
		editor.putInt(nreports, tempreports+1);
		editor.commit();
	}
	
	public String getName()
	{
		return pref.getString(name, null);
	}
	
	public String getType()
	{
		return pref.getString(type, null);
	}
	
	public String getImagePath()
	{
		return pref.getString(imagePath, null);
	}
	
	public int getNReports()
	{
		return pref.getInt(nreports, 0);
	}
}
