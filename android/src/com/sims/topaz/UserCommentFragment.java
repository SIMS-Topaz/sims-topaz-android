package com.sims.topaz;

import com.sims.topaz.adapter.UserMessageAdapter;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.SimsContext;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ListView;

public class UserCommentFragment  extends Fragment  {
	private ListView mListMessagesListView;
	private User mUser;
	private byte[] mImage;
	private static String USER = "user_comment_fragment_user";
	private static String PICTURE = "user_comment_fragment_picture";
	
	public static UserCommentFragment newInstance(User user, byte[] image){
		UserCommentFragment fragment= new UserCommentFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(USER, user);
		bundle.putByteArray(PICTURE, image);
		fragment.setArguments(bundle);
		return fragment;		
	}
	
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_user_comments, container, false);
		if(getArguments()!=null){
			if(mUser == null){
				mUser = new User();
			}
			mUser = (User) getArguments().getSerializable(USER);
			mImage = (byte[]) getArguments().getByteArray(PICTURE);
		}
		

		
		mListMessagesListView = (ListView)v.findViewById(R.id.fragment_user_comments__list);
		if(mUser!=null && mUser.getMessages()!=null){
			UserMessageAdapter adapter = new UserMessageAdapter(SimsContext.getContext(),
					R.layout.fragment_comment_item,
					mUser.getMessages(),
					mImage);
			mListMessagesListView.setAdapter(adapter);	
			mListMessagesListView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(!event.equals(MotionEvent.ACTION_SCROLL)){
						if(getParentFragment()!=null){
							((UserFragment)getParentFragment()).onShowDefaultPage();
						}
						return false;
					}else{
						v.onTouchEvent(event);
						return true;
					}
					
				}
			});
		}
		
		
		return v;
    }
    
    
}
