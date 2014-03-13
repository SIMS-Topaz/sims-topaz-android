package com.sims.topaz;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

public class TagSearchFragment extends Fragment implements MessageDelegate,ErreurDelegate {

	
	private NetworkRestModule mNetworkModule;
	private EditText text;
	private static String KEY_VISIBLE_REGION_LEFT_LAT = "key_visible_region_left_lat";
	private static String KEY_VISIBLE_REGION_RIGHT_LAT = "key_visible_region_right_lat";
	private static String KEY_VISIBLE_REGION_LEFT_LNG = "key_visible_region_left_lng";
	private static String KEY_VISIBLE_REGION_RIGHT_LNG = "key_visible_region_right_lng";
	private LatLng mFarLeft;
	private LatLng mNearRight;
	
	public static TagSearchFragment newInstance(GoogleMap mMap){
		VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
		TagSearchFragment fragment = new TagSearchFragment();

		Bundle args = new Bundle();
		args.putDouble(KEY_VISIBLE_REGION_LEFT_LAT, visibleRegion.farLeft.latitude);
		args.putDouble(KEY_VISIBLE_REGION_RIGHT_LAT, visibleRegion.nearRight.latitude);
		args.putDouble(KEY_VISIBLE_REGION_LEFT_LNG, visibleRegion.farLeft.longitude);
		args.putDouble(KEY_VISIBLE_REGION_RIGHT_LNG, visibleRegion.nearRight.longitude);
		fragment.setArguments(args);

		return fragment;
	}
	
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public TagSearchFragment() {}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNetworkModule = new NetworkRestModule(this);
		if(getArguments()!=null){
			mFarLeft = new LatLng(getArguments().getDouble(KEY_VISIBLE_REGION_LEFT_LAT), 
					getArguments().getDouble(KEY_VISIBLE_REGION_LEFT_LNG));
			mNearRight = new LatLng(getArguments().getDouble(KEY_VISIBLE_REGION_RIGHT_LAT), 
					getArguments().getDouble(KEY_VISIBLE_REGION_RIGHT_LNG));
		}
		setRetainInstance(true);
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

				
				executeSearch(text.getText());
			}
		});
		
		ListView tagList = (ListView) view.findViewById(R.id.tag_list);
		tagList.setAdapter(new TagSuggestionAdapter(getActivity(), R.layout.tag_suggestion_item, TagUtils.getAllTags()));
		return view;
	}
	
	public void executeSearch(CharSequence tx) {

		if( mFarLeft != null &&mNearRight !=null && mNetworkModule != null ){	
					Toast.makeText(SimsContext.getContext(), tx, Toast.LENGTH_SHORT).show();
					try {
						mNetworkModule.getPreviewsByTag(mFarLeft, mNearRight, 
								URLEncoder.encode(tx.toString().replaceAll("#", ""), "utf8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

		}
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

		Toast.makeText(SimsContext.getContext(),
				getResources().getString(R.string.erreur_gen),
				Toast.LENGTH_SHORT).show();		

	}

	@Override
	public void networkError() {
		Toast.makeText(SimsContext.getContext(),
				getResources().getString(R.string.erreur_gen),
				Toast.LENGTH_SHORT).show();
	}
	
	public EditText getEditText() {
		return text;
	}
}
