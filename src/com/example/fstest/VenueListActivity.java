package com.example.fstest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import com.example.fstest.fusiontables.FTClient;
import com.google.android.gms.maps.model.LatLng;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class VenueListActivity extends Activity 
{
	private FTClient ftclient;
	private ProgressDialog spinner;
	private ArrayList<FtVenue> list_venues;
	
	private String query_all="SELECT ROWID, fsqid, name, geo, accessLevel FROM "+Costants.tableId+" LIMIT 100";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_venue_list);
		
		list_venues=new ArrayList<FtVenue>();
		
		ftclient=new FTClient(this);
		
		spinner=new ProgressDialog(this);
        spinner.setMessage("Caricamento...");
        spinner.setCancelable(false);
        spinner.setMax(100); 
        spinner.setProgress(0); 
        spinner.show();
        
        ftclient.setQuery(query_all);
        ftclient.queryOnNewThread("venuelist");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.venue_list, menu);
		return true;
	}

	public void loadList(JSONArray venues)
	{
		FtVenue temp;
		String fsqid, name, acl, ll;
		LatLng geo;
		double lat,lng;
		
		Geocoder gc = new Geocoder(this, Locale.getDefault());
		
		for (int i=0; i<venues.length(); i++)
		{
			try 
			{
				JSONArray row=venues.getJSONArray(i);
				fsqid=row.get(1).toString();
				name=row.get(2).toString();
				ll=row.get(3).toString();
				String[] lls=ll.split("\\,");
				lat=Double.parseDouble(lls[0]);
				lng=Double.parseDouble(lls[1]);
				geo=new LatLng(lat, lng);
				acl=row.get(4).toString();
				
				temp=new FtVenue(fsqid, name, geo, acl);
				
				//Proviamo a fare un po' di reverse geocoding
				//È PESANTISSIMO!!!!!
				try 
				{
					List<Address> addresses = gc.getFromLocation(lat, lng, 1);
					if (addresses.size()>0)
					{
						temp.setCity(addresses.get(0).getLocality());
						temp.setAddress(addresses.get(0).getAddressLine(0));
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				
				
				list_venues.add(temp);
			} 
			catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		VenuesAdapter adapter=new VenuesAdapter(this);
		ListView lv_venues=(ListView)findViewById(R.id.lv_venues);
		adapter.setData(list_venues);
		lv_venues.setAdapter(adapter);
		
		lv_venues.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long d) 
			{
				FtVenue venue=list_venues.get(position);
				ftclient.setQuery("SELECT ROWID, name, accessLevel, comment, doorways, elevator, escalator, parking, user, date FROM "+Costants.tableId+" WHERE fsqid='"+venue.getFsqId()+"'");
				spinner.setMessage("Caricamento dati...");
				spinner.show();
				ftclient.queryOnNewThread("loadvenuelist");
			}
		});
		
		spinner.dismiss();
		
	}
	
	public void showInfoDialog (JSONArray venues, String acl)
    {
    	spinner.dismiss();
    	InfoDialog idialog = null;
    	if (acl.equals("A"))
    	{
    		idialog=new InfoDialog((Activity)this, venues, R.style.InfoDialogGreen);
    	}
    	else if (acl.equals("P"))
    	{
    		idialog=new InfoDialog((Activity)this, venues, R.style.InfoDialogYellow);
    	}
    	else if (acl.equals("N"))
    	{
    		idialog=new InfoDialog((Activity)this, venues, R.style.InfoDialogRed);
    	}
    	else
    	{
    		idialog=new InfoDialog((Activity)this, venues, R.style.InfoDialogGray);
    	}
		idialog.show();
    }
}
