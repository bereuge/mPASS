package com.example.fstest;

import com.example.fstest.foursquare.FoursquareApp;
import com.example.fstest.foursquare.FsqVenue;
import com.example.fstest.foursquare.FoursquareApp.FsqAuthListener;
import com.example.fstest.fusiontables.FTClient;
import com.example.fstest.utils.GPSTracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;

import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.ArrayList;

public class MainActivity extends Activity 
{
private FoursquareApp mFsqApp;
//private NearbyAdapter mAdapter;
private ArrayList<FsqVenue> mNearbyList;
private ProgressDialog mProgress;
private GPSTracker mGPS;

//Codici per l'autorizzazione a Foursquare
public static final String CLIENT_ID = "WRWWBSHWC1AFXVAB5SZPCWBO1X0QACFX302KRXKRPXRIVVAO";
public static final String CLIENT_SECRET = "00Z2J0045OFM0EZVZT43333QZ4PDFXCQCOYD32HAZQJS4LG5";

//Menu
private String[] menu;
private DrawerLayout drawer;
private ListView mDrawerList;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /*User user=new User(this);
        if (user.getName()!=null) Log.d("Debug",user.getName());*/
        
        if (firstCheck())
        {
        	//Toast.makeText(this, "Prima volta!", Toast.LENGTH_LONG).show();
        	Intent i_newuser=new Intent(MainActivity.this,NewProfileActivity.class);
        	startActivity(i_newuser);
        }
        /*AuthPreferences a = new AuthPreferences(this);
        Log.d("Debug",a.getUser());*/
        
        initialize_menu();
        
        final TextView nameTv = (TextView) findViewById(R.id.tv_name);
        Button connectBtn = (Button) findViewById(R.id.b_connect);
        Button profiloBtn = (Button) findViewById(R.id.b_profilo);
        Button goBtn	= (Button) findViewById(R.id.b_go);
        Button mapBtn=(Button) findViewById(R.id.b_map);
        
