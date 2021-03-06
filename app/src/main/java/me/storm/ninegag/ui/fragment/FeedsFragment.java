package me.storm.ninegag.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.storm.ninegag.App;
import me.storm.ninegag.R;
import me.storm.ninegag.api.GagApi;
import me.storm.ninegag.dao.GreenDaoHelper;
import me.storm.ninegag.data.GsonRequest;
import me.storm.ninegag.model.Category;
import me.storm.ninegag.model.Feed;
import me.storm.ninegag.ui.ImageViewActivity;
import me.storm.ninegag.ui.adapter.CardsAnimationAdapter;
import me.storm.ninegag.ui.adapter.FeedsAdapter;
import me.storm.ninegag.ui.provider.MyRefreshProvider;
import me.storm.ninegag.util.TaskUtils;
import me.storm.ninegag.util.ToastUtils;
import me.storm.ninegag.view.LoadingFooter;
import me.storm.ninegag.view.OnLoadNextListener;
import me.storm.ninegag.view.PageStaggeredGridView;

/**
 * Created by storm on 14-3-25.
 */
public class FeedsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {
    public static final String EXTRA_CATEGORY = "extra_category";

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeLayout;

    @InjectView(R.id.grid_view)
    PageStaggeredGridView gridView;

    private MenuItem mRefreshItem;

    private Category mCategory;
    //    private FeedsDataHelper mDataHelper;
    private GreenDaoHelper mGreenDaoHelper;
    private FeedsAdapter mAdapter;
    private Animation mAnimation;
    private long mPage = 0;

    //返回feedfragment的实例
    public static FeedsFragment newInstance(Category category) {
        FeedsFragment fragment = new FeedsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_CATEGORY, category.name());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        parseArgument();
        mGreenDaoHelper = new GreenDaoHelper(getContext(), mCategory);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.inject(this, contentView);

//        mDataHelper = new FeedsDataHelper(App.getContext(), mCategory);
        mAdapter = new FeedsAdapter(getActivity(), gridView);
        View header = new View(getActivity());
        gridView.addHeaderView(header);
        AnimationAdapter animationAdapter = new CardsAnimationAdapter(mAdapter);
        animationAdapter.setAbsListView(gridView);
        gridView.setAdapter(animationAdapter);
        //将loadNextListener传递给pageStaggeredGridView
        gridView.setLoadNextListener(new OnLoadNextListener() {
            @Override
            public void onLoadNext() {
                Log.i("majun", "loadNext");
                loadNext();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imageUrl = mAdapter.getItem(position - gridView.getHeaderViewsCount()).image_url;
                Intent intent = new Intent(getActivity(), ImageViewActivity.class);
                intent.putExtra(ImageViewActivity.IMAGE_URL, imageUrl);
                startActivity(intent);
            }
        });

        initActionBar();
        //重新加载数据
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeColors(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

            loadFirst();
        return contentView;
    }


    private void initActionBar() {
        mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.refresh_rotate);
        mAnimation.setInterpolator(new LinearInterpolator());
        //点击返回第一个item
//        View actionBarContainer = ActionBarUtils.findActionBarContainer(getActivity());
//        actionBarContainer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                ListViewUtils.smoothScrollListViewToTop(gridView);
//                gridView.smoothScrollToPositionFromTop(0, 0);
//            }
//        });
    }


    //根据数据库保存的category类型,设定mCategory
    private void parseArgument() {
        Bundle bundle = getArguments();
        mCategory = Category.valueOf(bundle.getString(EXTRA_CATEGORY));
    }

    private void loadData(long next) {
        if (!mSwipeLayout.isRefreshing() && (next == 0)) {
            setRefreshing(true);
        }
        String name;
        String url = null;
        try {
            name = URLEncoder.encode(mCategory.getDisplayName(), "utf-8");
            url = String.format(Locale.getDefault(), GagApi.LIST, next) + "%E5%85%A8%E9%83%A8&tag1=" + name;
        } catch (Exception e) {
            Log.e("majun", e.toString());
        }
        Log.i("majun", "url:" + url);
        executeRequest(new GsonRequest(url, Feed.FeedRequestData.class, responseListener(), errorListener()));
    }

    private Response.Listener<Feed.FeedRequestData> responseListener() {
        final boolean isRefreshFromTop = (mPage == 0);
        return new Response.Listener<Feed.FeedRequestData>() {
            @Override
            public void onResponse(final Feed.FeedRequestData response) {
                TaskUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        if (isRefreshFromTop) {
                            mGreenDaoHelper.deleteAll();
//                            mDataHelper.deleteAll();
                        }
                        mPage = response.getPage() + 1;
                        List<Feed> feeds = new ArrayList<>();
                        for (Feed feed : response.data) {
                            if (!TextUtils.isEmpty(feed.image_url)) {
                                feeds.add(feed);
                            }
                        }
                        mGreenDaoHelper.bulkInsert(feeds);
//                        mDataHelper.bulkInsert(feeds);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        //// TODO: 17/1/5 这边可以加一个判断,判断是否是加载的最后一项.
                        if (isRefreshFromTop) {
                            setRefreshing(false);
                        } else {
                            gridView.setState(LoadingFooter.State.Idle);
                        }
                    }
                });
            }
        };
    }

    protected Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("request", error.toString());
                ToastUtils.showShort(R.string.loading_failed);
                setRefreshing(false);
                gridView.setState(LoadingFooter.State.Idle, 3000);
            }
        };
    }

    private void loadFirst() {
        mPage = 0;
        loadData(mPage);
    }

    private void loadNext() {
        loadData(mPage);
    }

    public void loadFirstAndScrollToTop() {
        gridView.resetToTop();
        loadFirst();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mGreenDaoHelper.getCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);//将swapCursor分装
        if (data == null && data.getCount() == 0) {
            Log.i("majun", "loadfirst");
            loadFirst();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    //SwipeRefreshLayout.OnRefreshListener的接口
    @Override
    public void onRefresh() {
        loadFirst();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mRefreshItem = menu.findItem(R.id.action_refresh);
        MyRefreshProvider myRefreshProvider = new MyRefreshProvider(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFirstAndScrollToTop();
            }
        });
        MenuItemCompat.setActionProvider(mRefreshItem, myRefreshProvider);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setRefreshing(boolean refreshing) {

        mSwipeLayout.setRefreshing(refreshing);
        if (mRefreshItem == null) return;

        MyRefreshProvider myRefreshProvider = (MyRefreshProvider) MenuItemCompat.getActionProvider(mRefreshItem);
        if (myRefreshProvider.freshIcon != null) {
            if (refreshing) {
                myRefreshProvider.freshIcon.startAnimation(mAnimation);
            } else {
                myRefreshProvider.freshIcon.clearAnimation();
            }
        }
    }
}
