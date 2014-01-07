package com.example.fstest;

public class PrefEntry 
{
	private String entry;
	//private String value;
	private int value; //0 Neutral 1 Like 2 Dislike 3 Avoid
	
	public PrefEntry(String _entry, int _value)
	{
		entry=_entry;
		value=_value;
	}
	
	public void setEntry(String _entry)
	{
		entry=_entry;
	}
	
	public String getEntry()
	{
		return entry;
	}
	
	public void setValue(int _value)
	{
		value=_value;
	}
	
	public int getValue()
	{
		return value;
	}
}
