package com.sims.topaz;

import java.util.List;

import com.sims.topaz.adapter.CommentAdapter;
import com.sims.topaz.modele.CommentItem;
import com.sims.topaz.network.NetworkDelegate;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
public class CommentFragment extends Fragment implements NetworkDelegate{

	private TextView mFirstComment;
	private TextView mFirstCommentNameUser;
	private TextView mFirstCommentTimestamp;
	private EditText mNewComment;
	private ListView mListComments;
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


		if(getArguments()!=null && getArguments().containsKey("id_preview")){
			long id = getArguments().getLong("id_preview");
			restModule.getMessage(id);
		}
		mNewComment.setImeOptions(EditorInfo.IME_ACTION_GO);
		mNewComment.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				onDoneButton();
				return false;
			}
		});

		return v;
	}   
	public void onDoneButton(){
		getFragmentManager().beginTransaction().remove(this).commit();

	}
	
	@Override
	public void afterPostMessage(Message message) {}

	@Override
	public void afterGetMessage(Message message) {
		mFirstComment.setText(message.getText());
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

}
