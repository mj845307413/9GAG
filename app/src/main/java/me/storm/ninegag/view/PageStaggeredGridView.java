package me.storm.ninegag.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;

import com.etsy.android.grid.StaggeredGridView;

/**
 * Created by storm on 14-5-6.
 */
public class PageStaggeredGridView extends StaggeredGridView implements AbsListView.OnScrollListener {
    private LoadingFooter mLoadingFooter;//底部的加载loading

    private OnLoadNextListener mLoadNextListener;

    public PageStaggeredGridView(Context context) {
        super(context);
        init();
    }

    public PageStaggeredGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PageStaggeredGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mLoadingFooter = new LoadingFooter(getContext());
        //加入底部的footer
        addFooterView(mLoadingFooter.getView());
        setOnScrollListener(this);
    }

    public void setLoadNextListener(OnLoadNextListener listener) {
        mLoadNextListener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        try {
            if (mLoadingFooter.getState() == LoadingFooter.State.Loading
                    || mLoadingFooter.getState() == LoadingFooter.State.TheEnd) {
                return;
            }
            if (firstVisibleItem + visibleItemCount >= totalItemCount
                    && totalItemCount != 0
                    && totalItemCount != getHeaderViewsCount()
                    + getFooterViewsCount() && mLoadNextListener != null) {
                mLoadingFooter.setState(LoadingFooter.State.Loading);
                mLoadNextListener.onLoadNext();
            }
        } catch (Exception e) {
            Log.e("majun", e.toString(), e);
        }

    }

    public void setState(LoadingFooter.State status) {
        mLoadingFooter.setState(status);
    }

    public void setState(LoadingFooter.State status, long delay) {
        mLoadingFooter.setState(status, delay);
    }
}
