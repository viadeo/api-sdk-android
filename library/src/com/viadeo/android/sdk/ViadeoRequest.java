package com.viadeo.android.sdk;

import android.os.Bundle;

import com.viadeo.android.sdk.ViadeoAPIManager.Method;

/**
 * Object that create a request to the Viadeo graph API easily. This object must
 * be passed to the ViadeoRequester to execute the request asynchronously
 * 
 * @see ViadeoRequester
 */
public class ViadeoRequest {

	private int requestCode;
	private String graphPath;
	private Bundle params;
	private Method method;
	private Viadeo viadeo;
	private ViadeoRequestListener listener;
	
	
	/**
	 * Constructor for the request object
	 * 
	 * @param requestCode
	 *            this code will be returned in ViadeoRequestListener
	 * @param graphPath
	 *            Path to resource in the Viadeo graph, e.g., to fetch data
	 *            about the currently logged authenticated user, provide "/me",
	 *            which will fetch https://api.viadeo.com/me
	 * @param params
	 *            Parameters for the request, could be null
	 * @param method
	 *            The HTTP method (GET, POST, PUT, DELETE)
	 * @param viadeo
	 *            the Viadeo main object
	 * @param listener
	 *            Callback interface for notifying the calling application when
	 *            the request is completed or has failed
	 * @see Viadeo
	 * @see ViadeoAPIManager.Method
	 * @see ViadeoRequestListener
	 */
	public ViadeoRequest(int requestCode, String graphPath, Bundle params, Method method, Viadeo viadeo, ViadeoRequestListener listener) {
		this.requestCode = requestCode;
		this.graphPath = graphPath;
		this.params = params;
		this.method = method;
		this.viadeo = viadeo;
		this.listener = listener;
	}

	
	/**
	 * @return the request code
	 */
	public int getRequestCode() {
		return requestCode;
	}

	
	/**
	 * @return the graph path
	 */
	public String getGraphPath() {
		return graphPath;
	}

	
	/**
	 * @return request parameters
	 */
	public Bundle getParams() {
		return params;
	}

	
	/**
	 * @return the HTTP method
	 */
	public Method getMethod() {
		return method;
	}

	
	/**
	 * @return the Viadeo main object
	 */
	public Viadeo getViadeo() {
		return viadeo;
	}

	
	/**
	 * @return the callback interface
	 */
	public ViadeoRequestListener getListener() {
		return listener;
	}
	
}
