package com.viadeo.android.sample;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.viadeo.android.sdk.Viadeo;
import com.viadeo.android.sdk.ViadeoAPIManager.Method;
import com.viadeo.android.sdk.ViadeoConnectButton;
import com.viadeo.android.sdk.ViadeoOAuthListener;
import com.viadeo.android.sdk.ViadeoRequest;
import com.viadeo.android.sdk.ViadeoRequestListener;
import com.viadeo.android.sdk.ViadeoRequester;

public class MainActivity extends Activity implements ViadeoRequestListener {

	private static final String VIADEO_CLIENT_ID = "";
	private static final String VIADEO_CLIENT_SECRET = "";

	private static final int REQUEST_CODE_ME = 789546;
	private static final int REQUEST_CODE_UPDATE_STATUS = 125863;

	private Viadeo _viadeo;
	private ProgressBar _progressBar;
	private TextView _nametextView;
	private TextView _headlinetextView;
	private Button _statusButton;
	private EditText _statusEditText;
	private ViadeoConnectButton _button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		_button = (ViadeoConnectButton) findViewById(R.id.viadeoConnectButton);
		_progressBar = (ProgressBar) findViewById(R.id.progressBar);
		_nametextView = (TextView) findViewById(R.id.name_textView);
		_headlinetextView = (TextView) findViewById(R.id.headline_textView);
		_statusButton = (Button) findViewById(R.id.status_Button);
		_statusEditText = (EditText) findViewById(R.id.status_editText);

		initViadeo();

	}

	private void initViadeo() {

		_viadeo = new Viadeo(this, VIADEO_CLIENT_ID, VIADEO_CLIENT_SECRET);

		_button.refreshState(_viadeo);

		if (_viadeo.isLoggedIn()) {
			getMe();
		}

		_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!_viadeo.isLoggedIn()) {

					_viadeo.authorize(new ViadeoOAuthListener() {

						@Override
						public void onViadeoOAuthComplete() {
							_button.refreshState(_viadeo);
							getMe();
						}

						@Override
						public void onViadeoOAuthCancel() {
						}

						@Override
						public void onViadeoOAuthError(int errorCode,
								String description, String failingUrl) {
						}

					});

				} else {

					_viadeo.logOut();
					_button.refreshState(_viadeo);
					_nametextView.setVisibility(View.GONE);
					_headlinetextView.setVisibility(View.GONE);
					_statusButton.setVisibility(View.GONE);
					_statusEditText.setVisibility(View.GONE);
				}
			}
		});

		_statusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateStatus(_statusEditText.getText().toString());
			}

		});
	}

	private void getMe() {

		_progressBar.setVisibility(View.VISIBLE);
		ViadeoRequest viadeoRequest = new ViadeoRequest(REQUEST_CODE_ME, "/me",
				null, Method.GET, _viadeo, this);
		new ViadeoRequester().execute(viadeoRequest);

		// updateStatus();
	}

	private void updateStatus(String status) {

		Bundle params = new Bundle();
		params.putString("message", status);
		ViadeoRequest viadeoRequest = new ViadeoRequest(
				REQUEST_CODE_UPDATE_STATUS, "/status", params, Method.POST,
				_viadeo, this);
		new ViadeoRequester().execute(viadeoRequest);

	}

	@Override
	public void onViadeoRequestComplete(int requestCode, String response) {

		_progressBar.setVisibility(View.GONE);
		_nametextView.setVisibility(View.VISIBLE);
		_headlinetextView.setVisibility(View.VISIBLE);
		_statusButton.setVisibility(View.VISIBLE);
		_statusEditText.setVisibility(View.VISIBLE);

		switch (requestCode) {

		case REQUEST_CODE_ME:

			try {

				JSONObject json = new JSONObject(response);
				_nametextView.setText(json.getString("name"));
				_headlinetextView.setText(json.getString("headline"));

			} catch (JSONException e) {
			}

			break;

		case REQUEST_CODE_UPDATE_STATUS:

			_statusEditText.setText("");
			Toast.makeText(this, getString(R.string.post_status_success), Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}

	}

	@Override
	public void onViadeoRequestError(int requestCode, final String errorMessage) {

		_progressBar.setVisibility(View.GONE);
		Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
	}
}
