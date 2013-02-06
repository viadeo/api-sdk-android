package com.viadeo.android.sdk;

/**
 * Callback interface for the OAuth login process
 */
public interface ViadeoOAuthListener {

	/**
	 * Called when the user is logged in to Viadeo
	 */
	public void onViadeoOAuthComplete();
	
	/**
	 * Called when the user has canceled the login process
	 */
	public void onViadeoOAuthCancel();

	/**
	 * Called when the login process has failed
	 * 
	 * @param errorCode
	 * @param description
	 * @param failingUrl
	 */
	public void onViadeoOAuthError(int errorCode, String description, String failingUrl);
}
