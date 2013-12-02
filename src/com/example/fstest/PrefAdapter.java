package com.example.fstest;

import java.util.ArrayList;

import com.example.fstest.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PrefAdapter extends BaseAdapter
{
	private ArrayList<PrefEntry> entries;
	private LayoutInflater inflater;
	
	public PrefAdapter(Context context) 
	{
		inflater=LayoutInflater.from(context);
    }
	
	public void setData(ArrayList<PrefEntry> list) 
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
			convertView	=  inflater.inflate(R.layout.pref_list, null);
			holder = new ViewHolder();
			holder.entryTxt=(TextView) convertView.findViewById(R.id.tv_entry);
			holder.valueTxt=(TextView) convertView.findViewById(R.id.tv_value);
			convertView.setTag(holder);
		}
		else 
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		PrefEntry entry=entries.get(position);
		holder.entryTxt.setText(entry.getEntry());
		holder.valueTxt.setText(entry.getValue());
		
		return convertView;
	}

	static class ViewHolder 
	{
		TextView entryTxt;
		TextView valueTxt;
	}
}
