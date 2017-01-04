package me.storm.ninegag.ui.adapter;

import android.content.Context;
import android.content.Entity;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.etsy.android.grid.StaggeredGridView;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.storm.ninegag.R;
import me.storm.ninegag.dao.FeedsDataHelper;
import me.storm.ninegag.data.ImageCacheManager;
import me.storm.ninegag.gen.DataDao;
import me.storm.ninegag.model.Feed;
import me.storm.ninegag.util.DensityUtils;


/**
 * Created by storm on 14-3-26.
 */
public class FeedsAdapter extends CursorAdapter {
    private static final int[] COLORS = {R.color.holo_blue_light, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_purple_light, R.color.holo_red_light};

    private static final int IMAGE_MAX_HEIGHT = 240;

    private LayoutInflater mLayoutInflater;

    private StaggeredGridView mListView;

    private Drawable mDefaultImageDrawable;

    private Resources mResource;

    public FeedsAdapter(Context context, StaggeredGridView listView) {
        super(context, null, false);
        mResource = context.getResources();
        mLayoutInflater = LayoutInflater.from(context);
        mListView = listView;
    }

    @Override
    public Feed getItem(int position) {
        mCursor.moveToPosition(position);
        return Feed.fromCursor(mCursor);
    }

    //并不是每次都被调用的，它只在实例化的时候调用,数据增加的时候也会调用,但是在重绘(比如修改条目里的TextView的内容)的时候不会被调用
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mLayoutInflater.inflate(R.layout.listitem_feed, null);
    }

    //从代码中可以看出在绘制Item之前一定会调用bindView方法它在重绘的时候也同样被调用
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Holder holder = getHolder(view);
        if (holder.imageRequest != null) {
            holder.imageRequest.cancelRequest();
        }

        view.setEnabled(!mListView.isItemChecked(cursor.getPosition()
                + mListView.getHeaderViewsCount()));

        Feed feed = Feed.fromCursor(cursor);
        mDefaultImageDrawable = new ColorDrawable(mResource.getColor(COLORS[cursor.getPosition() % COLORS.length]));
        holder.imageRequest = ImageCacheManager.loadImage(feed.image_url, ImageCacheManager
                .getImageListener(holder.image, mDefaultImageDrawable, mDefaultImageDrawable), 0, DensityUtils.dip2px(context, IMAGE_MAX_HEIGHT));
        try {
            String s = URLDecoder.decode(feed.abs, "utf-8");
            holder.caption.setText(s);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Holder getHolder(final View view) {
        Holder holder = (Holder) view.getTag();
        if (holder == null) {
            holder = new Holder(view);
            view.setTag(holder);
        }
        return holder;
    }

    static class Holder {
        @InjectView(R.id.iv_normal)
        ImageView image;

        @InjectView(R.id.tv_caption)
        TextView caption;

        public ImageLoader.ImageContainer imageRequest;

        public Holder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
