package com.sims.topaz;

import java.util.List;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.MessageDelegate;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.utils.SimsContext;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class TagSearchFragment extends Fragment implements MessageDelegate {
	
	private NetworkRestModule mNetworkModule;
	private GoogleMap mMap;
	private EditText text;
	
	public TagSearchFragment(GoogleMap map) {
		super();
		mMap = map;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNetworkModule = new NetworkRestModule(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tag_search, container, false);
		text = (EditText) view.findViewById(R.id.tag_search);
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
				//Toast.makeText(SimsContext.getContext(), s, Toast.LENGTH_SHORT).show();
			}
		});
		ImageView image = (ImageView) view.findViewById(R.id.tag_search_icon);
		image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( mMap != null && mNetworkModule != null ){
					Toast.makeText(SimsContext.getContext(), text.getText(), Toast.LENGTH_SHORT).show();
					VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
					mNetworkModule.getPreviewsByTag(visibleRegion.farLeft, visibleRegion.nearRight, (CharSequence) text.getText());
				}
				
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
		Fragment f = PreviewListFragment.newInstance(list);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.drawable.animation_bottom_up,
				R.drawable.animation_bottom_down);
		transaction.replace(R.id.preview_list_tag, f);
		transaction.addToBackStack(null);
		transaction.commit();
	}
}
