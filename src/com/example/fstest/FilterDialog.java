package com.example.fstest;

import android.view.WindowManager.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

//Classe per il dialog che mostra le segnalazioni per un dato luogo
public class FilterDialog extends Dialog implements OnClickListener
{
	private Activity activity;
	private Boolean b_a=true,b_pa=true,b_n=true;
	
	public FilterDialog(Activity _activity)
	{
		super(_activity);
		activity=_activity;
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
				FilterDialog.this.dismiss();
			}
		});
        
        CheckBox cb_a=(CheckBox)findViewById(R.id.cb_a);
        cb_a.setOnCheckedChangeListener(new OnCheckedChangeListener() 
        {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				b_a=isChecked;
			}
		});
        
        CheckBox cb_pa=(CheckBox)findViewById(R.id.cb_pa);
        cb_a.setOnCheckedChangeListener(new OnCheckedChangeListener() 
        {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				b_pa=isChecked;
			}
		});
        
        CheckBox cb_n=(CheckBox)findViewById(R.id.cb_n);
        cb_a.setOnCheckedChangeListener(new OnCheckedChangeListener() 
        {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				b_n=isChecked;
			}
		});
	}
	
	@Override
	public void onClick(View v) 
	{
		Button btn_ok=(Button)findViewById(R.id.btn_setfilter);
		if (v.getId()==btn_ok.getId())
		{
			((MapActivity)activity).testCB(b_a, b_pa, b_n);
			dismiss();
		}
	}
	
	
	public Boolean getCBA()
	{
		return b_a;
	}
	
	public Boolean getCBPA()
	{
		return b_pa;
	}
	
	public Boolean getCBN()
	{
		return b_n;
	}
}
