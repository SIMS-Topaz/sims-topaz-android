package com.sims.topaz;


import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.SignUpDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.SimsContext;

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
    	User u = new User();
    	u.setName(mUserNameEditText.getText().toString());
    	u.setEmail(mEmailEditText.getText().toString());
    	u.setPassword(mPasswordEditText.getText().toString());
    	mRestModule.signupUser(u);
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