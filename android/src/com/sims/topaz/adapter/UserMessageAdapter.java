package com.sims.topaz.adapter;

import java.sql.Date;
import java.util.List;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sims.topaz.R;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.utils.MyTypefaceSingleton;

public class UserMessageAdapter extends ArrayAdapter<Message>  {
	
	private int count = 0;
	public UserMessageAdapter(Context mDelegate, int resource, List<Message> messagesList) {
		super(mDelegate, resource, messagesList);
		count = messagesList.size();
	}	
	

	@Override
	public int getCount() {
		return count;
	}
	
	
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;
		ViewHolder holder = null; 
		holder=new ViewHolder(); 
		if(view == null){
			LayoutInflater inflater = 
					(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.fragment_message_item, null);
		}
		if(position >= 0){
			holder.mUserName = (TextView) view.findViewById(R.id.message_item_person_name);
			holder.mUserName.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
			
			holder.mUserMessage = (TextView) view.findViewById(R.id.message_item_text);
			holder.mUserMessage.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
			
			holder.mMessageDate = (TextView) view.findViewById(R.id.message_item_time);
			holder.mMessageDate.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
			if(getItem(position)!=null){
				Message m = getItem(position);
				holder.mUserName.setText(m.getUserName());
				holder.mUserMessage.setText(m.getText());
				holder.mMessageDate.setText(DateFormat.format
						(getContext().getString(R.string.date_format), 
								new Date( m.getTimestamp()) ) );
			}
		}
		return view;
	}
	
	class ViewHolder {  
		 TextView mUserName;
		 TextView mUserMessage;
		 TextView mMessageDate;
    }  
}
