package com.example.fstest;

import java.util.ArrayList;

import com.example.fstest.NearbyAdapter.ViewHolder;
import com.example.fstest.foursquare.FsqVenue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VenuesAdapter extends BaseAdapter
{
	private ArrayList<FtVenue> venueList;
	private LayoutInflater mInflater;

	public VenuesAdapter(Context c) 
	{
        mInflater=LayoutInflater.from(c);
    }
	
	public void setData(ArrayList<FtVenue> poolList) 
	{
		venueList = poolList;
	}
	
	@Override
	public int getCount() 
	{
		return venueList.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return venueList.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder;
		
		if (convertView == null) {
			convertView	=  mInflater.inflate(R.layout.venue_list, null);
			
			holder = new ViewHolder();
			
			holder.nameTxt=(TextView)convertView.findViewById(R.id.tv_name);
			holder.aclImg=(ImageView)convertView.findViewById(R.id.iv_acl);
			holder.cityTxt=(TextView)convertView.findViewById(R.id.tv_city);
			holder.addressTxt=(TextView)convertView.findViewById(R.id.tv_name);
			
			convertView.setTag(holder);
		} 
		else 
		{
			holder = (ViewHolder) convertView.getTag();
		}

		FtVenue venue 	= venueList.get(position);
		
		holder.nameTxt.setText(venue.getName());
		holder.cityTxt.setText(venue.getCity());
		String acl=venue.getAccessLevel();
		if (acl.equals("A")) holder.aclImg.setImageResource(R.drawable.ic_green_acl);
		else if (acl.equals("P")) holder.aclImg.setImageResource(R.drawable.ic_yellow_acl);
		else if (acl.equals("N")) holder.aclImg.setImageResource(R.drawable.ic_red_acl);
		else holder.aclImg.setImageResource(R.drawable.ic_grey_acl);
		
        return convertView;
	}
	
	static class ViewHolder 
	{
		TextView nameTxt;
		ImageView aclImg;
		TextView cityTxt;
		TextView addressTxt;
	}
}
