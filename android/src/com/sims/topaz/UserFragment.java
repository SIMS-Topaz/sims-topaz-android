package com.sims.topaz;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sims.topaz.adapter.UserPageAdapter;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.UserDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.User;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.DebugUtils;
import com.sims.topaz.utils.MyTypefaceSingleton;
import com.sims.topaz.utils.SimsContext;

public class UserFragment  extends Fragment implements UserDelegate,ErreurDelegate {
	private TextView mUserTextView;
	private TextView mUserSnippetTextView;
	private ImageButton mUserImage;
	private ViewPager mViewPager;
	private ProgressBar mProgressBar;
	private final static int SELECT_FILE = 10;
	private final static int REQUEST_CAMERA = 11;
	private byte[] pictureData;
	private NetworkRestModule mRestModule = new NetworkRestModule(this);
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
		UserPageAdapter mTabsAdapter = new UserPageAdapter(getActivity().getSupportFragmentManager());
		mViewPager = (ViewPager) v.findViewById(R.id.pager);
		mViewPager.setAdapter(mTabsAdapter);
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
		//TODO network call 
		mRestModule.getUserInfo((long)0);

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

	@Override
	public void afterGetUserInfo(User user) {
		mUserSnippetTextView.setText(user.getStatus());
		mUserTextView.setText(user.getUserName());
		mProgressBar.setVisibility(View.GONE);
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
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (item == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, ""), SELECT_FILE);
                } else if (item == 2) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
        	
            if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String picturePath = getPath(selectedImageUri, this.getActivity());
                Bitmap bm = BitmapFactory.decodeFile(picturePath);
                mUserImage.setImageBitmap(bm);
                
                // Get data
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                
                int maxWidth = 1024;
                double aspectRatio = (double) bm.getHeight() / (double) bm.getWidth();
                int targetHeight = (int) (maxWidth * aspectRatio);
                
                bm = Bitmap.createScaledBitmap(bm, maxWidth, targetHeight, true);
                bm.compress(CompressFormat.JPEG, 85, bos);
                pictureData = bos.toByteArray();
                
            } else if (requestCode == REQUEST_CAMERA) {
            	
                Bundle extras = data.getExtras();
                Bitmap bm = (Bitmap) extras.get("data");
                mUserImage.setImageBitmap(bm);

                // Get data
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bm.compress(CompressFormat.JPEG, 75, bos);
                pictureData = bos.toByteArray();
            }
        }
    }
	
	public String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaColumns.DATA };
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
