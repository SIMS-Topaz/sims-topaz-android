package com.sims.topaz;

import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.SignInDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.SimsContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class SplashScreenActivity extends Activity implements SignInDelegate, ErreurDelegate{
	private NetworkRestModule mRestModule;
	private boolean mShowAuth = true;
	
	
	private CountDownTimer mTimer =  new CountDownTimer(2000, 100) {   	
		public void onFinish() {
			if(mShowAuth){
				Intent intent = new Intent(SimsContext.getContext(),
						AuthActivity.class);
				startActivity(intent);	
			}else{
				Intent intent = new Intent(SimsContext.getContext(),
						DrawerActivity.class);
				startActivity(intent);					
			}
		}
		@Override
		public void onTick(long millisUntilFinished) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splashscreen);
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
		mTimer.start();
	}
	@Override
	public void apiError(ApiError error) {
		mShowAuth = true;

	}
	@Override
	public void networkError() {
		mShowAuth = true;

	}
	@Override
	public void afterSignIn(User user) {
		mShowAuth = false;
	}
}
