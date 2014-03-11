package com.sims.topaz.adapter;

import java.lang.ref.WeakReference;
import java.util.List;

import com.sims.topaz.DrawerActivity;
import com.sims.topaz.R;
import com.sims.topaz.TagSearchFragment;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TagSuggestionAdapter extends ArrayAdapter<String> {
	
	private List<String> list;
	private DrawerActivity activity;
	private WeakReference<Context> delegate;
	
	public TagSuggestionAdapter(Context context, int resource,
			List<String> objects) {
		super(context, resource, objects);
		this.activity = (DrawerActivity) context;
		this.list = objects;
		this.delegate = new WeakReference<Context>(context);
	}
	
	@Override
	public int getCount() {
		return list.size();
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		LayoutInflater inflater = 
				(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Typeface face = MyTypefaceSingleton.getInstance().getTypeFace();
		view = inflater.inflate(R.layout.tag_suggestion_item, null);
		TextView textView = (TextView) view.findViewById(R.id.tag_suggestion);
		textView.setTypeface(face);
		if (list.get(position) != null) {
			int resId = activity.getResources().getIdentifier(list.get(position), "string",  activity.getPackageName());
			textView.setText("#" + SimsContext.getString(resId));
		}
		textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (activity.getLastFragment() instanceof TagSearchFragment) {
					TagSearchFragment fg = (TagSearchFragment) activity.getLastFragment();
					CharSequence seq = ((TextView) v).getText();
					fg.executeSearch(seq);
					fg.getEditText().setText(seq);
				}
			}
		});
		ImageView iv = (ImageView) view.findViewById(R.id.tag_icon);
		switch (position) {
		case 0:
			iv.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_restaurant));
			break;
		case 1:
			iv.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_party));
			break;
		case 2:
			iv.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_pizza));
			break;
		case 3:
			iv.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_shopping));
			break;
		case 4:
			iv.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_school));
			break;
		case 5:
			iv.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_music));
			break;
		case 6:
			iv.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_hospital));
			break;
		case 7:
			iv.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_sun));
			break;
		case 8:
			iv.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_car));
			break;
		case 9:
			iv.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_cycle));
			break;
		default:
			iv.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.ic_search));
			break;
		}
		return view;
	}

}
