package com.sims.topaz.adapter;

import java.lang.ref.WeakReference;

import com.sims.topaz.R;
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
		this.titles = new String[3];
		this.titles = mDrawerTitles;
		this.delegate = new WeakReference<Context>(mDelegate);
	}	

	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;
		
		if(view == null){
			LayoutInflater inflater = 
					(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if(position > 0){
				view = inflater.inflate(R.layout.drawer_list_item, null);
				Typeface face = MyTypefaceSingleton.getInstance().getTypeFace();
				String title = titles[position];

				if(title !=null){
					TextView titleTextView = (TextView) view.findViewById(R.id.drawer_text);
					titleTextView.setTypeface(face);
					titleTextView.setText(title);

				}else{
					view = inflater.inflate(R.layout.drawer_list_item_search_bar, null);
				}
			}else if (position == 0){ // do not remove the if from the elese (it is needed for gingerbread)
				view = inflater.inflate(R.layout.drawer_list_item_search_bar, null);
			}

			ImageView image = (ImageView)view.findViewById(R.id.drawer_icon);

			switch (position) {
			case 0:
				image.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.ic_search));
				break;
			case 1:
				image.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.ic_map));
				break;
			case 2:
				image.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.ic_settings));
				break;
			case 3:
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
