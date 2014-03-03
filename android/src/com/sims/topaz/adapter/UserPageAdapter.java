package com.sims.topaz.adapter;

import com.sims.topaz.UserCommentFragment;
import com.sims.topaz.UserInfoFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class UserPageAdapter extends FragmentStatePagerAdapter {
	public UserPageAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment;
		if(i==1){
			fragment = new UserCommentFragment();
		}else{
			fragment = new UserInfoFragment();
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		String name;
		if(position==1){
			name = "Comments TEMP";
		}else{
			name = "User Info TEMP";
		}
		return name;
	}
}