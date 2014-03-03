package com.sims.topaz;


import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.SimsContext;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class UserInfoFragment  extends Fragment  {
	private ImageButton mUnConnectButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_user_info, container, false);
		//unconnect button
		mUnConnectButton = (ImageButton) v.findViewById(R.id.user_unconnect);
		//unconnect action
		mUnConnectButton.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				NetworkRestModule.resetHttpClient();
				AuthUtils.unsetSession();
				Intent intent = new Intent(SimsContext.getContext(), AuthActivity.class);
				startActivity(intent);
			}
		});
		return v;
    }
}
