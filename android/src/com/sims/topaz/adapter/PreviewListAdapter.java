package com.sims.topaz.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sims.topaz.R;
import com.sims.topaz.network.modele.Preview;

public class PreviewListAdapter extends ArrayAdapter<Preview> {

	private int count = 0;
	public PreviewListAdapter(Context context, int resource,
			List<Preview> objects) {
		super(context, resource, objects);
		count = objects.size();
	}
	
	@Override
	public int getCount() {
		return count;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;
		ViewHolder holder = null; 
		holder=new ViewHolder(); 
		if(view == null){
			LayoutInflater inflater = 
					(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.adapter_preview_item, null);
		}
		if(position >= 0){
//			holder.mUserName = (TextView) view.findViewById(R.id.comment_item_person_name);
//			holder.mUserName.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
//			
//			holder.mUserComment = (TextView) view.findViewById(R.id.comment_item_text);
//			holder.mUserComment.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
//			
//			holder.mCommentDate = (TextView) view.findViewById(R.id.comment_item_time);
//			holder.mCommentDate.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
			if(getItem(position)!=null){
				Preview p = getItem(position);
//				holder.mUserName.setText(ci.getUser());
//				holder.mUserComment.setText(ci.getCommentText());
//				holder.mCommentDate.setText(DateFormat.format
//						(getContext().getString(R.string.date_format), 
//								new Date( ci.getDate()) ) );
			}
		}
		return view;
	}
	
	class ViewHolder {  
		
    }  

}
