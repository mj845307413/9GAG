package me.storm.ninegag.dao;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import org.greenrobot.greendao.query.CursorQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import me.storm.ninegag.gen.DaoSession;
import me.storm.ninegag.gen.DataDao;
import me.storm.ninegag.gen.GreenDaoManager;
import me.storm.ninegag.model.Category;
import me.storm.ninegag.model.Data;
import me.storm.ninegag.model.Feed;

/**
 * Created by majun on 17/1/3.
 */
public class GreenDaoHelper {
    private Category mCategory;
    private Context mContext;

    public GreenDaoHelper(Context context, Category category) {
        mCategory = category;
        mContext = context;
    }

    public Feed query(long imageID) {
        Feed feed = null;
        DaoSession session = GreenDaoManager.getInstance().getmDaoSession();
        DataDao feedsDao = session.getDataDao();
        QueryBuilder<Data> builder = feedsDao.queryBuilder();
        builder.where(DataDao.Properties.ImageID.eq(imageID)).orderAsc(DataDao.Properties.ImageID);
        List<Data> list = builder.list();
        CursorQuery cursorQuery = builder.buildCursor();
        Cursor cursor = cursorQuery.query();
        if (cursor.moveToFirst()) {
            feed = Feed.fromCursor(cursor);
        }
        cursor.close();
        return feed;
    }

    public void bulkInsert(List<Feed> feedList) {
        synchronized (DataProvider.DBLock) {
            DaoSession session = GreenDaoManager.getInstance().getmDaoSession();
            DataDao dataDao = session.getDataDao();
            List<Data> datas = new ArrayList<>();
            for (Feed feed : feedList) {
                Data data = new Data();
                data.setImageID(feed.id);
                data.setCategory(mCategory.ordinal());
                data.setJson(feed.toJson());
                datas.add(data);
            }
            dataDao.insertInTx(datas);

        }
    }

    public void deleteAll() {
        synchronized (DataProvider.DBLock) {
            DaoSession session = GreenDaoManager.getInstance().getmDaoSession();
            DataDao dataDao = session.getDataDao();
            dataDao.deleteAll();
        }
    }

    //这边需要再研究研究
    public CursorLoader getCursorLoader() {
        CursorLoader cursorLoader = new CursorLoader(mContext, DataProvider.FEEDS_CONTENT_URI, null, "CATEGORY=?",
                new String[]{
                        String.valueOf(mCategory.ordinal())
                }, DataDao.Properties.ImageID.columnName);
        return cursorLoader;
    }
}
