package me.storm.ninegag.ui;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.storm.ninegag.R;
import me.storm.ninegag.model.Category;
import me.storm.ninegag.ui.fragment.BaseFragment;
import me.storm.ninegag.ui.fragment.DrawerFragment;
import me.storm.ninegag.ui.fragment.FeedsFragment;
import me.storm.ninegag.view.BlurFoldingActionBarToggle;
import me.storm.ninegag.view.FoldingDrawerLayout;

/**
 * Created by storm on 14-3-24.
 */
public class MainActivity extends BaseActivity {
    @InjectView(R.id.drawer_layout)
    FoldingDrawerLayout mDrawerLayout;

    @InjectView(R.id.content_frame)
    FrameLayout contentLayout;

    @InjectView(R.id.blur_image)
    ImageView blurImage;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.content_name)
    ShimmerTextView contentName;

    private BlurFoldingActionBarToggle mDrawerToggle;

    private FeedsFragment mContentFragment;

    private Category mCategory;

    private Menu mMenu;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            ButterKnife.inject(this);
//            toolbar.setLogo(R.drawable.ic_actionbar);
//            toolbar.setTitle("majun");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            new Shimmer().start(contentName);
            //设置用于该掩盖的主要内容，而抽屉打开网眼织物的颜色
            mDrawerLayout.setScrimColor(Color.argb(100, 255, 255, 255));
            //与toolbar上面的icon绑定
            mDrawerToggle = new BlurFoldingActionBarToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

                @Override
                //抽屉打开时
                public void onDrawerOpened(View view) {
                    super.onDrawerOpened(view);
                    contentName.setText(R.string.app_name);
                    mMenu.findItem(R.id.action_refresh).setVisible(false);
                }

                @Override
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    contentName.setText(mCategory.getDisplayName());
                    mMenu.findItem(R.id.action_refresh).setVisible(true);

                    blurImage.setVisibility(View.GONE);
                    blurImage.setImageBitmap(null);
                }
            };
            mDrawerToggle.setBlurImageAndView(blurImage, contentLayout);//设置实现毛玻璃效果
            mDrawerLayout.setDrawerListener(mDrawerToggle);

            setCategory(Category.hot);
            replaceFragment(R.id.left_drawer, new DrawerFragment());
        } catch (Exception e) {
            Log.e("majun", e.toString(), e);
        }
    }

    //这里使用toolbar替代actionbar,所以将原来的actionbar的初始化覆盖掉
    @Override
    public void initActionBar() {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    protected void replaceFragment(int viewId, BaseFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(viewId, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //如何让app图标点击的时候能够展开或者隐藏侧边菜单
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setCategory(Category category) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if (mCategory == category) {
            return;
        }
        mCategory = category;
        contentName.setText(mCategory.getDisplayName());
        mContentFragment = FeedsFragment.newInstance(category);
        replaceFragment(R.id.content_frame, mContentFragment);
    }
}
