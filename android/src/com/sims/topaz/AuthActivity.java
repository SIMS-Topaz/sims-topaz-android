package com.sims.topaz;

import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class AuthActivity extends FragmentActivity{
	private Button mTempButton;
	private Fragment signInFragment;
	private Fragment signUpFragment;
	private TextView mTitleTextView;

	/**
	 * Whether or not we're showing the back of the card (otherwise showing the
	 * front).
	 */
	private boolean mShowingBack = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
		signInFragment = (Fragment)new SignInFragment();
		signUpFragment = (Fragment)new SignUpFragment();
		transaction.replace(R.id.auth, signInFragment).commit();
		
		mTitleTextView = (TextView) findViewById(R.id.auth_title);
		mTitleTextView.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());

	}


	
	public void onFlipCard(View v){
		if (mShowingBack) {
			mShowingBack = false;
			getSupportFragmentManager().beginTransaction()
			.setCustomAnimations(R.drawable.animation_grow_from_middle, R.drawable.animation_shrink_to_middle)
			.replace(R.id.auth, signInFragment)
					.addToBackStack(null)
					.commit();	
			return;
		}else{
			mShowingBack = true;
			getSupportFragmentManager().beginTransaction()
			.setCustomAnimations(R.drawable.animation_grow_from_middle, R.drawable.animation_shrink_to_middle)
			.replace(R.id.auth, signUpFragment)
					.addToBackStack(null)
					.commit();	
		}
	}

}
