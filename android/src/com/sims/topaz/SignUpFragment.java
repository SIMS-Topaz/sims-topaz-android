package com.sims.topaz;


import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.SignUpDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.SimsContext;
import com.sims.topaz.utils.AuthUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpFragment extends Fragment implements SignUpDelegate, ErreurDelegate {

	private EditText mUserNameEditText;
	private EditText mEmailEditText;
	private EditText mPasswordEditText;
	private EditText mPasswordConfirmEditText;
	private NetworkRestModule mRestModule;
	private Button mSignupButton;
	
	private TextView usernameError;
	private TextView emailError;
	private TextView passwordError;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_sign_up, container, false);
		mRestModule = new NetworkRestModule(this);
		mUserNameEditText = (EditText)v.findViewById(R.id.sign_up_username);
		mEmailEditText = (EditText)v.findViewById(R.id.sign_up_mail);
		mPasswordEditText = (EditText)v.findViewById(R.id.sign_up_password);
		mPasswordConfirmEditText = (EditText)v.findViewById(R.id.sign_up_confirm_password);
		mSignupButton = (Button)v.findViewById(R.id.sign_up_bt);
		
		usernameError = (TextView)v.findViewById(R.id.signup_username_error);
		usernameError.setVisibility(View.GONE);
		emailError = (TextView)v.findViewById(R.id.signup_email_error);
		emailError.setVisibility(View.GONE);
		passwordError = (TextView)v.findViewById(R.id.signup_password_error);
		passwordError.setVisibility(View.GONE);
		
		TextView.OnEditorActionListener listener=new TextView.OnEditorActionListener() {
			  @Override
			  public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
			    if (event==null) {
			      if (actionId==EditorInfo.IME_ACTION_DONE){signUpAction();}
			      return false;  // Let system handle all other null KeyEvents
			    } ;
				return false;
			  }
		};
		mSignupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {signUpAction();}
		});
		
		mUserNameEditText.setOnEditorActionListener(listener);
		mEmailEditText.setOnEditorActionListener(listener);
		mPasswordEditText.setOnEditorActionListener(listener);
		mPasswordConfirmEditText.setOnEditorActionListener(listener);
		
		return v;
    }

	private void signUpAction(){
		
		usernameError.setVisibility(View.GONE);
		emailError.setVisibility(View.GONE);
		passwordError.setVisibility(View.GONE);		
		
		String username = mUserNameEditText.getText().toString();
		String email = mEmailEditText.getText().toString();
		String password = mPasswordEditText.getText().toString();
		String confirmPassword = mPasswordConfirmEditText.getText().toString();
		
		if(!AuthUtils.isValidUsername(username)) {
			Toast.makeText(getActivity(), "not isValidUsername", Toast.LENGTH_SHORT).show();
			usernameError.setVisibility(TextView.VISIBLE);
		} else if(!AuthUtils.isValidEmail(email)) {
			Toast.makeText(getActivity(), "not isValidEmail", Toast.LENGTH_SHORT).show();
			emailError.setVisibility(TextView.VISIBLE);
		} else if(!AuthUtils.isValidPassword(password, confirmPassword, 6)) {
			Toast.makeText(getActivity(), "not isValidPassword", Toast.LENGTH_SHORT).show();
			passwordError.setVisibility(TextView.VISIBLE);
		} else {
	    	User u = new User();
	    	u.setName(username);
	    	u.setEmail(email);
	    	u.setPassword(password);
	    	mRestModule.signupUser(u);	
		}
		
	}

	@Override
	public void apiError(ApiError error) {
		Toast.makeText(getActivity(), "apiError", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void networkError() {
		Toast.makeText(getActivity(), "networkError", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void afterSignUp(User user) {
		MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext())
								   .putString(
										   MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME,
										   user.getName());
		MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext())
		   .putString(
				   MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME,
				   user.getName());
	}

}