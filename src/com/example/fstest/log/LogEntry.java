package com.example.fstest.log;

public class LogEntry 
{
	private int id;
	private String action;
	private String date;
	
	public LogEntry(int _id, String _action, String _date)
	{
		id=_id;
		action=_action;
		date=_date;
	}
	
	public void setId(int _id)
	{
		id=_id;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setAction(String _action)
	{
		action=_action;
	}
	
	public String getAction()
	{
		return action;
	}
	
	public void setDate(String _date)
	{
		date=_date;
	}
	
	public String getDate()
	{
		return date;
	}
}
