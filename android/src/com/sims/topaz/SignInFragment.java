package com.sims.topaz;


import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.SignInDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.SimsContext;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignInFragment extends Fragment implements SignInDelegate, ErreurDelegate{
	private EditText mUserNameEditText;
	private EditText mPasswordEditText;
	private NetworkRestModule mRestModule;
	private Button mLoginButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_sign_in, container, false);
		mRestModule = new NetworkRestModule(this);
		mUserNameEditText = (EditText)v.findViewById(R.id.sign_in_username);
		mPasswordEditText = (EditText)v.findViewById(R.id.sign_in_password);
		mLoginButton = (Button)v.findViewById(R.id.Sign_in);

		//set the done button to fields
		TextView.OnEditorActionListener listener=new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				if (event==null) {
					if (actionId==EditorInfo.IME_ACTION_DONE){signInAction();}
					return false;  // Let system handle all other null KeyEvents
				} ;
				return false;
			}
		};
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {signInAction();}
		});
		mUserNameEditText.setOnEditorActionListener(listener);
		mPasswordEditText.setOnEditorActionListener(listener);

		//set the username if the user already made an account 
		if(MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext())
				.hasKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME)){
			String username = MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext())
					.getString(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME, "");
			mUserNameEditText.setText(username);
		}

		return v;
	}

	public void checkInput(String user, String password){
		//if all the fileds are likely to be ok
		if(AuthUtils.isValidUsername(user) && AuthUtils.isValidPassword(password, 6)) {
			User u = new User();
			u.setName(user);
			u.setPassword(password);
			MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext())
			.putString(
					MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME,
					user);

			MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext())
			.putString(
					MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_PASSWORD,
					password);	
			mRestModule.signinUser(u);
		}else if(!AuthUtils.isValidUsername(user)) {
			//TODO show error field
		}else if(!AuthUtils.isValidPassword(password, 6)) {
			//TODO show error field
		}
	}
	@Override
	public void afterSignIn(User user) {

		Intent intent = new Intent(SimsContext.getContext(),
				DrawerActivity.class);
		startActivity(intent);		
	}

	@Override
	public void apiError(ApiError error) {
		Toast.makeText(getActivity(), "apiError", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void networkError() {
		Toast.makeText(getActivity(), "networkError", Toast.LENGTH_SHORT).show();
	}

	private void signInAction(){
		checkInput(mUserNameEditText.getText().toString(),
				mPasswordEditText.getText().toString());
	}

}
