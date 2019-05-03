package com.philosofy.nvn.philosofy.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Toast;

import com.philosofy.nvn.philosofy.R;
import com.philosofy.nvn.philosofy.database.DownloadableImage;
import com.philosofy.nvn.philosofy.database.Quote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetworkUtils {

    // Downloadable Image Params
    private static final String IMAGES_KEY_PARAM = "key";
    private static final String PER_PAGE_PARAM = "per_page";
    private static final String PAGE_PARAM = "page";
    private static final String CATEGORY_PARAM = "category";
    private static final String EDITORS_CHOICE_PARAM = "editors_choice";
    private static final String ORIENTATION_PARAM = "orientation";
    private static final String IMAGE_TYPE_PARAM = "image_type";
    private static final String COLOR_PARAM = "colors";
    private static final String ORDER_PARAM = "order";
    private static final String SAFESEARCH_PARAM = "safesearch";

    private final static String BASE_IMAGES_URL = "https://pixabay.com/api/?";

    // Quote of the day Params
    private static final String QOD_CATEGORY_PARAM = "category";
    private final static String BASE_QOD_URL = "http://quotes.rest/qod.json";

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }

            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

    // Utils for Downloadable Images
    public static List<DownloadableImage>
    getImagesDataFromJson(String jsonString, String category, String color,
                          int pageNo) throws JSONException {

        List<DownloadableImage> imagesData = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonString);

        JSONArray hitsArray = jsonObject.getJSONArray("hits");

        for (int i = 0; i < hitsArray.length(); ++i) {
            JSONObject hit = hitsArray.getJSONObject(i);
            int id = hit.getInt("id");
            String previewUrl = hit.getString("previewURL");
            String largeImageUrl = hit.getString("largeImageURL");
            int views = hit.getInt("views");
            imagesData.add(new DownloadableImage(id, previewUrl, largeImageUrl, category, color, views, pageNo));
        }

        return imagesData;
    }

    public static URL getUrlFromImageCategory(Context context, String category, int pageNo) {
        boolean isSafeSearchOn = PreferencesUtils.isSafeSearchOn(context);
        String preferredOrientation = PreferencesUtils.getPreferredImagesOrientation(context);

        Uri uri = Uri.parse(BASE_IMAGES_URL)
                .buildUpon()
                .appendQueryParameter(IMAGES_KEY_PARAM, context.getString(R.string.pixabay_api_key))
                .appendQueryParameter(PER_PAGE_PARAM, "200")
                .appendQueryParameter(EDITORS_CHOICE_PARAM, "true")
                .appendQueryParameter(IMAGE_TYPE_PARAM, "photo")
                .appendQueryParameter(ORIENTATION_PARAM, preferredOrientation)
                .appendQueryParameter(ORDER_PARAM,  "latest")
                .appendQueryParameter(CATEGORY_PARAM, category.toLowerCase())
                .appendQueryParameter(PAGE_PARAM, String.valueOf(pageNo))
                .appendQueryParameter(SAFESEARCH_PARAM, String.valueOf(isSafeSearchOn))
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    // QOD Utils
    static URL getQodUrlFromCategory(String category) {
        Uri uri = Uri.parse(BASE_QOD_URL)
                .buildUpon()
                .appendQueryParameter(QOD_CATEGORY_PARAM, category)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    static Quote getQodDataFromJson(String json) throws JSONException {

        JSONObject jsonObject = new JSONObject(json);
        JSONObject  contentsObject = jsonObject.getJSONObject("contents");
        JSONArray quoteDataArray = contentsObject.getJSONArray("quotes");

        JSONObject quoteObject = quoteDataArray.getJSONObject(0);

        //String id = quoteObject.getString("id");
        String quote = quoteObject.getString("quote");
        String author = quoteObject.getString("author");
        String category = quoteObject.getString("category");
        if (author == null || author.equalsIgnoreCase("null")) {
            author = "Unknown";
        }

        Date date = new Date();

        return new Quote(quote, author, category, date, Constants.QUOTE_DAILY_QODS);
    }

    // Accessing network state
    public static boolean hasInternetAccess(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;
        if (conMgr != null) {
            networkInfo = conMgr.getActiveNetworkInfo();
        } else {
            Toast.makeText(context, "Error retrieving network state.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return networkInfo != null;
    }
}
