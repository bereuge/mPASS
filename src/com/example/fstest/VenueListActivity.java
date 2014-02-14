package com.example.fstest;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.example.fstest.fusiontables.FTClient;
import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VenueListActivity extends Activity 
{
	private FTClient ftclient;
	private ProgressDialog spinner;
	private ArrayList<FtVenue> list_venues;
	private EditText edit_query;
	private TextView tv_num_venues;
	private Menu _menu;
	private int count;
	private String tv_text="Visualizzando @MIN-@MAX su @COUNT elementi.";
	
	private String query_all="SELECT ROWID, fsqid, name, geo, accessLevel FROM "+Costants.tableId+" OFFSET @OFFSET LIMIT 100";
	private String query_search="SELECT ROWID, fsqid, name, geo, accessLevel FROM "+Costants.tableId+" WHERE name CONTAINS IGNORING CASE '@NAME'";
	private String query_num="SELECT COUNT() FROM "+Costants.tableId;
	
	private int offset=0;
	private boolean end=false;
	
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
        
        ftclient.setQuery(query_num);
        ftclient.queryOnNewThread("countvenues");
        
        tv_num_venues=(TextView)findViewById(R.id.tv_num_venues);
        edit_query=(EditText)findViewById(R.id.et_queryname);
        ImageButton btn_query=(ImageButton)findViewById(R.id.btn_query);
        
        btn_query.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				String query=edit_query.getText().toString();
				if (query.equals("")) Toast.makeText(VenueListActivity.this, "Inserisci un nome da cercare!", Toast.LENGTH_SHORT).show();
				else 
				{
					query=query.replace(" ", "%20");
					String temp_query=query_search.replace("@NAME", query);
					ftclient.setQuery(temp_query);
					ftclient.queryOnNewThread("venuelist");
					spinner.show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.venue_list, menu);
		_menu=menu;
		return true;
	}

	public void loadList(JSONArray venues)
	{
		FtVenue temp;
		String fsqid, name, acl, ll;
		LatLng geo;
		double lat,lng;
		
		list_venues.clear(); //Svuotiamo la lista delle venue
		
		//Geocoder gc = new Geocoder(this, Locale.getDefault());
		if (venues!=null)
		{
			Log.d("Debug", String.valueOf(venues.length()));
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
					/*
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
					*/
					
					list_venues.add(temp);
				} 
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
		{
			Toast.makeText(VenueListActivity.this, "Nessun luogo trovato.", Toast.LENGTH_SHORT).show();
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
	
	public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
        	case R.id.action_left:if (offset>0) 
        						  {
        							  if (end) 
        							  {
        								  end=false;
        						      }
        							  offset-=100;
							          ftclient.setQuery(query_all.replace("@OFFSET", String.valueOf(offset)));
							          ftclient.queryOnNewThread("venuelist");
							          spinner.show();
							          reloadUI();
        						  }
        						  break;
        	
        	case R.id.action_right:if (!end)
        						   {
        							   offset+=100;
	        						   if (offset+200<count)
	        						   {
		        						   end=false;
	        						   }
							           else
							           {
							        	   end=true;
							           }
							           ftclient.setQuery(query_all.replace("@OFFSET", String.valueOf(offset)));
							           ftclient.queryOnNewThread("venuelist");
							           spinner.show();
							           reloadUI();
        						   }
        						   break;
        	default:break;
        }
        return true;
    }
	
	public void loadNumVenues(String scount)
	{
		scount=scount.substring(2, scount.length()-2);
		count=Integer.parseInt(scount);
		reloadUI();
		ftclient.setQuery(query_all.replace("@OFFSET", String.valueOf(offset)));
        ftclient.queryOnNewThread("venuelist");
	}
	
	private void reloadUI()
	{
		String s_num=tv_text.replace("@MIN", String.valueOf(offset));
		if (end) s_num=s_num.replace("@MAX", String.valueOf(offset+count-offset));
		else s_num=s_num.replace("@MAX", String.valueOf(offset+100));
		s_num=s_num.replace("@COUNT", String.valueOf(count));
		tv_num_venues.setText(s_num);
		
		MenuItem left=_menu.findItem(R.id.action_left);
		if (offset==0) 
		{
			left.getIcon().setAlpha(130);
			left.setEnabled(false);
		}
		else
		{
			left.getIcon().setAlpha(255);
			left.setEnabled(true);
		}
		
		MenuItem right=_menu.findItem(R.id.action_right);
		if (end) 
		{
			right.getIcon().setAlpha(130);
			right.setEnabled(false);
		}
		else 
		{
			right.getIcon().setAlpha(255);
			right.setEnabled(true);
		}
	}
}
