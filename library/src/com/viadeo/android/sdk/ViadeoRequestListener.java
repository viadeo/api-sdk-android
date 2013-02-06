package com.viadeo.android.sdk;

/**
 * Callback interface for the Viadeo request
 * 
 * @see ViadeoRequest
 */
public interface ViadeoRequestListener {

	/**
	 * Called when a request completes with the given response
	 * 
	 * @param requestCode
	 *            The integer request code originally supplied to ViadeoRequest
	 *            constructor, allowing you to identify who this result came
	 *            from
	 * @param response
	 *            Request response in JSON format
	 */
	public void onViadeoRequestComplete(int requestCode, String response);
	
	
	/**
	 * Called when a request has failed with the given error message
	 * 
	 * @param requestCode
	 *            The integer request code originally supplied to ViadeoRequest
	 *            constructor, allowing you to identify who this result came
	 *            from
	 * @param errorMessage
	 *            error message
	 */
	public void onViadeoRequestError(int requestCode, String errorMessage);
}
