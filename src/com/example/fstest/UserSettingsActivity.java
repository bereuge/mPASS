package com.example.fstest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.fstest.foursquare.FoursquareApp;
import com.example.fstest.foursquare.FoursquareApp.FsqAuthListener;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserSettingsActivity extends Activity 
{
	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_settings);
		
		user=new User(this);
		
		ImageView iv_image=(ImageView)findViewById(R.id.iv_photo_user);
		Bitmap image=BitmapFactory.decodeFile(user.getImagePath());
		iv_image.setImageBitmap(image);
		
		TextView tv_name=(TextView)findViewById(R.id.tv_username);
		tv_name.setText(user.getName());
		
		List<String> items = new ArrayList<String>();
		items.add("Camera");
		items.add("Galleria");
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
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
		
		Button btn_photo=(Button)findViewById(R.id.btn_modify_photo);
		btn_photo.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				dialog.show();
			}
		});
		
		Button btn_name=(Button)findViewById(R.id.btn_modify_name);
		btn_name.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				AlertDialog.Builder alert = new AlertDialog.Builder(UserSettingsActivity.this);
				alert.setTitle("Modifica nome");
				alert.setMessage("Inserisci il nuovo username");
				final EditText input = new EditText(UserSettingsActivity.this);
				alert.setView(input);
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						String value = input.getText().toString();
						user.setName(value);
						TextView tv_name=(TextView)findViewById(R.id.tv_username);
						tv_name.setText(value);
					}
				});
				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						
					}
				});
				alert.show();
			}
		});
		
		Button btn_fsq=(Button)findViewById(R.id.btn_fsq_login);
		btn_fsq.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				FsqAuthListener listener=new FsqAuthListener() 
				{
					
					@Override
					public void onSuccess() 
					{
						Toast.makeText(UserSettingsActivity.this, "Login effettuato con successo!", Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void onFail(String error) 
					{
						Toast.makeText(UserSettingsActivity.this, "Errore nel login. Ri", Toast.LENGTH_SHORT).show();
					}
				};
				
				FoursquareApp fsqApp=new FoursquareApp(UserSettingsActivity.this);
				fsqApp.setListener(listener);
				fsqApp.authorize();
				
				
			}
		});
		
		Button btn_reset=(Button)findViewById(R.id.btn_reset_user);
		btn_reset.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				AlertDialog.Builder alert = new AlertDialog.Builder(UserSettingsActivity.this);
				alert.setTitle("Eliminazione utente");
				alert.setMessage("Attenzione: Quest'azione cancellerà tutti i dati e le preferenze relative al tuo utente.");
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						user.resetPref();
						user.eraseUser();
						Intent startActivity = new Intent(UserSettingsActivity.this, SplashActivity.class);
						int mPendingIntentId = 123456;
						PendingIntent mPendingIntent = PendingIntent.getActivity(UserSettingsActivity.this, mPendingIntentId, startActivity, PendingIntent.FLAG_CANCEL_CURRENT);
						AlarmManager mgr = (AlarmManager)UserSettingsActivity.this.getSystemService(Context.ALARM_SERVICE);
						mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
						System.exit(0);
					}
				});
				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						
					}
				});
				alert.show();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_settings, menu);
		return true;
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
			
			ImageView image=(ImageView)findViewById(R.id.iv_photo_user);
			image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			user.setImagePath(picturePath);
		}
		else if (requestCode==2 && resultCode==RESULT_OK)
		{
			Bitmap photo=(Bitmap)data.getExtras().get("data");
			ImageView image=(ImageView)findViewById(R.id.iv_photo_user);
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
	
	private File createImageFile(Bitmap image) throws IOException
	{
		String dir=Environment.getExternalStorageDirectory().toString();
		OutputStream stream=null;
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
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
