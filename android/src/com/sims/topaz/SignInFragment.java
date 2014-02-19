package com.sims.topaz;


import com.sims.topaz.EditMessageFragment.OnNewMessageListener;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.SignInDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignInFragment extends Fragment implements SignInDelegate, ErreurDelegate, TextWatcher{
	private EditText mUserNameEditText;
	private EditText mPasswordEditText;
	private NetworkRestModule mRestModule;
	private Button mLoginButton;
	private TextView mUserNameErrorTextView;
	private TextView mPasswordErrorTextView;
	private int savedSoftInputMode;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		savedSoftInputMode = activity.getWindow().getAttributes().softInputMode;
		activity.getWindow()
		.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().getWindow()
		.setSoftInputMode(savedSoftInputMode);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_sign_in, container, false);
		mRestModule = new NetworkRestModule(this);
		mUserNameEditText = (EditText)v.findViewById(R.id.sign_in_username);
		mUserNameEditText.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		mPasswordEditText = (EditText)v.findViewById(R.id.sign_in_password);
		mPasswordEditText.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		mLoginButton = (Button)v.findViewById(R.id.Sign_in);
		mLoginButton.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		mUserNameErrorTextView = (TextView)v.findViewById(R.id.sign_in_username_error);
		mUserNameErrorTextView.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		mPasswordErrorTextView = (TextView)v.findViewById(R.id.sign_in_password_error);
		mPasswordErrorTextView.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		TextView mWelcomeTextView = (TextView)v.findViewById(R.id.sign_in_welcome);
		mWelcomeTextView.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		TextView mNoLoginTextView = (TextView)v.findViewById(R.id.text_sing_up);
		mNoLoginTextView.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		Button mSignUp = (Button)v.findViewById(R.id.Sign_up);
		mSignUp.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		//set TextWatcher listeners
		mUserNameEditText.addTextChangedListener(this);
		mPasswordEditText.addTextChangedListener(this);
		
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
		//make checks after the user has changed the focus
		mUserNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {		
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					String user = mUserNameEditText.getText().toString();
					if(!user.isEmpty()  && !AuthUtils.isValidUsername(user)) {
						mUserNameErrorTextView.setVisibility(View.VISIBLE);
					}else{
						mUserNameErrorTextView.setVisibility(View.GONE);
					}
				}
			}
		});
		mPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {		
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					String pass = mPasswordEditText.getText().toString();
					if(!pass.isEmpty()  && !AuthUtils.isValidPassword(pass, 6)) {
						mPasswordErrorTextView.setVisibility(View.VISIBLE);
					}else{
						mPasswordErrorTextView.setVisibility(View.GONE);
					}
				}
			}
		});
		
		
		mUserNameEditText.setOnEditorActionListener(listener);

		//set the username if the user already made an account 
		if(MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext())
				.hasKey(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME)){
			String username = MyPreferencesUtilsSingleton.getInstance(SimsContext.getContext())
					.getString(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME, "");
			mUserNameEditText.setText(username);
		}
		Bundle bundle = getArguments();
		if(bundle!=null && bundle.getBoolean(AuthActivity.IS_ON_TABLET)){
			mNoLoginTextView.setVisibility(View.GONE);
			mSignUp.setVisibility(View.GONE);
		}

		return v;
	}

	public void checkInput(String user, String password){
		//if all the fileds are likely to be ok
		if(AuthUtils.isValidUsername(user) && AuthUtils.isValidPassword(password, 6)) {
			User u = new User();
			u.setName(user);
			u.setPassword(password);	
			mRestModule.signinUser(u);
		}else if(!AuthUtils.isValidUsername(user)) {
			mUserNameErrorTextView.setVisibility(View.VISIBLE);
		}else if(!AuthUtils.isValidPassword(password, 6)) {
			mPasswordErrorTextView.setVisibility(View.VISIBLE);
		}
	}
	@Override
	public void afterSignIn(User user) {
		AuthUtils.setSession(mUserNameEditText.getText().toString(), mPasswordEditText.getText().toString());
		Intent intent = new Intent(SimsContext.getContext(), DrawerActivity.class);
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

	@Override
	public void afterTextChanged(Editable s) {
		String user = mUserNameEditText.getText().toString();
		if(!user.equals("") && !AuthUtils.isValidUsername(user)) {
			mUserNameErrorTextView.setVisibility(View.VISIBLE);
		}else {
			mUserNameErrorTextView.setVisibility(View.GONE);
		}
		
		if(AuthUtils.isValidPassword(mPasswordErrorTextView.getText().toString(), 6)) {
			mPasswordErrorTextView.setVisibility(View.GONE);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}

}
