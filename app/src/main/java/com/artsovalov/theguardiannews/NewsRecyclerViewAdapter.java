package com.artsovalov.theguardiannews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.NewsViewHolder> {

    private final String LOG_TAG = NewsRecyclerViewAdapter.class.getSimpleName();

    private ArrayList<News> mListNews;
    private Context context;

    public NewsRecyclerViewAdapter(Context context, ArrayList<News> listNews) {
        mListNews = listNews;
        this.context = context;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.news_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(layoutId, parent, false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        News news = mListNews.get(position);

        holder.tvTitle.setText(news.getTitle());

        String dateStringFromJson = news.getDate();
        dateStringFromJson = dateStringFromJson.replace('T', ' ');

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date datePublication = new Date();

        try {
            datePublication = sdf.parse(dateStringFromJson);
        } catch (ParseException e) {
            Log.i(LOG_TAG, "Exception parse(dateStringJson) was called");
        }

        String formattedDate = formatDate(datePublication);
        String formattedTime = formatTime(datePublication);

        holder.tvDate.setText(formattedDate);
        holder.tvTime.setText(formattedTime);

        Log.i(LOG_TAG, " " + formattedDate);
        Log.i(LOG_TAG, " " + formattedTime);

        Picasso.with(getContext()).load(news.getMiniature()).noFade().into(holder.ivNews);
    }

    @Override
    public int getItemCount() {
        return mListNews.size();
    }

    public Context getContext() {
        return context;
    }

    public void clear() {
        mListNews.clear();
    }

    public void addAll(List<News> data) {
        if (data != null) {
            clear();
        }
        mListNews.addAll(data);
        notifyDataSetChanged();
    }

    public String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(dateObject);
    }

    public String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(dateObject);
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTitle, tvDate, tvTime;
        ImageView ivNews;

        public NewsViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            ivNews = (ImageView) itemView.findViewById(R.id.ivNewsIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            News news = mListNews.get(getAdapterPosition());
            Uri newsUri = Uri.parse(news.getUrl());
            Intent webSiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
            getContext().startActivity(webSiteIntent);
        }
    }
}
