package com.sims.topaz;


import com.sims.topaz.interfaces.OnVisibilityProgressBar;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.SignUpDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;
import com.sims.topaz.utils.AuthUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpFragment extends Fragment implements SignUpDelegate, ErreurDelegate, TextWatcher {

	private EditText mUserNameEditText;
	private EditText mEmailEditText;
	private EditText mPasswordEditText;
	private EditText mPasswordConfirmEditText;
	private NetworkRestModule mRestModule;
	private Button mSignupButton;
	
	private TextView usernameError;
	private TextView emailError;
	private TextView passwordError;
	private TextView confirmpasswordError;
	private OnVisibilityProgressBar mCallback;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnVisibilityProgressBar) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnNewMessageListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallback = null;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_sign_up, container, false);
		mRestModule = new NetworkRestModule(this);
		TextView mTitleTextView = (TextView)v.findViewById(R.id.sign_up_welcome);
		mTitleTextView.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		// Init all EditText
		mUserNameEditText = (EditText)v.findViewById(R.id.sign_up_username);
		mUserNameEditText.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		mEmailEditText = (EditText)v.findViewById(R.id.sign_up_mail);
		mEmailEditText.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		mPasswordEditText = (EditText)v.findViewById(R.id.sign_up_password);
		mPasswordEditText.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		mPasswordConfirmEditText = (EditText)v.findViewById(R.id.sign_up_confirm_password);
		mPasswordConfirmEditText.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		mSignupButton = (Button)v.findViewById(R.id.sign_up_bt);
		mSignupButton.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		
		// Init all TextView error
		usernameError = (TextView)v.findViewById(R.id.signup_username_error);
		usernameError.setVisibility(View.GONE);
		emailError = (TextView)v.findViewById(R.id.signup_email_error);
		emailError.setVisibility(View.GONE);
		passwordError = (TextView)v.findViewById(R.id.signup_password_error);
		passwordError.setVisibility(View.GONE);
		confirmpasswordError = (TextView)v.findViewById(R.id.signup_confirmpassword_error);
		confirmpasswordError.setVisibility(View.GONE);
		
		// Set TextWatcher listeners
		mUserNameEditText.addTextChangedListener(this);
		mEmailEditText.addTextChangedListener(this);
		mPasswordEditText.addTextChangedListener(this);
		mPasswordConfirmEditText.addTextChangedListener(this);
		
		// Set EditText focus listeners
		mEmailEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					String email = mEmailEditText.getText().toString();
					if(!email.isEmpty() && !AuthUtils.isValidEmail(email)) {
						emailError.setText(R.string.auth_usermail_error);
						emailError.setVisibility(TextView.VISIBLE);
					} else {
						emailError.setVisibility(TextView.GONE);
					}
				}
			}
		});
		
		mPasswordEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					String password = mPasswordEditText.getText().toString();
					if(!password.isEmpty() && !AuthUtils.isValidPassword(password, 6)) {
						passwordError.setText(R.string.auth_userpwd_error);
						passwordError.setVisibility(TextView.VISIBLE);
					} else {
						passwordError.setVisibility(TextView.GONE);
					}
				}
			}
		});
		
		mPasswordConfirmEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					String password = mPasswordEditText.getText().toString();
					String confirmPassword = mPasswordConfirmEditText.getText().toString();
					if(!confirmPassword.isEmpty() && !AuthUtils.isValidPassword(password, confirmPassword, 6)) {
						confirmpasswordError.setText(R.string.auth_userconfirmpwd_error);
						confirmpasswordError.setVisibility(TextView.VISIBLE);
					} else {
						confirmpasswordError.setVisibility(TextView.GONE);
					}
				}
			}
		});
		
		// Signup button
		mSignupButton.setEnabled(true);
		mSignupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {signUpAction();}
		});
		
		// When user tap DONE key
		TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
			  @Override
			  public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
			    if (event==null) {
			      if (actionId==EditorInfo.IME_ACTION_DONE){signUpAction();}
			      return false;  // Let system handle all other null KeyEvents
			    } ;
				return false;
			  }
		};
		mPasswordConfirmEditText.setOnEditorActionListener(listener);
		Bundle bundle = getArguments();
		if(bundle!=null && bundle.getBoolean(AuthActivity.IS_ON_TABLET)){
			Button signIn = (Button)v.findViewById(R.id.Sign_up);
			TextView textSignIn = (TextView)v.findViewById(R.id.text_sing_up);
			textSignIn.setVisibility(View.GONE);
			signIn.setVisibility(View.GONE);
		}
		return v;
    }

	private void signUpAction() {
		
		usernameError.setVisibility(View.GONE);
		emailError.setVisibility(View.GONE);
		passwordError.setVisibility(View.GONE);
		confirmpasswordError.setVisibility(View.GONE);
		
		String username = mUserNameEditText.getText().toString();
		String email = mEmailEditText.getText().toString();
		String password = mPasswordEditText.getText().toString();
		String confirmPassword = mPasswordConfirmEditText.getText().toString();
		
		if(!AuthUtils.isValidUsername(username)) {
			usernameError.setText(R.string.auth_username_error);
			usernameError.setVisibility(TextView.VISIBLE);
		} else if(username != null && username.length() < 4) {
			usernameError.setText(R.string.auth_username_tooshort);
			usernameError.setVisibility(TextView.VISIBLE);
		} else if(!AuthUtils.isValidEmail(email)) {
			emailError.setText(R.string.auth_usermail_error);
			emailError.setVisibility(TextView.VISIBLE);
		} else if(!AuthUtils.isValidPassword(password, 6)) {
			passwordError.setText(R.string.auth_userpwd_error);
			passwordError.setVisibility(TextView.VISIBLE);
		} else if(!AuthUtils.isValidPassword(password, confirmPassword, 6)) {
			confirmpasswordError.setText(R.string.auth_userconfirmpwd_error);
			confirmpasswordError.setVisibility(TextView.VISIBLE);
		} else {
			mSignupButton.setEnabled(false);
	    	User u = new User();
	    	u.setUserName(username);
	    	u.setEmail(email);
	    	u.setPassword(password);
	    	mRestModule.signupUser(u);
	    	mCallback.onShowProgressBar();
		}
		
	}

	@Override
	public void apiError(ApiError error) {
		NetworkRestModule.resetHttpClient();
		mSignupButton.setEnabled(true);
		mCallback.onHideProgressBar();
		// Conflit
		if(error.getCode().equals(409)) {
			if(error.getMsg().equals("EMAIL_ERR")) {
				emailError.setText(R.string.auth_conflictusermail_error);
				emailError.setVisibility(TextView.VISIBLE);
			} else if(error.getMsg().equals("USERNAME_ERR")) {
				usernameError.setText(R.string.auth_conflictusername_error);
				usernameError.setVisibility(TextView.VISIBLE);
			}
		}
	}

	@Override
	public void networkError() {
		mSignupButton.setEnabled(true);
		mCallback.onHideProgressBar();
		Toast.makeText(getActivity(), "networkError", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void afterSignUp(User user) {
		mSignupButton.setEnabled(true);
		mCallback.onHideProgressBar();
		AuthUtils.setSession(mUserNameEditText.getText().toString(), mPasswordEditText.getText().toString(), user.getVerified(), user.getId());
		Intent intent = new Intent(SimsContext.getContext(), DrawerActivity.class);
		startActivity(intent);
	}

	@Override
	public void afterTextChanged(Editable s) {		
		String username = mUserNameEditText.getText().toString();
		String email = mEmailEditText.getText().toString();
		String password = mPasswordEditText.getText().toString();
		String confirmPassword = mPasswordConfirmEditText.getText().toString();
		
		if(!username.equals("") && !AuthUtils.isValidUsername(username)) {
			usernameError.setText(R.string.auth_username_error);
			usernameError.setVisibility(TextView.VISIBLE);
		} else {
			usernameError.setVisibility(TextView.GONE);
		}
		
		if(AuthUtils.isValidEmail(email)) {
			emailError.setVisibility(TextView.GONE);
		}

		if(AuthUtils.isValidPassword(password, 6)) {
			passwordError.setVisibility(TextView.GONE);
		}
		
		if(AuthUtils.isValidPassword(password, confirmPassword, 6)) {
			confirmpasswordError.setVisibility(TextView.GONE);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

}
