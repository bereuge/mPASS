package com.example.fstest.foursquare;

import java.io.Serializable;

//import android.location.Location;

//Se è troppo lento, bisogna implementare Parcelable invece che Serializable
//Implementare dei get/set decentemente?
public class FsqVenue implements Serializable
{
	//???? Cos'è?
	private static final long serialVersionUID = -2572320640022444725L;
	public String id;
	public String name;
	public String address;
	public String type;
	public Double latitude;
	public Double longitude;
	//public Location location;
	public int direction;
	public String distance;
	//public String distance;
	//public int herenow;
}