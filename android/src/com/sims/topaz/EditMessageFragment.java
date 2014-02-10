package com.sims.topaz;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.sims.topaz.network.NetworkDelegate;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class EditMessageFragment extends Fragment
				implements NetworkDelegate{
	
	NetworkRestModule restModule = new NetworkRestModule(this);
	LatLng position;
	
    public void setPosition(LatLng position) {
		this.position = position;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_message, container, false);
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

}
