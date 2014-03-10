package com.sims.topaz;

import java.util.List;

import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.MessageDelegate;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.utils.SimsContext;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class TagSearchFragment extends Fragment implements MessageDelegate {
	
	private NetworkRestModule mNetworkModule;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNetworkModule = new NetworkRestModule(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_tag_search, container, false);
		EditText text = (EditText) view.findViewById(R.id.tag_search);
		text.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				Toast.makeText(SimsContext.getContext(), s, Toast.LENGTH_SHORT).show();
			}
		});
		return view;
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
}
