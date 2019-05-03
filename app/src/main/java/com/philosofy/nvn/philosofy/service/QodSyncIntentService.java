package com.philosofy.nvn.philosofy.service;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;

import com.philosofy.nvn.philosofy.utils.SyncUtils;

public class QodSyncIntentService extends IntentService {

    public QodSyncIntentService() {
        super("QodSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SyncUtils.syncQodAndShowNotification(this);
    }
}
