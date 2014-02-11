package com.sims.topaz;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
public class CommentFragment extends Fragment  {
	private TextView mFirstComment;

    public interface OnSetCommets{
        public void onSetFirstComment(String text);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	View v = inflater.inflate(R.layout.fragment_comment, container, false);
    	mFirstComment = (TextView) v.findViewById(R.id.comment_first_comment_text);
        return v;
    }
    
}
