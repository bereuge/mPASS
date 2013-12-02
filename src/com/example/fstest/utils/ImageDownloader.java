package com.example.fstest.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class ImageDownloader extends AsyncTask<String,Void,String>
{
	
	public ImageDownloader()
	{
		
	}
	
    @Override
    protected String doInBackground(String... param) 
    {
        // TODO Auto-generated method stub
        return downloadBitmap(param[0]);
    }

    @Override
    protected void onPreExecute() {
        Log.i("Async-Example", "onPreExecute Called");
        /*simpleWaitDialog = ProgressDialog.show(ImageDownladerActivity.this,
                "Wait", "Downloading Image");*/

    }

    @Override
    protected void onPostExecute(String result) 
    {
        Log.i("Async-Example", "onPostExecute Called");
        /*try 
        {
			File imagefile=createImageFile(result);
		} 
        catch (IOException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        /*downloadedImg.setImageBitmap(result);
        simpleWaitDialog.dismiss();*/

    }

    private String downloadBitmap(String url) {
        // initilize the default HTTP client object
        final DefaultHttpClient client = new DefaultHttpClient();

        //forming a HttoGet request
        final HttpGet getRequest = new HttpGet(url);
        try {

            HttpResponse response = client.execute(getRequest);

            //check 200 OK for success
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode +
                        " while retrieving bitmap from " + url);
                return null;

            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try 
                {
                    // getting contents from the stream
                    inputStream = entity.getContent();

                    // decoding stream data back into image Bitmap that android understands
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    File imagefile=createImageFile(bitmap);
                    String imagepath=imagefile.getAbsolutePath();
                    return imagepath;
                } 
                finally 
                {
                    if (inputStream != null) 
                    {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } 
        catch (Exception e) 
        {
            // You Could provide a more explicit error message for IOException
            getRequest.abort();
            Log.e("ImageDownloader", "Something went wrong while" +
                    " retrieving bitmap from " + url + e.toString());
        }

        return null;
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