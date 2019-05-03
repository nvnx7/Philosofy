package com.philosofy.nvn.philosofy.service;


import android.os.AsyncTask;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.philosofy.nvn.philosofy.utils.SyncUtils;

public class FetchQodJobService extends JobService {
    private static final String TAG = FetchQodJobService.class.getSimpleName();

    AsyncTask<Void, Void, Void> mFetchQodTask;
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        AsyncTask<Void, Void, Void> fetchQodTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                SyncUtils.syncQodAndShowNotification(getApplicationContext());
                jobFinished(jobParameters, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters, false);
            }
        };

        fetchQodTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mFetchQodTask != null) {
            mFetchQodTask.cancel(true);
        }
        return true;
    }
}
