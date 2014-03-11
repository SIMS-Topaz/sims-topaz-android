package com.sims.topaz;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sims.topaz.AsyncTask.LoadPictureTask;
import com.sims.topaz.AsyncTask.LoadPictureTask.LoadPictureTaskInterface;
import com.sims.topaz.adapter.CommentAdapter;
import com.sims.topaz.interfaces.OnShowUserProfile;
import com.sims.topaz.modele.CommentItem;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.CommentDelegate;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.LikeStatusDelegate;
import com.sims.topaz.network.interfaces.MessageDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.Comment;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.DebugUtils;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

public class CommentFragment extends Fragment 
	implements MessageDelegate,LikeStatusDelegate,CommentDelegate,
	ErreurDelegate, LoadPictureTaskInterface, OnShowUserProfile{

	private TextView mFirstComment;
	private TextView mFirstCommentNameUser;
	private TextView mFirstCommentTimestamp;
	private ImageView mFirstCommentPicture;
	private EditText mNewComment;
	private ListView mListComments;
	private ImageButton mShareButton;
	private ImageButton mLikeButton;
	private ImageButton mDislikeButton;
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
		mFirstComment = (TextView) v.findViewById(R.id.comment_first_comment_text);
		mFirstComment.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		mFirstCommentNameUser = (TextView) v.findViewById(R.id.comment_person_name);
		mFirstCommentNameUser.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		mFirstCommentTimestamp = (TextView) v.findViewById(R.id.comment_time);
		mFirstCommentTimestamp.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		mFirstCommentPicture = (ImageView) v.findViewById(R.id.comment_first_picture_view);
		mFirstCommentPicture.setVisibility(View.GONE);
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
				lci));

		mSendCommentButton.setEnabled(true);
		mSendCommentButton.setClickable(true);
		
	}

	//Set Like, Dislike and Share Buttons
	private void setButtons(View v) {
		mShareButton = (ImageButton)v.findViewById(R.id.comment_share);
		mLikeButton = (ImageButton)v.findViewById(R.id.comment_like);
		mDislikeButton = (ImageButton)v.findViewById(R.id.comment_dislike);
		mSendCommentButton = (ImageButton)v.findViewById(R.id.send_comment_button);
		
		mShareButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {shareMessage();}
		});
		mShareButton.setClickable(false);
		mShareButton.setEnabled(false);
		
		mLikeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {likeMessage();}
		});
		mLikeButton.setClickable(false);
		mLikeButton.setEnabled(false);
		
		mDislikeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {dislikeMessage();}
		});
		mDislikeButton.setClickable(false);
		mDislikeButton.setEnabled(false);
		
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
	
	protected void likeMessage() {
		if(mMessage!=null) {
			switch(mMessage.likeStatus) {
			case LIKED: mMessage.unlike(); 
				((TransitionDrawable) mLikeButton.getDrawable())
					.reverseTransition(0);
				break;
			case NONE: 
				mMessage.like(); 
				((TransitionDrawable) mLikeButton.getDrawable())
					.startTransition(0);
				break;
			case DISLIKED: 
				mMessage.undislike();mMessage.like();
				((TransitionDrawable) mDislikeButton.getDrawable())
					.reverseTransition(0);
				((TransitionDrawable) mLikeButton.getDrawable())
					.startTransition(0);
				break;
			}
			//REST method call
			restModule.postLikeStatus(mMessage);
			//Update view
			updateLikes();
		}
	}
	
	protected void dislikeMessage() {
		if(mMessage!=null) {
			switch(mMessage.likeStatus) {
			case LIKED: 
				mMessage.unlike(); mMessage.dislike();
				((TransitionDrawable) mLikeButton.getDrawable())
					.reverseTransition(0);
				((TransitionDrawable) mDislikeButton.getDrawable())
					.startTransition(0);
				break;
			case NONE: 
				mMessage.dislike(); 
				((TransitionDrawable) mDislikeButton.getDrawable())
					.startTransition(0);
				break;
			case DISLIKED: 
				mMessage.undislike();
				((TransitionDrawable) mDislikeButton.getDrawable())
					.reverseTransition(0);
				break;
			}
			// REST method call
			restModule.postLikeStatus(mMessage);
			//update view
			updateLikes();
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
			mMessage=message;
			mFirstCommentNameUser.setText(message.getUserName());
			mFirstComment.setText(message.getText());
			mFirstCommentTimestamp.setText(DateFormat.format
					(getString(R.string.date_format), 
							new Date( message.getTimestamp() ) ) );
			
			if(message.getPictureUrl() != null && !message.getPictureUrl().isEmpty()) {
				LoadPictureTask setImageTask = new LoadPictureTask(this);
				setImageTask.execute(NetworkRestModule.SERVER_IMG_BASEURL + message.getPictureUrl());
				DebugUtils.log(NetworkRestModule.SERVER_IMG_BASEURL + message.getPictureUrl());
			}
			
			initLikeButtons();
			initShareButton();
			updateLikes();
			displayComments();
			mProgressBar.setVisibility(View.GONE);
		}
	}
	
	private void initShareButton() {
		mShareButton.setEnabled(true);
		mShareButton.setClickable(true);
	}

	private void initLikeButtons() {
		if(mMessage==null) return;
		mLikeButton.setEnabled(true);
		mLikeButton.setClickable(true);
		mDislikeButton.setEnabled(true);
		mDislikeButton.setClickable(true);
		
		switch(mMessage.likeStatus) {
			case NONE: //nothing
				break;
			case LIKED:
				((TransitionDrawable) mLikeButton.getDrawable())
					.startTransition(0);
				break;
			case DISLIKED:
				((TransitionDrawable) mDislikeButton.getDrawable())
					.startTransition(0);
				break;
		}
		
	}

	protected void updateLikes() {
		if(mMessage!=null) {
			TextView likes = (TextView) getView().findViewById(R.id.textViewLikes);
			likes.setText(Integer.toString(mMessage.getLikes()));
			TextView dislikes = (TextView) getView().findViewById(R.id.textViewDislikes);
			dislikes.setText(Integer.toString(mMessage.getDislikes()));
			
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
	public void shareMessage(){
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT, mFirstComment.getText());
		shareIntent.setType("text/plain");
		startActivity(Intent.createChooser(shareIntent, "Share Comment"));
	}

	@Override
	public void afterPostLikeStatus(Message message) {
		if(message != null && mMessage != null) {
			if(message.getLikeStatus()==mMessage.getLikeStatus()) {
				//Rafraichir les informations
				//Le nombre de like a pu changer
				//Le message aussi ? dépend d'une fonction modifier message
				mMessage = message;
				mFirstComment.setText(message.getText());
				mFirstCommentTimestamp.setText(DateFormat.format
						(getString(R.string.date_format), 
								new Date( message.getTimestamp() ) ) );
				updateLikes();
			}
		}
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
	public void loadPictureTaskOnPostExecute(Drawable image) {
		mFirstCommentPicture.setImageDrawable(image);
		mFirstCommentPicture.setVisibility(View.VISIBLE);
	}

	@Override
	public void onShowUserProfileFragment(long id) {
		mCallback.onShowUserProfileFragment(id);
		
	}

}
