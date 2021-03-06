package com.sims.topaz.adapter;

import java.lang.ref.WeakReference;

import com.sims.topaz.R;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.MyTypefaceSingleton;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerAdapter extends ArrayAdapter<String> {
	private String[] titles;
	private WeakReference<Context> delegate;
	public DrawerAdapter(Context mDelegate, int resource, String[] mDrawerTitles) {
		super(mDelegate, resource, mDrawerTitles);
		this.titles = new String[5];
		this.titles = mDrawerTitles;
		this.delegate = new WeakReference<Context>(mDelegate);
	}	

	@Override
	public int getCount() {
		return titles.length;
	}
	
	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;
		LayoutInflater inflater = 
				(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Typeface face = MyTypefaceSingleton.getInstance().getTypeFace();
		if(position > 1){
			view = inflater.inflate(R.layout.drawer_list_item, null);
			String title = titles[position];
			TextView titleTextView = (TextView) view.findViewById(R.id.drawer_text);
			titleTextView.setTypeface(face);
			titleTextView.setText(title);		
		}else if (position == 0){ 
			view = inflater.inflate(R.layout.drawer_list_auth_item, null);
			TextView user = (TextView)view.findViewById(R.id.username);
			user.setTypeface(face);
			TextView profile = (TextView)view.findViewById(R.id.auth_see_profile);
			profile.setTypeface(face);
			if(AuthUtils.sessionHasKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME)){
				user.setText(AuthUtils.getSessionStringValue(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME));
			}
		}else if(position == 1){
			view = inflater.inflate(R.layout.drawer_list_item_search_bar, null);
			TextView search = (TextView) view.findViewById(R.id.drawer_search);
			search.setTypeface(face);
			search.setText(R.string.drawer_search_hint);
		}

		if(position != 0){
		ImageView image = (ImageView)view.findViewById(R.id.drawer_icon);

		switch (position) {
			case 1:
				image.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.ic_search));
				break;
			case 2:
				image.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.ic_map));
				break;
			case 3:
				image.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.ic_settings));
				break;
			case 4:
				image.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.ic_about));
				break;
			default:
				image.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.ic_search));
				break;
			}
		}
		
	return view;
}
}
