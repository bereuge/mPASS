package com.example.fstest;

import com.google.android.gms.maps.model.LatLng;

public class FtVenue 
{
	private String fsqid;
	private String name;
	private LatLng geo;
	private String accessLevel;
	private String city;
	private String address;
	
	public FtVenue(String _fsqid, String _name, LatLng _geo, String _accessLevel)
	{
		fsqid=_fsqid;
		name=_name;
		geo=_geo;
		accessLevel=_accessLevel;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getAccessLevel()
	{
		return accessLevel;
	}
	
	public String getFsqId()
	{
		return fsqid;
	}
	
	public LatLng getGeo()
	{
		return geo;
	}
	
	public void setName(String _name)
	{
		name=_name;
	}
	
	public void setAccessLevel(String _accessLevel)
	{
		accessLevel=_accessLevel;
	}
	
	public void setFsqId(String _fsqid)
	{
		fsqid=_fsqid;
	}
	
	public void setGeo(LatLng _geo)
	{
		geo=_geo;
	}
	
	public void setCity(String _city)
	{
		city=_city;
	}
	
	public void setAddress(String _address)
	{
		address=_address;
	}
	
	public String getCity()
	{
		return city;
	}
	
	public String getAddress()
	{
		return address;
	}
}
