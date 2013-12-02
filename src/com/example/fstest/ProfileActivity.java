package com.example.fstest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.fstest.log.LogAdapter;
import com.example.fstest.log.LogDbManager;
import com.example.fstest.log.LogEntry;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProfileActivity extends Activity 
{
	User user;
	private ArrayList<LogEntry> entries;
	private LogAdapter adapter;
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		//Setup profilo
		user=new User(this);
		listView=(ListView)findViewById(R.id.lv_log);
		adapter=new LogAdapter(this);
		entries=new ArrayList<LogEntry>();
		
		TextView tv_name=(TextView)findViewById(R.id.textView1);
		tv_name.setText(user.getName());
		//tv_name.setText("Mario Rossi");
		TextView tv_type=(TextView)findViewById(R.id.textView2);
		tv_type.setText(user.getType());
		TextView tv_nrepo=(TextView)findViewById(R.id.textView7);
		ImageView iv_image=(ImageView)findViewById(R.id.imageView1);
		Bitmap image=BitmapFactory.decodeFile(user.getImagePath());
		iv_image.setImageBitmap(image);
		//iv_image.setImageResource(R.drawable.user);
		
		//Carica log utente
		LogDbManager log=new LogDbManager(getApplicationContext());
		log.openToRead();
		entries=log.getAllEntries();
		adapter.setData(entries);
		listView.setAdapter(adapter);
		
		//Log.d("Test",user.getName());
		
		tv_nrepo.setText("Number of reports: "+String.valueOf(user.getNReports()));
		//tv_nrepo.setText("Number of reports: 13");
		
		final String items[]=new String[] {"Camera","Galleria"};
		ArrayAdapter<String> adapter=new ArrayAdapter(this, android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle("Seleziona immagine");
		builder.setAdapter(adapter,new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				if (which==0)
				{
					Intent i=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					/*Uri captureUri=Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"temp_avatar.jpg"));
					i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, captureUri);
					i.putExtra("return-data", true);*/
					startActivityForResult(i, 2);
				}
				else
				{
					Intent i=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(i, 1);
				}
			}
		});
		
		final AlertDialog dialog=builder.create();
		
		Button btn=(Button)findViewById(R.id.btn_image2);
		btn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				/*Intent i=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, 1);*/
				dialog.show();
			}
		});
		
		Button btn_gaps=(Button)findViewById(R.id.btn_gaps);
		btn_gaps.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				showPrefDialog("Gaps");
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==1 && resultCode==RESULT_OK && data!=null)
		{
			Uri selectedImage=data.getData();
			String[] filePath={MediaStore.Images.Media.DATA};
			
			Cursor cursor=getContentResolver().query(selectedImage, filePath, null, null, null);
			cursor.moveToFirst();
			
			int columnIndex=cursor.getColumnIndex(filePath[0]);
			String picturePath=cursor.getString(columnIndex);
			cursor.close();
			
			ImageView image=(ImageView)findViewById(R.id.imageView1);
			image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			user.setImagePath(picturePath);
		}
		else if (requestCode==2 && resultCode==RESULT_OK)
		{
			Bitmap photo=(Bitmap)data.getExtras().get("data");
			ImageView image=(ImageView)findViewById(R.id.imageView1);
			image.setImageBitmap(photo);
			try 
			{
				File imagefile=createImageFile(photo);
				Log.d("dir",imagefile.getAbsolutePath());
				user.setImagePath(imagefile.getAbsolutePath());
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
		
	}
	
	private File createImageFile(Bitmap image) throws IOException
	{
		String dir=Environment.getExternalStorageDirectory().toString();
		OutputStream stream=null;
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File file=new File(dir,"temp_avatar"+timeStamp+".jpg");
		try
		{
			//Bitmap bitmap=BitmapFactory.decodeFile(file.getName());
			stream=new FileOutputStream(file);
			image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			stream.flush();
			stream.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
	    }
		return file;
	}
	
	private void showPrefDialog(String category)
	{
		PrefDialog pdialog=new PrefDialog(this, category, user);
		pdialog.show();
	}
}
