package com.sims.topaz;

import com.sims.topaz.utils.SimsContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class AuthActivity extends FragmentActivity{
	private Button mTempButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SimsContext.setContext(getApplicationContext());
		setContentView(R.layout.activity_auth);
		mTempButton = (Button)findViewById(R.id.go_to_map);
		mTempButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SimsContext.getContext(),
						DrawerActivity.class);
				startActivity(intent);
			}
		});
		
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.signin, (Fragment)new SignInFragment());
		transaction.replace(R.id.signup, (Fragment)new SignUpFragment());
		
	}
}
