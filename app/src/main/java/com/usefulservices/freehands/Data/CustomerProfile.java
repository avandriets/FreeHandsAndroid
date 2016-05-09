package com.usefulservices.freehands.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;


@DatabaseTable(tableName = "car_types")
public class CustomerProfile {

    public static final String ID               = "_id";
    public static final String FIRST_NAME       = "first_name";
    public static final String LAST_NAME        = "last_name";
    public static final String IS_DRIVER        = "is_driver";
    public static final String CAR_TYPE         = "car_type";
    public static final String USER             = "user";
    public static final String CITY             = "city";
    public static final String CAR_REGISTRATION_NUMBER = "car_registration_number";
    public static final String CAR_MODEL        = "car_model";
    public static final String LENGTH           = "length";
    public static final String CAPACITY         = "capacity";
    public static final String VOLUME           = "volume";
    public static final String HEIGHT           = "height";
    public static final String WIDTH            = "width";

    public static final String CREATED_AT       = "CREATED_AT";
    public static final String UPDATED_AT       = "UPDATED_AT";


    @DatabaseField(id = true, canBeNull = false, columnName = ID)
    @SerializedName(ID)
    @Expose
    private Long id;

    @DatabaseField(canBeNull = false, columnName = FIRST_NAME)
    @SerializedName(FIRST_NAME)
    @Expose
    private String firstName;

    @DatabaseField(canBeNull = true, columnName = LAST_NAME)
    @SerializedName(LAST_NAME)
    @Expose
    private String lastName;

    @DatabaseField(canBeNull = true, columnName = IS_DRIVER)
    @SerializedName(IS_DRIVER)
    @Expose
    private Long isDriver;

    @SerializedName(CAR_TYPE)
    @Expose
    private Long carType_id;

    @DatabaseField(canBeNull = true, columnName = CAR_TYPE, foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3, foreignColumnName = CarTypes.ID)
    private CarTypes carType;

    @DatabaseField(canBeNull = true, columnName = USER)
    @SerializedName(USER)
    @Expose
    private Long user;

    @SerializedName(CITY)
    @Expose
    private String city_id;

    @DatabaseField(canBeNull = true, columnName = CITY, foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3, foreignColumnName = City.ID)
    private City city;

    @DatabaseField(canBeNull = true, columnName = CAR_REGISTRATION_NUMBER)
    @SerializedName(CAR_REGISTRATION_NUMBER)
    @Expose
    private String carRegistrationNumber;

    @DatabaseField(canBeNull = true, columnName = CAR_MODEL)
    @SerializedName(CAR_MODEL)
    @Expose
    private String carModel;

    @DatabaseField(canBeNull = true, columnName = LENGTH)
    @SerializedName(LENGTH)
    @Expose
    private Float length;

    @DatabaseField(canBeNull = true, columnName = WIDTH)
    @SerializedName(WIDTH)
    @Expose
    private Float width;

    @DatabaseField(canBeNull = true, columnName = HEIGHT)
    @SerializedName(HEIGHT)
    @Expose
    private Float height;

    @DatabaseField(canBeNull = true, columnName = VOLUME)
    @SerializedName(VOLUME)
    @Expose
    private Float volume;

    @DatabaseField(canBeNull = true, columnName = CAPACITY)
    @SerializedName(CAPACITY)
    @Expose
    private Float capacity;

    @DatabaseField(canBeNull = true, columnName = UPDATED_AT, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd'T'HH:mm:ssZ")
    @SerializedName("updated_at")
    @Expose
    private Date updatedAt;

    @DatabaseField(canBeNull = true, columnName = CREATED_AT, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd'T'HH:mm:ssZ")
    @SerializedName("created_at")
    @Expose
    private Date createdAt;

    public CustomerProfile(){

    }

}