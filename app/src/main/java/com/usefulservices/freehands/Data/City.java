package com.usefulservices.freehands.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;


@DatabaseTable(tableName = "city")
public class City {

    public static final String ID               = "_id";
    public static final String NAME             = "NAME";
    public static final String COUNTRY_ID       = "COUNTRY_ID";
    public static final String CREATED_AT       = "CREATED_AT";
    public static final String UPDATED_AT       = "UPDATED_AT";

    @Expose
    @DatabaseField(id = true, canBeNull = false, columnName = ID)
    private long id;

    @Expose
    @DatabaseField(columnName = NAME)
    private String name;

    @SerializedName("country_id")
    @Expose
    private Long country_id;

    @DatabaseField(canBeNull = false, columnName = COUNTRY_ID, foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3, foreignColumnName = Country.ID)
    private Country country;

    @Expose
    @DatabaseField(canBeNull = true, columnName = CREATED_AT, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date created_at;

    @Expose
    @DatabaseField(canBeNull = true, columnName = UPDATED_AT, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date updated_at;

    public City(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCountry_id() {
        return country_id;
    }

    public void setCountry_id(Long country_id) {
        this.country_id = country_id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}
