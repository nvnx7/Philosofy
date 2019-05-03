package com.philosofy.nvn.philosofy.service;

import android.app.IntentService;
import android.content.Intent;

import com.philosofy.nvn.philosofy.utils.NotificationUtils;

public class ShareQodIntentService extends IntentService {

    public ShareQodIntentService() {
        super("ShareQodIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            String mimeType = "text/plain";
            String title = "Share Quote of the Day";

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            shareIntent.setType(mimeType);

            startActivity(Intent.createChooser(shareIntent, title));

            NotificationUtils.clearQodNotification(this);
        }
    }
}
