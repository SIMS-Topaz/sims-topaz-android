package com.sims.topaz;


import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.SignInDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.User;

import android.support.v4.app.Fragment;
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
		
		return v;
    }

    public void checkInput(String user, String password){
    	User u = new User();
    	u.setName(user);
    	u.setPassword(password);
    	mRestModule.signinUser(u);
    }
	@Override
	public void afterSignIn(User user) {
		Toast.makeText(getActivity(), "after sign up : "+user.toString(), Toast.LENGTH_SHORT).show();
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
