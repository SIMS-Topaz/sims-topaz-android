package com.sims.topaz;

import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.SignInDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.SimsContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class AuthActivity extends FragmentActivity implements SignInDelegate, ErreurDelegate{
	private Button mTempButton;
	private Fragment signInFragment;
	private Fragment signUpFragment;
	private NetworkRestModule mRestModule;
	/**
	 * Whether or not we're showing the back of the card (otherwise showing the
	 * front).
	 */
	private boolean mShowingBack = false;
	
    @Override
    public void onStart(){
        super.onStart();
        SimsContext.setContext(getApplicationContext());       
        if(MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext())
        		.hasKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME) && 
        		MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext())
        		.hasKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME)){
        	mRestModule = new NetworkRestModule(this);
        	String username = MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext())
            		.getString(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME, "");
        	String password = MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext())
            		.getString(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_PASSWORD, "");
        	User user = new User();
        	user.setPassword(password);
        	user.setName(username);
        	mRestModule.signinUser(user);
        }
        
    
    }
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
	@Override
	public void apiError(ApiError error) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void networkError() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void afterSignIn(User user) {
		Intent intent = new Intent(SimsContext.getContext(),
				DrawerActivity.class);
		startActivity(intent);	
	}
}
