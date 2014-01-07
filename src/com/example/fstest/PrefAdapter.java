package com.example.fstest;

import java.util.ArrayList;
import java.util.List;

import com.example.fstest.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class PrefAdapter extends BaseAdapter
{
	private ArrayList<PrefEntry> entries;
	private LayoutInflater inflater;
	private Context activity;
	
	public PrefAdapter(Context context) 
	{
		inflater=LayoutInflater.from(context);
		activity=context;
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
			//holder.valueTxt=(TextView) convertView.findViewById(R.id.tv_value);
			holder.valueSp=(Spinner)convertView.findViewById(R.id.sp_value);
			convertView.setTag(holder);
		}
		else 
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		PrefEntry entry=entries.get(position);
		Log.d("Debug", entry.getEntry()+" "+String.valueOf(entry.getValue()));
		holder.entryTxt.setText(entry.getEntry());
		//holder.valueTxt.setText(entry.getValue());
		List<String> list=new ArrayList<String>();
		list.add("Neutral");
		list.add("Like");
		list.add("Dislike");
		list.add("Avoid");
		holder.valueSp.setId(position); //Andrà bene?? Potrebbe creare collisioni?
		holder.valueSp.setAdapter(new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item,list));
		holder.valueSp.setSelection(entry.getValue());
		holder.valueSp.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,int pos, long id) 
			{
				//Questo evento accade DOPO il getView che per qualche motivo viene eseguito di nuovo al termine di selezione item di uno spinner
				//il getView viene eseguito non una ma ben due volte??
				//Le preferenze vengono salvate comunque dopo aver premuto il pulsante apposita
				entries.get(adapter.getId()).setValue(pos);
				Log.d("Debug", "Cambio!");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) 
			{
				
			}
			
		});
		return convertView;
	}
	
	static class ViewHolder 
	{
		TextView entryTxt;
		//TextView valueTxt;
		Spinner valueSp;
	}
	
	public ArrayList<PrefEntry> getNewPrefs()
	{
		return entries;
	}
}
