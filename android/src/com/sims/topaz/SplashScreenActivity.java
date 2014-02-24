package com.sims.topaz;

import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.SignInDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.InternetConnectionUtils;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

public class SplashScreenActivity extends Activity implements SignInDelegate, ErreurDelegate{
	private NetworkRestModule mRestModule;
	private boolean mShowAuth = true;
	private boolean mHasInternet = true;
	private CountDownTimer mTimer =  new CountDownTimer(2000, 100) {   	
		public void onFinish() {
			if(mHasInternet){
				if(mShowAuth){
					Intent intent = new Intent(SimsContext.getContext(),
							AuthActivity.class);
					startActivity(intent);	
				}else{
					Intent intent = new Intent(SimsContext.getContext(),
							DrawerActivity.class);
					startActivity(intent);					
				}
			}else{
				mTimer.start();
			}
		}
		@Override
		public void onTick(long millisUntilFinished) {
			if(!mHasInternet){
				mHasInternet = InternetConnectionUtils.hasConnection();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splashscreen);
		SimsContext.setContext(getApplicationContext());  
		TextView mTitleTextView = (TextView)findViewById(R.id.title);
		mTitleTextView.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		if(AuthUtils.sessionHasKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME) && 
				AuthUtils.sessionHasKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME)){
			mRestModule = new NetworkRestModule(this);

			String username = AuthUtils.getSessionValue(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME);
			String password = AuthUtils.getSessionValue(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_PASSWORD);
			User user = new User();
			user.setPassword(password);
			user.setName(username);
			mRestModule.signinUser(user);
		}
		TextView mWarningInternet = (TextView)findViewById(R.id.warning_internet);
		mWarningInternet.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		if(!InternetConnectionUtils.hasConnection()){
			mWarningInternet.setVisibility(View.VISIBLE);
			mHasInternet = false;
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
