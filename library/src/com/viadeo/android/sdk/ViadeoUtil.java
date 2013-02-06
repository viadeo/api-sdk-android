package com.viadeo.android.sdk;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.util.Log;

/**
 * Utility class to build and catch request parameters
 */
public final class ViadeoUtil {
	
	public static UrlEncodedFormEntity encodePostParams(Bundle parameters) {
		
		if(parameters != null) {
			
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			Iterator<String> keys = parameters.keySet().iterator();
			
			while(keys.hasNext()) {
				String key = keys.next();
				pairs.add(new BasicNameValuePair(key, parameters.getString(key)));
			}
			
			try {
				return new UrlEncodedFormEntity(pairs, "utf-8");
			} catch (UnsupportedEncodingException e) {
				Log.e(ViadeoConstants.LOG_TAG, "UnsupportedEncodingException", e);
			}
		}
		
		return null;
	}

    public static String encodeUrl(Bundle parameters) {
    	
        if (parameters == null) {
            return "";
        }

        try {

            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String key : parameters.keySet()) {
                if (first) first = false; else sb.append("&");
                sb.append(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(parameters.getString(key), "UTF-8"));
            }
            return sb.toString();

        } catch (UnsupportedEncodingException e) {
        	
        	return "";
        }
    }

    public static Bundle parseUrl(String url) {

    	try {
            URL u = new URL(url);
            Bundle b = decodeUrl(u.getQuery());
            b.putAll(decodeUrl(u.getRef()));
            return b;
        } catch (MalformedURLException e) {
            return new Bundle();
        }
    }
    
    public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        
        try {
        	
            if (s != null) {
                String array[] = s.split("&");
                for (String parameter : array) {
                    String v[] = parameter.split("=");
                    params.putString(URLDecoder.decode(v[0], "UTF-8"), URLDecoder.decode(v[1], "UTF-8"));
                }
            }
            
        } catch (UnsupportedEncodingException e) {}

        return params;
    }
}
