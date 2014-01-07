package com.example.fstest;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class PrefDialog extends Dialog
{
	private Activity activity;
	private String category;
	private User user;
	
	private ArrayList<PrefEntry> pref;
	private PrefAdapter adapter;
	
	public PrefDialog(Activity _activity, String _category, User _user) 
	{
		super(_activity);
		activity=_activity;
		category=_category.toLowerCase();
		user=_user;
		setTitle(_category);
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
        adapter=new PrefAdapter(activity);
	    if (category.equals("gaps"))
	    {
			pref=user.getPref("gaps");
			adapter.setData(pref);
			listView.setAdapter(adapter);
	    }
	    Button btn_savepref=(Button)findViewById(R.id.btn_savepref);
	    btn_savepref.setOnClickListener(new android.view.View.OnClickListener() 
	    {
			@Override
			public void onClick(View v) 
			{
				user.setPref(adapter.getNewPrefs(), category);
				dismiss();
			}
		});
	    /*
	    listView.setOnItemClickListener(new OnItemClickListener() 
	    {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				
			}
		});*/
	}
}
