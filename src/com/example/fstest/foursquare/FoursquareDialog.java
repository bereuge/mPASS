package com.example.fstest.foursquare;

import com.example.fstest.R;
import android.app.Dialog;
import android.app.ProgressDialog;

import android.graphics.Bitmap;

import android.os.Bundle;
import android.util.Log;
import android.content.Context;

import android.view.Window;

import android.webkit.CookieSyncManager;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FoursquareDialog extends Dialog 
{
    private String mUrl;
    private FsqDialogListener mListener;
    private ProgressDialog mSpinner;
 
    private static final String TAG = "Foursquare-WebView";
    
	public FoursquareDialog(Context context, String url, FsqDialogListener listener) 
	{
		super(context);
		mUrl	= url;
		mListener	= listener;
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_foursquare);
        
        mSpinner = new ProgressDialog(getContext());
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");
        setUpWebView();

        CookieSyncManager.createInstance(getContext());
	    CookieManager cookieManager = CookieManager.getInstance();
	    cookieManager.removeAllCookie();
    }
	
	private void setUpWebView() 
	{
		WebView webView=(WebView)findViewById(R.id.webview);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.setWebViewClient(new FsqWebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(mUrl);
	}
	
	private class FsqWebViewClient extends WebViewClient 
	{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) 
		{
			Log.d(TAG, "Redirecting URL " + url);
			//Quando viene reindirizzata al redirect url!
			if (url.startsWith(FoursquareApp.CALLBACK_URL)) 
			{
				String urls[] = url.split("=");
				mListener.onComplete(urls[1]);
				FoursquareDialog.this.dismiss();
				return true;	
			}
			return false;
		}
	
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) 
		{
			Log.d(TAG, "Page error: " + description);
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onError(description);
			FoursquareDialog.this.dismiss();
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) 
		{
			Log.d(TAG, "Loading URL: " + url);
			super.onPageStarted(view, url, favicon);
			mSpinner.show();
		}
		
		@Override
		public void onPageFinished(WebView view, String url) 
		{
			super.onPageFinished(view, url);
			mSpinner.dismiss();
		}
	}
	
	public interface FsqDialogListener 
	{
		public abstract void onComplete(String accessToken);
		public abstract void onError(String error);
	}
}