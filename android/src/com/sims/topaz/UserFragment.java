package com.sims.topaz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sims.topaz.adapter.UserPageAdapter;
import com.sims.topaz.interfaces.OnShowDefaultPage;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.UserDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.DebugUtils;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.SimsContext;


public class UserFragment  extends Fragment 
implements UserDelegate,ErreurDelegate, OnShowDefaultPage {

	private ViewPager mViewPager;
	private static String IS_MY_OWN_PROFILE = "user_fragment_is_my_own_profile";
	private static String USER_ID = "user_fragment_user_id";
	private boolean isMyProfile;
	
	private User mUser = null;


	private NetworkRestModule mRestModule = new NetworkRestModule(this);
	private UserInfoFragment userInfoFragment;
	private UserCommentFragment userCommentFragment;
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
		// Retain this fragment across configuration changes.
		setRetainInstance(true);
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
		Toast.makeText(SimsContext.getContext(),
				"afterGetUserInfo",Toast.LENGTH_SHORT).show();
		prepareFragments();
	}
	
	@Override
	public void afterPostUserInfo(User user) {}

	private void prepareFragments(){
		userInfoFragment = UserInfoFragment.newInstance(isMyProfile, mUser);
		userCommentFragment =UserCommentFragment.newInstance(mUser, null);
		userInfoGeneralFragment = UserInfoGeneralFragment.newInstance(mUser);
		//tabs
		boolean tabletSize = getResources().getBoolean(R.bool.isTablet);

		if (!tabletSize) {
			if(isMyProfile){
				UserPageAdapter mTabsAdapter = 
						new UserPageAdapter(getActivity().getSupportFragmentManager(),
								userCommentFragment,
								userInfoFragment);
				mViewPager.setAdapter(mTabsAdapter);
			}else{
				mViewPager.setVisibility(View.GONE);
				//conteneur
				FragmentTransaction transaction = getActivity().getSupportFragmentManager()
						.beginTransaction();
				transaction.replace(R.id.conteneur, userInfoGeneralFragment);
			}

		} else {
			FragmentTransaction transaction = getActivity().getSupportFragmentManager()
					.beginTransaction();

			transaction.replace(R.id.user_info_fragment, userInfoFragment);
			transaction.replace(R.id.user_info_comments_fragment, userCommentFragment);
			transaction.commit();	
			
		}	
	}



	@Override
	public void onShowDefaultPage() {
		mViewPager.setCurrentItem(0);
	}



}
