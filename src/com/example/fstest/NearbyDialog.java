package com.example.fstest;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.example.fstest.foursquare.FsqVenue;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

//Classe per il dialog che mostra le segnalazioni per un dato luogo
public class NearbyDialog extends Dialog implements OnClickListener
{
	private Activity activity;
	private ArrayList<FsqVenue> nearby_list;
	private ListView lv_nearby;
	private NearbyAdapter adapter;
	
	public NearbyDialog(Activity _activity, ArrayList<FsqVenue> _nearby_list)
	{
		super(_activity);
		
		activity=_activity;
		nearby_list=_nearby_list;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.nearby_layout);
	    setTitle("Nearby Venues");
	    adapter=new NearbyAdapter(activity);
	    lv_nearby=(ListView)findViewById(R.id.lv_nearby);
	    adapter.setData(nearby_list);
		lv_nearby.setAdapter(adapter);
		
		lv_nearby.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long d) 
			{
				Intent quiz_intent=new Intent(activity, QuizActivity.class);
				FsqVenue venue=new FsqVenue();
				venue=nearby_list.get(position);
				quiz_intent.putExtra("venue", venue);
				activity.startActivity(quiz_intent);
				dismiss();
			}
		});
	}
	
	@Override
	public void onClick(View v) 
	{
		dismiss();
	}

}
