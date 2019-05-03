package com.philosofy.nvn.philosofy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.PixelCopy;
import android.view.View;
import android.view.Window;

import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.IOException;
import java.io.InputStream;

public class BmpUtils {
    private static final String TAG = BmpUtils.class.getSimpleName();

    private static final float MAX_BLUR_RADIUS = 25f;

    public static Bitmap blur(Context context, Bitmap image, int progress) {
        if (image == null) return null;

        // Pass the untouched original image if seek bar progress is 0
        if (progress == 0) return image;

        Bitmap outputBitmap = image.copy(image.getConfig(), true);
        final RenderScript renderScript = RenderScript.create(context);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

        float radius = MAX_BLUR_RADIUS * ((float) progress / 100);

        ScriptIntrinsicBlur intrinsicBlur
                = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        intrinsicBlur.setRadius(radius);
        intrinsicBlur.setInput(tmpIn);
        intrinsicBlur.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        renderScript.destroy();
        return outputBitmap;
    }

    public static PorterDuffColorFilter getDarkColorFilter(int value) {
        return new PorterDuffColorFilter(Color.argb(value, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
    }

    public static ColorMatrixColorFilter getBrightColorFilter(int value) {
        ColorMatrix cmB = new ColorMatrix();
        cmB.set(new float[]{
                1, 0, 0, 0, value,
                0, 1, 0, 0, value,
                0, 0, 1, 0, value,
                0, 0, 0, 1, 0});

        return new ColorMatrixColorFilter(cmB);
    }


    public static Filter getFilterByName(Context context, String filterName) {
        Filter filter = FilterPack.getAweStruckVibeFilter(context);
        switch (filterName) {
            case "Struck":
                filter = FilterPack.getAweStruckVibeFilter(context);
                break;

            case "Clarendon":
                filter = FilterPack.getClarendon(context);
                break;

            case "OldMan":
                filter = FilterPack.getOldManFilter(context);
                break;

            case "Mars":
                filter = FilterPack.getMarsFilter(context);
                break;

            case "Rise":
                filter = FilterPack.getRiseFilter(context);
                break;

            case "April":
                filter = FilterPack.getAprilFilter(context);
                break;

            case "Amazon":
                filter = FilterPack.getAmazonFilter(context);
                break;

            case "Starlit":
                filter = FilterPack.getStarLitFilter(context);
                break;

            case "Whisper":
                filter = FilterPack.getNightWhisperFilter(context);
                break;

            case "Lime":
                filter = FilterPack.getLimeStutterFilter(context);
                break;

            case "Haan":
                filter = FilterPack.getHaanFilter(context);
                break;

            case "BlueMess":
                filter = FilterPack.getBlueMessFilter(context);
                break;

            case "Adele":
                filter = FilterPack.getAdeleFilter(context);
                break;

            case "Cruz":
                filter = FilterPack.getCruzFilter(context);
                break;

            case "Metropolis":
                filter = FilterPack.getMetropolis(context);
                break;

            case "Audrey":
                filter = FilterPack.getAudreyFilter(context);
                break;
        }
        return filter;
    }

    public static Bitmap getBitmapFromView(Window window, View view,
                                           PixelCopy.OnPixelCopyFinishedListener onPixelCopyFinishedListener) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int[] locationOfViewInWindow = new int[2];
            view.getLocationInWindow(locationOfViewInWindow);
            Rect rect = new Rect(locationOfViewInWindow[0], locationOfViewInWindow[1],
                    locationOfViewInWindow[0] + view.getWidth(), locationOfViewInWindow[1] + view.getHeight());
            PixelCopy.request(window, rect, bitmap, onPixelCopyFinishedListener, new Handler());
        }

        return bitmap;
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    public static Bitmap getColoredBitmap(int width, int height, int color) {
        Bitmap colorBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        colorBitmap.eraseColor(color);

        return colorBitmap;
    }

    public static Bitmap getCorrectedOrientationBitmap(Context context, Uri uri) {

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            InputStream stream = context.getContentResolver().openInputStream(uri);

            ExifInterface ei = new ExifInterface(stream);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    rotatedBitmap = bitmap;
                    break;
                default:
                    rotatedBitmap = bitmap;
            }
            return rotatedBitmap;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap rotateImage(Bitmap bitmap, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotatedBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);

        return rotatedBmp;
    }

}
