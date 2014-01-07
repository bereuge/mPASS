package com.example.fstest.log;

import java.util.ArrayList;

import com.example.fstest.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LogAdapter extends BaseAdapter
{
	private ArrayList<LogEntry> entries;
	private LayoutInflater inflater;
	
	public LogAdapter(Context context) 
	{
		inflater=LayoutInflater.from(context);
    }
	
	public void setData(ArrayList<LogEntry> list) 
	{
		entries = list;
	}
	
	@Override
	public int getCount() 
	{
		return entries.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return entries.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder;
		if (convertView == null) 
		{
			convertView	=  inflater.inflate(R.layout.logentry_list, null);
			holder = new ViewHolder();
			holder.actionTxt=(TextView) convertView.findViewById(R.id.tv_action);
			holder.dateTxt=(TextView) convertView.findViewById(R.id.tv_date);
			convertView.setTag(holder);
		}
		else 
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		LogEntry entry=entries.get(position);
		holder.actionTxt.setText(entry.getAction());
		holder.dateTxt.setText(entry.getDate());
		
		return convertView;
	}

	static class ViewHolder 
	{
		TextView actionTxt;
		TextView dateTxt;
	}
}
