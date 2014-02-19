package com.sims.topaz;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

public class UserFragment  extends Fragment  {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_user, container, false);
		Button unconnect = (Button) v.findViewById(R.id.user_unconnect);
		Typeface face = MyTypefaceSingleton.getInstance().getTypeFace();
		unconnect.setTypeface(face);
		TextView user = (TextView)v.findViewById(R.id.username);
		user.setTypeface(face);
		
		unconnect.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				NetworkRestModule.httpclient = null;
				AuthUtils.unsetSession();
				Intent intent = new Intent(SimsContext.getContext(), AuthActivity.class);
				startActivity(intent);
			}
		});
		return v;
	}
}
