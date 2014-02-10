package com.example.fstest;

import java.util.ArrayList;

import com.example.fstest.foursquare.FoursquareApp;
import com.example.fstest.foursquare.FsqVenue;
import com.example.fstest.foursquare.FoursquareApp.FsqAuthListener;
import com.example.fstest.fusiontables.FTClient;
import com.example.fstest.utils.GPSTracker;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NearbyActivity extends Activity 
{
	private FoursquareApp fsqApp;
	private ProgressDialog spinner;
	private GPSTracker gps;
	private ArrayList<FsqVenue> nearbyList;
	private FTClient ftclient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nearby);
		
		ftclient=new FTClient(this);
		
		spinner=new ProgressDialog(this);
        spinner.setMessage("Caricamento...");
        spinner.setCancelable(false);
        spinner.setMax(100); 
        spinner.setProgress(0); 
        spinner.show();
		
		fsqApp = new FoursquareApp(this, Costants.CLIENT_ID, Costants.CLIENT_SECRET);
        FsqAuthListener listener = new FsqAuthListener() 
        {
        	@Override
         	public void onSuccess() 
        	{
         	}
        
        	@Override
        	public void onFail(String error) 
        	{
        	}
        };
        fsqApp.setListener(listener);
        nearbyList=new ArrayList<FsqVenue>();
        gps=new GPSTracker(this);
        if (gps.canGetLocation()) 
        	loadNearbyPlaces(gps.getLatitude(), gps.getLongitude(),"");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nearby, menu);
		//MenuItem searchItem = menu.findItem(R.id.action_search);
		
		/*MenuItem item_addvenue=(MenuItem)findViewById(R.id.action_addvenue);
		item_addvenue.setOnMenuItemClickListener(new OnMenuItemClickListener() 
		{
			@Override
			public boolean onMenuItemClick(MenuItem item) 
			{
				ftclient.setQuery("SELECT fsqid FROM "+Costants.tableId+" WHERE fsqid LIKE \NF%25\ ORDER BY fsqid DESC LIMIT 1");
				ftclient.queryOnNewThread("lastid");
				return false;
			}
		});*/
		final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() 
		{
			@Override
			public boolean onQueryTextSubmit(String query) 
			{
				searchView.setQuery("", false);
				searchView.setIconified(true); 
				//Toast.makeText(NearbyActivity.this, "No nearby places available", Toast.LENGTH_SHORT).show();
				if (gps.canGetLocation()) 
		        	loadNearbyPlaces(gps.getLatitude(), gps.getLongitude(), query);
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) 
			{
				// TODO Auto-generated method stub
				return false;
			}
		});
		/*searchView.setOnCloseListener(new OnCloseListener() 
		{
			@Override
			public boolean onClose() 
			{
				Toast.makeText(NearbyActivity.this, "No nearby places available", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		*/
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
        	case R.id.action_addvenue://ftclient.setQuery("SELECT fsqid FROM "+Costants.tableId+" WHERE fsqid LIKE \NF%25\ ORDER BY fsqid DESC LIMIT 1");
        	//ftclient.setQuery("SELECT MAXIMUM(CAST(SUBSTRING(fsqid,3,length(fsqid)-2))) FROM "+Costants.tableId);
        		ftclient.setQuery("SELECT MAXIMUM(fsqid) FROM "+Costants.tableId);
        							  ftclient.queryOnNewThread("lastid");
        						      break;
        	default:break;				    
        }
		return true;
    }
	
	private void loadNearbyPlaces(final double latitude, final double longitude, final String query) 
    {
    	spinner.show();
    	new Thread() 
    	{
    		@Override
    		public void run() 
    		{
    			int what = 0;
    			try 
    			{
    				if (query.isEmpty())
    				{
    					Log.d("Debug","Ricerca normale");
    					nearbyList = fsqApp.getNearby(latitude, longitude);
    				}
    				else
    				{
    					Log.d("Debug","Ricerca con query");
    					nearbyList = fsqApp.getNearbyWithQuery(latitude, longitude, query);
    				}
    			} 
    			catch (Exception e) 
    			{
    				what = 1;
    				e.printStackTrace();
    			}
    			mHandler.sendMessage(mHandler.obtainMessage(what));
    		}
    	}.start();
    }
     
     
    private Handler mHandler = new Handler() 
    {
	    @Override
	    public void handleMessage(Message msg) 
	    {
	    	spinner.dismiss();
	        if (msg.what == 0) 
	        {
	        	if (nearbyList.size() == 0) 
	        		Toast.makeText(NearbyActivity.this, "No nearby places available", Toast.LENGTH_SHORT).show();
	      		else
	      		{
	      			/*NearbyDialog ndialog=new NearbyDialog(NearbyActivity.this, nearbyList, ftclient);
	      			ndialog.show();*/
	      			//Toast.makeText(NearbyActivity.this, "ok", Toast.LENGTH_SHORT).show();
	      			loadList();
	      		}
	        	/*mAdapter.setData(mNearbyList);
	      		mListView.setAdapter(mAdapter);*/
	      	}
	      	else 
	      	{
	      		Toast.makeText(NearbyActivity.this, "Failed to load nearby places", Toast.LENGTH_SHORT).show();
	      	}
	    }
    };
     
    private void loadList()
    {
    	FsqVenue newvenue=new FsqVenue();
 		newvenue.name="Non trovi il luogo che cerchi? Aggiungilo!";
 		newvenue.latitude=0.0;
 		newvenue.longitude=0.0;
 		newvenue.direction=0;
 		newvenue.id="0";
 		newvenue.distance="";
 		newvenue.type="";
 		nearbyList.add(newvenue);
 		
 		NearbyAdapter adapter=new NearbyAdapter(this);
 		ListView lv_nearby=(ListView)findViewById(R.id.lv_nearby2);
	    adapter.setData(nearbyList);
		lv_nearby.setAdapter(adapter);
		
		lv_nearby.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long d) 
			{
				FsqVenue venue=new FsqVenue();
				venue=nearbyList.get(position);
				if (venue.id.equals("0"))
				{
					ftclient.setQuery("SELECT fsqid FROM "+Costants.tableId+" WHERE fsqid LIKE 'NF%25' ORDER BY fsqid DESC LIMIT 1");
					ftclient.queryOnNewThread("lastid");
					//spinner.show();
					//NearbyActivity.this.finish();
					//Bisogna cambiare il codice onPostExecute di "lastid" e fare una nuova activity per la scelta del
					//nome e della posizione del luogo
				}
				else
				{
					Intent quiz_intent=new Intent(NearbyActivity.this, QuizActivity.class);
					quiz_intent.putExtra("venue", venue);
					NearbyActivity.this.startActivity(quiz_intent);
					NearbyActivity.this.finish();
					//dismiss();
				}
			}
		});
    }
    
    public void createNewVenue(String maxid)
    {
    	Intent i=new Intent(NearbyActivity.this, NewVenueActivity.class);
    	Bundle bundle=new Bundle();
    	bundle.putString("maxid", maxid);
    	i.putExtras(bundle);
    	startActivity(i);
    	finish();
    }
}
