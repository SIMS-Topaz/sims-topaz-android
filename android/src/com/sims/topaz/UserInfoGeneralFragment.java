package com.sims.topaz;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sims.topaz.AsyncTask.LoadPictureTask;
import com.sims.topaz.AsyncTask.LoadPictureTask.LoadPictureTaskInterface;
import com.sims.topaz.adapter.UserMessageAdapter;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.ListViewSizeHelper;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

public class UserInfoGeneralFragment extends Fragment implements  ErreurDelegate, LoadPictureTaskInterface{
	private static String USER = "user_info_general_fragment_user";
	private User mUser;
	private TextView mUserTitleTextView;
	private TextView mUserSnippetTextView;
	private ImageView mUserImage;
	private ProgressBar mProgressBar;
	private ListView mListMessagesListView;
	private byte[] mImage;
	
	public static UserInfoGeneralFragment newInstance(User mUser){
		UserInfoGeneralFragment fragment= new UserInfoGeneralFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(USER, mUser);
		fragment.setArguments(bundle);
		return fragment;
	}

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(getArguments()!=null){
			mUser = (User) getArguments().getSerializable(USER);
		}		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Typeface face = MyTypefaceSingleton.getInstance().getTypeFace();
		View v = inflater.inflate(R.layout.fragment_user_info, container, false);
		//username field
		mUserTitleTextView = (TextView)v.findViewById(R.id.username);
		mUserTitleTextView.setTypeface(face);
		//message fiels
		mUserSnippetTextView= (TextView)v.findViewById(R.id.username_snippet);
		mUserSnippetTextView.setTypeface(face);
		//user imgae
		mUserImage = (ImageView)v.findViewById(R.id.username_image);
		//Progress bar
		mProgressBar = (ProgressBar)v.findViewById(R.id.progressBar);
		mProgressBar.setVisibility(View.VISIBLE);
		//listview
		mListMessagesListView = (ListView)v.findViewById(R.id.fragment_user_comments__list);
		if(mUser!=null){
			if(mUser.getUserName()!=null)
				mUserTitleTextView.setText(mUser.getUserName());
			if(mUser.getStatus()!=null){
				mUserSnippetTextView.setText(mUser.getStatus());
			}
			if(mUser.getPictureUrl()!=null && !mUser.getPictureUrl().isEmpty()){
				LoadPictureTask setImageTask = new LoadPictureTask(this);
				setImageTask.execute(NetworkRestModule.SERVER_IMG_BASEURL + mUser.getPictureUrl());
				Toast.makeText(SimsContext.getContext(), 
						NetworkRestModule.SERVER_IMG_BASEURL+mUser.getPictureUrl(),
						Toast.LENGTH_SHORT).show();
			}
		}
		
		if(mUser!=null && mUser.getMessages()!=null){
			UserMessageAdapter adapter = new UserMessageAdapter(SimsContext.getContext(),
					R.layout.fragment_comment_item,
					mUser.getMessages(),
					mImage);
			mListMessagesListView.setAdapter(adapter);
			ListViewSizeHelper.getListViewSize(mListMessagesListView);
		}

		return v;
	}

	@Override
	public void loadPictureTaskOnPostExecute(Drawable image) {
		mUserImage.setImageDrawable(image);
		mProgressBar.setVisibility(View.GONE);
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
}

