package com.usefulservices.freehands.Data;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;


@DatabaseTable(tableName = "country")
public class Country {

    public static final String ID               = "_id";
    public static final String NAME_ENG         = "NAME_ENG";
    public static final String NAME_RUS         = "NAME_RUS";
    public static final String CREATED_AT       = "CREATED_AT";
    public static final String UPDATED_AT       = "UPDATED_AT";

    @Expose
    @DatabaseField(id = true, canBeNull = false, columnName = ID)
    private Long id;

    @Expose
    @DatabaseField(columnName = NAME_ENG)
    private String name_eng;

    @Expose
    @DatabaseField(columnName = NAME_RUS)
    private String name_rus;

    @Expose
    @DatabaseField(canBeNull = true, columnName = CREATED_AT, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date created_at;

    @Expose
    @DatabaseField(canBeNull = true, columnName = UPDATED_AT, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date updated_at;

    @ForeignCollectionField
    private transient ForeignCollection<City> cities;

    public Country(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName_eng() {
        return name_eng;
    }

    public void setName_eng(String name_eng) {
        this.name_eng = name_eng;
    }

    public String getName_rus() {
        return name_rus;
    }

    public void setName_rus(String name_rus) {
        this.name_rus = name_rus;
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
