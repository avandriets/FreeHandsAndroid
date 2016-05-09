package com.usefulservices.freehands.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.usefulservices.freehands.Data.AccountsStore;
import com.usefulservices.freehands.Data.City;
import com.usefulservices.freehands.Data.Country;
import com.usefulservices.freehands.Data.DatabaseHelper;
import com.usefulservices.freehands.Data.DbInstance;
import com.usefulservices.freehands.Data.TaxiService;
import com.usefulservices.freehands.R;
import com.usefulservices.freehands.Utils.Utility;
import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FreeHandsSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = FreeHandsSyncAdapter.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED = "com.digitallifelab.environmentmonitor.ACTION_DATA_UPDATED";
    // Interval at which to sync, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 5;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private Context mContext;

    private DatabaseHelper mDatabaseHelper = null;

    private DatabaseHelper getHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = OpenHelperManager.getHelper(mContext, DatabaseHelper.class);
        }
        return mDatabaseHelper;
    }

    public FreeHandsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d(LOG_TAG, "Starting sync");

        if(!Utility.isNetworkAvailable(getContext())){
            Log.e(LOG_TAG, "No internet connection.");
            return;
        }

        AccountsStore acc = AccountsStore.getActiveUser();


        String authString = "Bearer " + acc.getMy_server_access_token();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        //GET DATA from server
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utility.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        TaxiService service = retrofit.create(TaxiService.class);

        //get open data
        loadCountriesFromServer(service);
        loadCitiesFromServer(service);

        //get closed data
        if(acc != null) {


        }

        if (mDatabaseHelper != null) {
            OpenHelperManager.releaseHelper();
            mDatabaseHelper = null;
        }
    }

    private void loadCountriesFromServer(TaxiService service) {

        Call<List<Country>> retGetCountries = service.getCountry();

        try {
            Response<List<Country>> response = retGetCountries.execute();

            if (response.isSuccessful()) {
                List<Country> countries = response.body();

                Country newCountry = null;
                for (Country country :countries) {
                    newCountry = mDatabaseHelper.getCountryDataDao().createIfNotExists(country);
                }
                Log.d(LOG_TAG, "Get countries successful");
            } else {
                Log.d(LOG_TAG, "Get error.");
            }

        } catch ( Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Get error.");
        }

    }

    private void loadCitiesFromServer(TaxiService service) {

        Call<List<City>> retGetCities = service.getCity();

        try {
            Response<List<City>> response = retGetCities.execute();

            if (response.isSuccessful()) {
                List<City> cities = response.body();

                City newCity = null;
                for (City city :cities) {
                    Country country = mDatabaseHelper.getCountryDataDao().queryForId(city.getCountry_id());
                    city.setCountry(country);
                    newCity = mDatabaseHelper.getCityDataDao().createIfNotExists(city);
                }
                Log.d(LOG_TAG, "Get countries successful");
            } else {
                Log.d(LOG_TAG, "Get error.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Get error. " + e.getMessage());
        }

    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        FreeHandsSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}