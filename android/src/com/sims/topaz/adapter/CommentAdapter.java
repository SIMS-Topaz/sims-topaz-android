package com.sims.topaz.adapter;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
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
	
	private List<CommentItem> comments;
	public CommentAdapter(Context mDelegate, int resource, List<CommentItem> commentsList) {
		super(mDelegate, resource, (CommentItem [])commentsList.toArray());
		delegate = new WeakReference<Context>(mDelegate);
		comments = new ArrayList<CommentItem>(commentsList);
		
	}	
	
	public void addItem(CommentItem ci) {
		super.add(ci);
		comments.add(ci);
	}


	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;
		ViewHolder holder = null; 
		if(view == null){
			holder=new ViewHolder(); 
			LayoutInflater inflater = 
					(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.fragment_comment_item, null);
			if(position >= 0){
				holder.mUserName = (TextView) view.findViewById(R.id.comment_item_person_name);
				holder.mUserName.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
				
				holder.mUserComment = (TextView) view.findViewById(R.id.comment_item_text);
				holder.mUserComment.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
				
				holder.mCommentDate = (TextView) view.findViewById(R.id.comment_item_time);
				holder.mCommentDate.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
				if(comments.get(position)!=null){
					holder.mUserName.setText(comments.get(position).getUser());
					holder.mUserComment.setText(comments.get(position).getCommentText());
					holder.mCommentDate.setText(String.valueOf(comments.get(position).getDate()));						
				}
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
