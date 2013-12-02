package com.example.fstest;

public class PrefEntry 
{
	private String entry;
	private String value;
	
	public PrefEntry(String _entry, String _value)
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
	
	public void setValue(String _value)
	{
		value=_value;
	}
	
	public String getValue()
	{
		return value;
	}
}
