package com.example.fstest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.fstest.log.LogDbManager;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class NewProfileActivity extends Activity 
{
	User new_user;
	LogDbManager log;
	String imagepath=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_profile);
		
		log=new LogDbManager(this);
		/*log.openToWrite();
		log.deleteAll();
		log.close();*/
		
		Button skip=(Button)findViewById(R.id.btn_skip);
		skip.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				NewProfileActivity.this.finish();
			}
		});
		
		//final String items[]=new String[] {"Camera","Galleria"};
		List<String> items = new ArrayList<String>();
		items.add("Camera");
		items.add("Galleria");
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
		
		Button btn_image=(Button)findViewById(R.id.btn_image);
		btn_image.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				//Toast.makeText(NewProfileActivity.this, "Immagine", Toast.LENGTH_LONG).show();
				dialog.show();
			}
		});
		
		Button btn_confirm=(Button)findViewById(R.id.btn_confirmprofile);
		Button btn_anon=(Button)findViewById(R.id.btn_anon);
		btn_confirm.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				EditText et_user=(EditText)findViewById(R.id.et_user);
				String username=et_user.getText().toString();
				if (!username.isEmpty())
				{
					new_user=new User(NewProfileActivity.this);
					new_user.eraseUser();
					new_user.setName(et_user.getText().toString());
					new_user.setImagePath(imagepath);
					new_user.setType("User");
					log.openToWrite();
					log.deleteAll();
					Date date=new Date();
					SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String sdate=dateFormat.format(date);
					log.insertEntry("User created!", sdate);
					log.close();
					NewProfileActivity.this.finish();
				}
				else
				{
					Toast.makeText(NewProfileActivity.this, "Username non inserito", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		btn_anon.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				new_user=new User(NewProfileActivity.this);
				new_user.eraseUser();
				new_user.setName("Anonimo");
				new_user.setImagePath(null);
				new_user.setType("User");
				NewProfileActivity.this.finish();
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
			
			ImageView image=(ImageView)findViewById(R.id.imageView2);
			image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			imagepath=picturePath;
		}
		else if (requestCode==2 && resultCode==RESULT_OK)
		{
			Bitmap photo=(Bitmap)data.getExtras().get("data");
			ImageView image=(ImageView)findViewById(R.id.imageView2);
			image.setImageBitmap(photo);
			try 
			{
				File imagefile=createImageFile(photo);
				Log.d("dir",imagefile.getAbsolutePath());
				imagepath=imagefile.getAbsolutePath();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_profile, menu);
		return true;
	}

	@Override
    public void onBackPressed() 
    {
    	
    }
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		SharedPreferences pref=this.getSharedPreferences("activity", Context.MODE_PRIVATE);
		//pref.getBoolean("firsttime", true);
		Editor editor = pref.edit();
		editor.putBoolean("firsttime", false);
		editor.commit();
		Intent i=new Intent(NewProfileActivity.this,MapActivity.class);
		startActivity(i);
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
}
