package com.usefulservices.freehands.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.usefulservices.freehands.R;
import java.sql.SQLException;


/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "FreeHandsTaxi.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 6;

    // the DAO object we use to access the SimpleData table
    private Dao<AccountsStore, String> AccountsDao = null;
    private RuntimeExceptionDao<AccountsStore, String> AccountRuntimeDao = null;

    private Dao<Country, Long> CountryDao = null;
    private RuntimeExceptionDao<Country, Long> CountryRuntimeDao = null;

    private Dao<City, Long> CityDao = null;
    private RuntimeExceptionDao<City, Long> CityRuntimeDao = null;

    private Dao<CarTypes, Long> CarTypesDao = null;
    private RuntimeExceptionDao<CarTypes, Long> CarTypesRuntimeDao = null;

    private Dao<CustomerProfile, Long> CustomerProfileDao = null;
    private RuntimeExceptionDao<CustomerProfile, Long> CustomerProfileRuntimeDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {

        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, AccountsStore.class);
            TableUtils.createTable(connectionSource, City.class);
            TableUtils.createTable(connectionSource, Country.class);
            TableUtils.createTable(connectionSource, CarTypes.class);
            TableUtils.createTable(connectionSource, CustomerProfile.class);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, AccountsStore.class, true);
            TableUtils.dropTable(connectionSource, City.class, true);
            TableUtils.dropTable(connectionSource, Country.class, true);
            TableUtils.dropTable(connectionSource, CarTypes.class, true);
            TableUtils.dropTable(connectionSource, CustomerProfile.class, true);


            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    public Dao<AccountsStore, String> getAccountDao() throws SQLException {
        if (AccountsDao == null) {
            AccountsDao = getDao(AccountsStore.class);
        }
        return AccountsDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<AccountsStore, String> getAccountsDataDao() {
        if (AccountRuntimeDao == null) {
            AccountRuntimeDao = getRuntimeExceptionDao(AccountsStore.class);
        }
        return AccountRuntimeDao;
    }

    public Dao<Country, Long> getCountryDao() throws SQLException {
        if (CountryDao == null) {
            CountryDao = getDao(Country.class);
        }
        return CountryDao;
    }

    public RuntimeExceptionDao<Country, Long> getCountryDataDao() {
        if (CountryRuntimeDao == null) {
            CountryRuntimeDao = getRuntimeExceptionDao(Country.class);
        }
        return CountryRuntimeDao;
    }

    public Dao<City, Long> getCityDao() throws SQLException {
        if (CityDao == null) {
            CityDao = getDao(City.class);
        }
        return CityDao;
    }

    public RuntimeExceptionDao<City, Long> getCityDataDao() {
        if (CityRuntimeDao == null) {
            CityRuntimeDao = getRuntimeExceptionDao(City.class);
        }
        return CityRuntimeDao;
    }

    public Dao<CarTypes, Long> getCarTypesDao() throws SQLException {
        if (CarTypesDao == null) {
            CarTypesDao = getDao(CarTypes.class);
        }
        return CarTypesDao;
    }

    public RuntimeExceptionDao<CarTypes, Long> getCarTypesDataDao() {
        if (CarTypesRuntimeDao == null) {
            CarTypesRuntimeDao = getRuntimeExceptionDao(CarTypes.class);
        }
        return CarTypesRuntimeDao;
    }

    public Dao<CustomerProfile, Long> getCustomerProfileDao() throws SQLException {
        if (CustomerProfileDao == null) {
            CustomerProfileDao = getDao(CustomerProfile.class);
        }
        return CustomerProfileDao;
    }

    public RuntimeExceptionDao<CustomerProfile, Long> getCustomerProfileDataDao() {
        if (CustomerProfileRuntimeDao == null) {
            CustomerProfileRuntimeDao = getRuntimeExceptionDao(CustomerProfile.class);
        }
        return CustomerProfileRuntimeDao;
    }
    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();

        AccountsDao = null;
        AccountRuntimeDao = null;

        CountryDao = null;
        CountryRuntimeDao = null;

        CityDao = null;
        CityRuntimeDao = null;

        CarTypesDao = null;
        CarTypesRuntimeDao = null;

        CustomerProfileDao = null;
        CustomerProfileRuntimeDao = null;
    }
}
