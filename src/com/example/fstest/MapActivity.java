package com.example.fstest;

import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;

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
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MapActivity extends Activity implements Runnable
{
	private GPSTracker gps;
	private GoogleMap mMap;
	private FTClient ftclient;
	private ProgressDialog spinner;
	private Thread thread;  
    private Handler handler;
    private int counter=0;
    private Context context;
    private HashMap<Marker, String> markerIdMap;
    private String query_all="SELECT ROWID, fsqid, name, geo FROM 1JvwJIV2DOSiQSXeSCj8PA8uKuSmTXODy3QgikiQ";
    private String query_acc_a="SELECT ROWID, fsqid, name, geo, accessLevel FROM 1JvwJIV2DOSiQSXeSCj8PA8uKuSmTXODy3QgikiQ WHERE accessLevel='A'";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        context=this;
        setContentView(R.layout.activity_map);
        
        gps=new GPSTracker(this);
        //Test spinner , in teoria dovrebbe vedersi prima del caricamento della mappa ma non funziona
        spinner=new ProgressDialog(this);
        spinner.setMessage("Caricamento mappa...");
        spinner.setCancelable(false);
        spinner.setMax(100); 
        spinner.setProgress(0); 
        spinner.show();
        
        //Caricamento mappa normale
        spinner.dismiss();  
        //setContentView(R.layout.activity_map);
        mMap=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        ftclient=new FTClient(context);
        setUpMapIfNeeded();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.277205,12.191162) , 6.0f));
        mMap.setMyLocationEnabled(true);
        ftclient.setQuery(query_all);
        //ftclient.query("setmarkers");
        new Thread()
		{
			@Override
			public void run()
			{
				ftclient.query("setmarkers");
			}
		}.start();
		
        
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
        	default:break;
        }
        return true;
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
    	String name, ll, fsqid, min_fsqid="";
    	float min_distance=3000; //Ne cerco uno solo se è al massimo distante un tot di metri
    	double lat = 0, lng = 0;
    	FsqVenue venue=new FsqVenue();
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
    	for(int i=0;i<venues.length();i++)
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
				Log.d("Test",name);
				
				float temp_distance=distFrom(lat,lng,gps.getLatitude(),gps.getLongitude());
				if (temp_distance<=min_distance)
				{
					venue.id=fsqid;
					venue.name=name;
					venue.distance=String.valueOf(min_distance);
					venue.latitude=lat;
					
					min_fsqid=fsqid;
					min_distance=temp_distance;
					Log.d("Debug",name +" "+min_distance+" "+min_fsqid);
				}
				//Random r=new Random();
				/*int j=r.nextInt(3);
				if (j==0)
				{*/
				Marker marker=mMap.addMarker(new MarkerOptions()
	    		.position(new LatLng(lat, lng))
	    		.title(name)
	    		.snippet("Commento...")
	    		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
	    		.draggable(false)
	    		);
				markerIdMap.put(marker, fsqid);
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
    	if (venues.length()==0)
    	{
    		Log.d("Debug","Errore nella query");
    		ftclient.setQuery(query_all);
            ftclient.query("setmarkers");
            //Non ha senso questa cosa...meglio cavarla! Se il database è vuoto ripete la query all'infinito!
    	}
    	else
    	{
    		if (!min_fsqid.equals(""))
    		{
    			TextView tv=(TextView)findViewById(R.id.tv_notification);
    			tv.setText("Sei vicino a "+venue.name);
    			Button btn=(Button)findViewById(R.id.btn_notification);
    			btn.setVisibility(1);
    			btn.setText("Fai il quiz!");
    			btn.setOnClickListener(new OnClickListener() 
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
				                    break;
	
				                	case DialogInterface.BUTTON_NEGATIVE:
				                    //No button clicked
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
    	
    	mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() 
    	{
			@Override
			public void onInfoWindowClick(Marker marker) 
			{
				String fsqid=markerIdMap.get(marker);
				ftclient.setQuery("SELECT ROWID, name, accessLevel, comment, doorways, elevator, escalator, parking, user, date FROM 1JvwJIV2DOSiQSXeSCj8PA8uKuSmTXODy3QgikiQ WHERE fsqid='"+fsqid+"'");
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

    //Callback eseguito dopo il completamento della query, mostra le info sul luogo
    public void showInfoDialog (JSONArray venues)
    {
    	spinner.dismiss();
    	InfoDialog idialog=new InfoDialog((Activity)context, venues);
		idialog.show();
    }
    
    private void showFilterDialog()
    {
    	FilterDialog fdialog=new FilterDialog((Activity)context);
    	//fdialog.setCancelable(false);
    	fdialog.setCanceledOnTouchOutside(false);
    	/*fdialog.setOnDismissListener(new OnDismissListener() 
    	{
			@Override
			public void onDismiss(DialogInterface dialog) 
			{
				Toast.makeText(this, String.valueOf((FilterDialog)dialog.), Toast.LENGTH_LONG).show();
			}
		});*/
    	//testCB(true,true,true);
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
    
    public void testCB(boolean a, boolean pa, boolean n)
    {
    	Toast.makeText(this, String.valueOf(a)+" "+String.valueOf(pa)+" "+String.valueOf(n), Toast.LENGTH_LONG).show();
    	//Toast.makeText(this, "AAAAA", Toast.LENGTH_LONG).show();
    }
    
    //Test thread per visualizzare lo spinner prima del caricamento della mappa
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
