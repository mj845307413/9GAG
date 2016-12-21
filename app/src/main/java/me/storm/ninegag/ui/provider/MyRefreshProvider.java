package me.storm.ninegag.ui.provider;

import android.content.Context;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import me.storm.ninegag.R;

/**
 * Created by majun on 16/12/21.
 */
public class MyRefreshProvider extends ActionProvider {
    /**
     * Creates a new instance. ActionProvider classes should always implement a
     * constructor that takes a single Context parameter for inflating from menu XML.
     *
     * @param context Context for accessing resources.
     */
    private LayoutInflater mInflater;
    private View.OnClickListener mListener;
    private View mView;
    private View zone;
    public ImageView freshIcon;

    public MyRefreshProvider(Context context, View.OnClickListener listener) {
        super(context);
        mListener = listener;
        mInflater = LayoutInflater.from(context);
        mView = mInflater.inflate(R.layout.provider_refresh, null);
    }

    @Override
    public View onCreateActionView() {
        zone = mView.findViewById(R.id.refresh_zone);
        zone.setOnClickListener(mListener);
        freshIcon = (ImageView) mView.findViewById(R.id.refresh_icon);
        return mView;
    }
}
