package com.sims.topaz;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sims.topaz.AsyncTask.LoadPictureTask;
import com.sims.topaz.AsyncTask.LoadPictureTask.LoadPictureTaskInterface;
import com.sims.topaz.adapter.UserPageAdapter;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.PictureUploadDelegate;
import com.sims.topaz.network.interfaces.UserDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.CameraUtils;
import com.sims.topaz.utils.DebugUtils;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

import eu.janmuller.android.simplecropimage.CropImage;



public class UserFragment  extends Fragment 
implements UserDelegate,ErreurDelegate, LoadPictureTaskInterface,PictureUploadDelegate {
	private TextView mUserTextView;
	private TextView mUserSnippetTextView;
	private ImageButton mUserImage;
	private ViewPager mViewPager;
	private ProgressBar mProgressBar;
	private static String IS_MY_OWN_PROFILE = "user_fragment_is_my_own_profile";
	private static String USER_ID = "user_fragment_user_id";
	private boolean isMyProfile;
	private byte[] pictureData;
	private User mUser = null;
	

	private NetworkRestModule mRestModule = new NetworkRestModule(this);

	public static UserFragment newInstance(boolean isMyProfile){
		UserFragment fragment= new UserFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean(IS_MY_OWN_PROFILE, true);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	public static UserFragment newInstance(boolean isMyProfile, long id){
		UserFragment fragment= new UserFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean(IS_MY_OWN_PROFILE, isMyProfile);
		bundle.putLong(USER_ID, id);
		fragment.setArguments(bundle);
		return fragment;
	}	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_user, container, false);
		
		
		Typeface face = MyTypefaceSingleton.getInstance().getTypeFace();

		//username field
		mUserTextView = (TextView)v.findViewById(R.id.username);
		mUserTextView.setTypeface(face);
		//message fiels
		mUserSnippetTextView= (TextView)v.findViewById(R.id.username_snippet);
		mUserTextView.setTypeface(face);
		//user imgae
		mUserImage = (ImageButton)v.findViewById(R.id.username_image);
		//tabs
		boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
		if (!tabletSize) {
			mViewPager = (ViewPager) v.findViewById(R.id.pager);
		} 

		//Progress bar
		mProgressBar = (ProgressBar)v.findViewById(R.id.progressBar);
		mProgressBar.setVisibility(View.VISIBLE);

		//change image
		mUserImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				selectImage();

			}
		});
		if(mUser==null){
			mUser = new User();
		}
		isMyProfile = false;
		if(getArguments() != null 
				&& getArguments().containsKey(IS_MY_OWN_PROFILE) 
				&& getArguments().getBoolean(IS_MY_OWN_PROFILE)){
			
			mUserTextView.setText(AuthUtils.getSessionStringValue
					(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME));

			mUser.setUserName(AuthUtils.getSessionStringValue
					(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME));
			mUser.setPassword(AuthUtils.getSessionStringValue
					(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_PASSWORD));
			mUser.setId(AuthUtils.getSessionLongValue
					(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_ID, (long)0));
			isMyProfile = true;
			
		}else if(getArguments() != null 
				&& getArguments().containsKey(USER_ID)){
			mUser.setId(getArguments().getLong(USER_ID));
		}
		
		pictureData = null;
		if(mUser.getId()!=null){
			mRestModule.getUserInfo(mUser.getId());
		}else{
			Toast.makeText(SimsContext.getContext(), getResources().getString(R.string.erreur_gen), Toast.LENGTH_SHORT).show();
		}
		// Retain this fragment across configuration changes.
		setRetainInstance(true);
		return v;
	}

	@Override
	public void apiError(ApiError error) {
		DebugUtils.log("UserFragment_apiError");
	}

	@Override
	public void networkError() {
		DebugUtils.log("UserFragment_networkError");
	}



	//Image selector	
	private void selectImage() {
		final CharSequence[] items = { getString(R.string.select_img_take_photo),
				getString(R.string.select_img_from_lib),
				getString(R.string.select_img_close) };
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		builder.setTitle(R.string.edit_add_image);

		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					startActivityForResult(CameraUtils.takePicture(), CameraUtils.REQUEST_CODE_TAKE_PICTURE);
				} else if (item == 1) {
					startActivityForResult(CameraUtils.openGallery(), CameraUtils.REQUEST_CODE_GALLERY);
				} else if (item == 2) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
        Bitmap bitmap;

        switch (requestCode) {

            case CameraUtils.REQUEST_CODE_GALLERY:

                try {
                    InputStream inputStream = SimsContext.getContext().getContentResolver().openInputStream(data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(CameraUtils.getTempFile());
                    CameraUtils.copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();
                    startActivityForResult(CameraUtils.startCropImage(100,100,true), CameraUtils.REQUEST_CODE_CROP_IMAGE);
                } catch (Exception e) {
                    DebugUtils.log("Error while creating temp file"+ e);
                }

                break;
            case CameraUtils.REQUEST_CODE_TAKE_PICTURE:
                startActivityForResult(CameraUtils.startCropImage(100,100,true), CameraUtils.REQUEST_CODE_CROP_IMAGE);
                break;
            case CameraUtils.REQUEST_CODE_CROP_IMAGE:
            	if(data == null) {
            		Toast.makeText(SimsContext.getContext(), getResources().getString(R.string.erreur_gen), Toast.LENGTH_SHORT).show();
            		return;}
                String path = data.getStringExtra(CropImage.IMAGE_PATH);
                if (path == null) {return;}

                bitmap = BitmapFactory.decodeFile(CameraUtils.getTempFile().getPath());
                //Note: we cannot user setBackground since is available only from api 16
                mUserImage.setBackgroundDrawable(new BitmapDrawable(SimsContext.getContext().getResources(),bitmap));
                
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(CompressFormat.JPEG, 85, bos);
                pictureData = bos.toByteArray();
                mRestModule.uploadPicture(pictureData);
                break;
        }
        
        super.onActivityResult(requestCode, resultCode, data);

	}


	public String getPath(Uri uri, Activity activity) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	public Uri getImageUri(Bitmap inImage) {
		  ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		  inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		  String path = Images.Media.insertImage(SimsContext.getContext().getContentResolver(), inImage, "Title", null);
		  return Uri.parse(path);
		} 


	@Override
	public void afterGetUserInfo(User user) {
		mUser = user;
		if(mUser.getPictureUrl()!=null && !mUser.getPictureUrl().isEmpty()){
			LoadPictureTask setImageTask = new LoadPictureTask(this);
			setImageTask.execute(NetworkRestModule.SERVER_IMG_BASEURL + mUser.getPictureUrl());
			Toast.makeText(SimsContext.getContext(), 
					NetworkRestModule.SERVER_IMG_BASEURL+mUser.getPictureUrl(),
					Toast.LENGTH_SHORT).show();
		}
		mUserSnippetTextView.setText(mUser.getStatus());
		mUserTextView.setText(mUser.getUserName());
		mProgressBar.setVisibility(View.GONE);
		prepareFragments();
	}
	@Override
	public void afterPostUserInfo(User user) {
		//Normally only the picture changed.
		mUser = user;
		mProgressBar.setVisibility(View.GONE);	
	}
	
	private void prepareFragments(){
		Fragment userInfoFragment = UserInfoFragment.newInstance(isMyProfile, mUser);
		Fragment userCommentFragment =UserCommentFragment.newInstance(mUser, pictureData);
		
		//tabs
		boolean tabletSize = getResources().getBoolean(R.bool.isTablet);

		if (!tabletSize) {
			UserPageAdapter mTabsAdapter = 
					new UserPageAdapter(getActivity().getSupportFragmentManager(),
							userCommentFragment,
							userInfoFragment);
			mViewPager.setAdapter(mTabsAdapter);

		} else {
			FragmentTransaction transaction = getActivity().getSupportFragmentManager()
					.beginTransaction();

			transaction.replace(R.id.user_info_fragment, userInfoFragment);
			transaction.replace(R.id.user_info_comments_fragment, userCommentFragment);
			transaction.commit();		
		}	
	}

	@Override
	public void afterUploadPicture(String pictureUrl) {
		mUser.setPictureUrl(pictureUrl);
		Toast.makeText(SimsContext.getContext(), pictureUrl, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void loadPictureTaskOnPostExecute(Drawable image) {
		mUserImage.setImageDrawable(image);
	}
}
