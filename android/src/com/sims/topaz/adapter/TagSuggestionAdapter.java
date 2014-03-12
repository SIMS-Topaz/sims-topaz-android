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
	
	
	private class ViewHolder {
		TextView mTextView;
		ImageView mImageView;
		
	}
	@Override
	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder = null;
		if(view==null) {
			holder = new ViewHolder();
			LayoutInflater inflater = 
					(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			Typeface face = MyTypefaceSingleton.getInstance().getTypeFace();
			view = inflater.inflate(R.layout.tag_suggestion_item, null);
			holder.mImageView = (ImageView) view.findViewById(R.id.tag_icon);
			holder.mTextView = (TextView) view.findViewById(R.id.tag_suggestion);
			holder.mTextView.setTypeface(face);
			holder.mTextView.setOnClickListener(new OnClickListener() {
				
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
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		if (list.get(position) != null) {
			int resId = activity.getResources().getIdentifier(list.get(position), "string",  activity.getPackageName());
			holder.mTextView.setText("#" + SimsContext.getString(resId));
		}
		
		
		switch (position) {
		case 0:
			holder.mImageView.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_restaurant));
			break;
		case 1:
			holder.mImageView.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_party));
			break;
		case 2:
			holder.mImageView.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_pizza));
			break;
		case 3:
			holder.mImageView.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_shopping));
			break;
		case 4:
			holder.mImageView.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_school));
			break;
		case 5:
			holder.mImageView.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_music));
			break;
		case 6:
			holder.mImageView.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_hospital));
			break;
		case 7:
			holder.mImageView.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_sun));
			break;
		case 8:
			holder.mImageView.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_car));
			break;
		case 9:
			holder.mImageView.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.tag_cycle));
			break;
		default:
			holder.mImageView.setBackgroundDrawable(delegate.get().getResources().getDrawable(R.drawable.ic_search));
			break;
		}
		return view;
	}

}
