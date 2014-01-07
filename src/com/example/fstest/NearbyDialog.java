package com.example.fstest;

import java.util.ArrayList;

import com.example.fstest.foursquare.FsqVenue;
import com.example.fstest.fusiontables.FTClient;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

//Classe per il dialog che mostra le segnalazioni per un dato luogo
public class NearbyDialog extends Dialog implements OnClickListener
{
	private Activity activity;
	private ArrayList<FsqVenue> nearby_list;
	private ListView lv_nearby;
	private NearbyAdapter adapter;
	private FTClient ftclient;
	
	public NearbyDialog(Activity _activity, ArrayList<FsqVenue> _nearby_list, FTClient _ftclient)
	{
		super(_activity);
		
		activity=_activity;
		nearby_list=_nearby_list;
		ftclient=_ftclient;
		FsqVenue newvenue=new FsqVenue();
		newvenue.name="Non trovi il luogo che cerchi? Aggiungilo!";
		newvenue.latitude=0.0;
		newvenue.longitude=0.0;
		newvenue.direction=0;
		newvenue.id="0";
		newvenue.distance="";
		newvenue.type="";
		nearby_list.add(newvenue);
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
				FsqVenue venue=new FsqVenue();
				venue=nearby_list.get(position);
				if (venue.id.equals("0"))
				{
					ftclient.setQuery("SELECT fsqid FROM "+Costants.tableId+" WHERE fsqid LIKE 'NF%25' ORDER BY fsqid DESC LIMIT 1");
					ftclient.queryOnNewThread("lastid");
					dismiss();
					//Toast.makeText(activity, "Ultima view", Toast.LENGTH_LONG).show();
					/*AlertDialog.Builder ad_venue=new AlertDialog.Builder(activity);
					ad_venue.setTitle("Nuovo luogo");
					ad_venue.setMessage("Inserisci il nome del luogo");
					final EditText input=new EditText(activity);
					ad_venue.setView(input);
					ad_venue.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog, int whichButton) 
						{
							GPSTracker mGPS = new GPSTracker(activity);
							FsqVenue newvenue=new FsqVenue();
							newvenue.name=input.getText().toString();
							newvenue.latitude=mGPS.getLatitude();
							newvenue.longitude=mGPS.getLongitude();
							newvenue.direction=0;
							newvenue.id="0";
							newvenue.distance="";
							newvenue.type="";
							Intent quiz_intent=new Intent(activity, QuizActivity.class);
							quiz_intent.putExtra("venue", newvenue);
							activity.startActivity(quiz_intent);
							dismiss();
							//String value = input.getText();
						}
					});

					ad_venue.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog, int whichButton) 
						{
						     // Canceled.
						}
					});
					ad_venue.show();*/
				}
				else
				{
					Intent quiz_intent=new Intent(activity, QuizActivity.class);
					quiz_intent.putExtra("venue", venue);
					activity.startActivity(quiz_intent);
					dismiss();
				}
			}
		});
	}
	
	@Override
	public void onClick(View v) 
	{
		dismiss();
	}
}
