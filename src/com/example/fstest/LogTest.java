package com.example.fstest;

import java.util.ArrayList;

import com.example.fstest.log.LogAdapter;
import com.example.fstest.log.LogDbManager;
import com.example.fstest.log.LogEntry;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class LogTest extends Activity {

	private ArrayList<LogEntry> entries;
	private LogAdapter adapter;
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_test);
		
		listView=(ListView)findViewById(R.id.listView1);
		adapter=new LogAdapter(this);
		entries=new ArrayList<LogEntry>();
		Button write=(Button)findViewById(R.id.button1);
		Button readall=(Button)findViewById(R.id.button2);
		final EditText text=(EditText)findViewById(R.id.editText1);
		
		write.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				LogDbManager log=new LogDbManager(getApplicationContext());
				log.openToWrite();
				long res=log.insertEntry("prova", "11111");
				if (res==-1)
					Toast.makeText(getApplicationContext(), "nope", Toast.LENGTH_LONG).show();
				else
					Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_LONG).show();
				log.close();
			}
		});
		
		readall.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				LogDbManager log=new LogDbManager(getApplicationContext());
				log.openToRead();
				entries=log.getAllEntries();
				adapter.setData(entries);
				listView.setAdapter(adapter);
				/*
				LogDbManager log=new LogDbManager(getApplicationContext());
				log.openToRead();
				Cursor c=log.queueAll();
				if (c.getCount()>0)
				{
					text.setText("");
					while (c.moveToNext())
					{
						String id =c.getString(0);
						String action=c.getString(1);
						String date=c.getString(2);
						text.setText(id+" "+action+" "+date+"\n"+text.getText());
					}
				}
				log.close();
				*/
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_test, menu);
		return true;
	}

}
