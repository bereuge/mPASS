package com.example.fstest;

import java.util.ArrayList;

import com.example.fstest.log.LogAdapter;
import com.example.fstest.log.LogEntry;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PrefDialog extends Dialog
{
	private Activity activity;
	private String category;
	private User user;
	
	private ArrayList<PrefEntry> pref;
	
	public PrefDialog(Activity _activity, String _category, User _user) 
	{
		super(_activity);
		activity=_activity;
		category=_category;
		user=_user;
		setTitle(category);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.pref_layout);
	    
	    LayoutParams lp=getWindow().getAttributes(); 
        lp.width=300;lp.height=340;
        lp.dimAmount=0;            
        lp.flags=LayoutParams.FLAG_LAYOUT_NO_LIMITS | LayoutParams.FLAG_NOT_TOUCH_MODAL;
        getWindow().setAttributes(lp);   
        ListView listView=(ListView)findViewById(R.id.lv_pref);
	    if (category.equals("Gaps"))
	    {
			pref=user.getPref("Gaps");
			PrefAdapter adapter=new PrefAdapter(activity);
			adapter.setData(pref);
			listView.setAdapter(adapter);
	    }
	    listView.setOnItemClickListener(new OnItemClickListener() 
	    {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				
			}
		});
	}
}
