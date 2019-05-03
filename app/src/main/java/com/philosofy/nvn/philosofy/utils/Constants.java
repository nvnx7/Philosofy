package com.philosofy.nvn.philosofy.utils;

public class Constants {

    // MainActivity Constants
    public static final int REQUEST_CODE_CAMERA = 10;
    public static final int REQUEST_CODE_PHOTOS = 11;
    public static final String EXTRA_CAMERA_BUNDLE = "camera_bundle";
    public static final String EXTRA_PHOTOS_URI = "photos_uri";
    public static final String EXTRA_COLOR_CODE = "color_code";

    // EditorActivity Constants
    public static final int DEFAULT_IMAGE_SIZE = 346;
    public static final int MIN_TEXT_SIZE = 4;
    public static final int MIN_SHADOW_RADIUS = 10;
    public static final int MIN_SHADOW_DX = -40;
    public static final int MIN_SHADOW_DY = -30;
    public static final int MIN_IMAGE_SIZE = 24;
    public static final int MIN_IMAGE_OPACITY = 20;

    public static final int REQUEST_CODE_DOWNLOADABLE_FONT = 21;
    public static final int REQUEST_CODE_QUOTE = 22;
    public static final String PICKED_QUOTE = "quote_text";
    public static final String ACTION_QUOTE_REQUEST = "action_quote_request";
    public static final int REQUEST_CODE_BACKGROUND = 23;
    public static final String ACTION_BACKGROUND_REQUEST = "action_background_request";

    //LargeImagePreviewDialogFragment Constants
    public static final String EXTRA_QUOTE_IMAGE = "quote_image";

    //Downloadable Images Categories
    public static final String DOWNLOADED = "downloaded";

    public static final String CATEGORY_NATURE = "nature";
    public static final String CATEGORY_BACKGROUNDS = "backgrounds";
    public static final String CATEGORY_PEOPLE = "people";
    public static final String CATEGORY_FEELINGS = "feelings";
    public static final String CATEGORY_FASHION = "fashion";
    public static final String CATEGORY_TRAVEL = "travel";
    public static final String CATEGORY_PLACES = "places";
    public static final String CATEGORY_FOOD = "food";
    public static final String CATEGORY_HEALTH = "health";
    public static final String CATEGORY_SCIENCE = "science";
    public static final String CATEGORY_EDUCATION = "education";
    public static final String CATEGORY_COMPUTER = "computer";
    public static final String CATEGORY_MUSIC = "music";
    public static final String CATEGORY_SPORTS = "sports";
    public static final String CATEGORY_BUSINESS = "business";
    public static final String CATEGORY_ANIMALS = "animals";
    public static final String CATEGORY_BUILDINGS = "buildings";
    public static final String CATEGORY_INDUSTRY = "industry";

    public static final String KEY_LARGE_IMAGE_URL = "large_image_url";

    //DownloadableFontsActivity Constants
    public static final String PICKED_DOWNLOADABLE_FONT = "downloadable_font";
    public static final String LANGUAGE_LATIN = "latin";
    public static final String LANGUAGE_DEVANAGARI = "devanagari";
    public static final String LANGUAGE_ARABIC = "arabic";
    public static final String LANGUAGE_CHINESE = "chinese";
    public static final String LANGUAGE_JAPANESE = "japanese";
    public static final String LANGUAGE_KOREAN = "korean";
    public static final String LANGUAGE_CYRILLIC = "cyrillic";
    public static final String LANGUAGE_TAMIL = "tamil";
    public static final String LANGUAGE_TELUGU = "telugu";
    public static final String LANGUAGE_BENGALI = "bengali";

    public static final String KEY_BUNDLE_LANGUAGE = "language_key";

    // Favorite font tag constants
    public static final int FAVORITE = 1;
    public static final int NOT_FAVORITE = 0;

    //Quotes type
    public static final String QUOTE_DAILY_QODS = "daily_qods";
    public static final String QUOTE_USER = "user_quotes";

    public static final String KEY_BUNDLE_QUOTE = "quote_type";
    public static final String KEY_BUNDLE_DESIGNED_QUOTE = "designed_quote";

    // Storage constants
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 32;
    public static final int IMAGE_DOWNLOADED = 40;
    public static final int IMAGE_EDITED = 41;
}
