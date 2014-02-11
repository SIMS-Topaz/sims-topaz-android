package com.sims.topaz;

import java.util.Date;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.sims.topaz.network.NetworkDelegate;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter.LengthFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditMessageFragment extends Fragment
				implements NetworkDelegate{
	
	NetworkRestModule restModule = new NetworkRestModule(this);
	LatLng position;
	
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
	
    public void setPosition(LatLng position) {
		this.position = position;
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
    	message.setLongitude(position.longitude);
    	message.setLatitude(position.latitude);
    	message.setTimestamp(new Date().getTime());
    	
    	restModule.postMessage(message);
    	
    	
    }

	@Override
	public void afterPostMessage(Message message) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), getString(R.string.message_sent), Toast.LENGTH_SHORT).show();
		new AsyncNewMessage().execute(mCallback, message);
		getFragmentManager().popBackStack();
	}

	@Override
	public void afterGetMessage(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterGetPreviews(List<Preview> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void networkError() {
		// TODO Auto-generated method stub
		
	}
	
	private class AsyncNewMessage extends AsyncTask<Object, Integer, Void> {

		@Override
		protected Void doInBackground(Object... params) {
			OnNewMessageListener callback = (OnNewMessageListener) params[0];
			Message message = (Message) params[1];
			callback.onNewMessage(message);
			return null;
		}
		
	}

}
