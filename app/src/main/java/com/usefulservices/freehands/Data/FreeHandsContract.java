package com.usefulservices.freehands.Data;


import android.content.ContentResolver;
import android.net.Uri;

public class FreeHandsContract {

    public static final String CONTENT_AUTHORITY = "com.usefulservices.freehands";

    public static final String PATH_ORDERS              = "orders";

    public static final String ORDERS_CONTENT_TYPE         = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ORDERS;
    public static final String ORDERS_CONTENT_ITEM_TYPE    = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ORDERS;

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final Uri POINTS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ORDERS).build();

}
