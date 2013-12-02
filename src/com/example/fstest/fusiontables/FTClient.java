package com.example.fstest.fusiontables;


import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.fstest.MapActivity;
import com.example.fstest.QuizActivity;
/*import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;*/

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.app.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
/*import android.os.Handler;
import android.os.Message;*/

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;

//Classe per gestire la Fusion Table, inclusa l'autorizzazione e le query
//Per usarlo, basta crearne una nuova istanza, richiede il token in automatico.
//Per eseguire le query, usare prima il metodo .setQuery(-query sql-) e in seguito .query() con input il nome della function da usare come callback 
public class FTClient 
{
	private AuthPreferences authPreferences;
	private static final int AUTHORIZATION_CODE = 1993;
	//private static final int ACCOUNT_CODE = 1601;
	private final String SCOPE = "https://www.googleapis.com/auth/fusiontables";
	public static final String CALLBACK_URL = "http://localhost";
	//private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/auth?";
	private static String query_url="https://www.googleapis.com/fusiontables/v1/query?sql=";
	private Context context;
	private String instruction;
	
	public FTClient(Context _context) 
	{
		//Prendo il primo account Google associato al dispositivo
		context=_context;
		authPreferences=new AuthPreferences(context);
        AccountManager accountManager=AccountManager.get(context);
        Account[] ac=accountManager.getAccounts();
        //Account a=ac[0];
        authPreferences.setUser(ac[0].name);
        Log.d("Account Manger", authPreferences.getUser());
        requestToken();
	}
	
	private void requestToken() 
	{
		Account userAccount = null;
		String user = authPreferences.getUser();
		AccountManager accountManager=AccountManager.get(context);
		for (Account account : accountManager.getAccounts()) 
		{
			Log.d("Account", account.name);
			if (account.name.equals(user)) 
			{
				userAccount = account;
				break;
			}
		}
		//Utilizzo l'account per ottenere il token oauth2 per poter fare le query
		accountManager.getAuthToken(userAccount, "oauth2:" + SCOPE, null, (Activity)context,new OnTokenAcquired(), null);
	}
	
	private class OnTokenAcquired implements AccountManagerCallback<Bundle> 
	{
		 
		@Override
		public void run(AccountManagerFuture<Bundle> result) 
		{
			try 
			{
				Bundle bundle = result.getResult();
 
				Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
				if (launch != null) 
				{
					Log.d("Debug","Errore! Non ha preso il token?");
					((Activity)context).startActivityForResult(launch, AUTHORIZATION_CODE);
				}
				else 
				{
					String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
					//Token ottenuto
					authPreferences.setToken(token);
					Log.d("TOKEN", token);
				}
			} 
			catch (Exception e) 
			{
				//throw new RuntimeException(e);
			}
		}
	}
	
	//Il problema di questo metodo è che non restituisce tutto il json, ma è poco importante
	public void queryOnNewThread(final String code)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				query(code);
			}
		}.start();
	}
	
	//Metodo che esegue la query, utilizza una querytask per il compito
	public String query(String code)
	{
		//Se si creasse qua il nuovo thread?
		String result="";
		QueryTask task=new QueryTask(code, (Activity)context);
		//Test per vedere se il token non è ancora stato ottenuto
		if (authPreferences.getToken()==null || authPreferences.getToken()=="token")
		{
			//Apparentemente questo blocco è inutile, il token lo ottiene ma non è più valido
			try 
			{
				Log.d("Response","Token non ottenuto");
				//Non è abbastanza,servirebbe ripeterlo fino a che non si è ottenuto il token, invece che aspettare una volta sola
				//Usare handler con il metodo postDelayed() ?? Da controllare	
				//Sembra completamente inutile 18/11/13
				Thread.sleep(6000);
				try 
				{
					//Dopo aver aspettato, eseguo l'asynctask
					result=task.execute(instruction).get();
				} 
				catch (ExecutionException e) 
				{
					e.printStackTrace();
				}
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			try 
			{
				result=task.execute(instruction).get();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			} 
			catch (ExecutionException e) 
			{
				e.printStackTrace();
			}
		}
		return result;
	}
	
	//Asynctask che gestisce le richieste http per le query
	private class QueryTask extends AsyncTask<String, Void, String>
	{
		String code=null;
		String output=null;
		Activity activity;
		
		private QueryTask(String _code, Activity _activity)
		{
			code=_code;
			activity=_activity;
		}
		
		@Override
		protected String doInBackground(String... urls) 
		{
			String response=executeQuery(urls[0]);
			return response;
		}
		
		@Override
		protected void onPostExecute(String result) 
		{
			// Bisogna studiarsi i callback!
			Log.d("Response", "Finito!");
			
			//Richiama la procedura che ci è stata data in input tramite il parametro "code"
			if (code.equals("setmarkers"))
			{
				try 
				{
					JSONObject json_result;
					json_result = new JSONObject(output);
					JSONArray venues=json_result.getJSONArray("rows");
					((MapActivity) activity).setMarkers(venues);
				} 
				catch (JSONException e) 
				{
					((MapActivity) activity).setMarkers(null);
					e.printStackTrace();
				}
			}
			
			if (code.equals("insertvenue"))
			{
				try 
				{
					Boolean success=false;
					JSONObject insert_result=new JSONObject(output);
					if (insert_result.getJSONArray("rows")!=null)
					{
						success=true;
					}
					((QuizActivity) activity).loadResponse(output, success);
				} 
				catch (JSONException e) 
				{
					((QuizActivity) activity).loadResponse(output, false);
					e.printStackTrace();
				}
				
			}
			
			if (code.equals("loadvenue"))
			{
				try 
				{
					JSONObject json_result;
					json_result = new JSONObject(output);
					JSONArray venues=json_result.getJSONArray("rows");
					((MapActivity) activity).showInfoDialog(venues);
				} 
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		private String executeQuery(String _instruction)
		{
			//String output = null;
			String instruction=_instruction.replaceAll(" ", "%20");
			String url=query_url+instruction;
			url=url+"&access_token="+authPreferences.getToken().toString();
			//Se è una query SELECT, usiamo metodo GET, altrimenti metodo POST (per INSERT e UPDATE)
			if (instruction.contains("SELECT"))
			{
				try
				{
					HttpClient client=new DefaultHttpClient();
					HttpGet get=new HttpGet(url);
					org.apache.http.HttpResponse resp=client.execute(get);
					HttpEntity httpEntity = resp.getEntity();
					output=EntityUtils.toString(httpEntity);
					Log.d("Response", output);
				}
				catch(Exception ex)
				{
					Log.d("Response", "Errore");
				}
			}
			else if (instruction.contains("INSERT") || instruction.contains("UPDATE"))
			{
				try
				{
					HttpClient client=new DefaultHttpClient();
					HttpPost post=new HttpPost(url);
					org.apache.http.HttpResponse resp=client.execute(post);
					HttpEntity httpEntity = resp.getEntity();
					output=EntityUtils.toString(httpEntity);
					Log.d("Response", output);
				}
				catch(Exception ex)
				{
					Log.d("Response", "Errore");
				}
			}
			return output;
		}
	}
	
	public void setQuery(String _instruction)
	{
		instruction=_instruction;
	}
}