package com.sims.topaz;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sims.topaz.UserCommentFragment.OnMessageClickListener;
import com.sims.topaz.AsyncTask.LoadPictureTask;
import com.sims.topaz.AsyncTask.LoadPictureTask.LoadPictureTaskInterface;
import com.sims.topaz.adapter.UserMessageAdapter;
import com.sims.topaz.adapter.UserPageAdapter;
import com.sims.topaz.interfaces.OnShowDefaultPage;
import com.sims.topaz.interfaces.OnUserFilledInListener;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.UserDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.CameraUtils;
import com.sims.topaz.utils.DebugUtils;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.SimsContext;


public class UserFragment  extends Fragment 
implements UserDelegate,ErreurDelegate, OnShowDefaultPage,
LoadPictureTaskInterface,OnMessageClickListener,ListView.OnItemClickListener, OnUserFilledInListener {

	private ViewPager mViewPager;
	private static String FRAGMENT_GENERAL_USER = "fragment_user_general";
	private static String IS_MY_OWN_PROFILE = "user_fragment_is_my_own_profile";
	private static String USER_ID = "user_fragment_user_id";
	private boolean isMyProfile;

	private User mUser = null;
	private NetworkRestModule mRestModule = new NetworkRestModule(this);
	private ListView mListMessagesListView;
	private OnMessageClickListener mListener;
	
	private UserInfoFragment userInfoFragment;
	private UserCommentFragment userCommentFragment ;
	private UserInfoGeneralFragment userInfoGeneralFragment;

	public static UserFragment newInstance(boolean isMyProfile){
		UserFragment fragment= new UserFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean(IS_MY_OWN_PROFILE, true);
		fragment.setArguments(bundle);
		return fragment;
	}

	public static UserFragment newInstance(boolean isMyProfile, long id){
		UserFragment fragment= new UserFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean(IS_MY_OWN_PROFILE, isMyProfile);
		bundle.putLong(USER_ID, id);
		fragment.setArguments(bundle);
		return fragment;
	}	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnMessageClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnMessageClickListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_user, container, false);

		boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
		if (!tabletSize) {
			mViewPager = (ViewPager) v.findViewById(R.id.pager);
		} 


		if(mUser==null){
			mUser = new User();
		}
		isMyProfile = false;
		if(getArguments() != null 
				&& getArguments().containsKey(IS_MY_OWN_PROFILE) 
				&& getArguments().getBoolean(IS_MY_OWN_PROFILE)){

			mUser.setUserName(AuthUtils.getSessionStringValue
					(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME));
			mUser.setId(AuthUtils.getSessionLongValue
					(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_ID, (long)0));
			isMyProfile = true;

		}else if(getArguments() != null 
				&& getArguments().containsKey(USER_ID)){
			mUser.setId(getArguments().getLong(USER_ID));
		}

		if(mUser.getId()!=null){
			mRestModule.getUserInfo(mUser.getId());
		}else{
			Toast.makeText(SimsContext.getContext(), getResources().getString(R.string.erreur_gen), Toast.LENGTH_SHORT).show();
		}
		createNeededFragments(v);

		return v;
	}

	@Override
	public void apiError(ApiError error) {
		DebugUtils.log("UserFragment_apiError");
		Toast.makeText(SimsContext.getContext(),
				getResources().getString(R.string.erreur_gen),
				Toast.LENGTH_SHORT).show();		
	}

	@Override
	public void networkError() {
		DebugUtils.log("UserFragment_networkError");
		Toast.makeText(SimsContext.getContext(),
				getResources().getString(R.string.erreur_gen),
				Toast.LENGTH_SHORT).show();		
	}

	@Override
	public void afterGetUserInfo(User user) {
		mUser = user;

		onUserFilledIn(mUser);
	}

	@Override
	public void afterPostUserInfo(User user) {}


	private void createNeededFragments(View view){
		//tabs
		boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
		if(isMyProfile){
			if (!tabletSize) {
				userInfoFragment = UserInfoFragment.newInstance(isMyProfile, mUser);
				userCommentFragment  =UserCommentFragment.newInstance(mUser, null);
				UserPageAdapter mTabsAdapter = 
						new UserPageAdapter(getFragmentManager(),
								userCommentFragment,
								userInfoFragment);
				mViewPager.setAdapter(mTabsAdapter);
			} else {
				userInfoFragment = UserInfoFragment.newInstance(isMyProfile, mUser);
				FragmentTransaction transaction = getFragmentManager().beginTransaction();

				transaction.replace(R.id.user_info_fragment, userInfoFragment);
				transaction.commit();	

				mListMessagesListView = (ListView)view.findViewById(R.id.fragment_user_comments__list);
			}
		}else{
			if (tabletSize) {
				View v = (View)view.findViewById(R.id.fragment_user_comments__list);
				v.setVisibility(View.GONE);
			}
			userInfoGeneralFragment = UserInfoGeneralFragment.newInstance(mUser);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.addToBackStack(FRAGMENT_GENERAL_USER);
			transaction.replace(R.id.fragment_container, userInfoGeneralFragment);
			transaction.commit();			

		}	

	}
	
	
	@Override
	public void onUserFilledIn(User user) {
		mUser = user;
		//tabs
		boolean tabletSize = getResources().getBoolean(R.bool.isTablet);

		if(isMyProfile){
			if (!tabletSize) {
				userInfoFragment.onUserFilledIn(mUser);
				userCommentFragment.onUserFilledIn(mUser);
			} else {
				userInfoFragment.onUserFilledIn(mUser);
				mListMessagesListView.setOnItemClickListener(this);
				if(mUser!=null){
					if(mUser.getPictureUrl()!=null){
						LoadPictureTask setImageTask = new LoadPictureTask(this);
						setImageTask.execute(NetworkRestModule.SERVER_IMG_BASEURL + mUser.getPictureUrl());				
					}else{
						UserMessageAdapter adapter = new UserMessageAdapter(SimsContext.getContext(),
								R.layout.fragment_comment_item,
								mUser.getMessages(),
								null);
						mListMessagesListView.setAdapter(adapter);
					}
				}
			}
		}else{
			userInfoGeneralFragment.onUserFilledIn(mUser);			
		}	


	}



	@Override
	public void onShowDefaultPage() {
		mViewPager.setCurrentItem(0);
	}

	@Override
	public void loadPictureTaskOnPostExecute(Drawable image) {

		UserMessageAdapter adapter = new UserMessageAdapter(SimsContext.getContext(),
				R.layout.fragment_comment_item,
				mUser.getMessages(),
				CameraUtils.getBytesFromDrawable(image));

		mListMessagesListView.setAdapter(adapter);
	}

	@Override
	public void onMessageClick(Message message) {
		if (null != mListener) {
			mListener.onMessageClick(message);
		}				
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (null != mListener) {
			//mListener.onMessageClick(mUser.getMessages().get(position));
		}				
	}


	




}
