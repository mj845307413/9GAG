package me.storm.ninegag.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
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

    private ContentValues getContentValues(Feed feed) {
        ContentValues values = new ContentValues();
        values.put(DataDao.Properties.ImageID.columnName, feed.id);
        values.put(DataDao.Properties.Category.columnName, mCategory.ordinal());
        values.put(DataDao.Properties.Json.columnName, feed.toJson());
        return values;
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

    //与contentprovider配合使用不方便用greendao的方法
    public void bulkInsert(List<Feed> feedList) {
        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
        for (Feed feed : feedList) {
            ContentValues values = getContentValues(feed);
            contentValues.add(values);
        }
        ContentValues[] valueArray = new ContentValues[contentValues.size()];
        mContext.getContentResolver().bulkInsert(DataProvider.FEEDS_CONTENT_URI, contentValues.toArray(valueArray));
//
//
//        synchronized (DataProvider.DBLock) {
//            DaoSession session = GreenDaoManager.getInstance().getmDaoSession();
//            DataDao dataDao = session.getDataDao();
//            List<Data> datas = new ArrayList<>();
//            for (Feed feed : feedList) {
//                Data data = new Data();
//                data.setImageID(feed.id);
//                data.setCategory(mCategory.ordinal());
//                data.setJson(feed.toJson());
//                datas.add(data);
//            }
//            dataDao.insertInTx(datas);
//        }
    }

    public void deleteAll() {
        synchronized (DataProvider.DBLock) {
            mContext.getContentResolver().delete(DataProvider.FEEDS_CONTENT_URI,DataDao.Properties.Category.columnName,new String[]{String.valueOf(mCategory.ordinal())});
//            DaoSession session = GreenDaoManager.getInstance().getmDaoSession();
//            DataDao dataDao = session.getDataDao();
//            QueryBuilder queryBuilder=dataDao.queryBuilder();
//            queryBuilder.where(DataDao.Properties.Category.eq(mCategory.ordinal())).buildDelete();
        }
    }

    //这边需要再研究研究
    public CursorLoader getCursorLoader() {
        CursorLoader cursorLoader = new CursorLoader(mContext, DataProvider.FEEDS_CONTENT_URI, null, "CATEGORY=?",
                new String[]{
                        String.valueOf(mCategory.ordinal())
                }, null);
        return cursorLoader;
    }
}
