package com.philosofy.nvn.philosofy.utils;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.core.provider.FontRequest;
import androidx.core.provider.FontsContractCompat;

import com.philosofy.nvn.philosofy.R;

public class FontUtils {

    private static Handler handler = null;

    public static void requestFont(Context context, String fontName,
                                   FontsContractCompat.FontRequestCallback fontRequestCallback) {
        QueryBuilder queryBuilder = new QueryBuilder(fontName);
        String query = queryBuilder.build();

        FontRequest fontRequest = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                query,
                R.array.com_google_android_gms_fonts_certs);

        FontsContractCompat.requestFont(context, fontRequest, fontRequestCallback, getHandlerThreadHandler());
    }

    public static String[] getFontFamilyNamesByLanguage(Context context, String lang) {
        switch (lang) {
            case Constants.LANGUAGE_LATIN:
                return context.getResources().getStringArray(R.array.latin_font_families);
            case Constants.LANGUAGE_DEVANAGARI:
                return context.getResources().getStringArray(R.array.devanagari_font_families);
            case Constants.LANGUAGE_ARABIC:
                return context.getResources().getStringArray(R.array.arabic_font_families);
            case Constants.LANGUAGE_CYRILLIC:
                return context.getResources().getStringArray(R.array.cyrillic_font_families);
            case Constants.LANGUAGE_JAPANESE:
                return context.getResources().getStringArray(R.array.japanese_font_families);
            case Constants.LANGUAGE_KOREAN:
                return context.getResources().getStringArray(R.array.korean_font_families);
            case Constants.LANGUAGE_CHINESE:
                return context.getResources().getStringArray(R.array.chinese_font_families);
            case Constants.LANGUAGE_TAMIL:
                return context.getResources().getStringArray(R.array.tamil_font_families);
            case Constants.LANGUAGE_TELUGU:
                return context.getResources().getStringArray(R.array.telugu_font_families);
            case Constants.LANGUAGE_BENGALI:
                return context.getResources().getStringArray(R.array.bengali_font_families);
            default:
                return context.getResources().getStringArray(R.array.latin_font_families);
        }
    }

    private static Handler getHandlerThreadHandler() {
        if (handler == null) {
            HandlerThread handlerThread = new HandlerThread("fonts");
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        }
        return handler;
    }

    public static String getFontPreviewString(Context context, String lang) {
        String previewString;
        switch (lang) {
            case Constants.LANGUAGE_LATIN:
                previewString = context.getString(R.string.latin_font_preview);
                break;
            case Constants.LANGUAGE_DEVANAGARI:
                previewString = context.getString(R.string.devanagari_font_preview);
                break;
            case Constants.LANGUAGE_ARABIC:
                previewString = context.getString(R.string.arabic_font_preview);
                break;
            case Constants.LANGUAGE_CHINESE:
                previewString = context.getString(R.string.chinese_font_preview);
                break;
            case Constants.LANGUAGE_JAPANESE:
                previewString = context.getString(R.string.japanese_font_preview);
                break;
            case Constants.LANGUAGE_KOREAN:
                previewString = context.getString(R.string.korean_font_preview);
                break;
            case Constants.LANGUAGE_CYRILLIC:
                previewString = context.getString(R.string.cyrillic_font_preview);
                break;
            case Constants.LANGUAGE_TAMIL:
                previewString = context.getString(R.string.tamil_font_preview);
                break;
            case Constants.LANGUAGE_TELUGU:
                previewString = context.getString(R.string.telugu_font_preview);
                break;
            case Constants.LANGUAGE_BENGALI:
                previewString = context.getString(R.string.bengali_font_preview);
                break;
            default:
                previewString = context.getString(R.string.latin_font_preview);
        }

        return previewString;
    }

    public static String getShortFontPreviewString(Context context, String lang) {
        String previewString;
        switch (lang) {
            case Constants.LANGUAGE_LATIN:
                previewString = context.getString(R.string.short_latin_font_preview);
                break;
            case Constants.LANGUAGE_DEVANAGARI:
                previewString = context.getString(R.string.short_devanagari_font_preview);
                break;
            case Constants.LANGUAGE_ARABIC:
                previewString = context.getString(R.string.short_arabic_font_preview);
                break;
            case Constants.LANGUAGE_CHINESE:
                previewString = context.getString(R.string.short_chinese_font_preview);
                break;
            case Constants.LANGUAGE_JAPANESE:
                previewString = context.getString(R.string.short_japanese_font_preview);
                break;
            case Constants.LANGUAGE_KOREAN:
                previewString = context.getString(R.string.short_korean_font_preview);
                break;
            case Constants.LANGUAGE_CYRILLIC:
                previewString = context.getString(R.string.short_cyrillic_font_preview);
                break;
            case Constants.LANGUAGE_TAMIL:
                previewString = context.getString(R.string.short_tamil_font_preview);
                break;
            case Constants.LANGUAGE_TELUGU:
                previewString = context.getString(R.string.short_telugu_font_preview);
                break;
            case Constants.LANGUAGE_BENGALI:
                previewString = context.getString(R.string.short_bengali_font_preview);
                break;
            default:
                previewString = context.getString(R.string.short_latin_font_preview);
        }

        return previewString;
    }

}
