package com.sims.topaz.adapter;

import java.sql.Date;
import java.util.List;

import com.sims.topaz.R;
import com.sims.topaz.AsyncTask.LoadPictureTask;
import com.sims.topaz.AsyncTask.LoadPictureTask.LoadPictureTaskInterface;
import com.sims.topaz.modele.CommentItem;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.LikeStatusDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.utils.DebugUtils;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CommentAdapter extends ArrayAdapter<CommentItem> implements LoadPictureTaskInterface, LikeStatusDelegate, ErreurDelegate  {
	
	private Message mMessage = null;
	private TextView mFirstComment;
	private TextView mFirstCommentNameUser;
	private TextView mFirstCommentTimestamp;
	private TextView mCommentLikes;
	private TextView mCommentDislikes;
	private ImageView mFirstCommentPicture;
	private ImageButton mLikeButton;
	private ImageButton mDislikeButton;
	private ImageButton mShareButton;
	
	private int count = 0;
	private NetworkRestModule restModule = new NetworkRestModule(this);
	
	public CommentAdapter(Context mDelegate, int resource, Message message, List<CommentItem> commentsList) {
		super(mDelegate, resource, commentsList);
		count = commentsList.size();
		mMessage = message;
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
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if(position == 0) {
			view = inflater.inflate(R.layout.fragment_comment_message_item, null);

			mFirstCommentNameUser = (TextView) view.findViewById(R.id.comment_person_name);
			mFirstCommentNameUser.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
			
			mFirstComment = (TextView) view.findViewById(R.id.comment_first_comment_text);
			mFirstComment.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
			
			mFirstCommentTimestamp = (TextView) view.findViewById(R.id.comment_time);
			mFirstCommentTimestamp.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
			
			mCommentLikes = (TextView) view.findViewById(R.id.textViewLikes);
			mCommentLikes.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
			
			mCommentDislikes = (TextView) view.findViewById(R.id.textViewDislikes);
			mCommentDislikes.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
			
			mLikeButton =	(ImageButton) view.findViewById(R.id.comment_like);
			mDislikeButton = (ImageButton) view.findViewById(R.id.comment_dislike); 
			
			mShareButton = (ImageButton) view.findViewById(R.id.comment_share);
			
			mFirstCommentPicture = (ImageView) view.findViewById(R.id.comment_first_picture_view);

			DebugUtils.log("position == 0");
			if(mMessage != null) {
				mFirstCommentNameUser.setText(mMessage.getUserName());
				mFirstComment.setText(mMessage.getText());
				mFirstCommentTimestamp.setText(DateFormat.format(getContext().getString(R.string.date_format), new Date( mMessage.getTimestamp()) ) );
				mCommentLikes.setText(mMessage.getLikes() + "");
				mCommentDislikes.setText(mMessage.getDislikes() + "");
				
				if(mMessage.getPictureUrl() != null && !mMessage.getPictureUrl().isEmpty()) {
					LoadPictureTask setImageTask = new LoadPictureTask(this);
					setImageTask.execute(NetworkRestModule.SERVER_IMG_BASEURL + mMessage.getPictureUrl());
				}
				
				mShareButton.setOnClickListener(new View.OnClickListener() {		
					@Override
					public void onClick(View v) { shareMessage(mMessage.getText()); }
				});
				mShareButton.setEnabled(true);
				mShareButton.setClickable(true);
				
				initLikeButtons();
			}
		} else if(position > 0){
			
			ViewHolder holder = new ViewHolder(); 
			view = inflater.inflate(R.layout.fragment_comment_item, null);

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
				holder.mCommentDate.setText(DateFormat.format
						(getContext().getString(R.string.date_format), 
								new Date( ci.getDate()) ) );
			}
		}
		return view;
	}
	
	@Override
	public void loadPictureTaskOnPostExecute(Drawable image) {
		if(mFirstCommentPicture != null) {
			mFirstCommentPicture.setImageDrawable(image);
			mFirstCommentPicture.setVisibility(View.VISIBLE);
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

	protected void updateLikes() {
		mCommentLikes.setText(Integer.toString(mMessage.getLikes()));
		mCommentDislikes.setText(Integer.toString(mMessage.getDislikes()));
	}

	private void initLikeButtons() {
		if(mMessage==null) return;
		
		mLikeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {likeMessage();}
		});
		mDislikeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {dislikeMessage();}
		});

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
	
	public void shareMessage(String text){
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT, text);
		shareIntent.setType("text/plain");
		
		// TODO
		//startActivity(Intent.createChooser(shareIntent, "Share Comment"));
	}
	
	@Override
	public void networkError() {
		Toast.makeText(SimsContext.getContext(),SimsContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();	
	}

	@Override
	public void apiError(ApiError error) {
		Toast.makeText(SimsContext.getContext(),SimsContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();			
	}
	
	class ViewMessageHolder {  
		 TextView mUserName;
		 TextView mUserComment;
		 TextView mCommentDate;
		 TextView mCommentLikes;
		 TextView mCommentDislikes;
		 ImageButton mLikeButton;
		 ImageButton mDislikeButton;
		 ImageButton mShareButton;
    }
	
	class ViewHolder {  
		 TextView mUserName;
		 TextView mUserComment;
		 TextView mCommentDate;
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
				mFirstCommentTimestamp.setText(DateFormat.format(SimsContext.getString(R.string.date_format), new Date( message.getTimestamp() ) ) );
				updateLikes();
			}
		}
	}
}
