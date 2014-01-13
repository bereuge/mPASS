package com.example.fstest;

import com.example.fstest.foursquare.FsqVenue;
import com.example.fstest.utils.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class NewVenueActivity extends Activity 
{
	private String maxid;
	private GoogleMap mMap;
	private GPSTracker gps;
	private LatLng selectedLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_venue);
		gps=new GPSTracker(this);
		selectedLocation=new LatLng(gps.getLatitude(), gps.getLongitude());
		mMap=((MapFragment)getFragmentManager().findFragmentById(R.id.map2)).getMap();
		mMap.setMyLocationEnabled(true);
		setUpMapIfNeeded();
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLatitude(),gps.getLongitude()) , 15.0f));
		mMap.setOnMapClickListener(new OnMapClickListener() 
		{
			@Override
			public void onMapClick(LatLng point) 
			{
				mMap.clear();
				mMap.addMarker(new MarkerOptions()
				.position(point)
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
				selectedLocation=point;
			}
		});
		maxid=getIntent().getExtras().getString("maxid");
		Log.d("Debug", maxid);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_venue, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
        	case R.id.item_verify:EditText input=(EditText)findViewById(R.id.et_newvenue);
        						  if (input.getText().toString().isEmpty())
        							  Toast.makeText(NewVenueActivity.this, "Nome del luogo mancante!", Toast.LENGTH_SHORT).show();
        						  else
        						  {
	        						  maxid=maxid.substring(4);
						    		  maxid=maxid.substring(0, maxid.length()-2);
						    		  int newid=Integer.parseInt(maxid)+1;
						    		  final String snewid="NF"+Integer.toString(newid);
	        						  FsqVenue newvenue=new FsqVenue();
									  newvenue.name=input.getText().toString();
									  newvenue.latitude=selectedLocation.latitude;
									  newvenue.longitude=selectedLocation.longitude;
									  newvenue.direction=0;
									  newvenue.address=" ";
									  newvenue.id=snewid;
									  newvenue.distance="";
									  newvenue.type="";
									  Intent quiz_intent=new Intent(NewVenueActivity.this, QuizActivity.class);
									  quiz_intent.putExtra("venue", newvenue);
									  NewVenueActivity.this.startActivity(quiz_intent);
									  finish();
        						  }
        					      break;
        	default:break;
        }
        return true;
    }
	
	private Boolean setUpMapIfNeeded()
    {
    	Boolean needed=false;
    	if (mMap == null) 
    	{
    		needed=true;
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map2))
                                .getMap();
            if (mMap != null) {}
                // The Map is verified. It is now safe to manipulate the map.

    	} 
    	return needed;
    }
}
