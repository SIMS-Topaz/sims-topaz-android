package com.sims.topaz;

import java.util.Date;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.MessageDelegate;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditMessageFragment extends Fragment
implements MessageDelegate{

	private NetworkRestModule mRestModule = new NetworkRestModule(this);
	private LatLng mPosition;

	OnNewMessageListener mCallback;
	// Container Activity must implement this interface
	public interface OnNewMessageListener {
		public void onNewMessage(Message message);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnNewMessageListener) activity;
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
	}
	public void setPosition(LatLng position) {
		this.mPosition = position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_edit_message, container, false);
	
		setUpButtons(view);
		return view;
	}    

	private void setUpButtons(View view) {
		Button send = (Button) view.findViewById(R.id.button_send_message);
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSendButton(v);
			}
		});
		Button cancel = (Button) view.findViewById(R.id.button_cancel_message);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStack();
			}
		});
	}

	public void onSendButton(View view) {
		EditText editText = (EditText) getActivity().findViewById(R.id.editMessage);
		String text = editText.getText().toString();
		// Create the message
		Message message = new Message();

		message.setText(text);
		message.setLongitude(mPosition.longitude);
		message.setLatitude(mPosition.latitude);
		message.setTimestamp(new Date().getTime());

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


}
