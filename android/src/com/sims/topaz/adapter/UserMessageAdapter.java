package com.sims.topaz.adapter;

import java.sql.Date;
import java.util.List;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sims.topaz.R;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.utils.CameraUtils;
import com.sims.topaz.utils.MyTypefaceSingleton;

public class UserMessageAdapter extends ArrayAdapter<Message>  {
	
	private byte[] image;
	public UserMessageAdapter(Context mDelegate, int resource, List<Message> messagesList,byte[] mImage) {
		super(mDelegate, resource, messagesList);
		this.image = mImage;
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
			
			holder.mUserImage = (ImageView) view.findViewById(R.id.message_item_image);
			if(image!=null){
				holder.mUserImage.setImageDrawable(CameraUtils.getDrwableFromBytes(image));
			}
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
		 ImageView mUserImage;
    }  
}
