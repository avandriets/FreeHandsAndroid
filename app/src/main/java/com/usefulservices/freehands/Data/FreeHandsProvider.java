package com.usefulservices.freehands.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;


public class FreeHandsProvider extends ContentProvider {

    private static final int ORDERS = 100;
    private static final int ORDER_ID = 101;

    private DbInstance dbInstance  = null;
    private static final UriMatcher sUriMatcher = buildUriMatcher();


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FreeHandsContract.CONTENT_AUTHORITY;

        return matcher;
    }

    @Override
    public boolean onCreate() {

        dbInstance = new DbInstance();
        dbInstance.SetDBHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case ORDER_ID:

            case ORDERS:

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ORDERS:
                return FreeHandsContract.ORDERS_CONTENT_TYPE;
            case ORDER_ID:
                return FreeHandsContract.ORDERS_CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
