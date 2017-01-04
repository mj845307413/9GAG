package me.storm.ninegag.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by majun on 17/1/3.
 */
@Entity
public class Data {
    @Property(nameInDb = "_id")
    private long imageID;
    @Property(nameInDb = "CATEGORY")
    private int category;
    @Property(nameInDb = "JSON")
    private String json;
    @Generated(hash = 1937108420)
    public Data(long imageID, int category, String json) {
        this.imageID = imageID;
        this.category = category;
        this.json = json;
    }
    @Generated(hash = 2135787902)
    public Data() {
    }
    public long getImageID() {
        return this.imageID;
    }
    public void setImageID(long imageID) {
        this.imageID = imageID;
    }
    public int getCategory() {
        return this.category;
    }
    public void setCategory(int category) {
        this.category = category;
    }
    public String getJson() {
        return this.json;
    }
    public void setJson(String json) {
        this.json = json;
    }


}
