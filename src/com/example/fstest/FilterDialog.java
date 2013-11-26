package com.example.fstest;

import android.view.WindowManager.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

//Classe per il dialog che mostra le segnalazioni per un dato luogo
public class FilterDialog extends Dialog implements OnClickListener
{
	private Activity activity;
	private Boolean b_a,b_pa,b_n;
	private boolean[] preferences;
	
	public FilterDialog(Activity _activity, boolean[] _preferences)
	{
		super(_activity);
		activity=_activity;
		preferences=_preferences;
		//setTitle("Preferenze utente");
		setTitle("User preferences");
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.filter_layout);
	    
	    LayoutParams lp=getWindow().getAttributes(); 
        lp.width=300;lp.height=340;
        lp.dimAmount=0;            
        lp.flags=LayoutParams.FLAG_LAYOUT_NO_LIMITS | LayoutParams.FLAG_NOT_TOUCH_MODAL;
        getWindow().setAttributes(lp);   
        
        Button btn_ok=(Button)findViewById(R.id.btn_setfilter);
        btn_ok.setOnClickListener(new View.OnClickListener() 
        {
			@Override
			public void onClick(View arg0) 
			{
				Log.d("Debug", b_a.toString()+" "+b_pa.toString()+" "+b_n.toString());
				FilterDialog.this.dismiss();
				boolean[] newpref = new boolean[3];
				newpref[0]=b_a;
				newpref[1]=b_pa;
				newpref[2]=b_n;
				((MapActivity)activity).applyFilter(newpref);
			}
		});
        
        b_a=preferences[0];
        b_pa=preferences[1];
        b_n=preferences[2];
        
        CheckBox cb_a=(CheckBox)findViewById(R.id.cb_a);
        cb_a.setChecked(preferences[0]);
        cb_a.setOnCheckedChangeListener(new OnCheckedChangeListener() 
        {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				if (isChecked) b_a=true;
				else b_a=false;
				//Log.d("Debug", "b_a "+b_a.toString());
			}
		});
        
        CheckBox cb_pa=(CheckBox)findViewById(R.id.cb_pa);
        cb_pa.setChecked(preferences[1]);
        cb_pa.setOnCheckedChangeListener(new OnCheckedChangeListener() 
        {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				if (isChecked) b_pa=true;
				else b_pa=false;
				//Log.d("Debug", "b_pa "+b_pa.toString());
			}
		});
        
        CheckBox cb_n=(CheckBox)findViewById(R.id.cb_n);
        cb_n.setChecked(preferences[2]);
        cb_n.setOnCheckedChangeListener(new OnCheckedChangeListener() 
        {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				if (isChecked) b_n=true;
				else b_n=false;
				//Log.d("Debug", "b_n "+b_n.toString());
			}
		});
	}
	
	@Override
	public void onClick(View arg0) 
	{
		
	}
}
