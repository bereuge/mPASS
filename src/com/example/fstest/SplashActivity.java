package com.example.fstest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.widget.Spinner;

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
		/*spinner=new ProgressDialog(this);
		spinner.setIndeterminate(true);
		spinner.show();*/
		new Prefetcher().execute();
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
    				Intent i=new Intent(SplashActivity.this, MapActivity.class);
    				startActivity(i);
    				finish();
    			}
    			
    		}, 1000);
            /*Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            finish();*/
        }
	}
	
}
