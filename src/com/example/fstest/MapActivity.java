package com.example.fstest;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import com.example.fstest.foursquare.FoursquareApp;
import com.example.fstest.foursquare.FsqVenue;
import com.example.fstest.foursquare.FoursquareApp.FsqAuthListener;
import com.example.fstest.fusiontables.FTClient;
import com.example.fstest.utils.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MapActivity extends Activity implements Runnable
{
	private FsqVenue venue;
	private GPSTracker gps;
	private GoogleMap mMap;
	private FTClient ftclient;
	private FoursquareApp fsqApp;
	
	private ProgressDialog spinner;
	private Thread thread;  
    private Handler handler;
    private int counter=0;
    private Context context;
    
    private ArrayList<FsqVenue> nearbyList;
    private HashMap<Marker, String> markerIdMap;
    private boolean[] preferences;
    
    private String query_all="SELECT ROWID, fsqid, name, geo, accessLevel FROM "+Costants.tableId;
    private String query_acc="SELECT ROWID, fsqid, name, geo, accessLevel FROM "+Costants.tableId+" WHERE accessLevel in (@VALUES)";
    private String query_door="SELECT ROWID, fsqid, name, geo, accessLevel FROM "+Costants.tableId+" WHERE doorways in (@VALUES)";
    private String query_elev="SELECT ROWID, fsqid, name, geo, accessLevel FROM "+Costants.tableId+" WHERE elevator in (@VALUES)";
    private String query_esc="SELECT ROWID, fsqid, name, geo, accessLevel FROM "+Costants.tableId+" WHERE escalator in (@VALUES)";
    private String query_park="SELECT ROWID, fsqid, name, geo, accessLevel FROM "+Costants.tableId+" WHERE parking in (@VALUES)";
    //private String query_maxid="SELECT fsqid FROM "+Costants.tableId+" WHERE fsqid LIKE 'NF%25' ORDER BY fsqid DESC LIMIT 1";
    
    private Button btn_notif;
    private TextView tv_notif;
    
    //Menu
    private String[] menu;
    private DrawerLayout drawer;
    private ListView mDrawerList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        context=this;
        setContentView(R.layout.activity_map);
        
        nearbyList=new ArrayList<FsqVenue>();
        
        preferences=new boolean[3];
        preferences[0]=true; //Accessibile
        preferences[1]=true; //Parzialmente accessibile
        preferences[2]=true; //Non accessibile
        
        initialize_menu();
        tv_notif=(TextView)findViewById(R.id.tv_notification);
		//tv_notif.setText("Sei vicino a "+venue.name);
		btn_notif=(Button)findViewById(R.id.btn_notification);
        
		ImageButton btn_quiz=(ImageButton)findViewById(R.id.btn_quiz);
		btn_quiz.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				if (gps.canGetLocation())
				{
					double lat=gps.getLatitude();
					double lon=gps.getLongitude();
					loadNearbyPlaces(lat, lon);
				}
			}
		});
		
        gps=new GPSTracker(this);
        //Test spinner , in teoria dovrebbe vedersi prima del caricamento della mappa ma non funziona
        spinner=new ProgressDialog(this);
        spinner.setMessage("Caricamento...");
        spinner.setCancelable(false);
        spinner.setMax(100); 
        spinner.setProgress(0); 
        spinner.show();
        
        //Caricamento mappa normale
        //spinner.dismiss();  
        //setContentView(R.layout.activity_map);
        mMap=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        ftclient=new FTClient(context);
        setUpMapIfNeeded();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.277205,12.191162) , 6.0f));
        mMap.setMyLocationEnabled(true);
        ftclient.setQuery(query_all);
        ftclient.queryOnNewThread("setmarkers");
        
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
        
        ViewGroup mapHost = (ViewGroup) findViewById(R.id.mapView);
        mapHost.requestTransparentRegion(mapHost);
        
        //Caricamento in un altro thread ma rallenta solo di più
        /*handler=new Handler();
        thread=new Thread(this);
        thread.run();*/
        
        /*DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("There is a stair close to you. Can you confirm?").setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show();*/
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map, menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
        	case R.id.item_filter:showFilterDialog();
        					      break;
        	case R.id.item_mapmenu:if (!drawer.isDrawerOpen(mDrawerList))
										drawer.openDrawer(mDrawerList);
									else
										drawer.closeDrawer(mDrawerList);
						            break;
        	case R.id.item_refresh:spinner.show();
        						   ftclient.setQuery(query_all);
            					   ftclient.queryOnNewThread("setmarkers");
        						   break;
        	default:break;
        }
        return true;
    }
    
    private void initialize_menu()
    {
 	   menu=getResources().getStringArray(R.array.drawer_menu);
 	   //if (menu==null) Log.d("Debug", "menu null");
 	   drawer = (DrawerLayout) findViewById(R.id.drawer_layout_map);
 	   //if (drawer==null) Log.d("Debug", "drawer null");
 	   mDrawerList = (ListView) findViewById(R.id.drawer_map);
 	   //if (mDrawerList==null) Log.d("Debug", "mdrawerlist null");
 	   //if (this==null) Log.d("Debug", "this null");
 	   //ArrayAdapter adapter=new ArrayAdapter<String>(this, R.layout.drawer_list_item, menu);
 	   mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, menu));
 	   mDrawerList.setOnItemClickListener(new DrawerItemClickListener(drawer, mDrawerList, MapActivity.this));
    }
    
    //Resetta la mappa se c'è bisogno
    private Boolean setUpMapIfNeeded()
    {
    	Boolean needed=false;
    	if (mMap == null) 
    	{
    		needed=true;
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                                .getMap();
            ftclient.setQuery(query_all);
            ftclient.query("setmarkers");
            if (mMap != null) {}
                // The Map is verified. It is now safe to manipulate the map.

    	} 
    	return needed;
    }
    
    //Procedura per settare i marker dei luoghi nella mappa
    public void setMarkers(JSONArray venues)
    {
    	String name, ll, fsqid, min_fsqid="", accl;
    	float min_distance=3000; //Ne cerco uno solo se è al massimo distante un tot di metri
    	double lat = 0, lng = 0;
    	/*FsqVenue */venue=new FsqVenue();
    	
    	spinner.dismiss();
    	/*Marker tempmarker=mMap.addMarker(new MarkerOptions()
		.position(new LatLng(44.404356,12.19687))
		.title("")
		.snippet("Commento...")
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.solo_scala))
		.draggable(false)
		);*/
    	
    	markerIdMap=new HashMap<Marker, String>(); //associa ad ogni marker un foursquare id, utilizzato per fare query per i singoli luoghi
    	//Manca la gestione di marker doppi
    	/*
    	Random r=new Random();
    	double x=44.485223;
    	double y=11.320076;
    	for (int k=0;k<150;k++)
    	{
    		int j=r.nextInt(4)+2;
			if (j==0)
			{
			Marker marker=mMap.addMarker(new MarkerOptions()
    		.position(new LatLng(r.nextDouble()*0.05+x, r.nextDouble()*0.05+y))
    		.snippet("Commento...")
    		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
    		.draggable(false)
    		);
			}
			else if (j==1)
			{
				Marker marker=mMap.addMarker(new MarkerOptions()
				.position(new LatLng(r.nextDouble()*0.05+x, r.nextDouble()*0.05+y))
	    		.snippet("Commento...")
	    		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
	    		.draggable(false)
	    		);
			}
			else if (j==2)
			{
				Marker marker=mMap.addMarker(new MarkerOptions()
				.position(new LatLng(r.nextDouble()*0.05+x, r.nextDouble()*0.05+y))
	    		.snippet("Commento...")
	    		//.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
	    		.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_casina))
	    		.draggable(false)
	    		);
			}
			else if (j==3)
			{
				Marker marker=mMap.addMarker(new MarkerOptions()
				.position(new LatLng(r.nextDouble()*0.05+x, r.nextDouble()*0.05+y))
	    		.snippet("Commento...")
	    		.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_usergreen))
	    		.draggable(false)
	    		);
			}
			else if (j==4)
			{
				Marker marker=mMap.addMarker(new MarkerOptions()
				.position(new LatLng(r.nextDouble()*0.05+x, r.nextDouble()*0.05+y))
	    		.snippet("Commento...")
	    		.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_userver))
	    		.draggable(false)
	    		);
			}
    	}
    	*/
    	if (venues==null)
    	{
    		Log.d("Debug","Errore nella query");
    		Toast.makeText(this, "Errore nel caricamento della mappa", Toast.LENGTH_LONG).show();
    	}
    	else
    	{
	    	for(int i=0;i<venues.length();i++)
	    	{
	    		try 
	    		{
					JSONArray row=venues.getJSONArray(i);
					fsqid=row.get(1).toString();
					name=row.get(2).toString();
					ll=row.get(3).toString();
					accl=row.get(4).toString();
					String[] lls=ll.split("\\,");
					lat=Double.parseDouble(lls[0]);
					lng=Double.parseDouble(lls[1]);
					Log.d("Test",name);
					
					float temp_distance=distFrom(lat,lng,gps.getLatitude(),gps.getLongitude());
					if (temp_distance<=min_distance)
					{
						venue.id=fsqid;
						venue.name=name;
						venue.distance=String.valueOf(min_distance);
						venue.latitude=lat;
						venue.longitude=lng;
						
						min_fsqid=fsqid;
						min_distance=temp_distance;
						Log.d("Debug",name +" "+min_distance+" "+min_fsqid);
					}
					//Random r=new Random();
					/*int j=r.nextInt(3);
					if (j==0)
					{*/
					if (accl.equals("A"))
					{
						Marker marker=mMap.addMarker(new MarkerOptions()
			    		.position(new LatLng(lat, lng))
			    		.title(name)
			    		.snippet("Commento...")
			    		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
			    		.draggable(false)
			    		);
						markerIdMap.put(marker, fsqid);
					}
					else if (accl.equals("P"))
					{
						Marker marker=mMap.addMarker(new MarkerOptions()
			    		.position(new LatLng(lat, lng))
			    		.title(name)
			    		.snippet("Commento...")
			    		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
			    		.draggable(false)
			    		);
						markerIdMap.put(marker, fsqid);
					}
					else if (accl.equals("N"))
					{
						Marker marker=mMap.addMarker(new MarkerOptions()
			    		.position(new LatLng(lat, lng))
			    		.title(name)
			    		.snippet("Commento...")
			    		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
			    		.draggable(false)
			    		);
						markerIdMap.put(marker, fsqid);
					}
					else
					{
						Marker marker=mMap.addMarker(new MarkerOptions()
			    		.position(new LatLng(lat, lng))
			    		.title(name)
			    		.snippet("Commento...")
			    		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
			    		.draggable(false)
			    		);
						markerIdMap.put(marker, fsqid);
					}
					//}
					/*else if (j==1)
					{
						Marker marker=mMap.addMarker(new MarkerOptions()
			    		.position(new LatLng(lat, lng))
			    		.title(name)
			    		.snippet("Commento...")
			    		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
			    		.draggable(false)
			    		);
						markerIdMap.put(marker, fsqid);
					}
					else
					{
						Marker marker=mMap.addMarker(new MarkerOptions()
			    		.position(new LatLng(lat, lng))
			    		.title(name)
			    		.snippet("Commento...")
			    		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
			    		.draggable(false)
			    		);
						markerIdMap.put(marker, fsqid);
					}
					//markerIdMap.put(marker, fsqid);*/
				} 
	    		catch (JSONException e) 
				{
					e.printStackTrace();
				}
	    	}
	    	if (!min_fsqid.equals(""))
    		{
    			//tv_notif=(TextView)findViewById(R.id.tv_notification);
    			tv_notif.setText("Sei vicino a "+venue.name);
    			//Button btn_notif=(Button)findViewById(R.id.btn_notification);
    			btn_notif.setVisibility(View.VISIBLE);
    			btn_notif.setText("Fai il quiz!");
    			btn_notif.setOnClickListener(new OnClickListener() 
    			{
					@Override
					public void onClick(View arg0) 
					{
						DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() 
						{
				            @Override
				            public void onClick(DialogInterface dialog, int which) 
				            {
				                switch (which)
				                {
				                	case DialogInterface.BUTTON_POSITIVE:
				                		//Yes button clicked
				                		Intent quiz_intent=new Intent(MapActivity.this, QuizActivity.class);
				                		quiz_intent.putExtra("venue", venue);
				        				startActivity(quiz_intent);
				        				btn_notif.setVisibility(View.GONE);
				        			    tv_notif.setVisibility(View.GONE);
				        				btn_notif.setEnabled(false);
				                		tv_notif.setEnabled(false);
				                		break;
	
				                	case DialogInterface.BUTTON_NEGATIVE:
				                		//No button clicked
				                		btn_notif.setVisibility(View.GONE);
				        			    tv_notif.setVisibility(View.GONE);
				        				btn_notif.setEnabled(false);
				                		tv_notif.setEnabled(false);
				                		break;
				                }
				            }
						};

				        AlertDialog.Builder builder = new AlertDialog.Builder(context);
				        builder.setMessage("Ti va di fare un quiz?").setPositiveButton("Yes", dialogClickListener)
				            .setNegativeButton("No", dialogClickListener).show();
					}
				});
    		}
    	}
    	/*if (venues.length()==0)
    	{
    		Log.d("Debug","Errore nella query");
    		ftclient.setQuery(query_all);
            ftclient.query("setmarkers");
            //Non ha senso questa cosa...meglio cavarla! Se il database è vuoto ripete la query all'infinito!
    	}
    	else
    	{
    		
    	}*/
    	
    	mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() 
    	{
			@Override
			public void onInfoWindowClick(Marker marker) 
			{
				String fsqid=markerIdMap.get(marker);
				ftclient.setQuery("SELECT ROWID, name, accessLevel, comment, doorways, elevator, escalator, parking, user, date FROM "+Costants.tableId+" WHERE fsqid='"+fsqid+"'");
				spinner.setMessage("Caricamento dati...");
				spinner.show();
				new Thread()
				{
					@Override
					public void run()
					{
						ftclient.query("loadvenue");
					}
				}.start();
			}
		});
    }

    @Override
    public void onBackPressed() 
    {
    	
    }
    
    //Callback eseguito dopo il completamento della query, mostra le info sul luogo
    public void showInfoDialog (JSONArray venues)
    {
    	spinner.dismiss();
    	InfoDialog idialog=new InfoDialog((Activity)context, venues);
		idialog.show();
    }
    
    private void showFilterDialog()
    {
    	FilterDialog fdialog=new FilterDialog((Activity)context, preferences);
    	fdialog.setCanceledOnTouchOutside(false);
    	fdialog.show();
    }
    
    public static float distFrom(double lat1, double lng1, double lat2, double lng2) 
    {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        int meterConversion = 1609;

        return Float.valueOf((float) (dist * meterConversion));
    }
    
    private void clearMap()
    {
    	mMap.clear();
    	markerIdMap.clear();
    }
    
    public void applyFilter(boolean[] new_preferences, String category)
    {
    	//Categorie: aclevel, doorways, elevator, escalator, parking
    	//Log.d("Debug", temp[0]+" "+temp[1]+" "+temp[2]);
    	preferences=new_preferences;
    	//Creazione stringa di access level da inserire nella stringa
    	String values="";
    	if (preferences[0]==true) 
    	{
    		if (category=="aclevel") values="'A'";
    		else values="'Yes'";
    		if (preferences[1]==true || preferences[2]==true) values=values+",";
    		Log.d("Debug", values);
    	}
    	if (preferences[1]==true) 
    	{
    		//values=values+"'P'";
    		if (category=="aclevel") values=values+"'P'";
    		else values=values+"'No'";
    		if (preferences[2]==true && category=="aclevel") values=values+",";
    	}
    	if (preferences[2]==true && category=="aclevel")
    	{
    		values=values+"'N'";
    	}
    	if (values.equals("")) values="''";
    	
    	//Log.d("Debug", values);
    	//String temp_query_acc=query_acc.replace("@VALUES", values);
    	String temp_query_acc="";
    	if (category=="aclevel") temp_query_acc=query_acc.replace("@VALUES", values);
    	else if (category=="doorways") temp_query_acc=query_door.replace("@VALUES", values);
    	else if (category=="elevator") temp_query_acc=query_elev.replace("@VALUES", values);
    	else if (category=="escaltor") temp_query_acc=query_esc.replace("@VALUES", values);
    	else if (category=="parking") temp_query_acc=query_park.replace("@VALUES", values);
    	//Log.d("Debug", acl);
    	Log.d("Debug", temp_query_acc);
    	clearMap();
    	//Esecuzione query
    	spinner.show();
    	ftclient.setQuery(temp_query_acc);
        ftclient.queryOnNewThread("setmarkers");
    }
    
    private void loadNearbyPlaces(final double latitude, final double longitude) 
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
    				nearbyList = fsqApp.getNearby(latitude, longitude);
    				//test=mFsqApp.checkIn("4bf253cf52bda593bc7fb2b7");
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
    			 Toast.makeText(MapActivity.this, "No nearby places available", Toast.LENGTH_SHORT).show();
    		 else
    		 {
    			 NearbyDialog ndialog=new NearbyDialog(MapActivity.this, nearbyList, ftclient);
    			 ndialog.show();
    			 //Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();
    		 }
    		 /*mAdapter.setData(mNearbyList);
    		 mListView.setAdapter(mAdapter);*/
    	 }
    	 else 
    	 {
    		 Toast.makeText(MapActivity.this, "Failed to load nearby places", Toast.LENGTH_SHORT).show();
    	 }
     }
   };
    
	public void createNewVenue(String _maxid)
	{
		//Creazione nuovo id, successivo al maxid ottenuto con la query
		//Manca la gestione del primo record inserito senza foursquare, con id NF0
		String maxid=_maxid.substring(4);
		maxid=maxid.substring(0, maxid.length()-2);
		int newid=Integer.parseInt(maxid)+1;
		final String snewid="NF"+Integer.toString(newid);
		
		AlertDialog.Builder ad_venue=new AlertDialog.Builder(this);
		ad_venue.setTitle("Nuovo luogo");
		ad_venue.setMessage("Inserisci il nome del luogo");
		final EditText input=new EditText(this);
		ad_venue.setView(input);
		ad_venue.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				GPSTracker mGPS = new GPSTracker(MapActivity.this);
				FsqVenue newvenue=new FsqVenue();
				newvenue.name=input.getText().toString();
				newvenue.latitude=mGPS.getLatitude();
				newvenue.longitude=mGPS.getLongitude();
				newvenue.direction=0;
				newvenue.id=snewid;
				newvenue.distance="";
				newvenue.type="";
				Intent quiz_intent=new Intent(MapActivity.this, QuizActivity.class);
				quiz_intent.putExtra("venue", newvenue);
				MapActivity.this.startActivity(quiz_intent);
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
		ad_venue.show();
	}
   
    //Test thread per visualizzare lo spinner prima del caricamento della mappa
    //Al momento non utilizzato
	@Override
	public void run() 
	{
		try  
        {  
            synchronized (thread)  
            {  
                while(counter <= 4)  
                {  
                    thread.wait(1);  
                    counter++;  
                    handler.post(new Runnable()  
                    {  
                        @Override  
                        public void run()  
                        {   
                            spinner.setProgress(counter*25);  
                        }  
                    });  
                }  
            }  
        }  
		catch (InterruptedException e)  
        {  
            e.printStackTrace();  
        } 
		//Dopo lo spinner viene caricata la mappa
		handler.post(new Runnable()  
        {  
            @Override  
            public void run()  
            {  
                spinner.dismiss();  
                //setContentView(R.layout.activity_map);
                mMap=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
                ftclient=new FTClient(context);
                setUpMapIfNeeded();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.277205,12.191162) , 6.0f));
                mMap.setMyLocationEnabled(true);
                ftclient.setQuery(query_all);
                ftclient.query("setmarkers");
            }  
        });
		synchronized (thread)  
        {  
            thread.interrupt();  
        }
	}
}
