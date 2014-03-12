package com.sims.topaz;

import com.sims.topaz.adapter.UserMessageAdapter;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class UserCommentFragment  extends Fragment  {
	private ListView mListMessagesListView;
	private User mUser;
	private byte[] mImage;
	private static String USER = "user_comment_fragment_user";

	public static UserCommentFragment newInstance(User user, byte[] image){
		UserCommentFragment fragment= new UserCommentFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(USER, user);
		fragment.setArguments(bundle);
		return fragment;		
	}


	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
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
		}



		mListMessagesListView = (ListView)v.findViewById(R.id.fragment_user_comments__list);
		if(mUser!=null && mUser.getMessages()!=null){
			UserMessageAdapter adapter = new UserMessageAdapter(SimsContext.getContext(),
					R.layout.fragment_comment_item,
					mUser.getMessages(),
					mImage);
			
			if(mUser.getMessages().size() == 0){
				Typeface face = MyTypefaceSingleton.getInstance().getTypeFace();
				View v2 = inflater.inflate(R.layout.header_list_messages, null); 
				TextView text = (TextView)v2.findViewById(R.id.header_list_text);
				if(AuthUtils.getSessionLongValue
						(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_ID, (long)0) != mUser.getId()){
					text.setText(getResources().getString(R.string.user_tab_no_messages));
					
				}else{
					text.setText(getResources().getString(R.string.user_tab_no_messages_me));
				}
				text.setTypeface(face);
				mListMessagesListView.addHeaderView(v2);
			}
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
