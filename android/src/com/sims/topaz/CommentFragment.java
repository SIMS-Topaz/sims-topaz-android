package com.sims.topaz;

import java.sql.Date;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sims.topaz.adapter.CommentAdapter;
import com.sims.topaz.modele.CommentItem;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.LikeStatusDelegate;
import com.sims.topaz.network.interfaces.MessageDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

public class CommentFragment extends Fragment implements MessageDelegate,LikeStatusDelegate,ErreurDelegate{

	private TextView mFirstComment;
	private TextView mFirstCommentNameUser;
	private TextView mFirstCommentTimestamp;
	private EditText mNewComment;
	private ListView mListComments;
	private ImageButton mShareButton;
	private ImageButton mLikeButton;
	private ImageButton mDislikeButton;
	// The main message
	private Message mMessage=null;
	//intelligence
	private NetworkRestModule restModule = new NetworkRestModule(this);


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
		mNewComment = (EditText)v.findViewById(R.id.write_comment_text);
		mNewComment.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		mListComments = (ListView)v.findViewById(R.id.comment_list);
		
		//Set Like, Dislike and Share Buttons
		setButtons(v);
		
		//get the main message from the preview id
		loadMessage();
		
		//TODO remove this!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		CommentItem[] comments = new CommentItem[2];
		CommentItem a = new CommentItem(12321, "Dostoievski", "Plus j�aime l�humanit� en g�n�ral, moins j�aime les gens en particulier, comme individus.", 1392094361,
				null, (float) 8.9);
		CommentItem b = new CommentItem(12321, "Paulo Coelho", "And, when you want something, all the universe conspires in helping you to achieve it", 1392094362,
				null, (float) 7.9);
		comments[0]=a;
		comments[1]=b;
		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		mListComments.setAdapter(new CommentAdapter(SimsContext.getContext(),
				R.layout.fragment_comment_item,
				comments));


//		mNewComment.setImeOptions(EditorInfo.IME_ACTION_GO);
//		mNewComment.setOnKeyListener(new View.OnKeyListener() {
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				onDoneButton();
//				return false;
//			}
//		});
		

		return v;
	}   
	
	//Set Like, Dislike and Share Buttons
	private void setButtons(View v) {
		mShareButton = (ImageButton)v.findViewById(R.id.comment_share);
		mLikeButton = (ImageButton)v.findViewById(R.id.comment_like);
		mDislikeButton = (ImageButton)v.findViewById(R.id.comment_dislike);
		mShareButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {shareMessage();}
		});
		mLikeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {likeMessage();}
		});
		mDislikeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {dislikeMessage();}
		});
		
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
			mFirstComment.setText(message.getText());
			mFirstCommentTimestamp.setText(DateFormat.format
					(getString(R.string.date_format), 
							new Date( message.getTimestamp() ) ) );
			initLikeButtons();
			updateLikes();
		}
	}
	
	private void initLikeButtons() {
		if(mMessage==null) return;
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
		Toast.makeText(getActivity(),
				getResources().getString(R.string.network_error),
				Toast.LENGTH_SHORT).show();	}

	@Override
	public void apiError(ApiError error) {
		// TODO Auto-generated method stub
		
	}
	public void clearMessage(){
		mNewComment.setText("");
	}
	public void shareMessage(){
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, mFirstComment.getText());
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, "Share Comment"));
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

}
