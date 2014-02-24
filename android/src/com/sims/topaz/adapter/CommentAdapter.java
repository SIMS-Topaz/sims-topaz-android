package com.sims.topaz.adapter;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sims.topaz.R;
import com.sims.topaz.modele.CommentItem;
import com.sims.topaz.utils.MyTypefaceSingleton;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommentAdapter extends ArrayAdapter<CommentItem>  {
	private WeakReference<Context> delegate;
	
	private int count = 0;
	public CommentAdapter(Context mDelegate, int resource, List<CommentItem> commentsList) {
		super(mDelegate, resource, commentsList);
		delegate = new WeakReference<Context>(mDelegate);
		count = commentsList.size();
	}	
	
	public void addItem(CommentItem ci) {
		add(ci);
		count++;
		//notifyDataSetChanged();
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
			view = inflater.inflate(R.layout.fragment_comment_item, null);
		}
		if(position >= 0){
			holder.mUserName = (TextView) view.findViewById(R.id.comment_item_person_name);
			holder.mUserName.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
			
			holder.mUserComment = (TextView) view.findViewById(R.id.comment_item_text);
			holder.mUserComment.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
			
			holder.mCommentDate = (TextView) view.findViewById(R.id.comment_item_time);
			holder.mCommentDate.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
			if(getItem(position)!=null){
				CommentItem ci = getItem(position);
				holder.mUserName.setText(ci.getUser());
				holder.mUserComment.setText(ci.getCommentText());
				holder.mCommentDate.setText(String.valueOf(ci.getDate()));						
			}
		}
		return view;
	}
	
	class ViewHolder {  
		 TextView mUserName;
		 TextView mUserComment;
		 TextView mCommentDate;
    }  
}
