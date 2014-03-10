package com.sims.topaz;

import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.UserDelegate;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoFragment  extends Fragment  implements UserDelegate{
	private Button mUnConnectButton;
	private Button mSaveNewPasswordButton;
	private Button mCancelNewPasswordButton;
	private TextView mUserTextView;
	private TextView mEmailTextView;
	private Button mUserButton;
	private Button mEmailButton;
	//EditText
	private EditText mUserEditText;
	private EditText mStatusEditText;
	private EditText mEmailEditText;
	private EditText mPassEditText;
	private EditText mNewPassEditText;
	private EditText mConfirmEditText;
	//Error text view
	private TextView mErrorUserTextView;
	private TextView mErrorEmailTextView;
	private TextView mErrorPassTextView;
	private TextView mErrorNewPassTextView;
	private TextView mErrorConfirmPassTextView;
	private TextView mShowPasswordTextView;
	//Layout
	private LinearLayout mPasswordLayout;
	
	private User mUser;
	private boolean isMyProfile;
	private NetworkRestModule mRestModule = new NetworkRestModule(this);
	private Button mSaveUser;
	private Button mSaveEmail;
	private Button mSaveStatus;
	private Button mCancelUser;
	private Button mCancelEmail;
	private Button mCancelStatus;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isMyProfile = false;
		if(getArguments()!=null){
			mUser = (User) getArguments().getSerializable("user");
			isMyProfile = getArguments().getBoolean("isMyProfile");
		}
		
		
		Typeface face = MyTypefaceSingleton.getInstance().getTypeFace();
		View v = inflater.inflate(R.layout.fragment_user_info, container, false);
		
		mUserButton = (Button) v.findViewById(R.id.user_info_username_button);
		mUserButton.setTypeface(face);
		mEmailButton = (Button) v.findViewById(R.id.user_info_email_button);
		mEmailButton.setTypeface(face);
		
		mUserTextView = (TextView) v.findViewById(R.id.user_info_username_text);
		mUserTextView.setTypeface(face);
		
		
		mEmailTextView = (TextView) v.findViewById(R.id.user_info_email_text);
		mEmailTextView.setTypeface(face);
		
		TextView mStatusTextView = (TextView)v.findViewById(R.id.user_info_status_text);
		mStatusTextView.setTypeface(face);
		
		
		mUserEditText = (EditText) v.findViewById(R.id.user_info_username);
		mUserEditText.setTypeface(face);
		mUserEditText.setVisibility(View.GONE);

		mStatusEditText = (EditText) v.findViewById(R.id.user_info_status);
		mStatusEditText.setTypeface(face);
		

		mEmailEditText =(EditText)  v.findViewById(R.id.sign_up_mail);
		mEmailEditText.setTypeface(face);
		mEmailEditText.setVisibility(View.GONE);

		mPassEditText =(EditText)  v.findViewById(R.id.user_info_old_password);
		mPassEditText.setTypeface(face);
		mPassEditText.setVisibility(View.GONE);

		mNewPassEditText =(EditText)  v.findViewById(R.id.user_info_new_password);
		mNewPassEditText.setTypeface(face);
		mNewPassEditText.setVisibility(View.GONE);

		mConfirmEditText =(EditText)  v.findViewById(R.id.user_info_confirm_new_password);
		mConfirmEditText.setTypeface(face);
		mConfirmEditText.setVisibility(View.GONE);

		mErrorUserTextView = (TextView) v.findViewById(R.id.user_info_username_error);
		mErrorUserTextView.setTypeface(face);
		mErrorEmailTextView = (TextView) v.findViewById(R.id.signup_email_error);
		mErrorEmailTextView.setTypeface(face);
		mErrorPassTextView = (TextView) v.findViewById(R.id.user_info_password_error);
		mErrorPassTextView.setTypeface(face);
		mErrorNewPassTextView = (TextView) v.findViewById(R.id.user_info_new_password_error);
		mErrorNewPassTextView.setTypeface(face);
		mErrorConfirmPassTextView = (TextView) v.findViewById(R.id.user_info_confirm_new_password_error);
		mErrorConfirmPassTextView.setTypeface(face);
		mShowPasswordTextView = (TextView)v.findViewById(R.id.user_info_show_pass);
		mShowPasswordTextView.setTypeface(face);

		mSaveNewPasswordButton = (Button)v.findViewById(R.id.user_save);
		mSaveNewPasswordButton.setTypeface(face);
		mSaveNewPasswordButton.setVisibility(View.GONE);
		mCancelNewPasswordButton = (Button)v.findViewById(R.id.user_cancel);
		mCancelNewPasswordButton.setTypeface(face);
		mCancelNewPasswordButton.setVisibility(View.GONE);
		
		mPasswordLayout = (LinearLayout)v.findViewById(R.id.user_info_password_layout);

		

		
		mSaveUser = (Button)v.findViewById(R.id.view_save_username);
		mSaveEmail = (Button)v.findViewById(R.id.view_save_email);
		mSaveStatus = (Button)v.findViewById(R.id.view_save);
		mCancelUser = (Button)v.findViewById(R.id.view_cancel_username);
		mCancelEmail = (Button)v.findViewById(R.id.view_cancel_email);
		mCancelStatus = (Button)v.findViewById(R.id.view_cancel);	
		
		mSaveNewPasswordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveNewPassword();
				
			}
		});
		mSaveUser.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				saveNewUserName();
			}
		});
		
		mSaveEmail.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				saveNewEmail();
			}
		});
		mSaveStatus.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				saveNewStatus();
			}
		});
		mCancelNewPasswordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {		
				mConfirmEditText.setText("");
				mPassEditText.setText("");
				mNewPassEditText.setText("");
				onShowHidePassword();
				
			}
		});
		mCancelUser.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mErrorUserTextView.setVisibility(TextView.GONE);
				mUserEditText.setText(mUser.getUserName());
				
			}
		});
		mCancelEmail.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mErrorEmailTextView.setVisibility(TextView.GONE);
				mEmailEditText.setText(mUser.getEmail());
				
			}
		});
		mCancelStatus.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mStatusEditText.setText(mUser.getStatus());
				
			}
		});

		mUserButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mUserEditText.setVisibility(View.VISIBLE);
				mCancelUser.setVisibility(View.VISIBLE);
				mSaveUser.setVisibility(View.VISIBLE);
				
				mUserTextView.setVisibility(View.GONE);
				mUserButton.setVisibility(View.GONE);
				
			}
		});
		
		mEmailButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mEmailEditText.setVisibility(View.VISIBLE);
				mCancelEmail.setVisibility(View.VISIBLE);
				mSaveEmail.setVisibility(View.VISIBLE);
				
				mEmailTextView.setVisibility(View.GONE);
				mEmailButton.setVisibility(View.GONE);
			}
		});
		
		mShowPasswordTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onShowHidePassword();
			}
		});
		mConfirmEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					String password = mConfirmEditText.getText().toString();
					String confirmPassword = mConfirmEditText.getText().toString();
					if(!confirmPassword.isEmpty() && !AuthUtils.isValidPassword(password, confirmPassword, 6)) {
						mErrorConfirmPassTextView.setText(R.string.auth_userconfirmpwd_error);
						mErrorConfirmPassTextView.setVisibility(TextView.VISIBLE);
					} else {
						mErrorConfirmPassTextView.setVisibility(TextView.GONE);
					}
				}
			}
		});

		mNewPassEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					String password = mNewPassEditText.getText().toString();
					if(!password.isEmpty() && !AuthUtils.isValidPassword(password, 6)) {
						mErrorNewPassTextView.setText(R.string.auth_userpwd_error);
						mErrorNewPassTextView.setVisibility(TextView.VISIBLE);
					} else {
						mErrorNewPassTextView.setVisibility(TextView.GONE);
					}
				}
			}
		});
		mEmailEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					String email = mEmailEditText.getText().toString();
					if(!email.isEmpty() && !AuthUtils.isValidEmail(email)) {
						mErrorUserTextView.setText(R.string.auth_usermail_error);
						mErrorUserTextView.setVisibility(TextView.VISIBLE);
					} else {
						mErrorUserTextView.setVisibility(TextView.GONE);
					}
				}
			}
		});		
		//unconnect button
		mUnConnectButton = (Button) v.findViewById(R.id.user_unconnect);
		//unconnect action
		mUnConnectButton.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				NetworkRestModule.resetHttpClient();
				AuthUtils.unsetSession();
				Intent intent = new Intent(SimsContext.getContext(), AuthActivity.class);
				startActivity(intent);
			}
		});

		// When user tap DONE key
		TextView.OnEditorActionListener listenerConfirmPassword = new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				if (event==null) {
					if (actionId==EditorInfo.IME_ACTION_DONE){saveNewPassword();}
					return false;  // Let system handle all other null KeyEvents
				} ;
				return false;
			}
		};
		mConfirmEditText.setOnEditorActionListener(listenerConfirmPassword);
		
		mUserTextView.setText(mUser.getUserName());
		mEmailTextView.setText(mUser.getEmail());
		mStatusEditText.setText(mUser.getStatus());
		
		mUserEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {checkNewUserName();}
		});
		mEmailEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {checkNewEmail();}
		});
		mPassEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {
						checkOldPassword();					
				}
		});
		mNewPassEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {
						checkNewPassword();

				}
		});
		mConfirmEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {
						checkConfirmPassword();
				}
		});

		if(!isMyProfile){
			mPasswordLayout.setVisibility(View.GONE);
			LinearLayout mUnconnectLayout = (LinearLayout)v.findViewById(R.id.user_info_unconnect_layout);
			mUnconnectLayout.setVisibility(View.GONE);
			mSaveStatus.setVisibility(View.GONE);
			mCancelStatus.setVisibility(View.GONE);
			mUserButton.setVisibility(View.GONE);
			mEmailButton.setVisibility(View.GONE);
			//Transform this edit text into a textView
			mStatusEditText.setKeyListener(null);
			mStatusEditText.setEnabled(false);
			mStatusEditText.setCursorVisible(false);
			mStatusTextView.setFocusable(false);
		}
		return v;
	}

	private void onShowHidePassword(){
		if(mPassEditText.getVisibility()== View.GONE){
			mPassEditText.setVisibility(View.VISIBLE);
			mConfirmEditText.setVisibility(View.VISIBLE);
			mNewPassEditText.setVisibility(View.VISIBLE);
			mSaveNewPasswordButton.setVisibility(View.VISIBLE);
			mCancelNewPasswordButton.setVisibility(View.VISIBLE);
			
		}else{
			mPassEditText.setVisibility(View.GONE);
			mConfirmEditText.setVisibility(View.GONE);
			mNewPassEditText.setVisibility(View.GONE);	
			mSaveNewPasswordButton.setVisibility(View.GONE);
			mCancelNewPasswordButton.setVisibility(View.GONE);
			mErrorConfirmPassTextView.setVisibility(View.GONE);
			mErrorPassTextView.setVisibility(View.GONE);
			mErrorNewPassTextView.setVisibility(View.GONE);
		}
	}
	private void saveNewUserName(){
		String username = mUserEditText.getText().toString();
		boolean checkUserName = mUserEditText.getVisibility()== View.VISIBLE;
		if(checkUserName && checkNewUserName()){
				mUser.setUserName(username);
				mRestModule.postUserInfo(mUser);
		}
	}	
	
	private void saveNewStatus(){
		mUser.setStatus(mStatusEditText.getText().toString());
		mRestModule.postUserInfo(mUser);			
	}
	private void saveNewEmail(){
		String email = mEmailEditText.getText().toString();
		boolean checkEmail = mEmailEditText.getVisibility()== View.VISIBLE;
		if(checkEmail && checkNewEmail()){
			mUser.setEmail(email);
			mRestModule.postUserInfo(mUser);					
		}
	}

	private void saveNewPassword(){
		
		String password = mNewPassEditText.getText().toString();
		boolean checkPass = mPasswordLayout.getVisibility()== View.VISIBLE;
		
		if(checkPass && checkConfirmPassword()){
				mSaveNewPasswordButton.setEnabled(false);
				mUser.setPassword(password);
				mUser.setStatus(mStatusEditText.getText().toString());
				mRestModule.postUserInfo(mUser);
		}
	}
	private boolean checkNewEmail(){
		mErrorEmailTextView.setVisibility(TextView.GONE);
		String email = mEmailEditText.getText().toString();
		boolean checkEmail = mEmailEditText.getVisibility()== View.VISIBLE;
		if(checkEmail && !AuthUtils.isValidEmail(email)) {
			mErrorEmailTextView.setText(R.string.auth_usermail_error);
			mErrorEmailTextView.setVisibility(TextView.VISIBLE);
			return false;
		}
		return true;
	}	
	private boolean checkNewUserName(){
		mErrorUserTextView.setVisibility(TextView.GONE);
		String username = mUserEditText.getText().toString();
		boolean checkUserName = mUserEditText.getVisibility()== View.VISIBLE;
		if(checkUserName && !AuthUtils.isValidUsername(username)) {
			mErrorUserTextView.setText(R.string.auth_username_error);
			mErrorUserTextView.setVisibility(TextView.VISIBLE);
			return false;
		} else if(checkUserName && username != null && username.length() < 4) {
			mErrorUserTextView.setText(R.string.auth_username_tooshort);
			mErrorUserTextView.setVisibility(TextView.VISIBLE);
			return false;
		}
		return true;
	}
	private boolean checkOldPassword(){
		boolean checkPass = mPasswordLayout.getVisibility()== View.VISIBLE;
		mErrorPassTextView.setVisibility(View.GONE);
		String password = mPassEditText.getText().toString();
		if(checkPass && !AuthUtils.isValidPassword(password, 6)) {
			mErrorPassTextView.setText(R.string.auth_userpwd_error);
			mErrorPassTextView.setVisibility(View.VISIBLE);
			return false;
		}
		return true;
	}
	private boolean checkNewPassword(){
		boolean checkPass = mPasswordLayout.getVisibility()== View.VISIBLE;
		mErrorNewPassTextView.setVisibility(View.GONE);
		String password = mNewPassEditText.getText().toString();
		if(checkPass && !AuthUtils.isValidPassword(password, 6)) {
			mErrorNewPassTextView.setText(R.string.auth_userpwd_error);
			mErrorNewPassTextView.setVisibility(TextView.VISIBLE);
			return false;
		}
		return true;
	}
	private boolean checkConfirmPassword(){
		boolean checkPass = mPasswordLayout.getVisibility()== View.VISIBLE;
		mErrorConfirmPassTextView.setVisibility(View.GONE);
		String password = mNewPassEditText.getText().toString();
		String confirmPassword = mConfirmEditText.getText().toString();
		
		if(checkPass && !AuthUtils.isValidPassword(password, 6)) {
			mErrorNewPassTextView.setText(R.string.auth_userpwd_error);
			mErrorNewPassTextView.setVisibility(TextView.VISIBLE);
			return false;
		} else if(checkPass && !AuthUtils.isValidPassword(password, confirmPassword, 6)) {
			mErrorConfirmPassTextView.setText(R.string.auth_userconfirmpwd_error);
			mErrorConfirmPassTextView.setVisibility(TextView.VISIBLE);
			return false;
		}
		return true;
	}

	@Override
	public void afterGetUserInfo(User user) {}

	@Override
	public void afterPostUserInfo(User user) {
		mUser = user;
		//We are setting the fields with the fields received from the server
		mStatusEditText.setText(mUser.getStatus());

		mEmailEditText.setText(mUser.getEmail());
		mEmailTextView.setText(mUser.getEmail());
		
		mUserEditText.setText(mUser.getUserName());
		mUserTextView.setText(mUser.getUserName());
		
		Toast.makeText(SimsContext.getContext(), getResources().getString(R.string.user_tab_save_ok), Toast.LENGTH_SHORT).show();
	}
}
