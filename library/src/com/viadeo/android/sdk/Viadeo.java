package com.viadeo.android.sdk;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.viadeo.android.sdk.ViadeoAPIManager.Method;

/**
 * Main Viadeo object for interacting with the Viadeo Graph API. Provides
 * methods to log in and log out a user, make requests using the Graph API, and
 * start user interface interactions with the API (such as pop-ups promoting for
 * login)
 */
public class Viadeo {

	private static final String ACCESS_TOKEN = "access_token";
	public static boolean LOG = true; 
	
	private String _clientId;
	private String _clientSecret;
	private Context _context; 
	private ViadeoOAuthListener _listener;
	private SharedPreferences _settings;


	/**
	 * Constructor for Viadeo object
	 * 
	 * @param context
	 *            The context of the application or the activity
	 * @param clientId
	 *            Your Viadeo Client Id found at http://dev.viadeo.com
	 * @param clientSecret
	 *            Your Viadeo Client Secret found at http://dev.viadeo.com
	 */
	public Viadeo(Context context, String clientId, String clientSecret) {
	    if ((clientId == null || "".equals(clientId)) && (clientSecret == null || "".equals(clientSecret))) {
            throw new IllegalArgumentException("You must specify your \"client Id\" and your \"client Secret\" when instantiating a Viadeo object");
        }
	    _context = context;
	    _clientId = clientId;
	    _clientSecret = clientSecret;
	    
		_settings = _context.getSharedPreferences(ViadeoConstants.PREFS_NAME, 0);
		

	}


	/**
	 * Open a webpage in a dialog to prompt the user to login to his Viadeo
	 * account and grant the application to access to his account.
	 * <br />
	 * Note that this method is asynchronous and the callback will be invoked in
	 * the original calling thread (not in a background thread).
	 * 
	 * @param listener
	 *            Callback interface for notifying the calling application
	 *            when the authentication dialog has completed, failed, or been
	 *            canceled.
	 */
	public void authorize(ViadeoOAuthListener listener) {

		_listener = listener;
		
		ViadeoLoginPopup viadeoDialog = new ViadeoLoginPopup(this, _context);
		viadeoDialog.loadUrl(getAuthUrl()); 
		viadeoDialog.show();

	}
	
	
	/**
	 * Indicate if the user is already logged in to Viadeo through the
	 * application
	 * 
	 * @return true if already logged in
	 */
	public boolean isLoggedIn() {
		if(getAccessToken() != null)
			return true;
		return false;
	}
	
	
	/**
	 * Disconnect the user from Viadeo, delete his access token from the shared
	 * preferences and delete cookies
	 * 
	 * @return true if access token is correctly removed
	 */
	public boolean logOut() {
		CookieSyncManager.createInstance(_context); 
		CookieManager.getInstance().removeAllCookie();
		return _settings.edit().remove(ACCESS_TOKEN).commit();
	}
	
	/**
	 * Make a request to the Viadeo graph API with the given HTTP method and
	 * parameters.
	 * <br />
	 * (See http://dev.viadeo.com/documentation/) 
	 * <br />
	 * Note that this method blocks waiting for a network response, so do not
	 * call it in a UI thread.
	 * 
	 * @param graphPath
	 *            Path to resource in the Viadeo graph, e.g., to fetch data
	 *            about the currently logged authenticated user, provide "/me",
	 *            which will fetch https://api.viadeo.com/me
	 * @param method
	 *            The HTTP method (GET, POST, PUT, DELETE)
	 * @param params
	 *            Parameters for the request, could be null
	 *            
	 * @return Response in JSON format
	 * 
	 * @see ViadeoAPIManager.Method
	 * @see ViadeoRequester
	 * @see ViadeoRequest
	 */
	public String request(final String graphPath, Method method, final Bundle params) {
		
		return ViadeoAPIManager.getInstance().request(graphPath, method, params, getAccessToken());
	}
	
	
	/**
	 * Get the user access token to the Viadeo Graph API saved in the shared
	 * preferences
	 * 
	 * @return the user access token
	 */
	public String getAccessToken() {
		return _settings.getString(ACCESS_TOKEN, null);
	}
	
	
	protected void getAccessToken(final String code) {

		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... voids) {

				String result = ViadeoAPIManager.getInstance().getAccessToken(_clientId, _clientSecret, code);

				try {

					JSONObject response = new JSONObject(result);
					
					if(!response.isNull(("error"))) {
						return response.getString("error");
					}
					
					String accessToken = response.getString("access_token");
					_settings.edit().putString(ACCESS_TOKEN, accessToken).commit();
					
					return ACCESS_TOKEN;

				} catch (JSONException e) {
					return e.getMessage();
				}
				
			}

			@Override
			protected void onPostExecute(String result) {

				if(ACCESS_TOKEN.equals(result))
					_listener.onViadeoOAuthComplete();
				else
					_listener.onViadeoOAuthError(-1, result, ViadeoConstants.ACCESS_TOKEN_URL);
			}
			
		}.execute();

	}
	
	
	protected void error(int errorCode, String description, String failingUrl) {
		_listener.onViadeoOAuthError(errorCode, description, failingUrl);
	}
	
	
	protected void cancel() {
		_listener.onViadeoOAuthCancel();
	}


	private String getAuthUrl() {

		Bundle parameters = new Bundle();

		String endpoint = ViadeoConstants.AUTHORIZE_URL;
		parameters.putString("response_type", "code");
		parameters.putString("display", "popup");
		parameters.putString("lang", Locale.getDefault().getLanguage());
		parameters.putString("client_id", _clientId);
		parameters.putString("redirect_uri", ViadeoConstants.REDIRECT_URI);
		parameters.putString("cancel_url", ViadeoConstants.CANCEL_URI);

		String url = endpoint + "?" + ViadeoUtil.encodeUrl(parameters);

		return url;
	}

}
