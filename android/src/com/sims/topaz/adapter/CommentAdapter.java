package com.sims.topaz.adapter;

import java.lang.ref.WeakReference;
import java.sql.Date;
import java.util.List;

import com.sims.topaz.R;
import com.sims.topaz.interfaces.OnShowUserProfile;
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
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CommentAdapter extends ArrayAdapter<CommentItem> implements LoadPictureTaskInterface, LikeStatusDelegate, ErreurDelegate  {
	
	private Message mMessage = null;
	private ViewMessageHolder messageHolder = null;
	
	private int count = 0;
	private WeakReference<Context> delegate;
	private NetworkRestModule restModule = new NetworkRestModule(this);
	public CommentAdapter(Context mDelegate, int resource ,Message message, List<CommentItem> commentsList) {
		super(mDelegate, resource, commentsList);
		count = commentsList.size() + 1; // +1 pour le message
		mMessage = message;
		this.delegate = new WeakReference<Context>(mDelegate);
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
	
	
	public View getView(final int position, View convertView, ViewGroup parent){
		View view = convertView;

		if(position == 0) {

			if(view == null){
				messageHolder = new ViewMessageHolder();
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.fragment_comment_message_item, null);

				messageHolder.mUserName = (TextView) view.findViewById(R.id.comment_person_name);
				messageHolder.mUserName.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
				
				messageHolder.mUserComment = (TextView) view.findViewById(R.id.comment_first_comment_text);
				messageHolder.mUserComment.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
				
				messageHolder.mCommentDate = (TextView) view.findViewById(R.id.comment_time);
				messageHolder.mCommentDate.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
				
				messageHolder.mCommentLikes = (TextView) view.findViewById(R.id.textViewLikes);
				messageHolder.mCommentLikes.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
				
				messageHolder.mCommentDislikes = (TextView) view.findViewById(R.id.textViewDislikes);
				messageHolder.mCommentDislikes.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());

				messageHolder.mLikeButton = (ImageButton) view.findViewById(R.id.comment_like);;
				messageHolder.mDislikeButton = (ImageButton) view.findViewById(R.id.comment_dislike);
				
				messageHolder.mShareButton = (ImageButton) view.findViewById(R.id.comment_share);
				
				messageHolder.mFirstCommentPicture = (ImageView) view.findViewById(R.id.comment_first_picture_view);

				view.setTag(messageHolder);
			} else {
				messageHolder = (ViewMessageHolder) view.getTag();
			}
			
			messageHolder.mUserName.setText(mMessage.getUserName());
			messageHolder.mUserComment.setText(mMessage.getText());
			messageHolder.mCommentDate.setText(DateFormat.format(getContext().getString(R.string.date_format), new Date( mMessage.getTimestamp()) ) );
			messageHolder.mCommentLikes.setText(mMessage.getLikes() + "");
			messageHolder.mCommentDislikes.setText(mMessage.getDislikes() + "");

			if(mMessage.getPictureUrl() != null && !mMessage.getPictureUrl().isEmpty()) {
				LoadPictureTask setImageTask = new LoadPictureTask(this);
				setImageTask.execute(NetworkRestModule.SERVER_IMG_BASEURL + mMessage.getPictureUrl());
			}
			
			messageHolder.mShareButton.setOnClickListener(new View.OnClickListener() {		
				@Override
				public void onClick(View v) { shareMessage(mMessage.getText()); }
			});
			messageHolder.mShareButton.setEnabled(true);
			messageHolder.mShareButton.setClickable(true);
			
			initLikeButtons();
			
		} else if(position > 0) {
			
			ViewHolder holder = null; 
			if(view == null){
				holder=new ViewHolder();
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.fragment_comment_item, null);
				holder.mUserName = (TextView) view.findViewById(R.id.comment_item_person_name);
				holder.mUserName.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
	
				holder.mUserComment = (TextView) view.findViewById(R.id.comment_item_text);
				holder.mUserComment.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
	
				holder.mCommentDate = (TextView) view.findViewById(R.id.comment_item_time);
				holder.mCommentDate.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
				
				holder.mUserImage = (ImageButton) view.findViewById(R.id.comment_item_image_first_comment);
				holder.mUserImage.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						CommentItem ci = getItem(position);
						((OnShowUserProfile)delegate.get()).onShowUserProfileFragment(ci.getUserId());
						
					}
				});
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			if(getItem(position-1)!=null){
				CommentItem ci = getItem(position-1);
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
		if(messageHolder.mFirstCommentPicture != null) {
			messageHolder.mFirstCommentPicture.setImageDrawable(image);
			messageHolder.mFirstCommentPicture.setVisibility(View.VISIBLE);
		}
	}
	
	protected void likeMessage() {
		if(mMessage!=null) {
			switch(mMessage.likeStatus) {
			case LIKED: mMessage.unlike(); 
				((TransitionDrawable) messageHolder.mLikeButton.getDrawable())
					.reverseTransition(0);
				break;
			case NONE: 
				mMessage.like(); 
				((TransitionDrawable) messageHolder.mLikeButton.getDrawable())
					.startTransition(0);
				break;
			case DISLIKED: 
				mMessage.undislike();mMessage.like();
				((TransitionDrawable) messageHolder.mDislikeButton.getDrawable())
					.reverseTransition(0);
				((TransitionDrawable) messageHolder.mLikeButton.getDrawable())
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
				((TransitionDrawable) messageHolder.mLikeButton.getDrawable())
					.reverseTransition(0);
				((TransitionDrawable) messageHolder.mDislikeButton.getDrawable())
					.startTransition(0);
				break;
			case NONE: 
				mMessage.dislike(); 
				((TransitionDrawable) messageHolder.mDislikeButton.getDrawable())
					.startTransition(0);
				break;
			case DISLIKED: 
				mMessage.undislike();
				((TransitionDrawable) messageHolder.mDislikeButton.getDrawable())
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
		messageHolder.mCommentLikes.setText(Integer.toString(mMessage.getLikes()));
		messageHolder.mCommentDislikes.setText(Integer.toString(mMessage.getDislikes()));
	}

	private void initLikeButtons() {
		if(mMessage==null) return;
		
		messageHolder.mLikeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {likeMessage();}
		});
		messageHolder.mDislikeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {dislikeMessage();}
		});

		messageHolder.mLikeButton.setEnabled(true);
		messageHolder.mLikeButton.setClickable(true);
		messageHolder.mDislikeButton.setEnabled(true);
		messageHolder.mDislikeButton.setClickable(true);
		
		switch(mMessage.likeStatus) {
			case NONE: //nothing
				break;
			case LIKED:
				((TransitionDrawable) messageHolder.mLikeButton.getDrawable())
					.startTransition(0);
				break;
			case DISLIKED:
				((TransitionDrawable) messageHolder.mDislikeButton.getDrawable())
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
		 ImageView mFirstCommentPicture;
		 
    }
	
	class ViewHolder {  
		 TextView mUserName;
		 TextView mUserComment;
		 TextView mCommentDate;
		 ImageButton mUserImage;
    }  

	@Override
	public void afterPostLikeStatus(Message message) {
		if(message != null && mMessage != null) {
			if(message.getLikeStatus()==mMessage.getLikeStatus()) {
				//Rafraichir les informations
				//Le nombre de like a pu changer
				//Le message aussi ? dépend d'une fonction modifier message
				mMessage = message;
				messageHolder.mUserComment.setText(message.getText());
				messageHolder.mCommentDate.setText(DateFormat.format(SimsContext.getString(R.string.date_format), new Date( message.getTimestamp() ) ) );
				updateLikes();
			}
		}
	}
}
