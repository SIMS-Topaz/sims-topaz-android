package com.sims.topaz.adapter;

import java.util.List;

import com.sims.topaz.R;
import com.sims.topaz.utils.MyTypefaceSingleton;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TagSuggestionAdapter extends ArrayAdapter<String> {
	
	private List<String> list;
	
	public TagSuggestionAdapter(Context context, int resource,
			List<String> objects) {
		super(context, resource, objects);
		this.list = objects;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		LayoutInflater inflater = 
				(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Typeface face = MyTypefaceSingleton.getInstance().getTypeFace();
		view = inflater.inflate(R.layout.tag_suggestion_item, null);
		TextView textView = (TextView) view.findViewById(R.id.tag_suggestion);
		textView.setTypeface(face);
		if (list.get(position) != null) {
			textView.setText("#" + list.get(position));
		}
		return view;
	}

}
