package com.viadeo.android.sdk;

import org.json.JSONException;
import org.json.JSONObject;

import com.viadeo.android.sdk.ViadeoAPIManager.Method;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * Object that execute a ViadeoRequest to the Viadeo graph API easily.This class
 * perform the request in background and publish results on the UI thread
 * through the callback interface ViadeoRequestListener
 * 
 * @see ViadeoRequest
 * @see ViadeoRequestListener
 */
public class ViadeoRequester extends AsyncTask<ViadeoRequest, String, Void> {
	
	private ViadeoRequestListener _listener;
	private int _requestCode;
	private Viadeo _viadeo;
	
	protected Void doInBackground(ViadeoRequest... requests) {
		
		for(int i=0; i<requests.length; i++) {

			_requestCode = requests[i].getRequestCode();
			_viadeo = requests[i].getViadeo();
			_listener = requests[i].getListener();
			
			String graphPath = requests[i].getGraphPath();
			Bundle params = requests[i].getParams();
			Method method = requests[i].getMethod();

			publishProgress(_viadeo.request(graphPath, method, params));
		}

		return null;
	}


    protected void onProgressUpdate(String... response) {

		Log.d(ViadeoConstants.LOG_TAG, "[RESPONSE] " + response[0]);

		try {
			
			JSONObject responseObject = new JSONObject(response[0]);

			if (!responseObject.isNull("error")) {
				
				if(responseObject.getJSONObject("error").getString("type").equals("Token revoked")) {
					_viadeo.logOut();					
				}

				_listener.onViadeoRequestError(_requestCode, responseObject.getJSONObject("error").getString("message"));

			} else {
				
				_listener.onViadeoRequestComplete(_requestCode, response[0]);
			}

		} catch (JSONException e) {
			Log.e(ViadeoConstants.LOG_TAG, "JSONException", e);
		}
    }
    
	protected void onPostExecute(Void response) {
	}
}
