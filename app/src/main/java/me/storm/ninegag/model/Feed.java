package me.storm.ninegag.model;

import android.database.Cursor;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import me.storm.ninegag.dao.FeedsDataHelper;
import me.storm.ninegag.gen.DataDao;

/**
 * Created by storm on 14-3-25.
 */
public class Feed extends BaseModel implements Serializable {
    private static final HashMap<Long, Feed> CACHE = new HashMap<>();

    public long id;
    public String image_url;
    public String abs;

    private static void addToCache(Feed feed) {
        CACHE.put(feed.id, feed);
    }

    private static Feed getFromCache(long id) {
        return CACHE.get(id);
    }

    public static Feed fromJson(String json) {
        return new Gson().fromJson(json, Feed.class);
    }

    //先查询缓存,如果缓存没有,则在数据库中寻找
    public static Feed fromCursor(Cursor cursor) {
        long id = cursor.getLong(DataDao.Properties.ImageID.ordinal);
        Feed feed = getFromCache(id);
        if (feed != null) {
            return feed;
        }
        feed = new Gson().fromJson(
                cursor.getString(DataDao.Properties.Json.ordinal),
                Feed.class);
        addToCache(feed);
        return feed;
    }

    public static Feed fromFeeds(Data data) {
        long id = data.getImageID();
        Feed feed = getFromCache(id);
        if (feed != null) {
            return feed;
        }
        feed = new Gson().fromJson(data.getJson(), Feed.class);
        addToCache(feed);
        return feed;
    }

    public class FeedRequestData implements Serializable {
        public ArrayList<Feed> data;
        public int start_index;

        public int getPage() {
            return start_index;
        }
    }

}
