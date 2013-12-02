package com.example.fstest;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class DrawerItemClickListener implements ListView.OnItemClickListener
{
	private DrawerLayout drawer;
	private ListView drawerList;
	private Activity activity;
	
	public DrawerItemClickListener(DrawerLayout _drawer, ListView _drawerlist, Activity _activity)
	{
		drawer=_drawer;
		drawerList=_drawerlist;
		activity=_activity;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) 
	{
	   drawerList.setItemChecked(position, true);
 	   //Toast.makeText(MainActivity.this, Long.toString(id) , Toast.LENGTH_LONG).show();
 	   if (id==0)
 		   drawer.closeDrawer(drawerList);
 	   else if (id==1)
 	   {
 		   drawer.closeDrawer(drawerList);
 		   Intent intent=new Intent(activity, MapActivity.class);
 		   activity.startActivity(intent);
 	   }
 	   else if (id==2)
 	   {
 		   drawer.closeDrawer(drawerList);
 		   Intent profile_intent=new Intent(activity, ProfileActivity.class);
 		   activity.startActivity(profile_intent);
 	   } 
	}

}
