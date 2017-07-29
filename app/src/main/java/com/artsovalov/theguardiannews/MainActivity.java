package com.artsovalov.theguardiannews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<News>> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String GUARDIAN_NEWS = "https://content.guardianapis.com/";
    private static final String API_KEY = "test";
    private static final int NEWS_LOADER_ID = 1;

    private NewsRecyclerViewAdapter mAdapter;
    private TextView mNoNewsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNoNewsTextView = (TextView) findViewById(R.id.tvNoNews);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rvNews);

        mAdapter = new NewsRecyclerViewAdapter(this, new ArrayList<News>());

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false);

        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.pbLoadingIndicator);
            loadingIndicator.setVisibility(View.GONE);
            mNoNewsTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        getLoaderManager().destroyLoader(NEWS_LOADER_ID);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, " onCreateLoader callback");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String countryEndpoint = sharedPrefs.getString(getString(R.string.settings_country_key),
                getString(R.string.settings_country_default));

        String pageSize = sharedPrefs.getString(getString(R.string.settings_number_articles_key),
                getString(R.string.settings_number_articles_default));

        // Sets the title based on selected country using helper class
        if (!TextUtils.isEmpty(NewsSettingsActivity.country_label)) {
            setTitle(NewsSettingsActivity.country_label + getString(R.string.space) +
                    getString(R.string.news_text));
        }

        // Making respective url's based on country endpoints
        Uri baseUri = Uri.parse(GUARDIAN_NEWS + countryEndpoint);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        uriBuilder.appendQueryParameter("page-size", pageSize);
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("api-key", API_KEY);
        uriBuilder.appendQueryParameter("orderBy", "newest");

        Log.i(LOG_TAG, "URL: " + uriBuilder.toString());
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        View loadingIndicator = findViewById(R.id.pbLoadingIndicator);
        loadingIndicator.setVisibility(View.GONE);

        mNoNewsTextView.setText(R.string.no_news);
        mAdapter.clear();

        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
            mNoNewsTextView.setVisibility(View.GONE);
        }
        if (mAdapter.getItemCount() == 0) {
            mNoNewsTextView.setText(R.string.no_news);
            mNoNewsTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        if (mAdapter != null) {
            mAdapter.clear();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings_1) {
            Intent settingsIntent = new Intent(MainActivity.this, NewsSettingsActivity.class);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}















