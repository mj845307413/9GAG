package me.storm.ninegag.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by storm on 14-3-25.
 */
public abstract class BaseModel {
    public String toJson() {
        return new Gson().toJson(this);
    }
}
