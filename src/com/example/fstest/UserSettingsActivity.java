package com.example.fstest;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class UserSettingsActivity extends Activity 
{
	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_settings);
		
		user=new User(this);
		
		ImageView iv_image=(ImageView)findViewById(R.id.iv_photo_user);
		Bitmap image=BitmapFactory.decodeFile(user.getImagePath());
		iv_image.setImageBitmap(image);
		
		TextView tv_name=(TextView)findViewById(R.id.tv_username);
		tv_name.setText(user.getName());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_settings, menu);
		return true;
	}

}
