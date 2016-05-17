package com.example.ahsan.uipharmecy.util;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.system.ErrnoException;

import com.example.ahsan.uipharmecy.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharma on 5/17/16.
 */
public class BitmapUtils {

    public static boolean isUriRequiresPermissions(Uri uri, Context context) {
        try {
            ContentResolver resolver = context.getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getRealPathFromURI(Uri contentURI, Context context) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static Bitmap makeBitmap(Uri uri, Context context) {
        Bitmap mBitmap = null;
        try {
            mBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mBitmap;
    }

    public static Uri getPickImageResultUri(Intent data, Context context) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri(context) : data.getData();
    }

    public static Uri getCaptureImageOutputUri(Context context) {
        Uri outputFileUri = null;
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), context.getResources().getString(R.string.image_name)));
        }
        return outputFileUri;
    }

    public static Intent getPickImageChooserIntent(Context context) {
        Uri outputFileUri = BitmapUtils.getCaptureImageOutputUri(context);
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType(context.getResources().getString(R.string.type_image));
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals(Constant.DOCUMENTS_ACTIVITY)) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);
        Intent chooserIntent = Intent.createChooser(mainIntent, context.getResources().getString(R.string.select_source));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
        return chooserIntent;
    }
}
