package com.example.fstest;

import com.example.fstest.fusiontables.FTClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;

public class SplashActivity extends Activity 
{
	private ProgressDialog spinner;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		context=this;
		FTClient ft=new FTClient(context);
		/*spinner=new ProgressDialog(this);
		spinner.setIndeterminate(true);
		spinner.show();*/
		//new Prefetcher().execute();
		/*new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run() 
			{
				Intent i=new Intent(SplashActivity.this, MainActivity.class);
				startActivity(i);
				finish();
			}
			
		}, 2000);*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}
	/*
	private class Prefetcher extends AsyncTask<Void, Void, Void>
	{
        @Override
        protected void onPreExecute() 
        {
            super.onPreExecute();    
        }
        
		@Override
		protected Void doInBackground(Void... arg0) 
		{
			FTClient ft=new FTClient(context);
			return null;
		}
		
        @Override
        protected void onPostExecute(Void result) 
        {
            super.onPostExecute(result);
            // After completing http call
            // will close this activity and lauch main activity
            new Handler().postDelayed(new Runnable()
    		{
    			@Override
    			public void run() 
    			{
    				if (!firstTime())
    		        {
    		        	//Toast.makeText(this, "Prima volta!", Toast.LENGTH_LONG).show();
    		        	Intent i_newuser=new Intent(SplashActivity.this,MapActivity.class);
    		        	startActivity(i_newuser);
    		        }
    				else
    				{
	    				Intent i=new Intent(SplashActivity.this, WelcomeActivity.class);
	    				startActivity(i);
	    				//finish(); //Se uso il finish, non si vede nulla fra la newprofileactivity e l'activity principale per
	    				            //qualche secondo
    				}
    			}
    			
    		}, 1000);
            //Aumentare il delay
        }
	}
	*/
    private boolean firstTime()
    {
 	   boolean first=true;
 	   SharedPreferences pref=this.getSharedPreferences("activity", Context.MODE_PRIVATE);
 	   first=pref.getBoolean("firsttime", true);
 	   return first;
    }
    
    public void changeActivity()
    {
    	if (!firstTime())
        {
        	//Toast.makeText(this, "Prima volta!", Toast.LENGTH_LONG).show();
        	Intent i_newuser=new Intent(SplashActivity.this,MapActivity.class);
        	startActivity(i_newuser);
        }
		else
		{
			Intent i=new Intent(SplashActivity.this, WelcomeActivity.class);
			startActivity(i);
			//finish(); //Se uso il finish, non si vede nulla fra la newprofileactivity e l'activity principale per
			            //qualche secondo
		}
    }
}
