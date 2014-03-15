package com.sims.topaz.adapter;

import java.lang.ref.WeakReference;
import java.util.List;

import com.sims.topaz.DrawerActivity;
import com.sims.topaz.R;
import com.sims.topaz.TagSearchFragment;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;
import com.sims.topaz.utils.TagUtils;

import android.annotation.SuppressLint;
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
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
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
			view.setTag(holder);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (activity.getLastFragment() instanceof TagSearchFragment) {
						TagSearchFragment fg = (TagSearchFragment) activity.getLastFragment();
						CharSequence seq = ((ViewHolder) v.getTag()).mTextView.getText();
						fg.executeSearch(seq);
						fg.getEditText().setText(seq);
					}
				}
			});
			
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		if (list.get(position) != null) {
			int resId = activity.getResources().getIdentifier(
					//remove the '#' caracter
					list.get(position).replace("#", ""), "string",  activity.getPackageName());
			holder.mTextView.setText("#" + SimsContext.getString(resId));
			
			int drawableId = TagUtils.getDrawableForTag(list.get(position));
			
			if(android.os.Build.VERSION.SDK_INT >= 16) {
				holder.mImageView.setBackground(delegate.get().getResources().getDrawable(drawableId));
			} else {
				holder.mImageView.setBackgroundDrawable(delegate.get().getResources().getDrawable(drawableId));
			}
		}
		

		return view;
	}
	

}
