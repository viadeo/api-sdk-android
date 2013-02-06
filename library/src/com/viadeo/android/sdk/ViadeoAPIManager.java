package com.viadeo.android.sdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

/**
 * Internal SDK class to execute requests the Viadeo graph API and parse
 * responses
 */
public class ViadeoAPIManager {

	/**
	 * Available HTTP methods on the Viadeo graph API
	 */
	public enum Method {
		POST, 
		GET, 
		PUT, 
		DELETE
	};
	
	private static ViadeoAPIManager instance;
	
	protected static ViadeoAPIManager getInstance() {
		if (instance == null) {
			instance = new ViadeoAPIManager();
		}
		return instance;
	}
	
    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
	class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
		
		@Override
		public String getMethod() {
			return "DELETE";
		}

		public HttpDeleteWithBody(final String uri) {
			super();
			setURI(URI.create(uri));
		}

		public HttpDeleteWithBody(final URI uri) {
			super();
			setURI(uri);
		}

		public HttpDeleteWithBody() {
			super();
		}
	}
    
	private String connect(String url, Method method, Bundle headers, Bundle params) {
		
		HttpResponse resp = null;
		HttpClient asyncClient = new DefaultHttpClient();
		
		// fix for some providers
		HttpProtocolParams.setUseExpectContinue(asyncClient.getParams(), false);
		
		// create correct HttpUriRequest
		
		HttpUriRequest httpUriRequest = null;
		
		switch (method) {
		
		case POST:
			httpUriRequest = new HttpPost(url);
			((HttpPost)httpUriRequest).setEntity(ViadeoUtil.encodePostParams(params));
			break;
		case GET:
			httpUriRequest = new HttpGet(url + ((params != null) ? "&" + ViadeoUtil.encodeUrl(params) : ""));
			break;
		case PUT:
			httpUriRequest = new HttpPut(url);
			((HttpPut)httpUriRequest).setEntity(ViadeoUtil.encodePostParams(params));
			break;
		case DELETE:
			httpUriRequest = new HttpDeleteWithBody(url);
			((HttpDeleteWithBody)httpUriRequest).setEntity(ViadeoUtil.encodePostParams(params));
			break;
		default:
			httpUriRequest = new HttpGet(url + ((params != null) ? "&" + ViadeoUtil.encodeUrl(params) : ""));
			break;
		}
		
		// set headers
		
		if(headers != null) {
			
			Iterator<String> keys = headers.keySet().iterator();
			
			while(keys.hasNext()) {
				String key = keys.next();
				httpUriRequest.addHeader(key, headers.getString(key));
			}
			
		}

		/////////////////////////////// LOG

		Log.d(ViadeoConstants.LOG_TAG, "[" + httpUriRequest.getMethod() + "] " + httpUriRequest.getURI());
		
		for(int i=0; i<httpUriRequest.getAllHeaders().length; i++) {
			Log.d(ViadeoConstants.LOG_TAG, "[HEADER] " + httpUriRequest.getAllHeaders()[i].getName() + " : " + httpUriRequest.getAllHeaders()[i].getValue());
		}

		try {
			
			if(method == Method.POST || method == Method.PUT || method == Method.DELETE) {

				HttpEntity bodyEntity = ((HttpEntityEnclosingRequestBase)httpUriRequest).getEntity();
				if(bodyEntity != null) {
					InputStream bodyStream = bodyEntity.getContent();
					String body = convertStreamToString(bodyStream);
					Log.d(ViadeoConstants.LOG_TAG, "[BODY] " + body);
				}

			}
			
		} catch (IllegalStateException e) {
		} catch (IOException e) {
		}

		/////////////////////////////// LOG
		
		
		
		// execute HTTP Request
		
		try {
			
			resp = asyncClient.execute(httpUriRequest);

			HttpEntity entity = resp.getEntity();
			InputStream instream = entity.getContent();
			
			int responseCode =  resp.getStatusLine().getStatusCode();
			String response = convertStreamToString(instream);
					
			if((method == Method.POST || method ==  Method.PUT || method ==  Method.DELETE) && (responseCode == 200 || responseCode == 201) && !url.equals(ViadeoConstants.ACCESS_TOKEN_URL)) {
				return statusOkOjbect(String.valueOf(responseCode), response);
			}
			
			return response;
				
			
		} catch (ClientProtocolException e) {
			return errorOjbect("ClientProtocolException", e.getMessage());		
			
		} catch (IOException e) {
			return errorOjbect("IOException", e.getMessage());
		}
		
	}

	private String statusOkOjbect(String name, String message) {
		
		JSONObject responseObject = new JSONObject();
		
		try {

			JSONObject okObject = new JSONObject();
			okObject.put("code", name);
			okObject.put("message", message);
			
			responseObject.put("status", okObject);

		} catch (JSONException e) {}
		
		return responseObject.toString();		
	}

	private String errorOjbect(String name, String message) {
		
		JSONObject responseObject = new JSONObject();
		
		try {

			JSONObject errorObject = new JSONObject();
			errorObject.put("type", name);
			errorObject.put("message", message);
			
			responseObject.put("error", errorObject);

		} catch (JSONException e) {}
		
		return responseObject.toString();		
	}
	
	protected String getAccessToken(String clientId, String clientSecret, String code) {
		
		Bundle headers = new Bundle();
		headers.putString("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		
		Bundle params = new Bundle();
		params.putString("grant_type", "authorization_code");
		params.putString("client_id", clientId);
		params.putString("client_secret", clientSecret);
		params.putString("redirect_uri", ViadeoConstants.REDIRECT_URI);
		params.putString("code", code);
		
		return connect(ViadeoConstants.ACCESS_TOKEN_URL, Method.POST, headers, params);
		
	}
	
	protected String request(String graphPath, Method method, Bundle params, String accessToken) {
		
		Bundle headers = new Bundle();
		headers.putString("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		
		String url = ViadeoConstants.BASE_URL;
		url += graphPath;
		url += "?access_token=" + accessToken;
		
		if(method != Method.GET) {
			if(params == null)
				params = new Bundle();
			params.putString("access_token", accessToken);
		}
		
		return connect(url, method, headers, params);

	}


}
