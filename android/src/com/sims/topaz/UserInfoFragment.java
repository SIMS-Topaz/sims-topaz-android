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

public class UserInfoFragment  extends Fragment  implements TextWatcher,UserDelegate{
	private Button mUnConnectButton;
	private Button mSaveButton;
	private Button mCancelButton;
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
		
		
		mUserEditText = (EditText) v.findViewById(R.id.user_info_username);
		mUserEditText.setTypeface(face);
		mUserEditText.setEnabled(false);
		mUserEditText.setVisibility(View.GONE);

		mStatusEditText = (EditText) v.findViewById(R.id.user_info_status);
		mStatusEditText.setTypeface(face);
		mStatusEditText.setEnabled(false);
		

		mEmailEditText =(EditText)  v.findViewById(R.id.sign_up_mail);
		mEmailEditText.setTypeface(face);
		mEmailEditText.setEnabled(false);
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

		mSaveButton = (Button)v.findViewById(R.id.user_save);
		mSaveButton.setTypeface(face);
		mSaveButton.setVisibility(View.GONE);
		mCancelButton = (Button)v.findViewById(R.id.user_cancel);
		mCancelButton.setTypeface(face);
		mCancelButton.setVisibility(View.GONE);
		

		
		if(!isMyProfile){
			mPasswordLayout = (LinearLayout)v.findViewById(R.id.user_info_password_layout);
			mPasswordLayout.setVisibility(View.GONE);
			LinearLayout mUnconnectLayout = (LinearLayout)v.findViewById(R.id.user_info_unconnect_layout);
			mUnconnectLayout.setVisibility(View.GONE);	
		}
		

		mUserButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mUserEditText.setVisibility(View.VISIBLE);
				mUserTextView.setVisibility(View.GONE);
				if(mUserButton.getText().equals(getResources().getString(R.string.user_tab_edit))){
					mUserButton.setText(getResources().getString(R.string.user_tab_cancel));
				}else{
					mUserButton.setText(getResources().getString(R.string.user_tab_edit));
				}
			}
		});
		
		mEmailButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mEmailEditText.setVisibility(View.VISIBLE);
				mEmailTextView.setVisibility(View.GONE);
				if(mEmailButton.getText().equals(getResources().getString(R.string.user_tab_edit))){
					mEmailButton.setText(getResources().getString(R.string.user_tab_cancel));
				}else{
					mEmailButton.setText(getResources().getString(R.string.user_tab_edit));
				}
			}
		});
		
		mShowPasswordTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mPassEditText.getVisibility()== View.GONE){
					mPassEditText.setVisibility(View.VISIBLE);
					mConfirmEditText.setVisibility(View.VISIBLE);
					mNewPassEditText.setVisibility(View.VISIBLE);
					mSaveButton.setVisibility(View.VISIBLE);
					mCancelButton.setVisibility(View.VISIBLE);
				}else{
					mPassEditText.setVisibility(View.GONE);
					mConfirmEditText.setVisibility(View.GONE);
					mNewPassEditText.setVisibility(View.GONE);	
					mSaveButton.setVisibility(View.GONE);
					mCancelButton.setVisibility(View.GONE);
				}
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
		TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				if (event==null) {
					if (actionId==EditorInfo.IME_ACTION_DONE){saveAction();}
					return false;  // Let system handle all other null KeyEvents
				} ;
				return false;
			}
		};
		mConfirmEditText.setOnEditorActionListener(listener);
		
		mUserTextView.setText(getResources().getString(R.string.auth_user)+" : "+mUser.getUserName());
		mEmailTextView.setText(getResources().getString(R.string.auth_email)+" : "+mUser.getEmail());
		mStatusEditText.setText(getResources().getString(R.string.user_tab_info_status)+" : "+mUser.getStatus());
		
		mUserEditText.addTextChangedListener(this);
		mStatusEditText.addTextChangedListener(this);
		mEmailEditText.addTextChangedListener(this);
		mPassEditText.addTextChangedListener(this);
		mNewPassEditText.addTextChangedListener(this);
		mConfirmEditText.addTextChangedListener(this);

		return v;
	}

	public void saveAction(){
		mErrorUserTextView.setVisibility(View.GONE);
		mErrorEmailTextView.setVisibility(View.GONE);
		mErrorPassTextView.setVisibility(View.GONE);
		mErrorNewPassTextView.setVisibility(View.GONE);
		mErrorConfirmPassTextView.setVisibility(View.GONE);


		String username = mUserEditText.getText().toString();
		String email = mEmailEditText.getText().toString();
		String password = mNewPassEditText.getText().toString();
		String confirmPassword = mConfirmEditText.getText().toString();
		boolean checkUserName = mUserEditText.getVisibility()== View.VISIBLE;
		boolean checkEmail = mEmailEditText.getVisibility()== View.VISIBLE;
		boolean checkPass = mPasswordLayout.getVisibility()== View.VISIBLE;
		
		if(checkUserName && !AuthUtils.isValidUsername(username)) {
			mErrorUserTextView.setText(R.string.auth_username_error);
			mErrorUserTextView.setVisibility(TextView.VISIBLE);
		} else if(checkUserName && username != null && username.length() < 4) {
			mErrorUserTextView.setText(R.string.auth_username_tooshort);
			mErrorUserTextView.setVisibility(TextView.VISIBLE);
		} else if(checkEmail && !AuthUtils.isValidEmail(email)) {
			mErrorEmailTextView.setText(R.string.auth_usermail_error);
			mErrorEmailTextView.setVisibility(TextView.VISIBLE);
		} else if(checkPass && !AuthUtils.isValidPassword(password, 6)) {
			mErrorNewPassTextView.setText(R.string.auth_userpwd_error);
			mErrorNewPassTextView.setVisibility(TextView.VISIBLE);
		} else if(checkPass && !AuthUtils.isValidPassword(password, confirmPassword, 6)) {
			mErrorConfirmPassTextView.setText(R.string.auth_userconfirmpwd_error);
			mErrorConfirmPassTextView.setVisibility(TextView.VISIBLE);
		} else {
			if(mSaveButton.getVisibility() == View.VISIBLE){
				mSaveButton.setEnabled(false);
				User u = new User();
				u.setUserName(username);
				u.setEmail(email);
				u.setPassword(password);
				u.setStatus(mStatusEditText.getText().toString());
				mRestModule.postUserInfo(u, null);
			}
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		saveAction();		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}

	@Override
	public void afterGetUserInfo(User user) {}

	@Override
	public void afterPostUserInfo(User user) {
		Toast.makeText(SimsContext.getContext(), "afterPostUserInfo", Toast.LENGTH_SHORT).show();
	}
}
