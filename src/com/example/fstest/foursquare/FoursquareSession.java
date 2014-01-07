package com.example.fstest.foursquare;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;

public class FoursquareSession 
{
	private SharedPreferences sharedPref;
	private Editor editor;
	
	private static final String SHARED = "Foursquare_Preferences";
	private static final String FSQ_USERNAME = "username";
	private static final String FSQ_ACCESS_TOKEN = "access_token";
	private static final String FSQ_PHOTO_URL="photo";
	
	public FoursquareSession(Context context) 
	{
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		editor = sharedPref.edit();
	}
	
	public void storeAccessToken(String accessToken, String username, String photo) 
	{
		editor.putString(FSQ_ACCESS_TOKEN, accessToken);
		editor.putString(FSQ_USERNAME, username);
		editor.putString(FSQ_PHOTO_URL, photo);
		editor.commit();
	}
	
	public void resetAccessToken() 
	{
		editor.putString(FSQ_ACCESS_TOKEN, null);
		editor.putString(FSQ_USERNAME, null);
		editor.putString(FSQ_PHOTO_URL, null);
		editor.commit();
	}
	
	public String getUsername() 
	{
		return sharedPref.getString(FSQ_USERNAME, null);
	}
	
	public String getAccessToken() 
	{
		return sharedPref.getString(FSQ_ACCESS_TOKEN, null);
	}
	
	
	public String getPhotoUrl()
	{
		return sharedPref.getString(FSQ_PHOTO_URL, null);
	}
}