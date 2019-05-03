package com.philosofy.nvn.philosofy.utils;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.philosofy.nvn.philosofy.database.AppDatabase;
import com.philosofy.nvn.philosofy.database.CrudExecutors;
import com.philosofy.nvn.philosofy.database.Quote;
import com.philosofy.nvn.philosofy.service.FetchQodJobService;
import com.philosofy.nvn.philosofy.service.QodSyncIntentService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SyncUtils {

    private static final String QOD_SYNC_TAG = "qod_sync";

    private static boolean sQodJobInitialized = false;

    private static final int SYNC_INTERVAL_HOURS = 24;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 24;

    synchronized public static void syncQodAndShowNotification(Context context) {

        try {
            ArrayList<String> categories = PreferencesUtils.getPreferredQodCategories(context);
            if (categories == null || categories.isEmpty()) {
                return;
            }

            String randomCategory = getRandomQodCategory(categories);

            URL url = NetworkUtils.getQodUrlFromCategory(randomCategory);
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);

            Quote quote = NetworkUtils.getQodDataFromJson(jsonResponse);

            AppDatabase db = AppDatabase.getInstance(context);

            db.quotesDao().insertQuote(quote);

            boolean showNotification = PreferencesUtils.areNotificationsEnabled(context);

            if (showNotification) {
                NotificationUtils.showQodNotification(context, quote.getQuote(), quote.getAuthor());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void scheduleSyncQodFirebaseJobDispatcher(Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(driver);

        Job syncQodJob = jobDispatcher.newJobBuilder()
                .setService(FetchQodJobService.class)
                .setTag(QOD_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setReplaceCurrent(true)
                .setTrigger(Trigger.executionWindow(SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
//                .setTrigger(Trigger.executionWindow(20, 60))
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .build();

        jobDispatcher.mustSchedule(syncQodJob);
    }

    public synchronized static void scheduleFetchQodJob(final Context context) {
        if (sQodJobInitialized) {
            return;
        }

        sQodJobInitialized = true;

        scheduleSyncQodFirebaseJobDispatcher(context);

        CrudExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getInstance(context);

                List<Quote> quotesList = db.quotesDao().getQuotesListByType(Constants.QUOTE_DAILY_QODS);

                if (quotesList == null || quotesList.size() == 0) {
                    syncQodNow(context);
                }
            }
        });
    }

    private static void syncQodNow(Context context) {
        Intent intentToSyncNow = new Intent(context, QodSyncIntentService.class);
        context.startService(intentToSyncNow);
    }

    private static String getRandomQodCategory(ArrayList<String> categories) {
        int randomIndex = new Random().nextInt(categories.size());
        return categories.get(randomIndex);
    }

}
