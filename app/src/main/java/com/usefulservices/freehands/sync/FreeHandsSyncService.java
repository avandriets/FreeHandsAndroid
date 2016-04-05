package com.usefulservices.freehands.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class FreeHandsSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static FreeHandsSyncAdapter sFreeHandsSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("FreeHandsSyncService", "onCreate - FreeHandsSyncService");
        synchronized (sSyncAdapterLock) {
            if (sFreeHandsSyncAdapter == null) {
                sFreeHandsSyncAdapter = new FreeHandsSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sFreeHandsSyncAdapter.getSyncAdapterBinder();
    }
}