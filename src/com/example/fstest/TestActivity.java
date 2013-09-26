package com.example.fstest;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends Activity {

private Button button;

//private MyLocationListener locationListener;
private LocationManager locationManager;
private String locationProvider = LocationManager.GPS_PROVIDER;

private String tableid = "1JvwJIV2DOSiQSXeSCj8PA8uKuSmTXODy3QgikiQ";
private String username = "<username>";
private String password = "<password>";
private String embedLink = "http://gmaps-samples.googlecode.com/svn/trunk/"
+ "fusiontables/potholes.html";

private String helloString;

/**
* Authorizes the user and initializes the UI.
*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

	}

}