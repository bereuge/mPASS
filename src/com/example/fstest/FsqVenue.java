package com.example.fstest;

import java.io.Serializable;

import android.location.Location;

//Se è troppo lento, bisogna implementare Parcelable invece che Serializable
public class FsqVenue implements Serializable
{
	public String id;
	public String name;
	//public String address;
	public String type;
	public Double latitude;
	public Double longitude;
	//public Location location;
	public int direction;
	public String distance;
	//public String distance;
	public int herenow;
}