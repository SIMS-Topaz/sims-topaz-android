package com.sims.topaz;


import com.sims.topaz.adapter.UserMessageAdapter;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.SimsContext;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class UserCommentFragment  extends Fragment  {
	private ListView mListMessagesListView;
	private User mUser;
	private byte[] mImage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_user_comments, container, false);
		if(getArguments()!=null){
			if(mUser == null){
				mUser = new User();
			}
			mUser = (User) getArguments().getSerializable("user");
			mImage = (byte[]) getArguments().getByteArray("image");
		}
		
		mListMessagesListView = (ListView)v.findViewById(R.id.fragment_user_comments__list);
		mListMessagesListView.setAdapter(new UserMessageAdapter(SimsContext.getContext(),
				R.layout.fragment_comment_item,
				mUser.getMessages(),
				mImage));
		return v;
    }
}