        //Pulsante per la mappa
        mapBtn.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				if (checkConnection())
				{
					Intent intent=new Intent(MainActivity.this, MapActivity.class);
					startActivity(intent);
				}
				else
				{
					Toast.makeText(MainActivity.this, "Connessione a Internet assente", Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        //PULSANTE PER VISUALIZZARE PROFILO
        profiloBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				Intent profile_intent=new Intent(MainActivity.this, ProfileActivity.class);
				startActivity(profile_intent);
			}
		});
        /*
        mListView	= (ListView) findViewById(R.id.lv_places);
        
        mListView.setOnItemClickListener(new OnItemClickListener() 
        {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long d) 
			{
				//Toast.makeText(MainActivity.this, "Hai premuto "+mNearbyList.get(position).name, Toast.LENGTH_SHORT).show();
				Intent quiz_intent=new Intent(MainActivity.this, QuizActivity.class);
				FsqVenue venue=new FsqVenue();
				venue=mNearbyList.get(position);
				quiz_intent.putExtra("venue", venue);
				startActivity(quiz_intent);
			}
		});
        */
        mFsqApp = new FoursquareApp(this, CLIENT_ID, CLIENT_SECRET);
        mGPS = new GPSTracker(this);
        //mAdapter = new NearbyAdapter(this);
        mNearbyList	= new ArrayList<FsqVenue>();
        mProgress	= new ProgressDialog(this);
        
        mProgress.setMessage("Loading data ...");
        
        if (mFsqApp.hasAccessToken()) nameTv.setText("User: " + mFsqApp.getUserName());
        //nameTv.setText("User: Mario Rossi");
        
        FsqAuthListener listener = new FsqAuthListener() 
        {
        	@Override
         	public void onSuccess() 
        	{
        	 	Toast.makeText(MainActivity.this, "Connected as " + mFsqApp.getUserName(), Toast.LENGTH_SHORT).show();
        	 	nameTv.setText("Connected as " + mFsqApp.getUserName());
         	}
        
        	@Override
        	public void onFail(String error) 
        	{
        		Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
        	}
        };
        
        mFsqApp.setListener(listener);
        
        //Autorizzazione con foursquare
        connectBtn.setOnClickListener(new OnClickListener() 
        {
        	@Override
        	public void onClick(View v) 
        	{
        		mFsqApp.authorize();
        	}
        });
        
        //use access token to get nearby places
        goBtn.setOnClickListener(new OnClickListener() 
        {
        	@Override
        	public void onClick(View v) 
        	{
        		if (mGPS.canGetLocation())
        		{
        			double lat = mGPS.getLatitude();
        			double lon = mGPS.getLongitude();
        			loadNearbyPlaces(lat, lon);
        		}
        	}
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
    	return true;
    }
    
    @Override
    public void onBackPressed() 
    {
    	
    }
    
    //Gestione menù
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
        	case R.id.item_menu:if (!drawer.isDrawerOpen(mDrawerList))
        							drawer.openDrawer(mDrawerList);
        						else
        							drawer.closeDrawer(mDrawerList);
                                break;
        	default:break;
        }
        return true;
    }
    
    private void loadNearbyPlaces(final double latitude, final double longitude) 
    {
    	mProgress.show();
    	mProgress.setCancelable(false);
    	new Thread() 
    	{
    		@Override
    		public void run() 
    		{
    			int what = 0;
    			try 
    			{
    				mNearbyList = mFsqApp.getNearby(latitude, longitude);
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
    	 mProgress.dismiss();
    	 if (msg.what == 0) 
    	 {
    		 if (mNearbyList.size() == 0) 
    			 Toast.makeText(MainActivity.this, "No nearby places available", Toast.LENGTH_SHORT).show();
    		 /*if (test == false) 
    		 {
    			 Toast.makeText(MainActivity.this, "No nearby places available", Toast.LENGTH_SHORT).show();
    			 return;
    		 }*/
    		 else
    		 {
    			 NearbyDialog ndialog=new NearbyDialog(MainActivity.this, mNearbyList, new FTClient(MainActivity.this));
    			 ndialog.show();
    			 //Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();
    		 }
    		 /*mAdapter.setData(mNearbyList);
    		 mListView.setAdapter(mAdapter);*/
    	 }
    	 else 
    	 {
    		 Toast.makeText(MainActivity.this, "Failed to load nearby places", Toast.LENGTH_SHORT).show();
    	 }
     }
   };
   
   private void initialize_menu()
   {
	   menu=getResources().getStringArray(R.array.drawer_menu);
	   drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
	   mDrawerList = (ListView) findViewById(R.id.drawer);
	   //ArrayAdapter adapter=new ArrayAdapter<String>(this, R.layout.drawer_list_item, menu);
	   mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, menu));
	   mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
   }
   
   private class DrawerItemClickListener implements ListView.OnItemClickListener 
   {
       @Override
       public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
       {
    	   mDrawerList.setItemChecked(position, true);
    	   //Toast.makeText(MainActivity.this, Long.toString(id) , Toast.LENGTH_LONG).show();
    	   if (id==0)
    		   drawer.closeDrawer(mDrawerList);
    	   else if (id==1)
    	   {
    		   drawer.closeDrawer(mDrawerList);
    		   Intent intent=new Intent(MainActivity.this, MapActivity.class);
    		   startActivity(intent);
    	   }
    	   else if (id==2)
    	   {
    		   drawer.closeDrawer(mDrawerList);
    		   Intent profile_intent=new Intent(MainActivity.this, ProfileActivity.class);
    		   startActivity(profile_intent);
    	   }   
       }
   }
   
   private boolean firstCheck()
   {
	   boolean first=true;
	   SharedPreferences pref=this.getSharedPreferences("activity", Context.MODE_PRIVATE);
	   first=pref.getBoolean("firsttime", true);
	   return first;
   }
   
   private boolean checkConnection()
   {
	   boolean online=false;
	   ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	   NetworkInfo netInfo=cm.getActiveNetworkInfo();
	   if (netInfo!=null && netInfo.isConnectedOrConnecting())
	   {
		   online=true;
	   }
	   return online;
   }
}