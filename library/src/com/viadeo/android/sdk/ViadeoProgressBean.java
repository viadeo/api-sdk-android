package com.viadeo.android.sdk;

/**
 * Object used to transmit data when ViadeoRequester publish progress
 * 
 * @see ViadeoRequester
 */

public class ViadeoProgressBean {

	private String _response;
	private ViadeoRequestListener _listener;
	private int _requestCode;
	private Viadeo _viadeo;

	protected String getResponse() {
		return _response;
	}

	protected void setResponse(String response) {
		this._response = response;
	}

	protected ViadeoRequestListener getListener() {
		return _listener;
	}

	protected void setListener(ViadeoRequestListener listener) {
		this._listener = listener;
	}

	protected int getRequestCode() {
		return _requestCode;
	}

	protected void setRequestCode(int requestCode) {
		this._requestCode = requestCode;
	}

	public Viadeo getViadeo() {
		return _viadeo;
	}

	public void setViadeo(Viadeo viadeo) {
		this._viadeo = viadeo;
	}

}
