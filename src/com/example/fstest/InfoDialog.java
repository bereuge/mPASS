package com.example.fstest;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

//Classe per il dialog che mostra le segnalazioni per un dato luogo
public class InfoDialog extends Dialog implements OnClickListener
{
	private Activity activity;
	private JSONArray venues;
	
	public InfoDialog(Activity _activity, JSONArray _venues)
	{
		super(_activity);
		
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
			    tv_quiztitle.setPadding(0, 10, 0, 10);
			    ll.addView(tv_quiztitle);
				
				TextView tv_accesslvl=new TextView(activity);
				if (accesslevel.equals("A"))
					tv_accesslvl.setText("Livello accessibilità: Accessibile");
				else if (accesslevel.equals("P"))
					tv_accesslvl.setText("Livello accessibilità: Parz. accessibile");
				else if (accesslevel.equals("N"))
					tv_accesslvl.setText("Livello accessibilità: Non accessibile");
				else if (accesslevel.equals(" ") || accesslevel.equals("")) tv_accesslvl.setText("Livello accessibilità: Ignoto");
				tv_accesslvl.setPadding(0, 5, 0, 5);
				ll.addView(tv_accesslvl);
				
				TextView tv_comment=new TextView(activity);
				if (comment.equals(" ") || comment.equals("")) tv_comment.setText("Commento: Non inserito");
				else tv_comment.setText("Commento: "+comment);
			    tv_comment.setPadding(0, 5, 0, 5);
			    ll.addView(tv_comment);
			    
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
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
}
