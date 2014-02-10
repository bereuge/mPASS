package com.example.fstest;

import javax.annotation.meta.When;

import org.json.JSONArray;
import org.json.JSONException;

import android.R.color;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.Font;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//Classe per il dialog che mostra le segnalazioni per un dato luogo
public class InfoDialog extends Dialog implements OnClickListener
{
	private Activity activity;
	private JSONArray venues;
	
	public InfoDialog(Activity _activity, JSONArray _venues, int theme)
	{
		super(_activity, theme);
		
		activity=_activity;
		venues=_venues;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.info_layout);
	    try 
	    {
			setTitle(venues.getJSONArray(0).get(1).toString());
		} 
	    catch (JSONException e) 
		{
			e.printStackTrace();
		}
	    //Servirebbe una soluzione migliore per la dimensione del dialog
	    loadQuizzes();
	}
	
	@Override
	public void onClick(View v) 
	{
		dismiss();
	}

	private void loadQuizzes()
	{
		String name,accesslevel,comment,doorways,elevator,escalator,parking, user, date;
		int nquiz=venues.length();
		LinearLayout ll=(LinearLayout)findViewById(R.id.info_layout);
		ll.setPadding(10, 5, 10, 5);
		for(int i=0; i<nquiz; i++)
		{
			try 
			{
				JSONArray row=venues.getJSONArray(i);
				name=row.get(1).toString();
				accesslevel=row.get(2).toString();
				comment=row.get(3).toString();
				doorways=row.get(4).toString();
				elevator=row.get(5).toString();
				escalator=row.get(6).toString();
				parking=row.get(7).toString();
				user=row.get(8).toString();
				date=row.get(9).toString();
				Log.d("Valori", "Nome:"+name);
				Log.d("Valori", "AL:"+accesslevel);
				Log.d("Valori", "Comment:"+comment);
				Log.d("Valori", "Door:"+doorways);
				Log.d("Valori", "Elevator:"+elevator);
				Log.d("Valori", "Escalator:"+escalator);
				Log.d("Valori", "Parking:"+parking);
				/*if (i==0)
				{
					
					TextView tv_name=new TextView(activity);
				    tv_name.setText(name);
				    tv_name.setTextSize(20);
				    tv_name.setPadding(0, 0, 0, 15);
				    ll.addView(tv_name);
				    
				}*/
				TextView tv_quiztitle=new TextView(activity);
			    //tv_quiztitle.setText("Segnalazione "+Integer.toString(i+1));
				if (date.equals(""))
					tv_quiztitle.setText("Segnalazione di "+user);
				else
					tv_quiztitle.setText("Segnalazione del "+date.substring(0,10)+" di "+user);
			    tv_quiztitle.setTextSize(16);
			    tv_quiztitle.setTextColor(Color.WHITE);
			    tv_quiztitle.setTypeface(tv_quiztitle.getTypeface(), Typeface.BOLD);
			    tv_quiztitle.setPadding(0, 15, 0, 15);
			    if (accesslevel.equals("A"))
			    	tv_quiztitle.setBackgroundColor(Color.argb(255, 102, 204, 0));
				else if (accesslevel.equals("P"))
					tv_quiztitle.setBackgroundColor(Color.argb(255, 251, 236, 93));
				else if (accesslevel.equals("N"))
					tv_quiztitle.setBackgroundColor(Color.argb(255, 227, 38, 54));
				else tv_quiztitle.setBackgroundColor(Color.argb(255, 132, 132, 130));
			    ll.addView(tv_quiztitle);
				
				/*TextView tv_accesslvl=new TextView(activity);
				if (accesslevel.equals("A"))
					tv_accesslvl.setText("Livello accessibilità: Accessibile");
				else if (accesslevel.equals("P"))
					tv_accesslvl.setText("Livello accessibilità: Parz. accessibile");
				else if (accesslevel.equals("N"))
					tv_accesslvl.setText("Livello accessibilità: Non accessibile");
				else if (accesslevel.equals(" ") || accesslevel.equals("")) tv_accesslvl.setText("Livello accessibilità: Ignoto");
				tv_accesslvl.setPadding(0, 5, 0, 5);
				ll.addView(tv_accesslvl);*/
				
				TextView tv_titlecomment=new TextView(activity);
				tv_titlecomment.setText("Commento");
				ll.addView(tv_titlecomment);
				TextView tv_comment=new TextView(activity);
				if (comment.equals(" ") || comment.equals("")) tv_comment.append("Non inserito");
				else tv_comment.append(comment);
			    tv_comment.setPadding(10, 5, 10, 5);
			    tv_comment.setBackgroundResource(R.drawable.rounded_edittext);
			    tv_comment.setMaxWidth(50);
			    //tv_comment.setBackgroundColor(0xAAD2D2D2);
			    //tv_comment.setBackground(R.drawable.rounded_edittext);
			    ll.addView(tv_comment);
			    
			    LinearLayout ll_carat=new LinearLayout(activity);
			    ll_carat.setPadding(0, 15, 0, 0);
			    TextView tv_door=new TextView(activity);
			    if (doorways.equals("Yes")) tv_door.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_green_dot, 0, 0);
			    else if (doorways.equals("One floor")) tv_door.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_yellow_dot, 0, 0);
			    else if (doorways.equals("No")) tv_door.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_red_dot, 0, 0);
			    else tv_door.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_grey_dot, 0, 0);
			    tv_door.setText("Porte");
			    tv_door.setPadding(5, 0, 5, 0);
			    ll_carat.addView(tv_door);
			    
			    TextView tv_elevator=new TextView(activity);
			    if (elevator.equals("Yes")) tv_elevator.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_green_dot, 0, 0);
			    else if (elevator.equals("No")) tv_elevator.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_red_dot, 0, 0);
			    else tv_elevator.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_grey_dot, 0, 0);
			    tv_elevator.setText("Ascensore");
			    tv_elevator.setPadding(5, 0, 5, 0);
			    ll_carat.addView(tv_elevator);
			    
			    TextView tv_escalator=new TextView(activity);
			    if (escalator.equals("Yes")) tv_escalator.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_green_dot, 0, 0);
			    else if (escalator.equals("No")) tv_escalator.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_red_dot, 0, 0);
			    else tv_escalator.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_grey_dot, 0, 0);
			    tv_escalator.setText("Scale mobili");
			    tv_escalator.setPadding(5, 0, 5, 0);
			    ll_carat.addView(tv_escalator);
			    
			    TextView tv_parking=new TextView(activity);
			    if (parking.equals("Yes")) tv_parking.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_green_dot, 0, 0);
			    else if (parking.equals("No")) tv_parking.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_red_dot, 0, 0);
			    else tv_parking.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_grey_dot, 0, 0);
			    tv_parking.setText("Parcheggio");
			    tv_parking.setPadding(5, 0, 5, 0);
			    ll_carat.addView(tv_parking);
			    
			    ll.addView(ll_carat);
			    /*
			    TextView tv_door=new TextView(activity);
			    if (doorways.equals(" ") || doorways.equals("")) tv_door.setText("Porte: Ignoto");
			    else tv_door.setText("Porte: "+doorways);
			    tv_door.setPadding(0, 5, 0, 5);
			    ll.addView(tv_door);
			    
			    TextView tv_elevator=new TextView(activity);
			    if (elevator.equals(" ") || elevator.equals("")) tv_elevator.setText("Ascensori: Ignoto");
			    else tv_elevator.setText("Ascensori: "+elevator);
			    tv_elevator.setPadding(0, 5, 0, 5);
			    ll.addView(tv_elevator);
			    
			    TextView tv_escalator=new TextView(activity);
			    if (escalator.equals(" ") || escalator.equals("")) tv_escalator.setText("Scale mobili: Ignoto");
			    else tv_escalator.setText("Scale mobili: "+escalator);
			    tv_escalator.setPadding(0, 5, 0, 5);
			    ll.addView(tv_escalator);
			    
			    TextView tv_parking=new TextView(activity);
			    if (parking.equals(" ") || parking.equals("")) tv_parking.setText("Parcheggio: Ignoto");
			    else tv_parking.setText("Parcheggio: "+parking);
			    tv_parking.setPadding(0, 5, 0, 5);
			    ll.addView(tv_parking);
			    */
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
}
