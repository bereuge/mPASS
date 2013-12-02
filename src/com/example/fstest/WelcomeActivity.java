package com.example.fstest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import com.example.fstest.foursquare.FoursquareApp;
import com.example.fstest.foursquare.FoursquareApp.FsqAuthListener;
import com.example.fstest.log.LogDbManager;
import com.example.fstest.utils.ImageDownloader;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class WelcomeActivity extends Activity 
{
	private FoursquareApp mFsqApp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		mFsqApp = new FoursquareApp(this, Costants.CLIENT_ID, Costants.CLIENT_SECRET);
		FsqAuthListener listener = new FsqAuthListener() 
        {
        	@Override
         	public void onSuccess() 
        	{
        	 	Toast.makeText(WelcomeActivity.this, "Connected as " + mFsqApp.getUserName(), Toast.LENGTH_SHORT).show();
        	 	ImageDownloader downloader=new ImageDownloader();
        	 	try 
        	 	{
        	 		//Creo l'utente
					String imagepath=downloader.execute(mFsqApp.getPhoto()).get(); //Da controllare!
					Log.d("Debug", imagepath);
					User new_user=new User(WelcomeActivity.this);
					new_user.eraseUser();
					new_user.setName(mFsqApp.getUserName());
					new_user.setImagePath(imagepath);
					new_user.setType("User");
					//Creo il log
					LogDbManager log=new LogDbManager(WelcomeActivity.this);
					log.openToWrite();
					log.deleteAll();
					Date date=new Date();
					SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String sdate=dateFormat.format(date);
					log.insertEntry("User created!", sdate);
					log.close();
					Intent i=new Intent(WelcomeActivity.this,MapActivity.class);
					startActivity(i);
					WelcomeActivity.this.finish();
				} 
        	 	catch (InterruptedException e) 
				{
					e.printStackTrace();
				} 
        	 	catch (ExecutionException e) 
				{
					e.printStackTrace();
				}
        	 	//Creazione nuovo user e intent alla mapactivity
         	}
        
        	@Override
        	public void onFail(String error) 
        	{
        		Toast.makeText(WelcomeActivity.this, error, Toast.LENGTH_SHORT).show();
        		//Errore quindi deve registrarsi in un altro modo
        	}
        };
        mFsqApp.setListener(listener);
        
        Button btn_fsq=(Button)findViewById(R.id.btn_regfsq);
        btn_fsq.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				mFsqApp.authorize();
			}
		});
        
        Button btn_reg=(Button)findViewById(R.id.btn_regnew);
        btn_reg.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View arg0) 
			{
				Intent i=new Intent(WelcomeActivity.this,NewProfileActivity.class);
				startActivity(i);
				WelcomeActivity.this.finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		SharedPreferences pref=this.getSharedPreferences("activity", Context.MODE_PRIVATE);
		//pref.getBoolean("firsttime", true);
		Editor editor = pref.edit();
		editor.putBoolean("firsttime", false);
		editor.commit();
	}
}
