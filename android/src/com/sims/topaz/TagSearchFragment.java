package com.sims.topaz;

import java.util.List;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.sims.topaz.adapter.TagSuggestionAdapter;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.MessageDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;
import com.sims.topaz.utils.TagUtils;

import android.graphics.Typeface;
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
import android.widget.ListView;
import android.widget.Toast;

public class TagSearchFragment extends Fragment implements MessageDelegate, ErreurDelegate {
	
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
		Typeface face = MyTypefaceSingleton.getInstance().getTypeFace();
		text = (EditText) view.findViewById(R.id.tag_search);
		text.setTypeface(face);
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

					VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
					mNetworkModule.getPreviewsByTag(visibleRegion.farLeft, visibleRegion.nearRight, (CharSequence) text.getText());
				}
				
			}
		});
		
		ListView tagList = (ListView) view.findViewById(R.id.tag_list);
		tagList.setAdapter(new TagSuggestionAdapter(SimsContext.getContext(), R.layout.tag_suggestion_item, TagUtils.getAllTags()));
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

	@Override
	public void apiError(ApiError error) {
		NetworkRestModule.resetHttpClient();
		Toast.makeText(SimsContext.getContext(), "apiError", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void networkError() {
		Toast.makeText(SimsContext.getContext(), "networkError", Toast.LENGTH_SHORT).show();
	}
}
