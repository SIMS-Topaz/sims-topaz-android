package com.sims.topaz.adapter;


import java.lang.ref.WeakReference;

import com.sims.topaz.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class CommentAdapter extends ArrayAdapter<String>  {
	private WeakReference<Context> delegate;
	public CommentAdapter(Context mDelegate, int resource, String[] mDrawerTitles) {
		super(mDelegate, resource, mDrawerTitles);
		delegate = new WeakReference<Context>(mDelegate);
	}	


	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;
		if(view == null){
			LayoutInflater inflater = 
					(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.fragment_comment_item, null);
		}
		return parent;
	}
}
