package com.sims.topaz.adapter;

import java.sql.Date;
import java.util.List;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sims.topaz.R;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

public class PreviewListAdapter extends ArrayAdapter<Preview> {

	private int count = 0;
	public PreviewListAdapter(Context context, int resource,
			List<Preview> objects) {
		super(context, resource, objects);
		count = objects.size();
	}
	
	@Override
	public int getCount() {
		return count;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;
		ViewHolder holder = null; 
		if(view == null){
			holder=new ViewHolder();
			LayoutInflater inflater = 
					(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.adapter_preview_item, null);
			holder.mUserName = (TextView) view.findViewById(R.id.preview_item_username);
			holder.mUserName.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
			
			holder.mText = (TextView) view.findViewById(R.id.preview_item_text);
			holder.mText.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
			
			holder.mDate = (TextView) view.findViewById(R.id.preview_item_time);
			holder.mDate.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
			
			holder.mLikeBar = (ProgressBar) view.findViewById(R.id.preview_item_like_bar);
			
			holder.mNote = (TextView) view.findViewById(R.id.preview_item_note);
			holder.mNote.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		if(position >= 0){
			
			
			if(getItem(position)!=null){
				Preview p = getItem(position);
				holder.mUserName.setText(p.getUserName());
				holder.mText.setText(p.getText());
				holder.mDate.setText(DateFormat.format
						(getContext().getString(R.string.date_format), 
								new Date( p.getTimestamp()) ) );
				
				if(p.getLikes()==0 && p.getDislikes()==0) {
					//holder.mLikeBar.setIndeterminate(true);
					holder.mLikeBar.setProgress(0);
				} else {
					holder.mLikeBar.setMax(p.getLikes()+p.getDislikes());
					holder.mLikeBar.setProgress(p.getLikes());
				}
				holder.mNote.setText(Integer.toString(p.getLikes()+p.getDislikes())
						+" "+SimsContext.getContext().getString(R.string.bulle_note));
			}
		}
		return view;
	}
	
	class ViewHolder {
		TextView mUserName;
		TextView mDate;
		TextView mText;
		ProgressBar mLikeBar;
		TextView mNote;
		
    }  

}
