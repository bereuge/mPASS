package com.example.fstest;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ListView;

public class UserSettingsDialog extends Dialog
{
	private Activity activity;
	private User user;
	
	private ArrayList<PrefEntry> pref;
	private PrefAdapter adapter;
	
	public UserSettingsDialog(Activity _activity, User _user) 
	{
		super(_activity);
		activity=_activity;
		user=_user;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.user_settings_layout);
	}
}
