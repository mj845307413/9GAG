package me.storm.ninegag.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.storm.ninegag.R;
import me.storm.ninegag.ui.fragment.PreferenceFragment;
import me.storm.ninegag.view.swipeback.SwipeBackActivity;

/**
 * Created by storm on 14-4-16.
 */
public class PreferenceActivity extends SwipeBackActivity {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.content_name)
    ShimmerTextView contentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
//        toolbar.setLogo(R.drawable.ic_actionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        new Shimmer().start(contentName);
        contentName.setText(R.string.action_settings);
        getFragmentManager().beginTransaction().replace(R.id.content, new PreferenceFragment())
                .commit();
    }

    @Override
    public void initActionBar() {

    }
}
