package com.viadeo.android.sdk;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Represent a login/logout button to Viadeo 
 */
public class ViadeoConnectButton extends Button {
	
	/**
	 * Constructor for the Viadeo login/logout Button. This button could be
	 * create from a XML Layout
	 * 
	 * @param context
	 */
	public ViadeoConnectButton(Context context) {
		super(context);
		setBackgroundResource(R.drawable.viadeo_connect);
	}
	
	
	/**
	 * Constructor for the Viadeo login/logout Button. This button could be
	 * create from a XML Layout
	 * 
	 * @param context
	 */
	public ViadeoConnectButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBackgroundResource(R.drawable.viadeo_connect);
	}

	
	/**
	 * Change the aspect of the button if the user is logged in or not
	 * 
	 * @param viadeo
	 *            The Viadeo main object
	 */
	public void refreshState(Viadeo viadeo) {
		if(viadeo.isLoggedIn()) {
			setBackgroundResource(R.drawable.viadeo_disconnect);
		} else {
			setBackgroundResource(R.drawable.viadeo_connect);
		}
	}
}
