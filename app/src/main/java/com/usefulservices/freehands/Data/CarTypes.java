package com.usefulservices.freehands.Data;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "car_types")
public class CarTypes {
    public static final String ID               = "_id";
    public static final String NAME             = "NAME";
    public static final String CREATED_AT       = "CREATED_AT";
    public static final String UPDATED_AT       = "UPDATED_AT";

    @DatabaseField(id = true, canBeNull = false, columnName = ID)
    private long id;

    @DatabaseField(columnName = NAME)
    private String name;

    @DatabaseField(canBeNull = true, columnName = CREATED_AT, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date created_at;

    @DatabaseField(canBeNull = true, columnName = UPDATED_AT, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date updated_at;

    public CarTypes(){

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
