package com.philosofy.nvn.philosofy.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.philosofy.nvn.philosofy.BuildConfig;
import com.philosofy.nvn.philosofy.R;
import com.philosofy.nvn.philosofy.SettingsActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.SaveSettings;

public class StorageUtils {
    private static final String TAG = StorageUtils.class.getSimpleName();

    private static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isStoragePermissionGranted(Context context) {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        boolean isPermissionGranted
                = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;

        return isPermissionGranted;
    }

    public static void tryRequestStoragePermissionForSaving(Context context) {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
            showExplanationForSavingAlertDialog(context);
        } else {
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    new String[]{permission},
                    Constants.REQUEST_CODE_STORAGE_PERMISSION);
        }
    }

    public static void tryRequestStoragePermissionForSaveAndShare(Context context) {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
            showExplanationForSaveAndShareAlertDialog(context);
        } else {
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    new String[]{permission},
                    Constants.REQUEST_CODE_STORAGE_PERMISSION);
        }
    }

    private static void requestStoragePermission(Context context) {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        ActivityCompat.requestPermissions(
                (Activity) context,
                new String[]{permission},
                Constants.REQUEST_CODE_STORAGE_PERMISSION);
    }

    private static void showExplanationForSavingAlertDialog(final Context context) {
        final AlertDialog explanationDialog = new AlertDialog.Builder(context)
                .setTitle("Storage Permissions")
                .setIcon(R.drawable.ic_info)
                .setMessage(context.getString(R.string.explanation_saving_storage_permission))
                .create();

        explanationDialog.setButton(DialogInterface.BUTTON_POSITIVE, "RETRY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestStoragePermission(context);
            }
        });

        explanationDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "DENY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                explanationDialog.dismiss();
            }
        });

        explanationDialog.show();
    }

    private static void showExplanationForSaveAndShareAlertDialog(final Context context) {
        final AlertDialog explanationDialog = new AlertDialog.Builder(context)
                .setTitle("Storage Permissions")
                .setIcon(R.drawable.ic_info)
                .setMessage(context.getString(R.string.explanation_save_share_storage_permission))
                .create();

        explanationDialog.setButton(DialogInterface.BUTTON_POSITIVE, "RETRY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestStoragePermission(context);
                dialog.dismiss();
            }
        });

        explanationDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OPEN SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent settingsIntent = new Intent(context, SettingsActivity.class);
                context.startActivity(settingsIntent);
                dialog.dismiss();
            }
        });

        explanationDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "DENY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                explanationDialog.dismiss();
            }
        });

        explanationDialog.show();
    }

    public static void showExplanationForSettingsDialog(final Context context) {
        final AlertDialog explanationDialog = new AlertDialog.Builder(context)
                .setTitle("Storage Permissions")
                .setIcon(R.drawable.ic_info)
                .setMessage(context.getString(R.string.explaination_open_settings))
                .create();

        explanationDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OPEN SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openSettingsForPermission(context);
            }
        });

        explanationDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "DENY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                explanationDialog.dismiss();
            }
        });

        explanationDialog.show();
    }

    private static void openSettingsForPermission(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(intent);
    }

    public static File getStoragePath(int type) {
        String dirName = "Philosofy";
        String subDirName;
        if (type == Constants.IMAGE_EDITED) {
            subDirName = "Quotes";
        } else {
            subDirName = "Downloaded";
        }

        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + dirName + File.separator + subDirName);

        return file;
    }

    public static File getANewImageFile(int type) {
        String dirName = "Philosofy";
        String subDirName;
        if (type == Constants.IMAGE_EDITED) {
            subDirName = "Quotes";
        } else {
            subDirName = "Downloaded";
        }

        String fileName = "Philosofy_" + System.currentTimeMillis() + ".png";

        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + dirName + File.separator + subDirName);

        if (!file.exists()) {
            Log.i(TAG, "Folder doesn't already exist");
            boolean isFileCreated = file.mkdirs();
            if (!isFileCreated) {
                return null;
            }
        }

        return new File(file.getAbsolutePath() + File.separator + fileName);
    }

    public static File getANewTemporaryImageFile(Context context, String filename) {
        File tempImageFile;
        try {
            File cacheFile = new File(context.getCacheDir(), "images");
            cacheFile.mkdirs();
            tempImageFile = File.createTempFile(filename, ".png", cacheFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return tempImageFile;
    }

    public static File saveBitmap(Context context, PhotoEditor photoEditor, PhotoEditor.OnSaveListener onSaveListener) {
        if (!StorageUtils.isExternalStorageAvailable()) {
            Toast.makeText(context, "External Storage Unavailable!", Toast.LENGTH_SHORT).show();
            return null;
        }

        File file = StorageUtils.getANewImageFile(Constants.IMAGE_EDITED);
        if (file == null) {
            Toast.makeText(context, "Error creating file!", Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            file.createNewFile();

            SaveSettings saveSettings = new SaveSettings.Builder()
                    .setClearViewsEnabled(false)
                    .setTransparencyEnabled(false)
                    .build();

            photoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, onSaveListener);
        } catch (SecurityException | IOException e) {
            Log.i(TAG, "Exception occurred: " + e.toString());
            e.printStackTrace();
        }

        return file;
    }

    public static void shareSavedBitmap(Context context, File file) {
        Uri uri = FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".provider",
                file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("image/png");
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public static void shareUnsavedBitmap(Context context, Bitmap bitmap) {
        try {
            File cacheFile = new File(context.getCacheDir(), "images");
            cacheFile.mkdirs();
            FileOutputStream outputStream = new FileOutputStream(cacheFile + "/image.png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File imagePath = new File(context.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",
                newFile);

        if (uri != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, context.getContentResolver().getType(uri));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(Intent.createChooser(intent, "Share via"));
        }
    }
}
