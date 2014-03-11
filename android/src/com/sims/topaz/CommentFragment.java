package com.sims.topaz;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sims.topaz.adapter.CommentAdapter;
import com.sims.topaz.interfaces.OnShowUserProfile;
import com.sims.topaz.modele.CommentItem;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.CommentDelegate;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.MessageDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.Comment;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

public class CommentFragment extends Fragment 
	implements MessageDelegate,CommentDelegate,ErreurDelegate, OnShowUserProfile{


	private EditText mNewComment;
	private ListView mListComments;
	private ImageButton mSendCommentButton;
	private ProgressBar mProgressBar;
	// The main message
	private Message mMessage=null;
	//intelligence
	private NetworkRestModule restModule = new NetworkRestModule(this);
	OnShowUserProfile mCallback;
	
	private static String ID_PREVIEW = "id_preview";
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnShowUserProfile) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnShowUserProfile");
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mCallback = null;
	}
	public static CommentFragment newInstance(long id){
		CommentFragment fragment = new CommentFragment();
		Bundle args = new Bundle();
		args.putLong(ID_PREVIEW, id);
		fragment.setArguments(args);
		return fragment;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_comment, container, false);
		//setClickable(true) prevents from touching the view behind
		v.setClickable(true);
		mNewComment = (EditText)v.findViewById(R.id.write_comment_text);
		mNewComment.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		mListComments = (ListView)v.findViewById(R.id.comment_list);
		mProgressBar = (ProgressBar)v.findViewById(R.id.progressBar);
		mProgressBar.setVisibility(View.VISIBLE);
		//Set Like, Dislike and Share Buttons
		setButtons(v);
		
		//get the main message from the preview id
		loadMessage();

		
		return v;
	}   
	
	private void displayComments() {
		List<CommentItem> lci = new ArrayList<CommentItem>();
		for (Comment co : mMessage.getComments()) {
			lci.add(new CommentItem(co));
		}
		mListComments.setAdapter(new CommentAdapter(this.getView().getContext(),
				R.layout.fragment_comment_item,
				mMessage,
				lci));


		mSendCommentButton.setEnabled(true);
		mSendCommentButton.setClickable(true);
		
	}

	//Set Like, Dislike and Share Buttons
	private void setButtons(View v) {

		mSendCommentButton = (ImageButton)v.findViewById(R.id.send_comment_button);
		mSendCommentButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {sendComment();}
		});
		mSendCommentButton.setClickable(false);
		mSendCommentButton.setEnabled(false);
		
	}
	
	protected void sendComment() {
		if(mMessage==null) return;
		String comm = mNewComment.getText().toString();
		mNewComment.setEnabled(false);
		mSendCommentButton.setEnabled(false);
		Comment comment = new Comment();
		comment.setText(comm);
		comment.setUserName(AuthUtils.getSessionStringValue
				(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME));
		restModule.postComment(comment, mMessage);
		
	}

	private void loadMessage() {
		//get the main message from the preview id
		if(getArguments()!=null && getArguments().containsKey("id_preview")){
			Long id = getArguments().getLong("id_preview");
			restModule.getMessage(id);
		}		
	}


//	public void onDoneButton(){
//		getFragmentManager().beginTransaction().remove(this).commit();
//
//	}
	
	@Override
	public void afterPostMessage(Message message) {}

	@Override
	public void afterGetMessage(Message message) {
		if(message!=null) {
			mMessage = message;
			displayComments();
			mProgressBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void afterGetPreviews(List<Preview> list) {}

	@Override
	public void networkError() {
		Toast.makeText(SimsContext.getContext(),SimsContext.getString(R.string.network_error),
				Toast.LENGTH_SHORT).show();	
		mNewComment.setEnabled(true);
		mSendCommentButton.setEnabled(true);
	}

	@Override
	public void apiError(ApiError error) {
		Toast.makeText(SimsContext.getContext(),SimsContext.getString(R.string.network_error),
				Toast.LENGTH_SHORT).show();			
	}
	public void clearMessage(){
		mNewComment.setText("");
	}

	@Override
	public void afterPostComment(Comment comment) {
		CommentItem ci = new CommentItem(comment);
		CommentAdapter ca = (CommentAdapter) mListComments.getAdapter();
		ca.addItem(ci);
		mNewComment.setText("");
		mNewComment.setEnabled(true);
		mSendCommentButton.setEnabled(true);
	}

	@Override
	public void onShowUserProfileFragment(long id) {
		mCallback.onShowUserProfileFragment(id);
		
	}

}
