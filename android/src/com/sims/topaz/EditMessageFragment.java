package com.sims.topaz;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.sims.topaz.network.NetworkRestModule;
import com.sims.topaz.network.interfaces.ErreurDelegate;
import com.sims.topaz.network.interfaces.MessageDelegate;
import com.sims.topaz.network.interfaces.PictureUploadDelegate;
import com.sims.topaz.network.modele.ApiError;
import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;
import com.sims.topaz.utils.AuthUtils;
import com.sims.topaz.utils.CameraUtils;
import com.sims.topaz.utils.DebugUtils;
import com.sims.topaz.utils.MyPreferencesUtilsSingleton;
import com.sims.topaz.utils.MyTypefaceSingleton;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class EditMessageFragment extends Fragment
implements MessageDelegate,PictureUploadDelegate, ErreurDelegate{


	
	private final static int PICTURE_MAX_WIDTH = 1024;
	private final static int PICTURE_QUALITY = 85;
	
	private NetworkRestModule mRestModule = new NetworkRestModule(this);
	private LatLng mPosition;
	private EditText editText;
	private ImageView editImageView;

	OnNewMessageListener mCallback;
	// Container Activity must implement this interface
	public interface OnNewMessageListener {
		public void onNewMessage(Message message);
	}

	private int savedSoftInputMode;
	private String pictureUrl;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnNewMessageListener) activity;
			savedSoftInputMode = activity.getWindow().getAttributes().softInputMode;
			activity.getWindow()
			.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnNewMessageListener");
		}
	}

	/**
	 * Set the callback to null so we don't accidentally leak the 
	 * Activity instance.
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		mCallback = null;
		getActivity().getWindow()
		.setSoftInputMode(savedSoftInputMode);
	}
	public void setPosition(LatLng position) {
		this.mPosition = position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_edit_message, container, false);
		TextView mTextTextView = (TextView) view.findViewById(R.id.edit_message_text);
		mTextTextView.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		editText = (EditText) view.findViewById(R.id.editMessage);
		editText.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		editImageView = (ImageView) view.findViewById(R.id.edit_message_image_view);
		setUpButtons(view);
		pictureUrl = null;
		return view;
	}    

	private void setUpButtons(View view) {
		Button send = (Button) view.findViewById(R.id.button_send_message);
		send.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setEnabled(false);
				closeKeyboard();
				onSendButton(v);
			}
		});
		
		Button cancel = (Button) view.findViewById(R.id.button_cancel_message);
		cancel.setTypeface(MyTypefaceSingleton.getInstance().getTypeFace());
		
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeKeyboard();
				getFragmentManager().popBackStack(MapFragment.FRAGMENT_MESSAGE, 
						FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
		});

		editImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectImage();
			}
		});
		
		
		ProgressBar editMessagePictureLoader = (ProgressBar) view.findViewById(R.id.edit_message_picture_loader);
		editMessagePictureLoader.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resetForm();
				mRestModule.cancelLastTask();
			}
		});
	}

	protected void resetForm() {
		getView().findViewById(R.id.button_send_message).setEnabled(true);
		getView().findViewById(R.id.edit_message_picture_loader).setVisibility(View.GONE);
		editImageView.setImageDrawable(getResources().getDrawable(R.drawable.camera));
		editImageView.setVisibility(View.VISIBLE);
		pictureUrl = null;
	}
	
	protected void closeKeyboard() {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	public void onSendButton(View view) {
		String text = editText.getText().toString();
		// Create the message
		Message message = new Message();
		message.setText(text);
		message.setLongitude(mPosition.longitude);
		message.setLatitude(mPosition.latitude);
		message.setTimestamp(new Date().getTime());
		message.setUserName(AuthUtils.getSessionStringValue
				(MyPreferencesUtilsSingleton.SHARED_PREFERENCES_AUTH_USERNAME));
		message.setPictureUrl(pictureUrl);
		mRestModule.postMessage(message);
	}

	@Override
	public void afterPostMessage(Message message) {
		Toast.makeText(getActivity(), getString(R.string.message_sent), Toast.LENGTH_SHORT).show();
		getFragmentManager().beginTransaction().hide(this).commit();
		mCallback.onNewMessage(message);
		getFragmentManager().popBackStack();
	}

	@Override
	public void afterGetMessage(Message message) {}

	@Override
	public void afterGetPreviews(List<Preview> list) {}

	@Override
	public void networkError() {
		resetForm();
	}

	public void apiError(ApiError error) {
		resetForm();
	}

	private void selectImage() {
        final CharSequence[] items = { getString(R.string.select_img_take_photo), getString(R.string.select_img_from_lib), getString(R.string.select_img_close) };
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle(R.string.edit_add_image);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    startActivityForResult(CameraUtils.takePicture(), CameraUtils.REQUEST_CODE_TAKE_PICTURE);
                } else if (item == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, ""), CameraUtils.REQUEST_CODE_GALLERY);
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
        	
            if (requestCode == CameraUtils.REQUEST_CODE_GALLERY) {
                Uri selectedImageUri = data.getData();
                String picturePath = getPath(selectedImageUri, this.getActivity());
                Bitmap bm = BitmapFactory.decodeFile(picturePath);
                editImageView.setImageBitmap(bm);
                
                // Get data
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                
                if(bm.getHeight() > PICTURE_MAX_WIDTH) {
	                double aspectRatio = (double) bm.getHeight() / (double) bm.getWidth();
	                int targetHeight = (int) (PICTURE_MAX_WIDTH * aspectRatio);
	                bm = Bitmap.createScaledBitmap(bm, PICTURE_MAX_WIDTH, targetHeight, true);
                }
                
                bm.compress(CompressFormat.JPEG, PICTURE_QUALITY, bos);
                mRestModule.uploadPicture(bos.toByteArray());
                
                // Start loader, disable send button
                pictureUrl = null;
                editImageView.setVisibility(View.INVISIBLE);
                getView().findViewById(R.id.edit_message_picture_loader).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.button_send_message).setEnabled(false);
                
            } else if (requestCode == CameraUtils.REQUEST_CODE_TAKE_PICTURE) {
            	
                Bitmap bm = BitmapFactory.decodeFile(CameraUtils.getTempFile().getPath());
                
                DebugUtils.log("getHeight=" + bm.getHeight());
                
                editImageView.setImageBitmap(bm);

                // Get data
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                
                if(bm.getHeight() > PICTURE_MAX_WIDTH) {
	                double aspectRatio = (double) bm.getHeight() / (double) bm.getWidth();
	                int targetHeight = (int) (PICTURE_MAX_WIDTH * aspectRatio);
	                bm = Bitmap.createScaledBitmap(bm, PICTURE_MAX_WIDTH, targetHeight, true);
                }
                
                bm.compress(CompressFormat.JPEG, PICTURE_QUALITY, bos);
                mRestModule.uploadPicture(bos.toByteArray());

                // Start loader, disable send button
                pictureUrl = null;
                editImageView.setVisibility(View.INVISIBLE);
                getView().findViewById(R.id.edit_message_picture_loader).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.button_send_message).setEnabled(false);
                
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

	@Override
	public void afterUploadPicture(String url) {
		DebugUtils.log("afterUploadPicture pictureUrl="+ url);
		pictureUrl = url;
		getView().findViewById(R.id.edit_message_picture_loader).setVisibility(View.GONE);
		getView().findViewById(R.id.button_send_message).setEnabled(true);
		editImageView.setVisibility(View.VISIBLE);
	}

}
