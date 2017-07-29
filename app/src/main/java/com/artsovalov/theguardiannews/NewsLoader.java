package com.artsovalov.theguardiannews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private static final String LOG_TAG = NewsLoader.class.getSimpleName();

    private final String mUri;

    public NewsLoader(Context context, String url) {
        super(context);
        mUri = url;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
        Log.i(LOG_TAG, " onStartLoading method");
    }

    @Override
    public List<News> loadInBackground() {
        Log.i(LOG_TAG, " loadInBackground method");

        if (mUri == null) {
            return null;
        }
        return NewsUtils.fetchNewsData(mUri);
    }
}
