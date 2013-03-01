package com.viadeo.android.sdk;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.viadeo.android.sdk.ViadeoAPIManager.Method;

/**
 * Object that execute a ViadeoRequest to the Viadeo graph API easily.This class
 * perform the request in background and publish results on the UI thread
 * through the callback interface ViadeoRequestListener
 * 
 * @see ViadeoRequest
 * @see ViadeoRequestListener
 */
public class ViadeoRequester extends AsyncTask<ViadeoRequest, ViadeoProgressBean, Void> {

	protected Void doInBackground(ViadeoRequest... requests) {

		for (int i = 0; i < requests.length; i++) {

			String graphPath = requests[i].getGraphPath();
			Bundle params = requests[i].getParams();
			Method method = requests[i].getMethod();

			ViadeoProgressBean progressBean = new ViadeoProgressBean();
			progressBean.setResponse(requests[i].getViadeo().request(graphPath, method, params));
			progressBean.setListener(requests[i].getListener());
			progressBean.setRequestCode(requests[i].getRequestCode());
			progressBean.setViadeo(requests[i].getViadeo());

			publishProgress(progressBean);
		}

		return null;
	}

	protected void onProgressUpdate(ViadeoProgressBean... progressBean) {

		if(Viadeo.LOG)
			Log.d(ViadeoConstants.LOG_TAG, "[RESPONSE] " + progressBean[0].getResponse());

		try {

			JSONObject responseObject = new JSONObject(progressBean[0].getResponse());

			if (!responseObject.isNull("error")) {

				if (responseObject.getJSONObject("error").getString("type").equals("Token revoked")) {
					progressBean[0].getViadeo().logOut();
				}

				progressBean[0].getListener().onViadeoRequestError(progressBean[0].getRequestCode(), responseObject.getJSONObject("error").getString("message"));

			} else {

				progressBean[0].getListener().onViadeoRequestComplete(progressBean[0].getRequestCode(), progressBean[0].getResponse());
			}

		} catch (JSONException e) {
			Log.e(ViadeoConstants.LOG_TAG, "JSONException", e);
		}
	}

	protected void onPostExecute(Void response) {
	}

}
