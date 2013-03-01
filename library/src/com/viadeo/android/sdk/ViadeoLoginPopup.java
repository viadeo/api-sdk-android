package com.viadeo.android.sdk;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Dialog to show the login page and catch OAuth URI
 */
public class ViadeoLoginPopup extends Dialog {

	private WebView _webView;
	private Viadeo _viadeo;
	
	protected ViadeoLoginPopup(Viadeo viadeo, Context context) {
		super(context);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.viadeo_login_popup);
		
		LayoutParams params = getWindow().getAttributes();
		params.height = LayoutParams.MATCH_PARENT;
		params.width = LayoutParams.MATCH_PARENT;
		getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

		_viadeo = viadeo;

		init();
	}
	
	private void init() {
		
		final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		_webView = (WebView) findViewById(R.id.webView);
		_webView.getSettings().setJavaScriptEnabled(true);
		
		_webView.setWebViewClient(new WebViewClient() {
			
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				
				if(Viadeo.LOG)
					Log.d(ViadeoConstants.LOG_TAG, "onReceivedError errorCode = " + errorCode + " description = " + description + " failingUrl = " + failingUrl);

				if(errorCode != -2 && (!failingUrl.startsWith(ViadeoConstants.REDIRECT_URI) || !failingUrl.startsWith(ViadeoConstants.CANCEL_URI))) {
					_viadeo.error(errorCode, description, failingUrl);
					dismiss();
				}
				
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				progressBar.setVisibility(View.VISIBLE);
				
				if(url.startsWith(ViadeoConstants.REDIRECT_URI)) {
					
					if(_viadeo.getAccessToken() == null) {

                		Bundle values = ViadeoUtil.parseUrl(url);
                		_viadeo.getAccessToken(values.getString("code"));

					}
					_webView.clearCache(true);
	                dismiss();

				} else if(url.startsWith(ViadeoConstants.CANCEL_URI)) {
					_viadeo.cancel();
					_webView.clearCache(true);
					dismiss();
				}
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				progressBar.setVisibility(View.GONE);
			}

		});

	}

	protected void loadUrl(String url) {
		_webView.loadUrl(url);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			_webView.clearCache(true);
		}
		return super.onKeyDown(keyCode, event);
	}

}
