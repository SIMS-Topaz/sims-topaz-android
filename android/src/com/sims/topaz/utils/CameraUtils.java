package com.sims.topaz.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import eu.janmuller.android.simplecropimage.CropImage;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
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
	
	public static void removeTempFile(){
		if(mFileTemp!=null)	mFileTemp.delete();
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
		intent.putExtra(CropImage.ASPECT_X, 1);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

	public static byte[] getBytesFromDrawable(Drawable d){
		if(d!=null){
			Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			return stream.toByteArray();
		}
		else{
			return null;
		}
	}
	
	public static Bitmap imgRotation(String fileName) {

		Bitmap bm = BitmapFactory.decodeFile(fileName);
		
		try {
			ExifInterface exif = new ExifInterface(fileName);
	        DebugUtils.log("EXIF TAG_ORIENTATION value = " + exif.getAttribute(ExifInterface.TAG_ORIENTATION));
	        Matrix matrix = new Matrix();
			if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
				matrix.postRotate(90);
	        }else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
	        	matrix.postRotate(270);
	        }else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
	        	matrix.postRotate(180);
	        }
			bm = Bitmap.createBitmap(bm , 0, 0, bm .getWidth(), bm .getHeight(), matrix, true);
		} catch (IOException e) {
			DebugUtils.log("ExifInterface Error "+ e);
		}

        return bm;
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
}
