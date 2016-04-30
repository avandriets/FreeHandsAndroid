package com.usefulservices.freehands;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.squareup.picasso.Picasso;
import com.usefulservices.freehands.Data.AccountsStore;
import com.usefulservices.freehands.Data.DbInstance;
import com.usefulservices.freehands.Utils.MyGoogleApiClient_Singleton;
import com.usefulservices.freehands.Utils.OnGetGoogleTokenTaskCompleted;
import com.usefulservices.freehands.Utils.Utility;
import com.usefulservices.freehands.gcm.MyDevice;
import com.usefulservices.freehands.gcm.RegistrationIntentService;
import com.usefulservices.freehands.sync.FreeHandsSyncAdapter;

import java.sql.SQLException;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnGetGoogleTokenTaskCompleted {

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private MyGoogleApiClient_Singleton instance    = null;
    private DbInstance dbInstance  = null;

    private ProgressDialog progressDialog;

    public static boolean           mGoogleTokenWasGet;
    public static boolean           mExchangeTokenWasGet;

    private static final String KEY_TOKEN_WS_GET    = "KEY_TOKEN_WAS_GET";
    private static final java.lang.String EXCHANGE_TOKEN_WS_GET = "Key_exchange_token_get";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final int RC_SIGN_IN_ACTIVITY     = 200;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbInstance = new DbInstance();
        dbInstance.SetDBHelper(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Utility.mClientOkHttp = new OkHttpClient();

        progressDialog = new ProgressDialog(this, R.style.CustomProgress);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.login_label));

        mGoogleTokenWasGet = false;
        if (savedInstanceState != null) {
            mGoogleTokenWasGet = savedInstanceState.getBoolean(KEY_TOKEN_WS_GET);
        }

        mExchangeTokenWasGet = false;
        if (savedInstanceState != null) {
            mExchangeTokenWasGet = savedInstanceState.getBoolean(EXCHANGE_TOKEN_WS_GET);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FreeHandsSyncAdapter.initializeSyncAdapter(this);

        if (checkPlayServices()) {
            // Because this is the initial creation of the app, we'll want to be certain we have
            // a token. If we do not, then we will start the IntentService that will register this
            // application with GCM.
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            boolean sentToken = sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER, false);
            if (!sentToken) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }

        //Init Google Api Client
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(Utility.CLIENT_ID_SERVER)
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN), new Scope(Scopes.PLUS_ME))
                .requestServerAuthCode(Utility.Get_Client_ID_Server(this), false)
                .build();

        instance = new MyGoogleApiClient_Singleton();

        if(instance.get_GoogleApiClient() == null){

            GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addConnectionCallbacks(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .addApi(LocationServices.API)
                    .build();

            instance = MyGoogleApiClient_Singleton.getInstance(mGoogleApiClient);
        }

        AccountsStore acc = AccountsStore.getActiveUser();

        if(acc == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, RC_SIGN_IN_ACTIVITY);
        }else{
            if(!mExchangeTokenWasGet && Utility.isNetworkAvailable(this)) {
                GetAccessToken();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupDrawerContent(navigationView, acc);
    }

    private void setupDrawerContent(NavigationView navigationView, AccountsStore acc) {

        if (acc != null) {

            View headerLayout = navigationView.getHeaderView(0);

            ImageView imageView = (ImageView) headerLayout.findViewById(R.id.imageUserAvatar);
            TextView tvUserName = (TextView) headerLayout.findViewById(R.id.idUserName);
            TextView tvUserEmail = (TextView) headerLayout.findViewById(R.id.idUserEmail);

            tvUserName.setText(acc.getUserDisplayName());
            tvUserEmail.setText(acc.getEmail());

            String full_photo_url = acc.getPhotoUrl();
            if (full_photo_url != null && !full_photo_url.isEmpty())
                Picasso.with(this).load(full_photo_url).placeholder(R.drawable.ic_account_circle_white_24dp).error(R.drawable.ic_account_circle_white_24dp).into(imageView);
        }
    }

    private boolean checkPlayServices() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(LOG_TAG, "This device is not supported.");
                Toast.makeText(MainActivity.this, R.string.device_not_supported_message, Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    public void GetAccessToken(){

        AccountsStore activeUser = AccountsStore.getActiveUser();

        if(activeUser != null) {
            if(activeUser.getProvider() == AccountsStore.OAuthProviders.google){

                progressDialog.show();

                new GetUserTokenTask(this, activeUser.getEmail(), Utility.GOOGLE_SCOPE, this).execute();
            }
        }
    }

    public void handleException(final Exception e) {
        // Because this call comes from the AsyncTask, we must ensure that the following
        // code instead executes on the UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(progressDialog.isShowing())
                    progressDialog.dismiss();

                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException) e).getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode, MainActivity.this, Utility.REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    startActivityForResult(intent, Utility.REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGetGoogleTokenTaskCompleted(Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean obtainGoogleToken = sharedPreferences.getBoolean(Utility.GOOGLE_TOKEN_WAS_GOT, false);

        if(obtainGoogleToken) {
            mGoogleTokenWasGet = true;
        }

        if(intent.hasExtra(GetUserTokenTask.KEY_ERROR_MESSAGE_GET_TOKEN)){
            Toast.makeText(this, intent.getStringExtra(GetUserTokenTask.KEY_ERROR_MESSAGE_GET_TOKEN), Toast.LENGTH_SHORT).show();
        }else if(!intent.hasExtra(Utility.KEY_ACCESS_TOKEN)){
            Toast.makeText(this, R.string.error_access_message, Toast.LENGTH_SHORT).show();
        }else if(intent.hasExtra(Utility.KEY_ACCESS_TOKEN)){
            mExchangeTokenWasGet = true;
            SyncDataImmediately();

            AccountsStore acc = AccountsStore.getActiveUser();
            setupDrawerContent(navigationView, acc);
        }

        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void SyncDataImmediately() {
        FreeHandsSyncAdapter.syncImmediately(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        instance.get_GoogleApiClient().connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(LOG_TAG, "On destroy helper && GApiClient.");
        if (dbInstance.getDatabaseHelper() != null) {
            OpenHelperManager.releaseHelper();

            dbInstance.releaseHelper();
        }

        if (instance.get_GoogleApiClient() != null && instance.get_GoogleApiClient().isConnected()) {
            instance.get_GoogleApiClient().disconnect();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_ACTIVITY || resultCode == Utility.REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR) {
            if(resultCode == RESULT_OK) {
                GetAccessToken();

                if (checkPlayServices()) {
                    // Because this is the initial creation of the app, we'll want to be certain we have
                    // a token. If we do not, then we will start the IntentService that will register this
                    // application with GCM.
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

                    boolean sentToken = sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER, false);
                    if (!sentToken) {
                        Intent intent = new Intent(this, RegistrationIntentService.class);
                        startService(intent);
                    }
                }
            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_carrier) {
            // Handle the camera action
        } else if (id == R.id.nav_customer) {

        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            SignOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Connection failed");

        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "onConnected");
        MyGoogleApiClient_Singleton.setmLastLocation(LocationServices.FusedLocationApi.getLastLocation(instance.get_GoogleApiClient()));

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "onConnectionSuspended");
        instance.get_GoogleApiClient().connect();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(KEY_TOKEN_WS_GET, mGoogleTokenWasGet);
        outState.putBoolean(EXCHANGE_TOKEN_WS_GET, mExchangeTokenWasGet);
    }

    private void SignOut() {

        if(instance.get_GoogleApiClient().isConnected()) {
            Auth.GoogleSignInApi.signOut(instance.get_GoogleApiClient()).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Log.d(LOG_TAG, "Sign out " + status.toString());

                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

                            mGoogleTokenWasGet = false;
                            mExchangeTokenWasGet = false;

                            RuntimeExceptionDao<AccountsStore, String> dao  = dbInstance.getDatabaseHelper().getAccountsDataDao();

                            AccountsStore acc = AccountsStore.getActiveUser();

                            final String token = acc.getMy_server_access_token();
                            if (acc != null) {

                                //TODO unregister when user sign out
                                new Thread(new Runnable() {
                                    public void run() {
                                        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                                        MyDevice.unregister(deviceID, token);
                                    }
                                }).start();

                                //Clear data
                                ConnectionSource connectionSource = dbInstance.getDatabaseHelper().getConnectionSource();
                                //TODO Add deleting data from tables
//                                try {
//
//
//                                } catch (SQLException e) {
//                                    e.printStackTrace();
//                                }

                                sharedPreferences.edit().putBoolean(MainActivity.SENT_TOKEN_TO_SERVER, false).apply();

                                sharedPreferences.edit().putBoolean(Utility.EXCHANGE_TOKEN_WAS_GOT, false).apply();
                                sharedPreferences.edit().putBoolean(Utility.GOOGLE_TOKEN_WAS_GOT, false).apply();

                                acc.setActive(0);
                                dao.update(acc);
                            }

                            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(loginIntent, RC_SIGN_IN_ACTIVITY);
                        }
                    });
        }

    }

}
