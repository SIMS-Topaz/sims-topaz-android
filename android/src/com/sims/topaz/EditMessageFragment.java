package com.sims.topaz;

import java.util.Date;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.MessageDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.MyTypefaceSingleton;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditMessageFragment extends Fragment
implements MessageDelegate,ErreurDelegate{

	private NetworkRestModule mRestModule = new NetworkRestModule(this);
	private LatLng mPosition;
	private EditText editText;

	OnNewMessageListener mCallback;
	// Container Activity must implement this interface
	public interface OnNewMessageListener {
		public void onNewMessage(Message message);
	}

	private int savedSoftInputMode;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnNewMessageListener) activity;
			savedSoftInputMode = activity.getWindow().getAttributes().softInputMode;
			activity.getWindow()
			.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnNewMessageListener");
		}
	}

	/**
	 * Set the callback to null so we don't accidentally leak the 
	 * Activity instance.
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		mCallback = null;
		getActivity().getWindow()
		.setSoftInputMode(savedSoftInputMode);
	}
	public void setPosition(LatLng position) {
		this.mPosition = position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_edit_message, container, false);
		TextView mTextTextView = (TextView) view.findViewById(R.id.edit_message_text);
		mTextTextView.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		editText = (EditText) view.findViewById(R.id.editMessage);
		editText.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		setUpButtons(view);
		return view;
	}    

	private void setUpButtons(View view) {
		Button send = (Button) view.findViewById(R.id.button_send_message);
		send.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setEnabled(false);
				closeKeyboard();
				onSendButton(v);
			}
		});
		Button cancel = (Button) view.findViewById(R.id.button_cancel_message);
		cancel.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeKeyboard();
				getFragmentManager().popBackStack();
			}
		});
	}

	protected void closeKeyboard() {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	public void onSendButton(View view) {
		String text = editText.getText().toString();
		// Create the message
		Message message = new Message();
		message.setText(text);
		message.setLongitude(mPosition.longitude);
		message.setLatitude(mPosition.latitude);
		message.setTimestamp(new Date().getTime());
		message.setOwner(AuthUtils.getSessionStringValue
				(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME));
		mRestModule.postMessage(message);
	}

	@Override
	public void afterPostMessage(Message message) {
		Toast.makeText(getActivity(), getString(R.string.message_sent), Toast.LENGTH_SHORT).show();
		getFragmentManager().beginTransaction().hide(this).commit();
		mCallback.onNewMessage(message);
		getFragmentManager().popBackStack();
	}

	@Override
	public void afterGetMessage(Message message) {}

	@Override
	public void afterGetPreviews(List<Preview> list) {}

	@Override
	public void networkError() {
		getView().findViewById(R.id.button_send_message).setEnabled(true);
	}

	public void apiError(ApiError error) {}


}
