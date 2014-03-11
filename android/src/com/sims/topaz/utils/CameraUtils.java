package com.sims.topaz.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import eu.janmuller.android.simplecropimage.CropImage;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class CameraUtils {
	public static final int REQUEST_CODE_GALLERY      = 0x1;
	public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
	public static final int REQUEST_CODE_CROP_IMAGE   = 0x3;
	public static File      mFileTemp = null;
	public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
	
	
	
	public static File getTempFile(){
		String state = Environment.getExternalStorageState();			

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
		}
		else {
			mFileTemp = new File(SimsContext.getContext().getFilesDir(), TEMP_PHOTO_FILE_NAME);
		}
		return mFileTemp;

	}

	public static Intent takePicture() {
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		try {
			Uri mImageCaptureUri = null;
			String state = Environment.getExternalStorageState();			



			if (Environment.MEDIA_MOUNTED.equals(state)) {
				mImageCaptureUri = Uri.fromFile(getTempFile());
			}
			else {
				/*
				 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
				 */
				mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
			}	
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
			intent.putExtra("return-data", true);
			intent.setFlags(intent.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
		} catch (ActivityNotFoundException e) {
			DebugUtils.log("cannot take picture"+e);
		}
		return intent;
	}

	public static Intent openGallery() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		photoPickerIntent.setFlags(photoPickerIntent.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
		return photoPickerIntent;
	}
	
	public static  Intent startCropImage(int length, int height, boolean circle) {

        Intent intent = new Intent(SimsContext.getContext(), CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
        intent.putExtra(CropImage.CIRCLE_CROP, circle);
        intent.putExtra(CropImage.ASPECT_X, 1);
        intent.setFlags(intent.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(CropImage.ASPECT_Y, 1);
        return intent;
    }
	

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }
    
    
    @SuppressWarnings("deprecation")
	public static Drawable getDrwableFromBytes(byte[] imageBytes) {
    	  if (imageBytes != null)
    	   return new BitmapDrawable(BitmapFactory.decodeByteArray(imageBytes,
    	     0, imageBytes.length));
    	  else
    	   return null;
    	 }
}
